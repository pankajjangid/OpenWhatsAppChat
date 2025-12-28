package com.whatsappdirect.direct_cha.ui.screens.tools.bulkmessage

import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.net.URLEncoder

data class BulkContact(
    val countryCode: String,
    val phoneNumber: String,
    var status: SendStatus = SendStatus.PENDING
)

enum class SendStatus {
    PENDING, SENDING, SENT, FAILED
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BulkMessageScreen(
    onNavigateBack: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    
    var message by remember { mutableStateOf("") }
    var currentCountryCode by remember { mutableStateOf("91") }
    var currentPhoneNumber by remember { mutableStateOf("") }
    val contacts = remember { mutableStateListOf<BulkContact>() }
    
    var selectedAppIndex by remember { mutableIntStateOf(0) }
    val appOptions = listOf("WhatsApp", "WA Business")
    
    var isSending by remember { mutableStateOf(false) }
    var currentSendingIndex by remember { mutableIntStateOf(-1) }
    var sendProgress by remember { mutableStateOf(0f) }
    
    fun addContact() {
        if (currentPhoneNumber.isNotEmpty()) {
            val cleanNumber = currentPhoneNumber.filter { it.isDigit() }
            if (cleanNumber.isNotEmpty() && contacts.none { it.phoneNumber == cleanNumber && it.countryCode == currentCountryCode }) {
                contacts.add(BulkContact(currentCountryCode, cleanNumber))
                currentPhoneNumber = ""
            }
        }
    }
    
    fun startBulkSend() {
        if (contacts.isEmpty() || message.isEmpty()) {
            Toast.makeText(context, "Add contacts and message first", Toast.LENGTH_SHORT).show()
            return
        }
        
        isSending = true
        scope.launch {
            contacts.forEachIndexed { index, contact ->
                currentSendingIndex = index
                contact.status = SendStatus.SENDING
                
                try {
                    val packageName = if (selectedAppIndex == 0) {
                        "com.whatsapp"
                    } else {
                        "com.whatsapp.w4b"
                    }
                    
                    val fullNumber = "${contact.countryCode}${contact.phoneNumber}"
                    val encodedMessage = URLEncoder.encode(message, "UTF-8")
                    val uri = Uri.parse("https://api.whatsapp.com/send?phone=$fullNumber&text=$encodedMessage")
                    
                    val intent = Intent(Intent.ACTION_VIEW, uri).apply {
                        setPackage(packageName)
                        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    }
                    
                    context.startActivity(intent)
                    contact.status = SendStatus.SENT
                    
                    // Wait for user to send and return
                    delay(3000)
                    
                } catch (e: Exception) {
                    contact.status = SendStatus.FAILED
                }
                
                sendProgress = (index + 1).toFloat() / contacts.size
            }
            
            isSending = false
            currentSendingIndex = -1
            Toast.makeText(context, "Bulk send completed!", Toast.LENGTH_SHORT).show()
        }
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Bulk Message") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        },
        floatingActionButton = {
            if (!isSending && contacts.isNotEmpty() && message.isNotEmpty()) {
                FloatingActionButton(
                    onClick = { startBulkSend() },
                    containerColor = MaterialTheme.colorScheme.primary
                ) {
                    Icon(
                        imageVector = Icons.Default.PlayArrow,
                        contentDescription = "Start Bulk Send"
                    )
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            // App Selection
            SingleChoiceSegmentedButtonRow(
                modifier = Modifier.fillMaxWidth()
            ) {
                appOptions.forEachIndexed { index, label ->
                    SegmentedButton(
                        shape = SegmentedButtonDefaults.itemShape(
                            index = index,
                            count = appOptions.size
                        ),
                        onClick = { selectedAppIndex = index },
                        selected = index == selectedAppIndex,
                        enabled = !isSending
                    ) {
                        Text(label)
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Message Input
            OutlinedTextField(
                value = message,
                onValueChange = { message = it },
                label = { Text("Message") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3,
                maxLines = 5,
                enabled = !isSending
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Add Contact Section
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "Add Contacts",
                        style = MaterialTheme.typography.titleSmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedTextField(
                            value = currentCountryCode,
                            onValueChange = { currentCountryCode = it.filter { c -> c.isDigit() }.take(4) },
                            label = { Text("Code") },
                            modifier = Modifier.width(80.dp),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            singleLine = true,
                            prefix = { Text("+") },
                            enabled = !isSending
                        )
                        
                        Spacer(modifier = Modifier.width(8.dp))
                        
                        OutlinedTextField(
                            value = currentPhoneNumber,
                            onValueChange = { currentPhoneNumber = it.filter { c -> c.isDigit() } },
                            label = { Text("Phone Number") },
                            modifier = Modifier.weight(1f),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                            singleLine = true,
                            trailingIcon = {
                                if (currentPhoneNumber.isNotEmpty()) {
                                    IconButton(onClick = { currentPhoneNumber = "" }) {
                                        Icon(Icons.Default.Clear, "Clear")
                                    }
                                }
                            },
                            enabled = !isSending
                        )
                        
                        Spacer(modifier = Modifier.width(8.dp))
                        
                        IconButton(
                            onClick = { addContact() },
                            enabled = !isSending && currentPhoneNumber.isNotEmpty()
                        ) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = "Add",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Progress indicator
            if (isSending) {
                Column {
                    Text(
                        text = "Sending... ${(sendProgress * 100).toInt()}%",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    LinearProgressIndicator(
                        progress = { sendProgress },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
            
            // Contacts List Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Contacts (${contacts.size})",
                    style = MaterialTheme.typography.titleMedium
                )
                
                if (contacts.isNotEmpty() && !isSending) {
                    OutlinedButton(
                        onClick = { contacts.clear() }
                    ) {
                        Text("Clear All")
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Contacts List
            if (contacts.isEmpty()) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = null,
                            modifier = Modifier.size(48.dp),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "No contacts added yet",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    itemsIndexed(contacts) { index, contact ->
                        ContactItem(
                            contact = contact,
                            isCurrentlySending = index == currentSendingIndex,
                            onDelete = { 
                                if (!isSending) {
                                    contacts.removeAt(index)
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ContactItem(
    contact: BulkContact,
    isCurrentlySending: Boolean,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = when {
                isCurrentlySending -> MaterialTheme.colorScheme.primaryContainer
                contact.status == SendStatus.SENT -> MaterialTheme.colorScheme.secondaryContainer
                contact.status == SendStatus.FAILED -> MaterialTheme.colorScheme.errorContainer
                else -> MaterialTheme.colorScheme.surfaceVariant
            }
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Person,
                contentDescription = null,
                modifier = Modifier.size(32.dp),
                tint = MaterialTheme.colorScheme.primary
            )
            
            Spacer(modifier = Modifier.width(12.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "+${contact.countryCode} ${contact.phoneNumber}",
                    style = MaterialTheme.typography.bodyLarge
                )
                Text(
                    text = when (contact.status) {
                        SendStatus.PENDING -> "Pending"
                        SendStatus.SENDING -> "Sending..."
                        SendStatus.SENT -> "Sent ✓"
                        SendStatus.FAILED -> "Failed ✗"
                    },
                    style = MaterialTheme.typography.bodySmall,
                    color = when (contact.status) {
                        SendStatus.SENT -> MaterialTheme.colorScheme.primary
                        SendStatus.FAILED -> MaterialTheme.colorScheme.error
                        else -> MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                    }
                )
            }
            
            if (contact.status == SendStatus.PENDING) {
                IconButton(onClick = onDelete) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete",
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            }
        }
    }
}
