# Personal CFO v2.8.1 — Accounts Touch Fix

## What was fixed
The Accounts tab could highlight as selected while the Home screen remained visible. This was caused by a JavaScript render exception in the Accounts view when legacy/local data was missing one or more arrays (especially `tx`).

The patch:
- normalizes missing `tx`, `assets`, `debts`, `budgets`, `recurring`, `tempMoney`, and `commitments` arrays before rendering;
- migrates opening-balance metadata without calling account logic against the uninitialized global state;
- makes `accountHasActivity()` defensive;
- adds a render guard so one malformed legacy field cannot leave a stale Home screen;
- preserves Accounts Edit/Delete and Transfer behavior.

## Android / Play Store configuration
- compileSdk 36
- targetSdk 36
- minSdk 24
- applicationId `com.example.personalcfo`
- versionName `2.8.1`
- versionCode `34`
- AGP 9.1.1
- Gradle 9.3.1
- no machine-specific keystore path
- release build intentionally has no embedded signing key; use Android Studio's **Generate Signed App Bundle** with your own permanent Play signing/upload key.

## Important
Install this build over the existing app when possible so existing `localStorage` data is retained. Do not clear app data.
