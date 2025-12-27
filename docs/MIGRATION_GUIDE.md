# Migration Guide: OpenWhatsAppChat v1.x to v2.0

## Overview

This document outlines the major changes made during the upgrade from the legacy Android project to the modern architecture.

---

## Build System Changes

### Before (Groovy DSL)
- Gradle 6.1.1
- Android Gradle Plugin 4.0.1
- Kotlin 1.3.72
- compileSdkVersion 29

### After (Kotlin DSL)
- Gradle 8.9
- Android Gradle Plugin 8.7.3
- Kotlin 2.0.21
- compileSdk 35

### Key Files Changed
| Old File | New File |
|----------|----------|
| `build.gradle` | `build.gradle.kts` |
| `settings.gradle` | `settings.gradle.kts` |
| `app/build.gradle` | `app/build.gradle.kts` |
| - | `gradle/libs.versions.toml` (Version Catalog) |

---

## Architecture Changes

### Before
- Activity-based UI with XML layouts
- No dependency injection
- SharedPreferences for storage
- Manual view binding

### After
- **Jetpack Compose** for UI
- **Hilt** for dependency injection
- **Room** for database
- **DataStore** for preferences
- **MVVM** architecture pattern
- **Kotlin Coroutines & Flow** for async operations

---

## Project Structure

```
app/src/main/java/com/whatsappdirect/direct_chat/
├── WhatsAppDirectApp.kt          # Application class with Hilt
├── di/
│   └── AppModule.kt              # Hilt dependency injection module
├── data/
│   ├── local/
│   │   ├── AppDatabase.kt        # Room database
│   │   ├── PreferencesManager.kt # DataStore preferences
│   │   ├── RecentNumberDao.kt    # DAO for recent numbers
│   │   └── MessageTemplateDao.kt # DAO for message templates
│   └── model/
│       └── RecentNumber.kt       # Data models
├── navigation/
│   ├── Screen.kt                 # Navigation routes
│   └── NavGraph.kt               # Navigation graph
├── ui/
│   ├── MainActivityCompose.kt    # Main activity with Compose
│   ├── theme/
│   │   ├── Color.kt              # Color definitions
│   │   ├── Theme.kt              # Material 3 theme
│   │   └── Type.kt               # Typography
│   ├── components/
│   │   └── CountryCodeSelector.kt
│   └── screens/
│       ├── splash/
│       ├── onboarding/
│       ├── directchat/
│       ├── tools/
│       ├── contacts/
│       └── settings/
```

---

## Dependency Changes

### Removed
- `io.fabric.tools:gradle` (deprecated)
- `com.crashlytics.sdk.android:crashlytics` (replaced by Firebase)
- `com.hbb20:ccp` (replaced with custom Compose component)
- `com.github.traex.rippleeffect:library` (native in Compose)

### Added
- Jetpack Compose BOM 2024.12.01
- Material 3
- Hilt 2.53.1
- Room 2.6.1
- DataStore 1.1.1
- Navigation Compose 2.8.5
- Coil 2.7.0 (image loading)
- Accompanist 0.36.0 (permissions)
- ZXing 3.5.3 (QR codes)
- CameraX 1.4.1

---

## Migration Steps

### 1. Sync Project
After pulling the changes, sync the project with Gradle:
```bash
./gradlew clean build
```

### 2. Update Android Studio
Ensure you have Android Studio Ladybug (2024.2.1) or later for full Kotlin 2.0 and Compose support.

### 3. JDK Requirements
The project now requires JDK 17. Update your `JAVA_HOME` if needed.

### 4. Signing Configuration
Update signing config in environment variables:
```bash
export KEYSTORE_PASSWORD="your_password"
export KEY_ALIAS="your_alias"
export KEY_PASSWORD="your_key_password"
```

Or update `app/build.gradle.kts` directly (not recommended for version control).

---

## Breaking Changes

### 1. Package Structure
Old activities in `Activity/` package are deprecated. New screens are in `ui/screens/`.

### 2. SharedPreferences → DataStore
```kotlin
// Old
val prefs = getSharedPreferences("prefs", MODE_PRIVATE)
val isFirst = prefs.getBoolean("first_launch", true)

// New
@Inject lateinit var preferencesManager: PreferencesManager
preferencesManager.isFirstLaunch.collect { isFirst -> ... }
```

### 3. XML Layouts → Compose
All UI is now declarative Compose. XML layouts in `res/layout/` are no longer used by the new screens.

### 4. AdMob SDK
Updated to latest SDK. InterstitialAd API has changed:
```kotlin
// Old
val ad = InterstitialAd(context)
ad.adUnitId = "..."
ad.loadAd(AdRequest.Builder().build())

// New - Use AdMob's new API with callbacks
```

---

## Testing

### Run Unit Tests
```bash
./gradlew test
```

### Run Instrumented Tests
```bash
./gradlew connectedAndroidTest
```

---

## Known Issues

1. **Old Activities**: Legacy activities still exist but are not used. They can be removed after verifying the new Compose screens work correctly.

2. **Firebase Setup**: Firebase Crashlytics needs to be configured. Add `google-services.json` and enable Firebase plugins if needed.

3. **ProGuard Rules**: Updated for new dependencies. Test release builds thoroughly.

---

## Rollback

If you need to rollback to v1.x:
1. Checkout the previous commit/tag
2. Or restore the old build files from git history

---

## Support

For issues with the migration, check:
1. Android Studio Build output
2. Logcat for runtime errors
3. Gradle sync issues in IDE

---

*Last Updated: December 2024*
