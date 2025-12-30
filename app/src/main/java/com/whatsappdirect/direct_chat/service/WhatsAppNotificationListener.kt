package com.whatsappdirect.direct_cha.service

import android.app.Notification
import android.os.Build
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import android.util.Log
import com.whatsappdirect.direct_cha.data.local.DeletedMessageDao
import com.whatsappdirect.direct_cha.data.model.DeletedMessage
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class WhatsAppNotificationListener : NotificationListenerService() {
    
    @Inject
    lateinit var deletedMessageDao: DeletedMessageDao
    
    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    
    // Store messages with unique key based on sender + message content
    private val activeMessages = mutableMapOf<String, MessageData>()
    
    // Track notification keys to message keys mapping
    private val notificationToMessageKey = mutableMapOf<String, String>()
    
    // Track recently saved messages to prevent duplicates
    private val recentlySaved = mutableSetOf<String>()
    
    companion object {
        private const val TAG = "WANotificationListener"
        private const val WHATSAPP_PACKAGE = "com.whatsapp"
        private const val WHATSAPP_BUSINESS_PACKAGE = "com.whatsapp.w4b"
        
        // System notification titles to ignore
        private val IGNORED_TITLES = setOf(
            "WhatsApp",
            "WhatsApp Business",
            "Checking for new messages",
            "WhatsApp Web",
            "WhatsApp Web is currently active",
            "Backup in progress",
            "Restoring messages",
            "Downloading",
            "Uploading"
        )
        
        // System notification text patterns to ignore
        private val IGNORED_TEXT_PATTERNS = listOf(
            "Checking for new messages",
            "new messages",
            "messages from",
            "Tap for more info",
            "Missed voice call",
            "Missed video call",
            "Incoming voice call",
            "Incoming video call",
            "Ongoing voice call",
            "Ongoing video call",
            "Backup in progress",
            "Waiting for this message"
        )
    }
    
    data class MessageData(
        val senderName: String,
        val messageText: String,
        val timestamp: Long,
        val packageName: String,
        val isGroupMessage: Boolean = false,
        val groupName: String? = null,
        val hasMedia: Boolean = false,
        val mediaType: String? = null,
        val notificationKey: String
    )
    
    override fun onNotificationPosted(sbn: StatusBarNotification) {
        super.onNotificationPosted(sbn)
        
        if (!isWhatsAppNotification(sbn.packageName)) return
        
        val notification = sbn.notification ?: return
        val extras = notification.extras ?: return
        
        try {
            val title = extras.getCharSequence(Notification.EXTRA_TITLE)?.toString() ?: return
            val text = extras.getCharSequence(Notification.EXTRA_TEXT)?.toString() ?: return
            
            // Skip empty notifications
            if (text.isEmpty() || title.isEmpty()) return
            
            // Skip system notifications by title
            if (IGNORED_TITLES.any { title.equals(it, ignoreCase = true) }) {
                Log.d(TAG, "Ignoring system notification title: $title")
                return
            }
            
            // Skip system notifications by text pattern
            if (IGNORED_TEXT_PATTERNS.any { text.contains(it, ignoreCase = true) }) {
                Log.d(TAG, "Ignoring system notification text: $text")
                return
            }
            
            // Skip typing indicators and call notifications
            if (text.contains("typing...", ignoreCase = true) || 
                text.contains("recording audio", ignoreCase = true) ||
                text.contains("recording video", ignoreCase = true)) {
                return
            }
            
            // Skip summary notifications (group notifications that summarize multiple messages)
            if (notification.flags and Notification.FLAG_GROUP_SUMMARY != 0) {
                Log.d(TAG, "Ignoring group summary notification")
                return
            }
            
            // Detect if it's a group message (format: "GroupName: SenderName" or "SenderName @ GroupName")
            val isGroup = title.contains(":") || title.contains("@")
            val senderName: String
            val groupName: String?
            
            if (isGroup && title.contains(":")) {
                val parts = title.split(":")
                groupName = parts.getOrNull(0)?.trim()
                senderName = parts.getOrNull(1)?.trim() ?: title
            } else if (isGroup && title.contains("@")) {
                val parts = title.split("@")
                senderName = parts.getOrNull(0)?.trim() ?: title
                groupName = parts.getOrNull(1)?.trim()
            } else {
                senderName = title
                groupName = null
            }
            
            // Skip if sender name looks like a system notification
            if (IGNORED_TITLES.any { senderName.equals(it, ignoreCase = true) }) {
                return
            }
            
            // Detect media type
            val hasMedia = text.startsWith("📷") || text.startsWith("🎥") || 
                          text.startsWith("🎵") || text.startsWith("📄") ||
                          text.startsWith("🎤") || text.startsWith("📍") ||
                          text.contains("Photo", ignoreCase = true) ||
                          text.contains("Video", ignoreCase = true) ||
                          text.contains("Audio", ignoreCase = true) ||
                          text.contains("Document", ignoreCase = true) ||
                          text.contains("Voice message", ignoreCase = true) ||
                          text.contains("Sticker", ignoreCase = true) ||
                          text.contains("GIF", ignoreCase = true) ||
                          text.contains("Contact card", ignoreCase = true) ||
                          text.contains("Location", ignoreCase = true)
            
            val mediaType = when {
                text.startsWith("📷") || text.contains("Photo", ignoreCase = true) -> "image"
                text.startsWith("🎥") || text.contains("Video", ignoreCase = true) -> "video"
                text.startsWith("🎵") || text.startsWith("🎤") || 
                    text.contains("Audio", ignoreCase = true) || 
                    text.contains("Voice message", ignoreCase = true) -> "audio"
                text.startsWith("📄") || text.contains("Document", ignoreCase = true) -> "document"
                text.contains("Sticker", ignoreCase = true) || text.contains("GIF", ignoreCase = true) -> "sticker"
                text.startsWith("📍") || text.contains("Location", ignoreCase = true) -> "location"
                text.contains("Contact card", ignoreCase = true) -> "contact"
                else -> null
            }
            
            // Create unique message key based on sender + text + approximate time (within 1 second)
            val timeKey = sbn.postTime / 1000
            val messageKey = "${senderName}_${text.hashCode()}_$timeKey"
            
            val messageData = MessageData(
                senderName = senderName,
                messageText = text,
                timestamp = sbn.postTime,
                packageName = sbn.packageName,
                isGroupMessage = isGroup,
                groupName = groupName,
                hasMedia = hasMedia,
                mediaType = mediaType,
                notificationKey = sbn.key
            )
            
            // Store message with unique key
            activeMessages[messageKey] = messageData
            notificationToMessageKey[sbn.key] = messageKey
            
            Log.d(TAG, "Stored message: $senderName - $text")
            
        } catch (e: Exception) {
            Log.e(TAG, "Error processing notification", e)
        }
    }
    
    override fun onNotificationRemoved(sbn: StatusBarNotification, rankingMap: RankingMap?, reason: Int) {
        super.onNotificationRemoved(sbn, rankingMap, reason)
        
        if (!isWhatsAppNotification(sbn.packageName)) return
        
        // Get the message key for this notification
        val messageKey = notificationToMessageKey.remove(sbn.key) ?: return
        val messageData = activeMessages.remove(messageKey) ?: return
        
        // IMPORTANT: Only save if the notification was removed by the app (not user swipe)
        // Reason codes:
        // REASON_CANCEL = 2 (user dismissed)
        // REASON_CLICK = 1 (user clicked)
        // REASON_APP_CANCEL = 8 (app cancelled - this is what WhatsApp does for deleted messages)
        // REASON_CANCEL_ALL = 3 (user cleared all)
        // REASON_LISTENER_CANCEL = 10 (listener cancelled)
        
        // We want to capture when WhatsApp removes the notification (REASON_APP_CANCEL = 8)
        // This happens when a message is "deleted for everyone"
        val shouldSave = reason == 8 // REASON_APP_CANCEL
        
        Log.d(TAG, "Notification removed - Reason: $reason, Sender: ${messageData.senderName}, Text: ${messageData.messageText}")
        
        if (!shouldSave) {
            Log.d(TAG, "Skipping - not an app cancellation (reason: $reason)")
            return
        }
        
        // Check for duplicates
        val duplicateKey = "${messageData.senderName}_${messageData.messageText.hashCode()}"
        if (recentlySaved.contains(duplicateKey)) {
            Log.d(TAG, "Skipping duplicate message")
            return
        }
        
        // Add to recently saved (will be cleared periodically)
        recentlySaved.add(duplicateKey)
        
        // Clean up old entries if too many
        if (recentlySaved.size > 100) {
            recentlySaved.clear()
        }
        
        // Save to database
        serviceScope.launch {
            try {
                val deletedMessage = DeletedMessage(
                    senderName = messageData.senderName,
                    senderNumber = null,
                    messageText = messageData.messageText,
                    timestamp = messageData.timestamp,
                    deletedTimestamp = System.currentTimeMillis(),
                    packageName = messageData.packageName,
                    isGroupMessage = messageData.isGroupMessage,
                    groupName = messageData.groupName,
                    hasMedia = messageData.hasMedia,
                    mediaType = messageData.mediaType
                )
                
                deletedMessageDao.insertDeletedMessage(deletedMessage)
                Log.d(TAG, "Saved deleted message from ${messageData.senderName}")
            } catch (e: Exception) {
                Log.e(TAG, "Error saving deleted message", e)
            }
        }
    }
    
    // Override the simpler version too for compatibility
    override fun onNotificationRemoved(sbn: StatusBarNotification) {
        // This will be called on older Android versions
        // We can't determine the reason here, so we'll be more conservative
        super.onNotificationRemoved(sbn)
    }
    
    override fun onListenerConnected() {
        super.onListenerConnected()
        // Service is connected and ready
    }
    
    override fun onListenerDisconnected() {
        super.onListenerDisconnected()
        // Try to reconnect
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            requestRebind(android.content.ComponentName(this, WhatsAppNotificationListener::class.java))
        }
    }
    
    override fun onDestroy() {
        super.onDestroy()
        serviceScope.cancel()
        activeMessages.clear()
    }
    
    private fun isWhatsAppNotification(packageName: String): Boolean {
        return packageName == WHATSAPP_PACKAGE || packageName == WHATSAPP_BUSINESS_PACKAGE
    }
}
