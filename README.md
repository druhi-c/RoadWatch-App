# 🛣️ RoadWatch

## 🌟 Overview
**RoadWatch** is an offline-first mobile application designed for crowdsourcing road defect detection. Built to empower citizens and support municipal maintenance, RoadWatch ensures that no infrastructure issue goes unnoticed, regardless of network connectivity.

## ✨ Features
- 🧠 **YOLOv8 Edge AI**: Instant on-device evaluation and severity assignment of road defects using Edge AI logic.
- 📡 **Offline-First Sync via WorkManager**: Never lose a report. Capture issues in dead zones, and our robust WorkManager will automatically sync your data the moment the connection is restored using exponential backoff.
- 📍 **Fused Location GPS**: Highly accurate geo-tagging using Google's Fused Location Provider, dynamically mapped to Well-Known Text (WKT) points.
- 🚗 **DriveLegal Reporting**: A streamlined, modern dark-mode UI built to make defect reporting intuitive, fast, and visually striking.

## 🛠️ Tech Stack
- **Mobile Frontend**: Android (Java)
- **Backend Infrastructure**: Spring Boot
- **Database Layer**: PostgreSQL with PostGIS
- **Offline Persistence**: Room DB (SQLite)

## 🚀 How to Test
We've made testing incredibly easy for the judges!
1. **Install the App**: Install the `app-debug.apk` onto an Android device or emulator.
2. **Grant Permissions**: Launch the app and grant the necessary Camera and Location permissions.
3. **Simulate a Dead Zone**: Turn on Airplane mode to test our offline resilience.
4. **Snap a Photo**: Use the custom HUD camera view to capture a road defect. Watch the data hit the Room DB immediately.
5. **Verify the Sync**: Turn the network back on. The WorkManager will automatically awaken and dispatch the queued payload directly to the Spring Boot backend!
