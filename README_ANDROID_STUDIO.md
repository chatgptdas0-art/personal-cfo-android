# Android Studio setup — Personal CFO v2.8.3

1. Extract this ZIP.
2. Open the extracted folder that directly contains `settings.gradle`, `build.gradle`, `gradle/`, and `app/`.
3. Let Android Studio sync Gradle 9.3.1.
4. Use a compatible JDK (JDK 17–25; JDK 21 or 25 are suitable for this Gradle/AGP combination).
5. Build a debug APK with **Build → Build APK(s)**.
6. For Google Play, use **Build → Generate Signed App Bundle / APK → Android App Bundle** and configure your own permanent release/Play App Signing key.

The project targets API 36. A Play Store upload still requires the developer's own signing configuration, store listing, policy declarations, and Play Console testing/release requirements.
