package com.whatsappdirect.direct_chat.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.whatsappdirect.direct_chat.ui.screens.contacts.ContactsScreen
import com.whatsappdirect.direct_chat.ui.screens.directchat.DirectChatScreen
import com.whatsappdirect.direct_chat.ui.screens.onboarding.OnboardingScreen
import com.whatsappdirect.direct_chat.ui.screens.settings.SettingsScreen
import com.whatsappdirect.direct_chat.ui.screens.splash.SplashScreen
import com.whatsappdirect.direct_chat.ui.screens.tools.ToolsScreen

@Composable
fun NavGraph(
    navController: NavHostController,
    startDestination: String
) {
    NavHost(
        navController = navController,
        startDestination = startDestination
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
                    navController.navigate(Screen.CallLogPicker.route)
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
        
        composable(Screen.Contacts.route) {
            ContactsScreen()
        }
        
        composable(Screen.Settings.route) {
            SettingsScreen()
        }
    }
}
