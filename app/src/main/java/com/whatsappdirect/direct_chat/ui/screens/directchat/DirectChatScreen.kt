package com.whatsappdirect.direct_cha.ui.screens.directchat

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.NoteAlt
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.whatsappdirect.direct_cha.ui.components.CountryCodeSelector
import com.whatsappdirect.direct_cha.ui.screens.directchat.components.MessageTemplatesSheet

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DirectChatScreen(
    onNavigateToCallLog: () -> Unit,
    viewModel: DirectChatViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsState()
    val recentNumbers by viewModel.recentNumbers.collectAsState(initial = emptyList())
    val templates by viewModel.messageTemplates.collectAsState(initial = emptyList())
    val isIncognito by viewModel.incognitoMode.collectAsState(initial = false)
    
    var selectedAppIndex by remember { mutableIntStateOf(0) }
    val appOptions = listOf("WA", "WA Business")
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Direct Chat") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                ),
                actions = {
                    if (isIncognito) {
                        Icon(
                            imageVector = Icons.Default.VisibilityOff,
                            contentDescription = "Incognito Mode Active",
                            tint = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                    IconButton(onClick = onNavigateToCallLog) {
                        Icon(
                            imageVector = Icons.Default.History,
                            contentDescription = "Call Log",
                            tint = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
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
                        selected = index == selectedAppIndex
                    ) {
                        Text(label)
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Phone Number Input
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
                        text = "Phone Number",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        CountryCodeSelector(
                            selectedCode = uiState.countryCode,
                            onCodeSelected = { viewModel.updateCountryCode(it) },
                            modifier = Modifier.width(120.dp)
                        )
                        
                        Spacer(modifier = Modifier.width(8.dp))
                        
                        OutlinedTextField(
                            value = uiState.phoneNumber,
                            onValueChange = { viewModel.updatePhoneNumber(it) },
                            modifier = Modifier.weight(1f),
                            placeholder = { Text("Phone number") },
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Phone
                            ),
                            singleLine = true,
                            trailingIcon = {
                                if (uiState.phoneNumber.isNotEmpty()) {
                                    IconButton(onClick = { viewModel.clearPhoneNumber() }) {
                                        Icon(
                                            imageVector = Icons.Default.Clear,
                                            contentDescription = "Clear"
                                        )
                                    }
                                }
                            }
                        )
                    }
                    
                    if (uiState.isValidNumber != null) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = if (uiState.isValidNumber == true) "✓ Valid number" else "✗ Invalid number",
                            style = MaterialTheme.typography.bodySmall,
                            color = if (uiState.isValidNumber == true) 
                                MaterialTheme.colorScheme.primary 
                            else 
                                MaterialTheme.colorScheme.error
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Message Input
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Message (Optional)",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        
                        IconButton(onClick = { viewModel.showTemplates() }) {
                            Icon(
                                imageVector = Icons.Default.NoteAlt,
                                contentDescription = "Templates",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    OutlinedTextField(
                        value = uiState.message,
                        onValueChange = { viewModel.updateMessage(it) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(120.dp),
                        placeholder = { Text("Type your message here...") },
                        trailingIcon = {
                            if (uiState.message.isNotEmpty()) {
                                IconButton(onClick = { viewModel.clearMessage() }) {
                                    Icon(
                                        imageVector = Icons.Default.Clear,
                                        contentDescription = "Clear"
                                    )
                                }
                            }
                        }
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Send Button
            Button(
                onClick = {
                    val packageName = if (selectedAppIndex == 0) {
                        "com.whatsapp"
                    } else {
                        "com.whatsapp.w4b"
                    }
                    
                    val fullNumber = "${uiState.countryCode}${uiState.phoneNumber}"
                    var url = "https://api.whatsapp.com/send?phone=$fullNumber"
                    if (uiState.message.isNotEmpty()) {
                        url += "&text=${Uri.encode(uiState.message)}"
                    }
                    
                    val intent = Intent(Intent.ACTION_VIEW).apply {
                        data = Uri.parse(url)
                        setPackage(packageName)
                    }
                    
                    try {
                        context.startActivity(intent)
                        viewModel.saveRecentNumber()
                    } catch (e: Exception) {
                        // Fallback without package
                        val fallbackIntent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                        context.startActivity(fallbackIntent)
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                enabled = uiState.phoneNumber.isNotEmpty()
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.Send,
                    contentDescription = null
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Send Message")
            }
            
            // Recent Numbers
            if (recentNumbers.isNotEmpty()) {
                Spacer(modifier = Modifier.height(24.dp))
                
                Text(
                    text = "Recent Numbers",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.align(Alignment.Start)
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    recentNumbers.take(4).forEach { recent ->
                        FilterChip(
                            onClick = {
                                viewModel.updateCountryCode(recent.countryCode)
                                viewModel.updatePhoneNumber(recent.phoneNumber)
                            },
                            label = { 
                                Text(
                                    text = recent.displayName ?: "+${recent.countryCode}${recent.phoneNumber}",
                                    maxLines = 1
                                )
                            },
                            selected = false
                        )
                    }
                }
            }
        }
    }
    
    if (uiState.showTemplates) {
        MessageTemplatesSheet(
            templates = templates,
            onDismiss = { viewModel.hideTemplates() },
            onSelectTemplate = { viewModel.selectTemplate(it) },
            onAddTemplate = { title, content -> viewModel.addTemplate(title, content) },
            onEditTemplate = { viewModel.updateTemplate(it) },
            onDeleteTemplate = { viewModel.deleteTemplate(it) }
        )
    }
}
