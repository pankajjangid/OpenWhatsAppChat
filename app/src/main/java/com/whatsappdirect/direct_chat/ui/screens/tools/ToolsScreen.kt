package com.whatsappdirect.direct_chat.ui.screens.tools

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Computer
import androidx.compose.material.icons.filled.ContentCut
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.EmojiEmotions
import androidx.compose.material.icons.filled.FormatBold
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.Repeat
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.SpaceBar
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

data class Tool(
    val id: String,
    val name: String,
    val description: String,
    val icon: ImageVector,
    val isAvailable: Boolean = true
)

val tools = listOf(
    Tool(
        id = "status_saver",
        name = "Status Saver",
        description = "Save WhatsApp statuses",
        icon = Icons.Default.Download
    ),
    Tool(
        id = "qr_generator",
        name = "QR Generator",
        description = "Generate WhatsApp QR",
        icon = Icons.Default.QrCode
    ),
    Tool(
        id = "text_formatter",
        name = "Text Formatter",
        description = "Bold, italic & more",
        icon = Icons.Default.FormatBold
    ),
    Tool(
        id = "text_repeater",
        name = "Text Repeater",
        description = "Repeat text multiple times",
        icon = Icons.Default.Repeat
    ),
    Tool(
        id = "qr_scanner",
        name = "QR Scanner",
        description = "Scan WhatsApp QR codes",
        icon = Icons.Default.QrCodeScanner
    ),
    Tool(
        id = "bulk_message",
        name = "Bulk Message",
        description = "Send to multiple numbers",
        icon = Icons.Default.Group
    ),
    Tool(
        id = "image_to_sticker",
        name = "Image to Sticker",
        description = "Convert images to stickers",
        icon = Icons.Default.Image
    ),
    Tool(
        id = "scheduler",
        name = "Message Scheduler",
        description = "Schedule messages",
        icon = Icons.Default.Schedule
    ),
    Tool(
        id = "video_splitter",
        name = "Video Splitter",
        description = "Split videos for status",
        icon = Icons.Default.ContentCut
    ),
    Tool(
        id = "blank_message",
        name = "Blank Message",
        description = "Send invisible messages",
        icon = Icons.Default.SpaceBar
    ),
    Tool(
        id = "emoji_combos",
        name = "Emoji Combos",
        description = "Popular emoji combinations",
        icon = Icons.Default.EmojiEmotions
    ),
    Tool(
        id = "wa_web",
        name = "WhatsApp Web",
        description = "Link to WhatsApp Web",
        icon = Icons.Default.Computer
    )
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ToolsScreen(
    onNavigateToStatusSaver: () -> Unit,
    onNavigateToQrGenerator: () -> Unit,
    onNavigateToTextFormatter: () -> Unit,
    onNavigateToTextRepeater: () -> Unit,
    onNavigateToQrScanner: () -> Unit,
    onNavigateToBulkMessage: () -> Unit,
    onNavigateToImageToSticker: () -> Unit,
    onNavigateToScheduler: () -> Unit,
    onNavigateToVideoSplitter: () -> Unit,
    onNavigateToBlankMessage: () -> Unit,
    onNavigateToEmojiCombos: () -> Unit,
    onNavigateToWAWeb: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Tools") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) { paddingValues ->
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(tools) { tool ->
                ToolCard(
                    tool = tool,
                    onClick = {
                        when (tool.id) {
                            "status_saver" -> onNavigateToStatusSaver()
                            "qr_generator" -> onNavigateToQrGenerator()
                            "text_formatter" -> onNavigateToTextFormatter()
                            "text_repeater" -> onNavigateToTextRepeater()
                            "qr_scanner" -> onNavigateToQrScanner()
                            "bulk_message" -> onNavigateToBulkMessage()
                            "image_to_sticker" -> onNavigateToImageToSticker()
                            "scheduler" -> onNavigateToScheduler()
                            "video_splitter" -> onNavigateToVideoSplitter()
                            "blank_message" -> onNavigateToBlankMessage()
                            "emoji_combos" -> onNavigateToEmojiCombos()
                            "wa_web" -> onNavigateToWAWeb()
                        }
                    }
                )
            }
        }
    }
}

@Composable
fun ToolCard(
    tool: Tool,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(enabled = tool.isAvailable) { onClick() },
        colors = CardDefaults.cardColors(
            containerColor = if (tool.isAvailable) 
                MaterialTheme.colorScheme.surfaceVariant 
            else 
                MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = tool.icon,
                contentDescription = tool.name,
                modifier = Modifier.size(48.dp),
                tint = if (tool.isAvailable) 
                    MaterialTheme.colorScheme.primary 
                else 
                    MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Text(
                text = tool.name,
                style = MaterialTheme.typography.titleSmall,
                textAlign = TextAlign.Center,
                color = if (tool.isAvailable) 
                    MaterialTheme.colorScheme.onSurfaceVariant 
                else 
                    MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
            )
            
            Spacer(modifier = Modifier.height(4.dp))
            
            Text(
                text = if (tool.isAvailable) tool.description else "Coming Soon",
                style = MaterialTheme.typography.bodySmall,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
            )
        }
    }
}
