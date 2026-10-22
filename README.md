# SkyCast — Native Android Weather Forecast App

SkyCast is a native Android weather application built with **Kotlin**, **Jetpack Compose**, and **Material 3**, powered by the free Open-Meteo Weather API.

Founded by **Naitik**.

---

## Features

- **Real-Time Weather**: Live temperature, feels-like, humidity, wind speed, precipitation, and rain probability.
- **Hourly Forecast**: Horizontally scrollable 24-hour weather timeline.
- **7-Day Outlook**: Detailed day-by-day forecast with max/min temperatures and solar timings.
- **India Nationwide Support**: Search any city, town, or district across all 28 Indian States & 8 Union Territories, plus international locations.
- **India States Hub**: Dedicated India weather overview screen with state filters and quick hubs.
- **Smart City Search**: Dynamic Open-Meteo geocoding search with debounce, search history, and favorite cities.
- **GPS Location**: One-tap current location weather with standard Android permissions.
- **Customizable Units**: Toggle Celsius (°C) / Fahrenheit (°F) and wind units (km/h, m/s, mph).
- **Adaptive Atmosphere**: Dynamic backgrounds and animated weather art matching real-time conditions.

---

## How to Build the APK using GitHub Actions

You can build and download an installable Android APK directly using GitHub without installing Android Studio.

### Step 1: Upload or Push this Project to GitHub

1. If using GitHub web interface:
   - Create a new repository on GitHub (e.g. `skycast-android`).
   - Push this codebase to the repository:
     ```bash
     git init
     git add .
     git commit -m "Initial commit of SkyCast Android app"
     git branch -M main
     git remote add origin https://github.com/YOUR_USERNAME/skycast-android.git
     git push -u origin main
     ```
2. Or use the AI Studio top-right settings menu to **Push to GitHub** or **Export as ZIP**.

### Step 2: Automated Build via GitHub Actions

1. In your GitHub repository, click on the **Actions** tab at the top.
2. You will see the workflow named **Build Android APK**.
3. It runs automatically on every `push` to `main` or `master`.
4. You can also trigger it manually anytime:
   - Click **Build Android APK** in the left sidebar under Workflows.
   - Click the **Run workflow** dropdown button on the right.
   - Select the `main` branch and click **Run workflow**.

### Step 3: Download and Install the APK

1. Once the workflow run finishes with a green checkmark (usually ~1–2 minutes), click on the completed run.
2. Scroll down to the **Artifacts** section at the bottom of the page.
3. Click on **SkyCast-v1.0.0-debug-apk** to download the ZIP file.
4. Extract the ZIP on your computer or phone to obtain `SkyCast-debug.apk`.
5. Transfer `SkyCast-debug.apk` to your Android phone (or download directly via browser on your phone).
6. Tap on the APK to install (enable "Install from unknown sources" or your browser's installation permission if prompted).

---

## Local Development & Android Studio

To run or build the project on your machine with Android Studio:

1. Open Android Studio and select **Open** -> choose this repository folder.
2. Wait for Gradle sync to complete (uses JDK 17 or JDK 21).
3. Connect your Android device via USB or start an Android Virtual Device (AVD).
4. Click the green **Run** button (or press `Shift + F10`).
5. To build the debug APK via terminal:
   ```bash
   ./gradlew assembleDebug
   ```
   The APK is generated at `app/build/outputs/apk/debug/app-debug.apk`.
