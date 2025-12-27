# Product Requirements Document (PRD)
## OpenWhatsAppChat - WhatsApp Tools Suite

**Version:** 2.0  
**Last Updated:** December 2024  
**Author:** Development Team

---

## 1. Executive Summary

OpenWhatsAppChat is a utility app that provides tools to enhance WhatsApp and WhatsApp Business user experience. The app allows users to send messages to unsaved numbers, manage quick contacts, and provides various WhatsApp-related utilities.

---

## 2. Current Features (v1.x)

| Feature | Description | Status |
|---------|-------------|--------|
| Direct Chat | Send WhatsApp messages without saving contacts | ✅ Active |
| Call Log Picker | Select numbers from call history | ✅ Active |
| SMS Picker | Select numbers from SMS messages | ✅ Active |
| Phone Validation | Validate phone numbers with country codes | ✅ Active |
| Onboarding | First-time user tutorial | ✅ Active |

---

## 3. Proposed Features for v2.0

### 3.1 Core Features (Priority: High)

#### 3.1.1 Direct Chat Enhancement
- **WhatsApp/WhatsApp Business Toggle** - Allow users to choose which app to open
- **Quick Message Templates** - Pre-saved message templates for quick sending
- **Recent Numbers History** - Store recently contacted numbers locally
- **Favorite Contacts** - Mark frequently used numbers as favorites

#### 3.1.2 Status Saver
- **View & Save Status** - Download WhatsApp/WA Business status (photos & videos)
- **Auto-detect new statuses** - Notify when new statuses are available
- **Organized gallery** - Separate folders for photos and videos
- **Share directly** - Re-share saved statuses

#### 3.1.3 QR Code Generator
- **Personal WhatsApp QR** - Generate QR code for your WhatsApp number
- **Custom message QR** - QR with pre-filled message
- **Share/Save QR** - Export as image or share directly
- **QR Scanner** - Scan WhatsApp QR codes to start chat

### 3.2 Productivity Features (Priority: Medium)

#### 3.2.1 Bulk Messaging
- **Contact Groups** - Create groups of numbers for bulk operations
- **Message Scheduler** - Schedule messages (opens WhatsApp at scheduled time)
- **Import from CSV/Excel** - Import contact lists

#### 3.2.2 Chat Tools
- **Text Formatting Helper** - Bold, italic, strikethrough, monospace preview
- **Emoji Combos** - Popular emoji combinations
- **Blank Message Sender** - Send invisible/blank messages
- **Text Repeater** - Repeat text multiple times

#### 3.2.3 WhatsApp Web Tools
- **WA Web QR Scanner** - Quick access to WhatsApp Web
- **Multi-device status** - Check linked devices info

### 3.3 Business Features (Priority: Medium)

#### 3.3.1 WhatsApp Business Integration
- **Business Profile Viewer** - View business details before chatting
- **Catalog Link Generator** - Generate shareable catalog links
- **Business Hours Display** - Show if business is currently available

#### 3.3.2 Analytics (Local)
- **Chat Statistics** - Track messages sent via app
- **Most contacted numbers** - Usage analytics
- **Export reports** - CSV export of usage data

### 3.4 Utility Features (Priority: Low)

#### 3.4.1 Number Utilities
- **Number Formatter** - Format numbers to international format
- **Duplicate Checker** - Find duplicate contacts
- **Invalid Number Detector** - Identify numbers not on WhatsApp

#### 3.4.2 Media Tools
- **Image to Sticker** - Convert images to WhatsApp stickers
- **Video Splitter** - Split long videos for status (30-sec segments)
- **Audio to Status** - Convert audio to video for status

#### 3.4.3 Privacy Features
- **Incognito Mode** - Don't save numbers to recent history
- **App Lock** - PIN/Biometric lock for the app
- **Clear History** - One-tap clear all local data

---

## 4. Technical Requirements

### 4.1 Platform Support
- **Minimum SDK:** 24 (Android 7.0)
- **Target SDK:** 35 (Android 15)
- **Compile SDK:** 35

### 4.2 Architecture
- **UI Framework:** Jetpack Compose with Material 3
- **Architecture Pattern:** MVVM with Clean Architecture
- **Dependency Injection:** Hilt
- **Navigation:** Compose Navigation
- **Local Storage:** Room Database + DataStore
- **Async Operations:** Kotlin Coroutines + Flow

### 4.3 Build System
- **Gradle:** 8.5+
- **Android Gradle Plugin:** 8.2+
- **Kotlin:** 2.0+
- **Build Files:** Kotlin DSL (.kts)

### 4.4 Key Dependencies
```
- Jetpack Compose BOM (latest)
- Material 3
- Hilt for DI
- Room for database
- DataStore for preferences
- Coil for image loading
- libphonenumber for validation
- CameraX for QR scanning
- ZXing for QR generation
- Firebase Crashlytics (replacing Fabric)
- Google AdMob (updated SDK)
```

---

## 5. User Interface Design

### 5.1 Design System
- **Theme:** Material 3 Dynamic Colors
- **Dark Mode:** Full support with system toggle
- **Typography:** Material 3 type scale
- **Icons:** Material Symbols (outlined)

### 5.2 Navigation Structure
```
Home (Bottom Navigation)
├── Direct Chat (Default)
│   ├── Phone Input
│   ├── Message Input
│   ├── Quick Templates
│   └── Recent Numbers
├── Tools
│   ├── Status Saver
│   ├── QR Generator
│   ├── Text Formatter
│   ├── Bulk Message
│   └── More Tools...
├── Contacts
│   ├── Favorites
│   ├── Groups
│   └── Import/Export
└── Settings
    ├── App Preferences
    ├── Privacy
    ├── About
    └── Premium (Future)
```

### 5.3 Key Screens
1. **Home/Direct Chat** - Main functionality with clean input
2. **Tools Dashboard** - Grid of available tools
3. **Status Saver** - Gallery-style status viewer
4. **QR Generator** - QR code display with sharing
5. **Settings** - Preferences and app info

---

## 6. Monetization Strategy

### 6.1 Free Tier
- Direct chat (unlimited)
- Call log/SMS picker
- Basic text formatting
- Limited status saves (5/day)
- Banner ads

### 6.2 Premium Tier (Future)
- Unlimited status saves
- Bulk messaging
- Message scheduler
- No ads
- Priority support
- Custom themes

### 6.3 Ad Placements
- Banner ad on home screen (bottom)
- Interstitial after status save
- Rewarded ad for extra features

---

## 7. Privacy & Compliance

### 7.1 Permissions Required
| Permission | Purpose | Required |
|------------|---------|----------|
| INTERNET | Ads, analytics | Yes |
| READ_EXTERNAL_STORAGE | Status saver | Optional |
| CAMERA | QR scanner | Optional |
| READ_CALL_LOG | Call log picker | Optional |
| READ_SMS | SMS picker | Optional |

### 7.2 Data Handling
- All data stored locally on device
- No personal data sent to servers
- Clear data option available
- GDPR/Privacy policy compliance

### 7.3 WhatsApp Compliance
- No automation of WhatsApp actions
- Uses official WhatsApp API intents
- No modification of WhatsApp app
- Respects WhatsApp ToS

---

## 8. Development Phases

### Phase 1: Foundation (Week 1-2)
- [ ] Migrate to Kotlin DSL
- [ ] Update to latest SDK (35)
- [ ] Setup Jetpack Compose
- [ ] Implement new architecture (MVVM)
- [ ] Migrate Direct Chat to Compose
- [ ] Update AdMob SDK

### Phase 2: Core Features (Week 3-4)
- [ ] Recent numbers history
- [ ] Quick message templates
- [ ] Favorites system
- [ ] WhatsApp/WA Business toggle
- [ ] New onboarding flow

### Phase 3: Tools (Week 5-6)
- [ ] Status Saver
- [ ] QR Code Generator/Scanner
- [ ] Text formatting tools
- [ ] Blank message sender

### Phase 4: Advanced (Week 7-8)
- [ ] Bulk messaging UI
- [ ] Contact groups
- [ ] Local analytics
- [ ] Settings & preferences

### Phase 5: Polish (Week 9-10)
- [ ] UI/UX refinement
- [ ] Performance optimization
- [ ] Testing & bug fixes
- [ ] Play Store preparation

---

## 9. Success Metrics

### 9.1 Key Performance Indicators
- **DAU/MAU Ratio:** Target 30%+
- **Session Duration:** Target 2+ minutes
- **Feature Adoption:** 50%+ users try tools
- **Crash-free Rate:** 99.5%+
- **App Rating:** 4.5+ stars

### 9.2 Analytics Events to Track
- Direct chat initiated
- Tool feature used
- Status saved
- QR generated
- Template used
- Ad impressions/clicks

---

## 10. Risks & Mitigations

| Risk | Impact | Mitigation |
|------|--------|------------|
| WhatsApp API changes | High | Use official intents only |
| Play Store policy | High | Follow all guidelines |
| Storage permission changes | Medium | Use MediaStore API |
| Competition | Medium | Focus on UX & unique features |

---

## 11. Future Considerations

- **iOS Version** - Kotlin Multiplatform
- **Wear OS** - Quick chat from watch
- **Widget** - Home screen quick chat widget
- **Voice Input** - Speech-to-text for messages
- **AI Features** - Smart reply suggestions

---

## Appendix A: Competitor Analysis

| App | Direct Chat | Status Saver | QR | Bulk | Rating |
|-----|-------------|--------------|-----|------|--------|
| Click to Chat | ✅ | ❌ | ❌ | ❌ | 4.2 |
| Direct Message | ✅ | ✅ | ❌ | ❌ | 4.0 |
| Status Saver | ❌ | ✅ | ❌ | ❌ | 4.3 |
| **Our App** | ✅ | ✅ | ✅ | ✅ | Target: 4.5 |

---

## Appendix B: User Stories

### Direct Chat
- As a user, I want to send a WhatsApp message without saving the contact
- As a user, I want to choose between WhatsApp and WhatsApp Business
- As a user, I want to use my recent numbers quickly

### Status Saver
- As a user, I want to save my friends' WhatsApp statuses
- As a user, I want to organize saved statuses by date/contact

### QR Code
- As a business user, I want to generate a QR code for customers to chat with me
- As a user, I want to scan a QR code to start a chat

---

*Document End*
