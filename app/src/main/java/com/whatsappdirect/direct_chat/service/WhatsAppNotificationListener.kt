package com.whatsappdirect.direct_cha.service

import android.app.Notification
import android.content.Intent
import android.os.Build
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
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
    
    private val activeMessages = mutableMapOf<String, MessageData>()
    
    companion object {
        private const val WHATSAPP_PACKAGE = "com.whatsapp"
        private const val WHATSAPP_BUSINESS_PACKAGE = "com.whatsapp.w4b"
        private const val ACTION_NOTIFICATION_LISTENER_SETTINGS = "android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS"
    }
    
    data class MessageData(
        val senderName: String,
        val messageText: String,
        val timestamp: Long,
        val packageName: String,
        val isGroupMessage: Boolean = false,
        val groupName: String? = null,
        val hasMedia: Boolean = false,
        val mediaType: String? = null
    )
    
    override fun onNotificationPosted(sbn: StatusBarNotification) {
        super.onNotificationPosted(sbn)
        
        if (!isWhatsAppNotification(sbn.packageName)) return
        
        val notification = sbn.notification ?: return
        val extras = notification.extras ?: return
        
        try {
            val title = extras.getCharSequence(Notification.EXTRA_TITLE)?.toString() ?: return
            val text = extras.getCharSequence(Notification.EXTRA_TEXT)?.toString() ?: return
            
            // Skip notifications that are not messages
            if (text.isEmpty() || title.isEmpty()) return
            if (text.contains("typing...") || text.contains("recording audio")) return
            
            // Detect if it's a group message
            val isGroup = title.contains(":")
            val senderName: String
            val groupName: String?
            
            if (isGroup) {
                val parts = title.split(":")
                senderName = parts.getOrNull(1)?.trim() ?: title
                groupName = parts.getOrNull(0)?.trim()
            } else {
                senderName = title
                groupName = null
            }
            
            // Detect media type
            val hasMedia = text.startsWith("📷") || text.startsWith("🎥") || 
                          text.startsWith("🎵") || text.startsWith("📄") ||
                          text.contains("image omitted") || text.contains("video omitted") ||
                          text.contains("audio omitted") || text.contains("document omitted")
            
            val mediaType = when {
                text.startsWith("📷") || text.contains("image omitted") -> "image"
                text.startsWith("🎥") || text.contains("video omitted") -> "video"
                text.startsWith("🎵") || text.contains("audio omitted") -> "audio"
                text.startsWith("📄") || text.contains("document omitted") -> "document"
                else -> null
            }
            
            val messageData = MessageData(
                senderName = senderName,
                messageText = text,
                timestamp = sbn.postTime,
                packageName = sbn.packageName,
                isGroupMessage = isGroup,
                groupName = groupName,
                hasMedia = hasMedia,
                mediaType = mediaType
            )
            
            // Store message with notification key
            activeMessages[sbn.key] = messageData
            
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    
    override fun onNotificationRemoved(sbn: StatusBarNotification) {
        super.onNotificationRemoved(sbn)
        
        if (!isWhatsAppNotification(sbn.packageName)) return
        
        // Check if this message was stored
        val messageData = activeMessages.remove(sbn.key) ?: return
        
        // Message was removed - likely deleted by sender
        // Save to database
        serviceScope.launch {
            try {
                val deletedMessage = DeletedMessage(
                    senderName = messageData.senderName,
                    senderNumber = null, // Not available from notification
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
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
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
