# APK / AAB build

## Debug APK
Android Studio → Build → Build APK(s).

## Play Store AAB
Android Studio → Build → Generate Signed App Bundle / APK → Android App Bundle.

Do not put a personal keystore path in `build.gradle`. Keep the release key outside source control and use Android Studio/Play App Signing for release signing.
