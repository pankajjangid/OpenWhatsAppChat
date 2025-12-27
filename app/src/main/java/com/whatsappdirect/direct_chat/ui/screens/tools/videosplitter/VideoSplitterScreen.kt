package com.whatsappdirect.direct_chat.ui.screens.tools.videosplitter

import android.content.ContentValues
import android.content.Context
import android.content.Intent
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
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.ContentCut
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Upload
import androidx.compose.material.icons.filled.VideoFile
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import android.media.MediaCodec
import android.media.MediaExtractor
import android.media.MediaFormat
import android.media.MediaMuxer
import androidx.core.content.FileProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

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
                                context = context,
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
    context: Context,
    index: Int,
    segment: VideoSegment
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.VideoFile,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(40.dp)
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
                    if (segment.fileUri != null) {
                        Text(
                            text = "✓ Saved",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
            
            if (segment.fileUri != null) {
                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Share to WhatsApp
                    Button(
                        onClick = {
                            shareToWhatsApp(context, segment.fileUri, "com.whatsapp")
                        },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF25D366)
                        )
                    ) {
                        Icon(
                            Icons.AutoMirrored.Filled.Send,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("WhatsApp", style = MaterialTheme.typography.labelMedium)
                    }
                    
                    // Share to WA Business
                    Button(
                        onClick = {
                            shareToWhatsApp(context, segment.fileUri, "com.whatsapp.w4b")
                        },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF128C7E)
                        )
                    ) {
                        Icon(
                            Icons.AutoMirrored.Filled.Send,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Business", style = MaterialTheme.typography.labelMedium)
                    }
                    
                    // General Share
                    OutlinedButton(
                        onClick = {
                            shareVideo(context, segment.fileUri)
                        }
                    ) {
                        Icon(
                            Icons.Default.Share,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }
        }
    }
}

data class VideoSegment(
    val index: Int,
    val startTime: Int,
    val endTime: Int,
    val filePath: String,
    val fileUri: Uri? = null
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

private fun shareToWhatsApp(context: Context, uri: Uri, packageName: String) {
    try {
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "video/*"
            putExtra(Intent.EXTRA_STREAM, uri)
            setPackage(packageName)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        context.startActivity(Intent.createChooser(intent, "Share to WhatsApp"))
    } catch (e: Exception) {
        Toast.makeText(context, "WhatsApp not installed", Toast.LENGTH_SHORT).show()
    }
}

private fun shareVideo(context: Context, uri: Uri) {
    try {
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "video/*"
            putExtra(Intent.EXTRA_STREAM, uri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        context.startActivity(Intent.createChooser(intent, "Share Video"))
    } catch (e: Exception) {
        Toast.makeText(context, "Error sharing video", Toast.LENGTH_SHORT).show()
    }
}

private fun copyUriToFile(context: Context, uri: Uri, destFile: File): Boolean {
    return try {
        context.contentResolver.openInputStream(uri)?.use { input ->
            FileOutputStream(destFile).use { output ->
                input.copyTo(output)
            }
        }
        true
    } catch (e: Exception) {
        e.printStackTrace()
        false
    }
}

private fun getOutputDir(context: Context): File {
    val dir = File(context.getExternalFilesDir(Environment.DIRECTORY_MOVIES), "VideoSplitter")
    if (!dir.exists()) {
        dir.mkdirs()
    }
    return dir
}

private fun saveToGallery(context: Context, file: File): Uri? {
    return try {
        val contentValues = ContentValues().apply {
            put(MediaStore.Video.Media.DISPLAY_NAME, file.name)
            put(MediaStore.Video.Media.MIME_TYPE, "video/mp4")
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                put(MediaStore.Video.Media.RELATIVE_PATH, Environment.DIRECTORY_MOVIES + "/WhatsAppTools")
                put(MediaStore.Video.Media.IS_PENDING, 1)
            }
        }
        
        val uri = context.contentResolver.insert(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, contentValues)
        uri?.let {
            context.contentResolver.openOutputStream(it)?.use { output ->
                file.inputStream().use { input ->
                    input.copyTo(output)
                }
            }
            
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                contentValues.clear()
                contentValues.put(MediaStore.Video.Media.IS_PENDING, 0)
                context.contentResolver.update(it, contentValues, null, null)
            }
        }
        uri
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}

private suspend fun splitVideo(
    context: Context,
    videoUri: Uri,
    segmentDurationSec: Int,
    onProgress: (Float) -> Unit
): List<VideoSegment> = withContext(Dispatchers.IO) {
    val segments = mutableListOf<VideoSegment>()
    
    try {
        // Get video duration
        val retriever = MediaMetadataRetriever()
        retriever.setDataSource(context, videoUri)
        val durationMs = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)?.toLongOrNull() ?: 0
        val totalDurationSec = (durationMs / 1000).toInt()
        retriever.release()
        
        // Create output directory
        val outputDir = getOutputDir(context)
        
        // Copy input video to temp file
        val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val inputFile = File(outputDir, "input_$timestamp.mp4")
        
        if (!copyUriToFile(context, videoUri, inputFile)) {
            withContext(Dispatchers.Main) {
                Toast.makeText(context, "Error copying video file", Toast.LENGTH_SHORT).show()
            }
            return@withContext segments
        }
        
        // Calculate segments and split using MediaMuxer
        var currentTimeMs: Long = 0
        var segmentIndex = 0
        val segmentDurationMs = segmentDurationSec * 1000L
        val totalDurationMs = durationMs
        val totalSegments = kotlin.math.ceil(totalDurationMs.toDouble() / segmentDurationMs).toInt()
        
        while (currentTimeMs < totalDurationMs) {
            val endTimeMs = minOf(currentTimeMs + segmentDurationMs, totalDurationMs)
            
            val outputFile = File(outputDir, "segment_${timestamp}_${segmentIndex + 1}.mp4")
            
            val success = splitVideoSegment(
                inputFile.absolutePath,
                outputFile.absolutePath,
                currentTimeMs * 1000, // Convert to microseconds
                endTimeMs * 1000
            )
            
            if (success && outputFile.exists() && outputFile.length() > 0) {
                // Save to gallery
                val galleryUri = saveToGallery(context, outputFile)
                
                // Get file provider URI for sharing
                val fileUri = try {
                    FileProvider.getUriForFile(
                        context,
                        "${context.packageName}.fileprovider",
                        outputFile
                    )
                } catch (e: Exception) {
                    galleryUri
                }
                
                segments.add(
                    VideoSegment(
                        index = segmentIndex,
                        startTime = (currentTimeMs / 1000).toInt(),
                        endTime = (endTimeMs / 1000).toInt(),
                        filePath = outputFile.absolutePath,
                        fileUri = fileUri
                    )
                )
            } else {
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "Error splitting segment ${segmentIndex + 1}", Toast.LENGTH_SHORT).show()
                }
            }
            
            currentTimeMs = endTimeMs
            segmentIndex++
            onProgress(segmentIndex.toFloat() / totalSegments)
        }
        
        // Clean up input file
        inputFile.delete()
        
        withContext(Dispatchers.Main) {
            if (segments.isNotEmpty()) {
                Toast.makeText(context, "Split complete! ${segments.size} segments saved to Movies/WhatsAppTools", Toast.LENGTH_LONG).show()
            }
        }
        
    } catch (e: Exception) {
        e.printStackTrace()
        withContext(Dispatchers.Main) {
            Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }
    
    segments
}

private fun splitVideoSegment(
    inputPath: String,
    outputPath: String,
    startTimeUs: Long,
    endTimeUs: Long
): Boolean {
    var extractor: MediaExtractor? = null
    var muxer: MediaMuxer? = null
    
    try {
        extractor = MediaExtractor()
        extractor.setDataSource(inputPath)
        
        val trackCount = extractor.trackCount
        muxer = MediaMuxer(outputPath, MediaMuxer.OutputFormat.MUXER_OUTPUT_MPEG_4)
        
        val indexMap = mutableMapOf<Int, Int>()
        
        // Add tracks
        for (i in 0 until trackCount) {
            val format = extractor.getTrackFormat(i)
            val mime = format.getString(MediaFormat.KEY_MIME) ?: continue
            
            if (mime.startsWith("video/") || mime.startsWith("audio/")) {
                val newTrackIndex = muxer.addTrack(format)
                indexMap[i] = newTrackIndex
            }
        }
        
        if (indexMap.isEmpty()) {
            return false
        }
        
        muxer.start()
        
        val bufferSize = 1024 * 1024 // 1MB buffer
        val buffer = java.nio.ByteBuffer.allocate(bufferSize)
        val bufferInfo = MediaCodec.BufferInfo()
        
        // Process each track
        for ((extractorTrackIndex, muxerTrackIndex) in indexMap) {
            extractor.selectTrack(extractorTrackIndex)
            extractor.seekTo(startTimeUs, MediaExtractor.SEEK_TO_CLOSEST_SYNC)
            
            while (true) {
                val sampleTime = extractor.sampleTime
                if (sampleTime < 0 || sampleTime > endTimeUs) {
                    break
                }
                
                buffer.clear()
                val sampleSize = extractor.readSampleData(buffer, 0)
                
                if (sampleSize < 0) {
                    break
                }
                
                bufferInfo.offset = 0
                bufferInfo.size = sampleSize
                bufferInfo.presentationTimeUs = sampleTime - startTimeUs
                bufferInfo.flags = extractor.sampleFlags
                
                muxer.writeSampleData(muxerTrackIndex, buffer, bufferInfo)
                
                if (!extractor.advance()) {
                    break
                }
            }
            
            extractor.unselectTrack(extractorTrackIndex)
        }
        
        return true
        
    } catch (e: Exception) {
        e.printStackTrace()
        return false
    } finally {
        try {
            muxer?.stop()
            muxer?.release()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        extractor?.release()
    }
}
