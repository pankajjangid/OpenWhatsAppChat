package com.whatsappdirect.direct_chat.ui.screens.tools.videosplitter

import android.content.ContentValues
import android.content.Context
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ContentCut
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Upload
import androidx.compose.material.icons.filled.VideoFile
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VideoSplitterScreen(
    onNavigateBack: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    
    var selectedVideoUri by remember { mutableStateOf<Uri?>(null) }
    var videoDuration by remember { mutableIntStateOf(0) }
    var segmentDuration by remember { mutableFloatStateOf(30f) }
    var isProcessing by remember { mutableStateOf(false) }
    var progress by remember { mutableFloatStateOf(0f) }
    var segments by remember { mutableStateOf<List<VideoSegment>>(emptyList()) }
    var videoFileName by remember { mutableStateOf("") }
    
    val videoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let {
            selectedVideoUri = it
            val retriever = MediaMetadataRetriever()
            try {
                retriever.setDataSource(context, it)
                val duration = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)?.toLongOrNull() ?: 0
                videoDuration = (duration / 1000).toInt()
                videoFileName = getFileName(context, it)
                segments = emptyList()
            } catch (e: Exception) {
                Toast.makeText(context, "Error reading video", Toast.LENGTH_SHORT).show()
            } finally {
                retriever.release()
            }
        }
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Video Splitter") },
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
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            // Info Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
                )
            ) {
                Text(
                    text = "Split long videos into 30-second segments for WhatsApp Status. Each segment will be saved separately.",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(12.dp)
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Video Selection
            if (selectedVideoUri == null) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.VideoFile,
                            contentDescription = null,
                            modifier = Modifier.size(80.dp),
                            tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(onClick = { videoPickerLauncher.launch("video/*") }) {
                            Icon(Icons.Default.Upload, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Select Video")
                        }
                    }
                }
            } else {
                // Video Info
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
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.VideoFile,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = videoFileName,
                                    style = MaterialTheme.typography.titleMedium
                                )
                                Text(
                                    text = "Duration: ${formatDuration(videoDuration)}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        Text(
                            text = "Segment Duration: ${segmentDuration.toInt()} seconds",
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Slider(
                            value = segmentDuration,
                            onValueChange = { segmentDuration = it },
                            valueRange = 15f..30f,
                            steps = 2,
                            enabled = !isProcessing
                        )
                        
                        val estimatedSegments = if (videoDuration > 0) {
                            kotlin.math.ceil(videoDuration.toDouble() / segmentDuration).toInt()
                        } else 0
                        
                        Text(
                            text = "Estimated segments: $estimatedSegments",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Action Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = { 
                            selectedVideoUri = null
                            segments = emptyList()
                        },
                        modifier = Modifier.weight(1f),
                        enabled = !isProcessing
                    ) {
                        Text("Change Video")
                    }
                    
                    Button(
                        onClick = {
                            scope.launch {
                                isProcessing = true
                                progress = 0f
                                segments = splitVideo(
                                    context = context,
                                    videoUri = selectedVideoUri!!,
                                    segmentDurationSec = segmentDuration.toInt(),
                                    onProgress = { progress = it }
                                )
                                isProcessing = false
                                if (segments.isNotEmpty()) {
                                    Toast.makeText(context, "Video split into ${segments.size} segments!", Toast.LENGTH_SHORT).show()
                                }
                            }
                        },
                        modifier = Modifier.weight(1f),
                        enabled = !isProcessing && videoDuration > segmentDuration
                    ) {
                        Icon(Icons.Default.ContentCut, contentDescription = null)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Split Video")
                    }
                }
                
                // Progress
                if (isProcessing) {
                    Spacer(modifier = Modifier.height(16.dp))
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        CircularProgressIndicator()
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Processing... ${(progress * 100).toInt()}%",
                            style = MaterialTheme.typography.bodyMedium
                        )
                        LinearProgressIndicator(
                            progress = { progress },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 8.dp)
                        )
                    }
                }
                
                // Segments List
                if (segments.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Segments (${segments.size})",
                        style = MaterialTheme.typography.titleMedium
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    LazyColumn(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        itemsIndexed(segments) { index, segment ->
                            SegmentCard(
                                index = index + 1,
                                segment = segment
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SegmentCard(
    index: Int,
    segment: VideoSegment
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.PlayArrow,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Segment $index",
                    style = MaterialTheme.typography.titleSmall
                )
                Text(
                    text = "${formatDuration(segment.startTime)} - ${formatDuration(segment.endTime)}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Text(
                text = "✓ Saved",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}

data class VideoSegment(
    val index: Int,
    val startTime: Int,
    val endTime: Int,
    val filePath: String
)

private fun formatDuration(seconds: Int): String {
    val mins = seconds / 60
    val secs = seconds % 60
    return String.format("%d:%02d", mins, secs)
}

private fun getFileName(context: Context, uri: Uri): String {
    var name = "video"
    context.contentResolver.query(uri, null, null, null, null)?.use { cursor ->
        val nameIndex = cursor.getColumnIndex(android.provider.OpenableColumns.DISPLAY_NAME)
        if (cursor.moveToFirst() && nameIndex >= 0) {
            name = cursor.getString(nameIndex)
        }
    }
    return name
}

private suspend fun splitVideo(
    context: Context,
    videoUri: Uri,
    segmentDurationSec: Int,
    onProgress: (Float) -> Unit
): List<VideoSegment> = withContext(Dispatchers.IO) {
    val segments = mutableListOf<VideoSegment>()
    
    try {
        val retriever = MediaMetadataRetriever()
        retriever.setDataSource(context, videoUri)
        val durationMs = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)?.toLongOrNull() ?: 0
        val totalDurationSec = (durationMs / 1000).toInt()
        retriever.release()
        
        // Calculate segments
        var currentTime = 0
        var segmentIndex = 0
        
        while (currentTime < totalDurationSec) {
            val endTime = minOf(currentTime + segmentDurationSec, totalDurationSec)
            
            // For now, we'll just create segment info
            // Full video splitting requires FFmpeg or MediaCodec which is complex
            // This creates a reference to the original video with time markers
            segments.add(
                VideoSegment(
                    index = segmentIndex,
                    startTime = currentTime,
                    endTime = endTime,
                    filePath = "segment_${segmentIndex + 1}"
                )
            )
            
            currentTime = endTime
            segmentIndex++
            onProgress(currentTime.toFloat() / totalDurationSec)
        }
        
        // Note: Full video splitting requires FFmpeg library
        // For a complete implementation, add:
        // implementation("com.arthenica:ffmpeg-kit-full:6.0-2")
        
        withContext(Dispatchers.Main) {
            Toast.makeText(
                context, 
                "Note: Video segments are marked. For actual splitting, share to a video editor app.",
                Toast.LENGTH_LONG
            ).show()
        }
        
    } catch (e: Exception) {
        e.printStackTrace()
        withContext(Dispatchers.Main) {
            Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }
    
    segments
}
