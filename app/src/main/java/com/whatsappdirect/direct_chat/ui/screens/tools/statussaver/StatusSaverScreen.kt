package com.whatsappdirect.direct_chat.ui.screens.tools.statussaver

import android.Manifest
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.PlayCircle
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import coil.compose.AsyncImage
import coil.decode.VideoFrameDecoder
import coil.request.ImageRequest
import java.io.File
import java.io.FileInputStream
import java.io.OutputStream

data class StatusItem(
    val file: File,
    val isVideo: Boolean,
    val uri: Uri
)

enum class StatusFilter {
    ALL, IMAGES, VIDEOS
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StatusSaverScreen(
    onNavigateBack: () -> Unit
) {
    val context = LocalContext.current
    var statuses by remember { mutableStateOf<List<StatusItem>>(emptyList()) }
    var selectedFilter by remember { mutableStateOf(StatusFilter.ALL) }
    var hasPermission by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(true) }
    
    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        hasPermission = permissions.values.all { it }
        if (hasPermission) {
            statuses = loadStatuses(context)
        }
        isLoading = false
    }
    
    // Check permission when screen is visible (handles return from settings)
    val lifecycleOwner = androidx.lifecycle.compose.LocalLifecycleOwner.current
    androidx.compose.runtime.DisposableEffect(lifecycleOwner) {
        val observer = androidx.lifecycle.LifecycleEventObserver { _, event ->
            if (event == androidx.lifecycle.Lifecycle.Event.ON_RESUME) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                    val newPermission = Environment.isExternalStorageManager()
                    if (newPermission != hasPermission) {
                        hasPermission = newPermission
                        if (hasPermission) {
                            statuses = loadStatuses(context)
                        }
                    }
                }
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }
    
    LaunchedEffect(Unit) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            hasPermission = Environment.isExternalStorageManager()
            if (!hasPermission) {
                isLoading = false
            } else {
                statuses = loadStatuses(context)
                isLoading = false
            }
        } else {
            permissionLauncher.launch(
                arrayOf(
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                )
            )
        }
    }
    
    val filteredStatuses = remember(statuses, selectedFilter) {
        when (selectedFilter) {
            StatusFilter.ALL -> statuses
            StatusFilter.IMAGES -> statuses.filter { !it.isVideo }
            StatusFilter.VIDEOS -> statuses.filter { it.isVideo }
        }
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Status Saver") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { 
                        isLoading = true
                        statuses = loadStatuses(context)
                        isLoading = false
                    }) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Refresh"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary,
                    actionIconContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            if (!hasPermission && Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                PermissionRequiredContent(context)
            } else if (isLoading) {
                LoadingContent()
            } else if (statuses.isEmpty()) {
                EmptyContent()
            } else {
                // Filter Chips
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    FilterChip(
                        onClick = { selectedFilter = StatusFilter.ALL },
                        label = { Text("All (${statuses.size})") },
                        selected = selectedFilter == StatusFilter.ALL
                    )
                    FilterChip(
                        onClick = { selectedFilter = StatusFilter.IMAGES },
                        label = { Text("Images (${statuses.count { !it.isVideo }})") },
                        selected = selectedFilter == StatusFilter.IMAGES
                    )
                    FilterChip(
                        onClick = { selectedFilter = StatusFilter.VIDEOS },
                        label = { Text("Videos (${statuses.count { it.isVideo }})") },
                        selected = selectedFilter == StatusFilter.VIDEOS
                    )
                }
                
                // Status Grid
                LazyVerticalGrid(
                    columns = GridCells.Fixed(3),
                    contentPadding = PaddingValues(8.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    items(filteredStatuses) { status ->
                        StatusItemCard(
                            status = status,
                            onSave = { saveStatus(context, status) },
                            onShare = { shareStatus(context, status) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun StatusItemCard(
    status: StatusItem,
    onSave: () -> Unit,
    onShare: () -> Unit
) {
    var showActions by remember { mutableStateOf(false) }
    
    Card(
        modifier = Modifier
            .aspectRatio(1f)
            .clickable { showActions = !showActions },
        shape = RoundedCornerShape(8.dp)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(status.file)
                    .crossfade(true)
                    .apply {
                        if (status.isVideo) {
                            decoderFactory { result, options, _ ->
                                VideoFrameDecoder(result.source, options)
                            }
                        }
                    }
                    .build(),
                contentDescription = "Status",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
            
            if (status.isVideo) {
                Icon(
                    imageVector = Icons.Default.PlayCircle,
                    contentDescription = "Video",
                    modifier = Modifier
                        .align(Alignment.Center)
                        .size(40.dp),
                    tint = Color.White.copy(alpha = 0.9f)
                )
            }
            
            if (showActions) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.6f))
                ) {
                    Row(
                        modifier = Modifier.align(Alignment.Center),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        IconButton(
                            onClick = onSave,
                            modifier = Modifier
                                .size(48.dp)
                                .background(
                                    MaterialTheme.colorScheme.primary,
                                    CircleShape
                                )
                        ) {
                            Icon(
                                Icons.Default.Download,
                                contentDescription = "Save",
                                tint = Color.White
                            )
                        }
                        
                        IconButton(
                            onClick = onShare,
                            modifier = Modifier
                                .size(48.dp)
                                .background(
                                    MaterialTheme.colorScheme.secondary,
                                    CircleShape
                                )
                        ) {
                            Icon(
                                Icons.Default.Share,
                                contentDescription = "Share",
                                tint = Color.White
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun PermissionRequiredContent(context: Context) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.Folder,
            contentDescription = null,
            modifier = Modifier.size(64.dp),
            tint = MaterialTheme.colorScheme.primary
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Text(
            text = "Storage Permission Required",
            style = MaterialTheme.typography.titleLarge,
            textAlign = TextAlign.Center
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Text(
            text = "To view WhatsApp statuses, please grant \"All files access\" permission to this app.",
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Text(
            text = "For Xiaomi/Mi phones: Go to Settings → Apps → WhatsApp Direct → Permissions → Storage → Allow management of all files",
            style = MaterialTheme.typography.bodySmall,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
        )
        
        Spacer(modifier = Modifier.height(24.dp))
        
        Button(
            onClick = {
                openAllFilesAccessSettings(context)
            }
        ) {
            Text("Grant Permission")
        }
        
        Spacer(modifier = Modifier.height(12.dp))
        
        TextButton(
            onClick = {
                openAppSettings(context)
            }
        ) {
            Text("Open App Settings")
        }
    }
}

private fun openAllFilesAccessSettings(context: Context) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
        try {
            // Try to open the specific app's all files access settings
            val intent = Intent(
                android.provider.Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION,
                Uri.parse("package:${context.packageName}")
            )
            context.startActivity(intent)
        } catch (e: Exception) {
            try {
                // Fallback to general all files access settings
                val intent = Intent(android.provider.Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION)
                context.startActivity(intent)
            } catch (e2: Exception) {
                // Final fallback to app settings
                openAppSettings(context)
            }
        }
    }
}

private fun openAppSettings(context: Context) {
    try {
        val intent = Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
            data = Uri.parse("package:${context.packageName}")
        }
        context.startActivity(intent)
    } catch (e: Exception) {
        Toast.makeText(context, "Unable to open settings", Toast.LENGTH_SHORT).show()
    }
}

@Composable
private fun LoadingContent() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text("Loading statuses...")
    }
}

@Composable
private fun EmptyContent() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.Folder,
            contentDescription = null,
            modifier = Modifier.size(64.dp),
            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Text(
            text = "No Statuses Found",
            style = MaterialTheme.typography.titleLarge,
            textAlign = TextAlign.Center
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Text(
            text = "View some WhatsApp statuses first, then come back here to save them.",
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

private fun loadStatuses(context: Context): List<StatusItem> {
    val statusPaths = listOf(
        File(Environment.getExternalStorageDirectory(), "WhatsApp/Media/.Statuses"),
        File(Environment.getExternalStorageDirectory(), "Android/media/com.whatsapp/WhatsApp/Media/.Statuses"),
        File(Environment.getExternalStorageDirectory(), "WhatsApp Business/Media/.Statuses"),
        File(Environment.getExternalStorageDirectory(), "Android/media/com.whatsapp.w4b/WhatsApp Business/Media/.Statuses")
    )
    
    val statuses = mutableListOf<StatusItem>()
    
    for (path in statusPaths) {
        if (path.exists() && path.isDirectory) {
            path.listFiles()?.forEach { file ->
                if (file.isFile && !file.name.equals(".nomedia", ignoreCase = true)) {
                    val isVideo = file.extension.lowercase() in listOf("mp4", "3gp", "mkv", "avi")
                    val isImage = file.extension.lowercase() in listOf("jpg", "jpeg", "png", "gif", "webp")
                    
                    if (isVideo || isImage) {
                        val uri = FileProvider.getUriForFile(
                            context,
                            "${context.packageName}.fileprovider",
                            file
                        )
                        statuses.add(StatusItem(file, isVideo, uri))
                    }
                }
            }
        }
    }
    
    return statuses.sortedByDescending { it.file.lastModified() }
}

private fun saveStatus(context: Context, status: StatusItem) {
    try {
        val outputStream: OutputStream?
        val mimeType = if (status.isVideo) "video/mp4" else "image/jpeg"
        val directory = if (status.isVideo) Environment.DIRECTORY_MOVIES else Environment.DIRECTORY_PICTURES
        
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val contentValues = ContentValues().apply {
                put(MediaStore.MediaColumns.DISPLAY_NAME, status.file.name)
                put(MediaStore.MediaColumns.MIME_TYPE, mimeType)
                put(MediaStore.MediaColumns.RELATIVE_PATH, "$directory/WhatsAppStatuses")
            }
            
            val collection = if (status.isVideo) {
                MediaStore.Video.Media.EXTERNAL_CONTENT_URI
            } else {
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI
            }
            
            val uri = context.contentResolver.insert(collection, contentValues)
            outputStream = uri?.let { context.contentResolver.openOutputStream(it) }
        } else {
            val dir = File(
                Environment.getExternalStoragePublicDirectory(directory),
                "WhatsAppStatuses"
            )
            if (!dir.exists()) dir.mkdirs()
            
            val destFile = File(dir, status.file.name)
            outputStream = destFile.outputStream()
        }
        
        outputStream?.use { out ->
            FileInputStream(status.file).use { input ->
                input.copyTo(out)
            }
        }
        
        Toast.makeText(context, "Status saved!", Toast.LENGTH_SHORT).show()
    } catch (e: Exception) {
        e.printStackTrace()
        Toast.makeText(context, "Failed to save status", Toast.LENGTH_SHORT).show()
    }
}

private fun shareStatus(context: Context, status: StatusItem) {
    try {
        val uri = FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            status.file
        )
        
        val mimeType = if (status.isVideo) "video/*" else "image/*"
        
        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            type = mimeType
            putExtra(Intent.EXTRA_STREAM, uri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        
        context.startActivity(Intent.createChooser(shareIntent, "Share Status"))
    } catch (e: Exception) {
        e.printStackTrace()
        Toast.makeText(context, "Failed to share status", Toast.LENGTH_SHORT).show()
    }
}
