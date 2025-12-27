package com.whatsappdirect.direct_chat.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Contacts
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.whatsappdirect.direct_chat.navigation.Screen
import com.whatsappdirect.direct_chat.ui.screens.contacts.ContactsScreen
import com.whatsappdirect.direct_chat.ui.screens.directchat.DirectChatScreen
import com.whatsappdirect.direct_chat.ui.screens.onboarding.OnboardingScreen
import com.whatsappdirect.direct_chat.ui.screens.settings.SettingsScreen
import com.whatsappdirect.direct_chat.ui.screens.splash.SplashScreen
import com.whatsappdirect.direct_chat.ui.screens.tools.ToolsScreen
import com.whatsappdirect.direct_chat.ui.screens.tools.textformatter.TextFormatterScreen
import com.whatsappdirect.direct_chat.ui.screens.tools.qrgenerator.QrGeneratorScreen
import com.whatsappdirect.direct_chat.ui.screens.tools.statussaver.StatusSaverScreen
import com.whatsappdirect.direct_chat.ui.screens.settings.AppLockSettingsScreen
import com.whatsappdirect.direct_chat.ui.screens.tools.textrepeater.TextRepeaterScreen
import com.whatsappdirect.direct_chat.ui.screens.tools.qrscanner.QrScannerScreen
import com.whatsappdirect.direct_chat.ui.screens.tools.bulkmessage.BulkMessageScreen
import com.whatsappdirect.direct_chat.ui.screens.tools.sticker.ImageToStickerScreen
import com.whatsappdirect.direct_chat.ui.screens.groups.ContactGroupsScreen
import com.whatsappdirect.direct_chat.ui.screens.tools.scheduler.MessageSchedulerScreen
import com.whatsappdirect.direct_chat.ui.theme.WhatsAppDirectTheme
import dagger.hilt.android.AndroidEntryPoint

data class BottomNavItem(
    val route: String,
    val title: String,
    val icon: ImageVector
)

val bottomNavItems = listOf(
    BottomNavItem(Screen.DirectChat.route, "Chat", Icons.AutoMirrored.Filled.Chat),
    BottomNavItem(Screen.Tools.route, "Tools", Icons.Default.Build),
    BottomNavItem(Screen.Contacts.route, "Contacts", Icons.Default.Contacts),
    BottomNavItem(Screen.Settings.route, "Settings", Icons.Default.Settings)
)

@AndroidEntryPoint
class MainActivityCompose : ComponentActivity() {
    
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        
        setContent {
            WhatsAppDirectTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MainApp()
                }
            }
        }
    }
}

@Composable
fun MainApp() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    
    val showBottomBar = currentRoute in bottomNavItems.map { it.route }
    
    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                NavigationBar {
                    bottomNavItems.forEach { item ->
                        NavigationBarItem(
                            icon = { Icon(item.icon, contentDescription = item.title) },
                            label = { Text(item.title) },
                            selected = currentRoute == item.route,
                            onClick = {
                                navController.navigate(item.route) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        )
                    }
                }
            }
        }
    ) { paddingValues ->
        NavHost(
            navController = navController,
            startDestination = Screen.Splash.route,
            modifier = Modifier.padding(paddingValues)
        ) {
            composable(Screen.Splash.route) {
                SplashScreen(
                    onNavigateToOnboarding = {
                        navController.navigate(Screen.Onboarding.route) {
                            popUpTo(Screen.Splash.route) { inclusive = true }
                        }
                    },
                    onNavigateToHome = {
                        navController.navigate(Screen.DirectChat.route) {
                            popUpTo(Screen.Splash.route) { inclusive = true }
                        }
                    }
                )
            }
            
            composable(Screen.Onboarding.route) {
                OnboardingScreen(
                    onFinished = {
                        navController.navigate(Screen.DirectChat.route) {
                            popUpTo(Screen.Onboarding.route) { inclusive = true }
                        }
                    }
                )
            }
            
            composable(Screen.DirectChat.route) {
                DirectChatScreen(
                    onNavigateToCallLog = {
                        // TODO: Navigate to call log picker
                    }
                )
            }
            
            composable(Screen.Tools.route) {
                ToolsScreen(
                    onNavigateToStatusSaver = {
                        navController.navigate(Screen.StatusSaver.route)
                    },
                    onNavigateToQrGenerator = {
                        navController.navigate(Screen.QrGenerator.route)
                    },
                    onNavigateToTextFormatter = {
                        navController.navigate(Screen.TextFormatter.route)
                    },
                    onNavigateToTextRepeater = {
                        navController.navigate(Screen.TextRepeater.route)
                    },
                    onNavigateToQrScanner = {
                        navController.navigate(Screen.QrScanner.route)
                    },
                    onNavigateToBulkMessage = {
                        navController.navigate(Screen.BulkMessage.route)
                    },
                    onNavigateToImageToSticker = {
                        navController.navigate(Screen.ImageToSticker.route)
                    },
                    onNavigateToScheduler = {
                        navController.navigate(Screen.MessageScheduler.route)
                    }
                )
            }
            
            composable(Screen.TextFormatter.route) {
                TextFormatterScreen(
                    onNavigateBack = { navController.popBackStack() }
                )
            }
            
            composable(Screen.QrGenerator.route) {
                QrGeneratorScreen(
                    onNavigateBack = { navController.popBackStack() }
                )
            }
            
            composable(Screen.StatusSaver.route) {
                StatusSaverScreen(
                    onNavigateBack = { navController.popBackStack() }
                )
            }
            
            composable(Screen.TextRepeater.route) {
                TextRepeaterScreen(
                    onNavigateBack = { navController.popBackStack() }
                )
            }
            
            composable(Screen.QrScanner.route) {
                QrScannerScreen(
                    onNavigateBack = { navController.popBackStack() }
                )
            }
            
            composable(Screen.BulkMessage.route) {
                BulkMessageScreen(
                    onNavigateBack = { navController.popBackStack() }
                )
            }
            
            composable(Screen.ImageToSticker.route) {
                ImageToStickerScreen(
                    onNavigateBack = { navController.popBackStack() }
                )
            }
            
            composable(Screen.ContactGroups.route) {
                ContactGroupsScreen(
                    onNavigateBack = { navController.popBackStack() }
                )
            }
            
            composable(Screen.MessageScheduler.route) {
                MessageSchedulerScreen(
                    onNavigateBack = { navController.popBackStack() }
                )
            }
            
            composable(Screen.Contacts.route) {
                ContactsScreen(
                    onNavigateToGroups = {
                        navController.navigate(Screen.ContactGroups.route)
                    }
                )
            }
            
            composable(Screen.Settings.route) {
                SettingsScreen(
                    onNavigateToAppLock = {
                        navController.navigate(Screen.AppLockSettings.route)
                    }
                )
            }
            
            composable(Screen.AppLockSettings.route) {
                AppLockSettingsScreen(
                    onNavigateBack = { navController.popBackStack() }
                )
            }
        }
    }
}
