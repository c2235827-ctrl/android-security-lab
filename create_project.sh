#!/usr/bin/env bash
set -Eeuo pipefail

echo "=========================================="
echo " ANDROID SECURITY LAB - CLEAN PROJECT SETUP"
echo "=========================================="

if [[ ! -d ".git" ]]; then
    echo "ERROR: This is not the project repository root."
    echo "Open /workspaces/android-security-lab and run again."
    exit 1
fi

echo "[1/8] Creating project directories..."

mkdir -p \
  app/src/main/java/com/securitylab/demo \
  .github/workflows

echo "[2/8] Creating Gradle configuration..."

cat > settings.gradle <<'EOF'
pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = 'AndroidSecurityLab'
include ':app'
EOF

cat > build.gradle <<'EOF'
plugins {
    id 'com.android.application' version '8.7.3' apply false
}
EOF

cat > gradle.properties <<'EOF'
org.gradle.jvmargs=-Xmx2048m -Dfile.encoding=UTF-8
org.gradle.parallel=true
android.useAndroidX=true
EOF

cat > app/build.gradle <<'EOF'
plugins {
    id 'com.android.application'
}

android {
    namespace 'com.securitylab.demo'
    compileSdk 35

    defaultConfig {
        applicationId 'com.securitylab.demo'
        minSdk 29
        targetSdk 35
        versionCode 1
        versionName '1.0'
    }

    compileOptions {
        sourceCompatibility JavaVersion.VERSION_17
        targetCompatibility JavaVersion.VERSION_17
    }
}
EOF

echo "[3/8] Creating Android manifest..."

cat > app/src/main/AndroidManifest.xml <<'EOF'
<?xml version="1.0" encoding="utf-8"?>
<manifest xmlns:android="http://schemas.android.com/apk/res/android">

    <application
        android:allowBackup="false"
        android:label="Security Lab Demo"
        android:theme="@android:style/Theme.Material.Light.NoActionBar"
        android:usesCleartextTraffic="false">

        <activity
            android:name=".MainActivity"
            android:exported="true">

            <intent-filter>
                <action android:name="android.intent.action.MAIN" />
                <category android:name="android.intent.category.LAUNCHER" />
            </intent-filter>

        </activity>
    </application>
</manifest>
EOF

echo "[4/8] Creating application..."

cat > app/src/main/java/com/securitylab/demo/MainActivity.java <<'EOF'
package com.securitylab.demo;

import android.app.Activity;
import android.os.Bundle;
import android.graphics.Color;
import android.graphics.Typeface;
import android.text.InputType;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.content.SharedPreferences;

public class MainActivity extends Activity {

    private SharedPreferences prefs;
    private LinearLayout root;
    private EditText messageInput;
    private EditText pinInput;
    private EditText newPinInput;
    private TextView statusText;
    private TextView bannerText;

    private static final String DEFAULT_PIN = "1234";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        prefs = getSharedPreferences("SecurityLabDemo", MODE_PRIVATE);
        buildScreen();
        refreshScreen();
    }

    private int dp(int value) {
        return (int) (value * getResources().getDisplayMetrics().density);
    }

    private TextView text(String value, int size, boolean bold) {
        TextView view = new TextView(this);
        view.setText(value);
        view.setTextSize(size);

        if (bold) {
            view.setTypeface(null, Typeface.BOLD);
        }

        view.setTextColor(Color.rgb(35, 35, 35));
        view.setPadding(0, dp(7), 0, dp(7));
        return view;
    }

    private EditText field(String hint, boolean numeric) {
        EditText edit = new EditText(this);
        edit.setHint(hint);
        edit.setSingleLine(true);
        edit.setPadding(dp(12), dp(8), dp(12), dp(8));

        if (numeric) {
            edit.setInputType(InputType.TYPE_CLASS_NUMBER |
                    InputType.TYPE_NUMBER_VARIATION_PASSWORD);
        } else {
            edit.setInputType(InputType.TYPE_CLASS_TEXT |
                    InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);
        }

        return edit;
    }

    private Button button(String label) {
        Button b = new Button(this);
        b.setText(label);
        b.setAllCaps(false);
        return b;
    }

    private void addSpace() {
        View space = new View(this);
        root.addView(space, new LinearLayout.LayoutParams(
                1, dp(10)));
    }

    private void buildScreen() {
        ScrollView scroll = new ScrollView(this);

        root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(dp(22), dp(24), dp(22), dp(24));
        root.setBackgroundColor(Color.rgb(245, 247, 250));

        scroll.addView(root);
        setContentView(scroll);

        TextView heading = text("ANDROID SECURITY LAB", 22, true);
        heading.setGravity(Gravity.CENTER);
        root.addView(heading);

        TextView subtitle = text(
                "Reversible ransomware simulation",
                15, false);
        subtitle.setGravity(Gravity.CENTER);
        root.addView(subtitle);

        addSpace();

        statusText = text("", 16, true);
        root.addView(statusText);

        bannerText = text("", 18, true);
        bannerText.setTextColor(Color.WHITE);
        bannerText.setBackgroundColor(Color.rgb(145, 35, 35));
        bannerText.setPadding(dp(15), dp(18), dp(15), dp(18));
        root.addView(bannerText);

        addSpace();

        root.addView(text("DEMO MESSAGE", 14, true));

        messageInput = field("Enter your demonstration message", false);
        root.addView(messageInput);

        Button activate = button("Activate demonstration");
        root.addView(activate);

        activate.setOnClickListener(v -> {
            String message = messageInput.getText().toString().trim();

            if (message.isEmpty()) {
                message = "EDUCATIONAL SIMULATION: Your device is not locked.";
            }

            prefs.edit()
                    .putString("message", message)
                    .putBoolean("active", true)
                    .apply();

            pinInput.setText("");
            refreshScreen();
        });

        addSpace();
        root.addView(text("DEMO PIN", 14, true));

        pinInput = field("Enter current PIN", true);
        root.addView(pinInput);

        Button unlock = button("Unlock / dismiss with PIN");
        root.addView(unlock);

        unlock.setOnClickListener(v -> {
            String entered = pinInput.getText().toString();
            String saved = prefs.getString("pin", DEFAULT_PIN);

            if (entered.equals(saved)) {
                prefs.edit().putBoolean("active", false).apply();
                pinInput.setText("");
                refreshScreen();
            } else {
                statusText.setText("Incorrect PIN. Try again.");
            }
        });

        addSpace();
        root.addView(text("CHANGE DEMO PIN", 14, true));

        newPinInput = field("New PIN (4 to 12 digits)", true);
        root.addView(newPinInput);

        Button changePin = button("Change PIN");
        root.addView(changePin);

        changePin.setOnClickListener(v -> {
            String current = pinInput.getText().toString();
            String saved = prefs.getString("pin", DEFAULT_PIN);
            String replacement = newPinInput.getText().toString();

            if (!current.equals(saved)) {
                statusText.setText("Enter your current PIN first.");
                return;
            }

            if (!replacement.matches("[0-9]{4,12}")) {
                statusText.setText("New PIN must contain 4 to 12 digits.");
                return;
            }

            prefs.edit().putString("pin", replacement).apply();
            pinInput.setText("");
            newPinInput.setText("");
            refreshScreen();
        });

        addSpace();

        Button emergency = button("EMERGENCY EXIT - STOP DEMO");
        emergency.setTextColor(Color.rgb(170, 25, 25));
        root.addView(emergency);

        emergency.setOnClickListener(v -> {
            prefs.edit().putBoolean("active", false).apply();
            pinInput.setText("");
            refreshScreen();
        });

        addSpace();

        TextView note = text(
                "SAFETY NOTICE\n" +
                "This application is an educational simulation only. " +
                "It does not encrypt files, restrict other apps, " +
                "or interfere with Android system controls. " +
                "The emergency exit is always available.",
                13, false);

        root.addView(note);
    }

    private void refreshScreen() {
        boolean active = prefs.getBoolean("active", false);
        String message = prefs.getString(
                "message",
                "EDUCATIONAL SIMULATION: Your device is not locked."
        );

        statusText.setText(active
                ? "DEMONSTRATION STATUS: ACTIVE"
                : "DEMONSTRATION STATUS: INACTIVE");

        bannerText.setVisibility(active ? View.VISIBLE : View.GONE);
        bannerText.setText(message);

        messageInput.setText(message);
    }
}
EOF

echo "[5/8] Creating GitHub Actions workflow..."

cat > .github/workflows/build.yml <<'EOF'
name: Build Android Security Lab APK

on:
  push:
    branches: [ main ]
  pull_request:
    branches: [ main ]
  workflow_dispatch:

permissions:
  contents: read

jobs:
  build:
    runs-on: ubuntu-latest

    steps:
      - name: Checkout repository
        uses: actions/checkout@v4

      - name: Set up Java 17
        uses: actions/setup-java@v4
        with:
          distribution: temurin
          java-version: '17'

      - name: Set up Android SDK
        uses: android-actions/setup-android@v3
        with:
          packages: 'platform-tools platforms;android-35 build-tools;35.0.0'

      - name: Build debug APK
        uses: gradle/gradle-build-action@v3
        with:
          gradle-version: '8.9'
          arguments: assembleDebug

      - name: Verify APK exists
        run: test -s app/build/outputs/apk/debug/app-debug.apk

      - name: Upload APK
        uses: actions/upload-artifact@v4
        with:
          name: security-lab-demo-apk
          path: app/build/outputs/apk/debug/app-debug.apk
          if-no-files-found: error
          retention-days: 7
EOF

echo "[6/8] Creating README and ignore rules..."

cat > README.md <<'EOF'
# Android Security Lab

An educational, reversible Android security simulation.

## Compatibility

- Minimum Android version: Android 10 (API 29)
- Compile SDK: 35
- Target SDK: 35
- Java: 17
- Gradle: 8.9

## Demonstration features

- Customizable in-app warning message
- Default demonstration PIN: 1234
- Changeable PIN
- Demo state saved locally across app restarts and device restarts
- PIN-controlled dismissal
- Always-available emergency exit

The application does not encrypt files, block other applications,
launch at boot, or interfere with Android system controls.

## Build

```bash
gradle assembleDebug
```

APK output:

`app/build/outputs/apk/debug/app-debug.apk`

GitHub Actions also builds the APK and publishes it as an artifact.
EOF

cat > .gitignore <<'EOF'
.gradle/
build/
app/build/
local.properties
*.iml
.idea/
.DS_Store
EOF

echo "[7/8] Checking required files..."

required_files=(
  settings.gradle
  build.gradle
  gradle.properties
  app/build.gradle
  app/src/main/AndroidManifest.xml
  app/src/main/java/com/securitylab/demo/MainActivity.java
  .github/workflows/build.yml
)

for file in "${required_files[@]}"; do
    if [[ ! -s "$file" ]]; then
        echo "ERROR: Missing or empty file: $file"
        exit 1
    fi
    echo "OK: $file"
done

echo "[8/8] Checking Java and Gradle availability..."

if command -v java >/dev/null 2>&1; then
    java -version
else
    echo "NOTICE: Java is not installed in this Codespace terminal."
    echo "GitHub Actions will use Java 17 to build the APK."
fi

if command -v gradle >/dev/null 2>&1; then
    gradle --version
    echo ""
    echo "Starting local Android build..."
    gradle --no-daemon assembleDebug
    echo ""
    if [[ -s app/build/outputs/apk/debug/app-debug.apk ]]; then
        echo "SUCCESS: Local APK created."
        ls -lh app/build/outputs/apk/debug/app-debug.apk
    else
        echo "ERROR: Gradle finished but APK was not found."
        exit 1
    fi
else
    echo "Gradle is not installed locally."
    echo "The GitHub Actions workflow will build the APK."
fi

echo ""
echo "=========================================="
echo " PROJECT SETUP FINISHED"
echo "=========================================="
echo "Next: commit and push these changes to GitHub."
