package com.whatsappdirect.direct_chat.navigation

sealed class Screen(val route: String) {
    data object Splash : Screen("splash")
    data object Onboarding : Screen("onboarding")
    data object Home : Screen("home")
    data object DirectChat : Screen("direct_chat")
    data object Tools : Screen("tools")
    data object Contacts : Screen("contacts")
    data object Settings : Screen("settings")
    data object CallLogPicker : Screen("call_log_picker")
    data object SmsPicker : Screen("sms_picker")
    data object StatusSaver : Screen("status_saver")
    data object QrGenerator : Screen("qr_generator")
    data object TextFormatter : Screen("text_formatter")
    data object BlankMessage : Screen("blank_message")
    data object TextRepeater : Screen("text_repeater")
    data object QrScanner : Screen("qr_scanner")
    data object AppLock : Screen("app_lock")
    data object AppLockSettings : Screen("app_lock_settings")
    data object BulkMessage : Screen("bulk_message")
    data object ImageToSticker : Screen("image_to_sticker")
    data object ContactGroups : Screen("contact_groups")
    data object MessageScheduler : Screen("message_scheduler")
}

sealed class BottomNavItem(
    val route: String,
    val title: String,
    val icon: String
) {
    data object DirectChat : BottomNavItem("direct_chat", "Chat", "chat")
    data object Tools : BottomNavItem("tools", "Tools", "build")
    data object Contacts : BottomNavItem("contacts", "Contacts", "contacts")
    data object Settings : BottomNavItem("settings", "Settings", "settings")
}
