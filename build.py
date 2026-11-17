
from pathlib import Path

files = {
"settings.gradle": """
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
rootProject.name = 'SecurityLab'
include ':app'
""",

"build.gradle": """
plugins {
    id 'com.android.application' version '8.7.3' apply false
}
""",

"app/build.gradle": """
plugins {
    id 'com.android.application'
}

android {
    namespace 'com.securitylab.demo'
    compileSdk 35

    defaultConfig {
        applicationId 'com.securitylab.demo'
        minSdk 23
        targetSdk 35
        versionCode 1
        versionName '1.0'
    }
}
""",

"app/src/main/AndroidManifest.xml": """
<manifest xmlns:android="http://schemas.android.com/apk/res/android">
    <application
        android:theme="@android:style/Theme.Material.NoActionBar"
        android:label="Security Lab Demo"
        android:allowBackup="false">

        <activity
            android:name=".MainActivity"
            android:exported="true">

            <intent-filter>
                <action android:name="android.intent.action.MAIN"/>
                <category android:name="android.intent.category.LAUNCHER"/>
            </intent-filter>

        </activity>
    </application>
</manifest>
""",

"app/src/main/java/com/securitylab/demo/MainActivity.java": """
package com.securitylab.demo;

import android.app.Activity;
import android.os.Bundle;
import android.graphics.Color;
import android.view.Gravity;
import android.widget.*;

public class MainActivity extends Activity {

    private static final String DEMO_PIN = "1234";

    @Override
    protected void onCreate(Bundle state) {
        super.onCreate(state);
        showDemo();
    }

    private void showDemo() {
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setGravity(Gravity.CENTER);
        layout.setPadding(32, 40, 32, 40);
        layout.setBackgroundColor(Color.rgb(28, 12, 18));

        TextView title = new TextView(this);
        title.setText("SECURITY LAB");
        title.setTextColor(Color.WHITE);
        title.setTextSize(30);
        title.setGravity(Gravity.CENTER);

        TextView warning = new TextView(this);
        warning.setText(
            "SIMULATED SECURITY INCIDENT\\n\\n" +
            "This is an educational lock-screen demonstration.\\n" +
            "Your files and Android security settings are untouched."
        );
        warning.setTextColor(Color.WHITE);
        warning.setTextSize(16);
        warning.setGravity(Gravity.CENTER);
        warning.setPadding(10, 35, 10, 35);

        EditText pin = new EditText(this);
        pin.setHint("Enter demo PIN");
        pin.setTextColor(Color.WHITE);
        pin.setHintTextColor(Color.LTGRAY);
        pin.setGravity(Gravity.CENTER);
        pin.setInputType(2);
        pin.setSingleLine(true);

        Button unlock = new Button(this);
        unlock.setText("UNLOCK DEMO");

        TextView result = new TextView(this);
        result.setTextColor(Color.WHITE);
        result.setTextSize(16);
        result.setGravity(Gravity.CENTER);
        result.setPadding(10, 25, 10, 25);

        unlock.setOnClickListener(v -> {
            if (DEMO_PIN.equals(pin.getText().toString())) {
                result.setText("SIMULATION UNLOCKED");
                result.setTextColor(Color.GREEN);
                pin.setEnabled(false);
                unlock.setEnabled(false);
            } else {
                result.setText("Incorrect PIN. Try again.");
                result.setTextColor(Color.RED);
            }
        });

        Button exit = new Button(this);
        exit.setText("EXIT DEMONSTRATION");
        exit.setOnClickListener(v -> finish());

        layout.addView(title);
        layout.addView(warning);
        layout.addView(pin);
        layout.addView(unlock);
        layout.addView(result);
        layout.addView(exit);

        setContentView(layout);
    }
}
""",

".github/workflows/build.yml": """
name: Build Security Lab APK

on:
  workflow_dispatch:
  push:
    branches: [main]

jobs:
  build:
    runs-on: ubuntu-latest

    steps:
      - uses: actions/checkout@v4

      - uses: actions/setup-java@v4
        with:
          distribution: temurin
          java-version: '17'

      - uses: android-actions/setup-android@v3

      - uses: gradle/actions/setup-gradle@v4
        with:
          gradle-version: '8.9'

      - name: Build APK
        run: gradle assembleDebug

      - name: Upload APK
        uses: actions/upload-artifact@v4
        with:
          name: security-lab-demo
          path: app/build/outputs/apk/debug/app-debug.apk
          if-no-files-found: error
          retention-days: 7
"""
}

for name, content in files.items():
    path = Path(name)
    path.parent.mkdir(parents=True, exist_ok=True)
    path.write_text(content.strip() + "\n")

print("=" * 45)
print("SECURITY LAB PROJECT GENERATED")
print("=" * 45)

for name in files:
    print("[CREATED]", name)

print("\nNo device permissions or system-lock controls added.")
print("Next: commit and push the project to GitHub.")
