# OpenWhatsAppChat - WhatsApp Direct Tools

A modern Android app that provides useful WhatsApp tools including direct chat without saving contacts, status saver, QR code generator, and more.

## Features

### Current Features (v2.0)
- **Direct Chat** - Send WhatsApp messages to any number without saving to contacts
- **WhatsApp/Business Toggle** - Choose between WhatsApp and WhatsApp Business
- **Country Code Picker** - Easy country code selection with flags
- **Phone Validation** - Real-time phone number validation
- **Recent Numbers** - Quick access to recently used numbers
- **Favorites** - Save frequently used numbers
- **Modern UI** - Material 3 design with dark mode support

### Planned Features
- Status Saver
- QR Code Generator
- Text Formatter
- Bulk Messaging
- Message Templates

## Tech Stack

- **Language:** Kotlin 2.0
- **UI:** Jetpack Compose with Material 3
- **Architecture:** MVVM + Clean Architecture
- **DI:** Hilt
- **Database:** Room
- **Preferences:** DataStore
- **Async:** Coroutines + Flow
- **Navigation:** Compose Navigation

## Requirements

- Android Studio Ladybug (2024.2.1) or later
- JDK 17
- Android SDK 35
- Min SDK 24 (Android 7.0)

## Setup

1. Clone the repository
```bash
git clone https://github.com/yourusername/OpenWhatsAppChat.git
```

2. Open in Android Studio

3. Sync Gradle files

4. Run the app

## Build

### Debug Build
```bash
./gradlew assembleDebug
```

### Release Build
Set environment variables for signing:
```bash
export KEYSTORE_PASSWORD="your_password"
export KEY_ALIAS="your_alias"
export KEY_PASSWORD="your_key_password"
```

Then build:
```bash
./gradlew assembleRelease
```

## Project Structure

```
app/
├── src/main/
│   ├── java/com/whatsappdirect/direct_chat/
│   │   ├── WhatsAppDirectApp.kt
│   │   ├── di/                 # Dependency Injection
│   │   ├── data/               # Data layer
│   │   │   ├── local/          # Room DB, DataStore
│   │   │   └── model/          # Data models
│   │   ├── navigation/         # Navigation
│   │   └── ui/                 # UI layer
│   │       ├── theme/          # Compose theme
│   │       ├── components/     # Reusable components
│   │       └── screens/        # App screens
│   └── res/                    # Resources
├── build.gradle.kts
└── proguard-rules.pro
```

## Documentation

- [PRD Document](docs/PRD.md) - Product requirements and roadmap
- [Migration Guide](docs/MIGRATION_GUIDE.md) - v1.x to v2.0 migration

## Contributing

1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Submit a pull request

## License

This project is licensed under the MIT License.

## Acknowledgments

- [libphonenumber](https://github.com/google/libphonenumber) for phone validation
- [Jetpack Compose](https://developer.android.com/jetpack/compose) for modern UI
- [Material 3](https://m3.material.io/) for design system
