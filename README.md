# Android Security Lab — Educational Simulation

A reversible Android security-awareness demonstration.

## Compatibility
- Minimum Android: 10 (API 29)
- Compile SDK: 35
- Target SDK: 35
- Java: 17
- Android Gradle Plugin: 8.7.3
- Gradle: 8.9

## Behaviour
- Opens directly into the red simulation screen.
- Default demonstration PIN: 1234.
- Active/dismissed state is saved locally.
- Timer is illustrative only.
- Correct PIN dismisses the demonstration.
- Normal Android navigation and exit controls remain available.

## Safety
This app does not encrypt, delete, upload or modify files.
It does not draw over other apps or prevent system navigation,
force-stop, uninstall or owner recovery.

## Build
GitHub Actions creates a debug APK artifact named
security-lab-demo-apk.
