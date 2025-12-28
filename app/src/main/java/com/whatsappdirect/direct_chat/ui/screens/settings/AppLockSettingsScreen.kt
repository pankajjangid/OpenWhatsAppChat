package com.whatsappdirect.direct_cha.ui.screens.settings

import android.widget.Toast
import androidx.biometric.BiometricManager
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Backspace
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.whatsappdirect.direct_cha.data.local.PreferencesManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AppLockSettingsViewModel @Inject constructor(
    private val preferencesManager: PreferencesManager
) : ViewModel() {
    
    val appLockEnabled = preferencesManager.appLockEnabled
    val useBiometric = preferencesManager.useBiometric
    
    private val _currentPin = MutableStateFlow("")
    val currentPin: StateFlow<String> = _currentPin.asStateFlow()
    
    fun setAppLockEnabled(enabled: Boolean, pin: String? = null) {
        viewModelScope.launch {
            if (enabled && pin != null) {
                preferencesManager.setAppLockPin(pin)
            }
            preferencesManager.setAppLockEnabled(enabled)
            if (!enabled) {
                preferencesManager.setAppLockPin("")
            }
        }
    }
    
    fun setUseBiometric(enabled: Boolean) {
        viewModelScope.launch {
            preferencesManager.setUseBiometric(enabled)
        }
    }
    
    fun verifyCurrentPin(pin: String, onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            val savedPin = preferencesManager.appLockPin.first()
            onResult(pin == savedPin)
        }
    }
    
    fun changePin(newPin: String) {
        viewModelScope.launch {
            preferencesManager.setAppLockPin(newPin)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppLockSettingsScreen(
    onNavigateBack: () -> Unit,
    viewModel: AppLockSettingsViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val appLockEnabled by viewModel.appLockEnabled.collectAsState(initial = false)
    val useBiometric by viewModel.useBiometric.collectAsState(initial = true)
    
    var showSetPinDialog by remember { mutableStateOf(false) }
    var showChangePinDialog by remember { mutableStateOf(false) }
    var showDisableDialog by remember { mutableStateOf(false) }
    
    val biometricAvailable = remember {
        val biometricManager = BiometricManager.from(context)
        biometricManager.canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_STRONG) == 
            BiometricManager.BIOMETRIC_SUCCESS
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("App Lock") },
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
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Lock,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.size(12.dp))
                            Column {
                                Text(
                                    text = "Enable App Lock",
                                    style = MaterialTheme.typography.titleMedium
                                )
                                Text(
                                    text = "Require PIN to open app",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                        Switch(
                            checked = appLockEnabled,
                            onCheckedChange = { enabled ->
                                if (enabled) {
                                    showSetPinDialog = true
                                } else {
                                    showDisableDialog = true
                                }
                            }
                        )
                    }
                }
            }
            
            if (appLockEnabled) {
                Spacer(modifier = Modifier.height(16.dp))
                
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        if (biometricAvailable) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.Fingerprint,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.primary
                                    )
                                    Spacer(modifier = Modifier.size(12.dp))
                                    Column {
                                        Text(
                                            text = "Use Fingerprint",
                                            style = MaterialTheme.typography.titleMedium
                                        )
                                        Text(
                                            text = "Unlock with fingerprint",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }
                                Switch(
                                    checked = useBiometric,
                                    onCheckedChange = { viewModel.setUseBiometric(it) }
                                )
                            }
                            
                            Spacer(modifier = Modifier.height(16.dp))
                        }
                        
                        TextButton(
                            onClick = { showChangePinDialog = true },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Change PIN")
                        }
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "🔒 Security Tip",
                        style = MaterialTheme.typography.titleSmall,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "App lock protects your recent numbers and chat history from unauthorized access.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }
        }
    }
    
    if (showSetPinDialog) {
        SetPinDialog(
            onDismiss = { showSetPinDialog = false },
            onPinSet = { pin ->
                viewModel.setAppLockEnabled(true, pin)
                showSetPinDialog = false
                Toast.makeText(context, "App lock enabled", Toast.LENGTH_SHORT).show()
            }
        )
    }
    
    if (showChangePinDialog) {
        ChangePinDialog(
            onDismiss = { showChangePinDialog = false },
            onVerifyOldPin = { pin, callback -> viewModel.verifyCurrentPin(pin, callback) },
            onPinChanged = { newPin ->
                viewModel.changePin(newPin)
                showChangePinDialog = false
                Toast.makeText(context, "PIN changed", Toast.LENGTH_SHORT).show()
            }
        )
    }
    
    if (showDisableDialog) {
        VerifyPinDialog(
            title = "Disable App Lock",
            onDismiss = { showDisableDialog = false },
            onVerify = { pin, callback -> viewModel.verifyCurrentPin(pin, callback) },
            onSuccess = {
                viewModel.setAppLockEnabled(false)
                showDisableDialog = false
                Toast.makeText(context, "App lock disabled", Toast.LENGTH_SHORT).show()
            }
        )
    }
}

@Composable
private fun SetPinDialog(
    onDismiss: () -> Unit,
    onPinSet: (String) -> Unit
) {
    var pin by remember { mutableStateOf("") }
    var confirmPin by remember { mutableStateOf("") }
    var step by remember { mutableStateOf(1) }
    var error by remember { mutableStateOf<String?>(null) }
    
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (step == 1) "Set PIN" else "Confirm PIN") },
        text = {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = if (step == 1) "Enter a 4-digit PIN" else "Re-enter your PIN",
                    style = MaterialTheme.typography.bodyMedium
                )
                
                Spacer(modifier = Modifier.height(24.dp))
                
                PinDots(length = if (step == 1) pin.length else confirmPin.length)
                
                if (error != null) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = error!!,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
                
                Spacer(modifier = Modifier.height(24.dp))
                
                MiniNumPad(
                    onDigit = { digit ->
                        if (step == 1 && pin.length < 4) {
                            pin += digit
                            error = null
                            if (pin.length == 4) step = 2
                        } else if (step == 2 && confirmPin.length < 4) {
                            confirmPin += digit
                            error = null
                            if (confirmPin.length == 4) {
                                if (confirmPin == pin) {
                                    onPinSet(pin)
                                } else {
                                    error = "PINs don't match"
                                    confirmPin = ""
                                }
                            }
                        }
                    },
                    onDelete = {
                        if (step == 1 && pin.isNotEmpty()) {
                            pin = pin.dropLast(1)
                        } else if (step == 2 && confirmPin.isNotEmpty()) {
                            confirmPin = confirmPin.dropLast(1)
                        }
                        error = null
                    }
                )
            }
        },
        confirmButton = {},
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
private fun VerifyPinDialog(
    title: String,
    onDismiss: () -> Unit,
    onVerify: (String, (Boolean) -> Unit) -> Unit,
    onSuccess: () -> Unit
) {
    var pin by remember { mutableStateOf("") }
    var error by remember { mutableStateOf<String?>(null) }
    
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title) },
        text = {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Enter your current PIN",
                    style = MaterialTheme.typography.bodyMedium
                )
                
                Spacer(modifier = Modifier.height(24.dp))
                
                PinDots(length = pin.length)
                
                if (error != null) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = error!!,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
                
                Spacer(modifier = Modifier.height(24.dp))
                
                MiniNumPad(
                    onDigit = { digit ->
                        if (pin.length < 4) {
                            pin += digit
                            error = null
                            if (pin.length == 4) {
                                onVerify(pin) { success ->
                                    if (success) {
                                        onSuccess()
                                    } else {
                                        error = "Incorrect PIN"
                                        pin = ""
                                    }
                                }
                            }
                        }
                    },
                    onDelete = {
                        if (pin.isNotEmpty()) {
                            pin = pin.dropLast(1)
                        }
                        error = null
                    }
                )
            }
        },
        confirmButton = {},
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
private fun ChangePinDialog(
    onDismiss: () -> Unit,
    onVerifyOldPin: (String, (Boolean) -> Unit) -> Unit,
    onPinChanged: (String) -> Unit
) {
    var step by remember { mutableStateOf(1) }
    var oldPin by remember { mutableStateOf("") }
    var newPin by remember { mutableStateOf("") }
    var confirmPin by remember { mutableStateOf("") }
    var error by remember { mutableStateOf<String?>(null) }
    
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { 
            Text(
                when (step) {
                    1 -> "Current PIN"
                    2 -> "New PIN"
                    else -> "Confirm PIN"
                }
            )
        },
        text = {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = when (step) {
                        1 -> "Enter your current PIN"
                        2 -> "Enter new 4-digit PIN"
                        else -> "Re-enter new PIN"
                    },
                    style = MaterialTheme.typography.bodyMedium
                )
                
                Spacer(modifier = Modifier.height(24.dp))
                
                PinDots(
                    length = when (step) {
                        1 -> oldPin.length
                        2 -> newPin.length
                        else -> confirmPin.length
                    }
                )
                
                if (error != null) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = error!!,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
                
                Spacer(modifier = Modifier.height(24.dp))
                
                MiniNumPad(
                    onDigit = { digit ->
                        when (step) {
                            1 -> {
                                if (oldPin.length < 4) {
                                    oldPin += digit
                                    error = null
                                    if (oldPin.length == 4) {
                                        onVerifyOldPin(oldPin) { success ->
                                            if (success) {
                                                step = 2
                                            } else {
                                                error = "Incorrect PIN"
                                                oldPin = ""
                                            }
                                        }
                                    }
                                }
                            }
                            2 -> {
                                if (newPin.length < 4) {
                                    newPin += digit
                                    error = null
                                    if (newPin.length == 4) step = 3
                                }
                            }
                            3 -> {
                                if (confirmPin.length < 4) {
                                    confirmPin += digit
                                    error = null
                                    if (confirmPin.length == 4) {
                                        if (confirmPin == newPin) {
                                            onPinChanged(newPin)
                                        } else {
                                            error = "PINs don't match"
                                            confirmPin = ""
                                        }
                                    }
                                }
                            }
                        }
                    },
                    onDelete = {
                        when (step) {
                            1 -> if (oldPin.isNotEmpty()) oldPin = oldPin.dropLast(1)
                            2 -> if (newPin.isNotEmpty()) newPin = newPin.dropLast(1)
                            3 -> if (confirmPin.isNotEmpty()) confirmPin = confirmPin.dropLast(1)
                        }
                        error = null
                    }
                )
            }
        },
        confirmButton = {},
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
private fun PinDots(length: Int) {
    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        repeat(4) { index ->
            Box(
                modifier = Modifier
                    .size(16.dp)
                    .clip(CircleShape)
                    .background(
                        if (index < length) 
                            MaterialTheme.colorScheme.primary 
                        else 
                            MaterialTheme.colorScheme.outlineVariant
                    )
            )
        }
    }
}

@Composable
private fun MiniNumPad(
    onDigit: (String) -> Unit,
    onDelete: () -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        listOf(
            listOf("1", "2", "3"),
            listOf("4", "5", "6"),
            listOf("7", "8", "9"),
            listOf("", "0", "⌫")
        ).forEach { row ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                row.forEach { digit ->
                    if (digit.isEmpty()) {
                        Box(modifier = Modifier.size(56.dp))
                    } else if (digit == "⌫") {
                        IconButton(
                            onClick = onDelete,
                            modifier = Modifier.size(56.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Backspace,
                                contentDescription = "Delete"
                            )
                        }
                    } else {
                        Box(
                            modifier = Modifier
                                .size(56.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.surfaceVariant)
                                .clickable { onDigit(digit) },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = digit,
                                style = MaterialTheme.typography.titleLarge
                            )
                        }
                    }
                }
            }
        }
    }
}
