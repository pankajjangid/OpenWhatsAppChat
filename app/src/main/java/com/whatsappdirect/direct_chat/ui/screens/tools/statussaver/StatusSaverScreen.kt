package com.whatsappdirect.direct_chat.ui.screens.tools.statussaver

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.DocumentsContract
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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.FolderOpen
import androidx.compose.material.icons.filled.PlayCircle
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.documentfile.provider.DocumentFile
import coil.compose.AsyncImage
import coil.decode.VideoFrameDecoder
import coil.request.ImageRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

data class StatusItem(
    val name: String,
    val isVideo: Boolean,
    val uri: Uri,
    val size: Long = 0
)

enum class StatusFilter {
    ALL, IMAGES, VIDEOS
}

private const val PREFS_NAME = "status_saver_prefs"
private const val KEY_STATUS_FOLDER_URI = "status_folder_uri"

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StatusSaverScreen(
    onNavigateBack: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var statuses by remember { mutableStateOf<List<StatusItem>>(emptyList()) }
    var selectedFilter by remember { mutableStateOf(StatusFilter.ALL) }
    var hasFolderAccess by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(true) }
    var savedFolderUri by remember { mutableStateOf<Uri?>(null) }
    
    // SAF folder picker launcher
    val folderPickerLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.OpenDocumentTree()
    ) { uri ->
        uri?.let {
            // Take persistent permission
            val takeFlags = Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION
            context.contentResolver.takePersistableUriPermission(it, takeFlags)
            
            // Save the URI
            saveFolderUri(context, it)
            savedFolderUri = it
            hasFolderAccess = true
            
            // Load statuses
            scope.launch {
                isLoading = true
                statuses = loadStatusesFromSAF(context, it)
                isLoading = false
            }
        }
    }
    
    // Check for saved folder access on launch
    LaunchedEffect(Unit) {
        val savedUri = getSavedFolderUri(context)
        if (savedUri != null && hasPersistedPermission(context, savedUri)) {
            savedFolderUri = savedUri
            hasFolderAccess = true
            statuses = loadStatusesFromSAF(context, savedUri)
        }
        isLoading = false
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
                        savedFolderUri?.let { uri ->
                            scope.launch {
                                isLoading = true
                                statuses = loadStatusesFromSAF(context, uri)
                                isLoading = false
                            }
                        }
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
            if (isLoading) {
                LoadingContent()
            } else if (!hasFolderAccess) {
                SelectFolderContent(
                    onSelectFolder = { folderPickerLauncher.launch(getWhatsAppStatusUri()) }
                )
            } else if (statuses.isEmpty()) {
                EmptyContent(
                    onChangeFolder = { folderPickerLauncher.launch(getWhatsAppStatusUri()) }
                )
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
                    .data(status.uri)
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
private fun SelectFolderContent(
    onSelectFolder: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.FolderOpen,
            contentDescription = null,
            modifier = Modifier.size(80.dp),
            tint = MaterialTheme.colorScheme.primary
        )
        
        Spacer(modifier = Modifier.height(24.dp))
        
        Text(
            text = "Select WhatsApp Status Folder",
            style = MaterialTheme.typography.titleLarge,
            textAlign = TextAlign.Center
        )
        
        Spacer(modifier = Modifier.height(12.dp))
        
        Text(
            text = "To view and save WhatsApp statuses, please select the status folder:",
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = androidx.compose.material3.CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            )
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = "📁 Navigate to:",
                    style = MaterialTheme.typography.titleSmall
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Android → media → com.whatsapp → WhatsApp → Media → .Statuses",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Or for WhatsApp Business:",
                    style = MaterialTheme.typography.titleSmall
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Android → media → com.whatsapp.w4b → WhatsApp Business → Media → .Statuses",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        Button(
            onClick = onSelectFolder
        ) {
            Icon(Icons.Default.Folder, contentDescription = null)
            Spacer(modifier = Modifier.size(8.dp))
            Text("Select Status Folder")
        }
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
private fun EmptyContent(
    onChangeFolder: () -> Unit
) {
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
        
        Spacer(modifier = Modifier.height(16.dp))
        
        OutlinedButton(onClick = onChangeFolder) {
            Icon(Icons.Default.FolderOpen, contentDescription = null)
            Spacer(modifier = Modifier.size(8.dp))
            Text("Change Folder")
        }
    }
}

// SAF Helper Functions
private fun saveFolderUri(context: Context, uri: Uri) {
    val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    prefs.edit().putString(KEY_STATUS_FOLDER_URI, uri.toString()).apply()
}

private fun getSavedFolderUri(context: Context): Uri? {
    val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    val uriString = prefs.getString(KEY_STATUS_FOLDER_URI, null)
    return uriString?.let { Uri.parse(it) }
}

private fun hasPersistedPermission(context: Context, uri: Uri): Boolean {
    return context.contentResolver.persistedUriPermissions.any { 
        it.uri == uri && it.isReadPermission 
    }
}

private fun getWhatsAppStatusUri(): Uri? {
    // Try to start in the Android/media folder for easier navigation
    return try {
        val baseUri = DocumentsContract.buildDocumentUri(
            "com.android.externalstorage.documents",
            "primary:Android/media"
        )
        DocumentsContract.buildDocumentUri(
            "com.android.externalstorage.documents",
            "primary:Android"
        )
    } catch (e: Exception) {
        null
    }
}

private suspend fun loadStatusesFromSAF(context: Context, folderUri: Uri): List<StatusItem> = withContext(Dispatchers.IO) {
    val statuses = mutableListOf<StatusItem>()
    
    try {
        val documentFile = DocumentFile.fromTreeUri(context, folderUri)
        
        documentFile?.listFiles()?.forEach { file ->
            if (file.isFile && file.name != null && !file.name.equals(".nomedia", ignoreCase = true)) {
                val name = file.name ?: return@forEach
                val extension = name.substringAfterLast('.', "").lowercase()
                
                val isVideo = extension in listOf("mp4", "3gp", "mkv", "avi")
                val isImage = extension in listOf("jpg", "jpeg", "png", "gif", "webp")
                
                if (isVideo || isImage) {
                    file.uri.let { uri ->
                        statuses.add(
                            StatusItem(
                                name = name,
                                isVideo = isVideo,
                                uri = uri,
                                size = file.length()
                            )
                        )
                    }
                }
            }
        }
    } catch (e: Exception) {
        e.printStackTrace()
    }
    
    statuses.sortedByDescending { it.size }
}

private fun saveStatus(context: Context, status: StatusItem) {
    try {
        val mimeType = if (status.isVideo) "video/mp4" else "image/jpeg"
        val directory = if (status.isVideo) Environment.DIRECTORY_MOVIES else Environment.DIRECTORY_PICTURES
        
        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, status.name)
            put(MediaStore.MediaColumns.MIME_TYPE, mimeType)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                put(MediaStore.MediaColumns.RELATIVE_PATH, "$directory/WhatsAppStatuses")
            }
        }
        
        val collection = if (status.isVideo) {
            MediaStore.Video.Media.EXTERNAL_CONTENT_URI
        } else {
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        }
        
        val destUri = context.contentResolver.insert(collection, contentValues)
        
        destUri?.let { dest ->
            context.contentResolver.openOutputStream(dest)?.use { outputStream ->
                context.contentResolver.openInputStream(status.uri)?.use { inputStream ->
                    inputStream.copyTo(outputStream)
                }
            }
        }
        
        Toast.makeText(context, "Status saved to $directory/WhatsAppStatuses!", Toast.LENGTH_SHORT).show()
    } catch (e: Exception) {
        e.printStackTrace()
        Toast.makeText(context, "Failed to save status: ${e.message}", Toast.LENGTH_SHORT).show()
    }
}

private fun shareStatus(context: Context, status: StatusItem) {
    try {
        val mimeType = if (status.isVideo) "video/*" else "image/*"
        
        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            type = mimeType
            putExtra(Intent.EXTRA_STREAM, status.uri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        
        context.startActivity(Intent.createChooser(shareIntent, "Share Status"))
    } catch (e: Exception) {
        e.printStackTrace()
        Toast.makeText(context, "Failed to share status", Toast.LENGTH_SHORT).show()
    }
}
