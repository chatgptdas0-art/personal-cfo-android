# Personal CFO Android v2.8.3

## Controlled update
This package is based on the existing Personal CFO Android WebView architecture. It does not replace the finance app with a new implementation.

### v2.8.3 changes
- Fixed primary bottom navigation for Android WebView by using real DOM click listeners instead of inline navigation handlers.
- Added touch-safe navigation CSS (`touch-action: manipulation`, explicit pointer events).
- Kept Accounts functionality: Add, Edit, Delete, Transfer.
- Added defensive normalization for malformed/legacy account records so Accounts rendering cannot fail because of null account entries or missing basic fields.
- Kept financial-history deletion protection.
- Preserved external backup/restore flow.
- compileSdk 36 / targetSdk 36.
- No machine-specific keystore path; release signing must be configured with the developer's own Play App Signing/release key in Android Studio.

## Verification performed
- Extracted package and inspected project structure.
- Confirmed the ZIP root is a direct Android Studio project (`app/`, `build.gradle`, `settings.gradle`, `gradle/`).
- Node JavaScript syntax check: PASS.
- Node DOM-stub render test: Accounts page renders on initial Accounts tab and after `go('accounts')`: PASS.
- Accounts UI contains Edit/Delete actions: PASS.
- Gradle/APK/AAB build was not executed in this environment because Android SDK/Gradle executable is unavailable here.
