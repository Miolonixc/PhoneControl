# Phone Control

Native Android app for phone management and optimization.

## Features

- **Dashboard** — Device info, quick actions
- **System Info** — Device properties, hardware details
- **Apps Manager** — List, uninstall, force stop apps
- **Tools** — Screenshot, display control, input simulation
- **Network** — WiFi, Bluetooth, connections
- **Optimizer** — Clean cache, kill background processes
- **Terminal** — Run shell commands directly

## Requirements

- Android 8.0+ (API 26)
- Termux installed on device
- Termux:API app (for battery, notifications)

## Build

### Option 1: GitHub Actions (Recommended)
1. Push this repo to GitHub
2. Go to Actions tab → "Build APK" workflow
3. Download APK from Artifacts section

### Option 2: Android Studio
1. Open the project in Android Studio
2. Sync Gradle
3. Build > Build APK

### Option 3: Command line (x86_64 only)
```bash
cd PhoneControl
./gradlew assembleDebug
```

**Note:** Gradle build does NOT work on ARM64 (Termux on phone) because Android SDK tools are x86_64-only. Use GitHub Actions or a PC.

APK will be at: `app/build/outputs/apk/debug/app-debug.apk`

## Permissions

- Network state — for network info
- Storage — for screenshots
- No special permissions required for basic features

## Architecture

- Kotlin + Jetpack Compose
- Single activity with Navigation Compose
- Shell command execution via Runtime.exec()
- Material Design 3 with dynamic colors

## License

MIT
