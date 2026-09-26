# Personal CFO Android

## Stable baseline: v21.1 — Cash Projection Fix

This repository contains the existing Personal CFO Android project used as the stable baseline for controlled maintenance and future updates.

### Architecture
- Android WebView wrapper
- Existing HTML/JavaScript application in `app/src/main/assets/index.html`
- Local/offline persistence in the existing app
- No Kotlin/Jetpack Compose architecture
- No Room database
- No cloud backend
- No AI dependency

### Important baseline rules
- Treat this repository as the source of truth.
- Do not replace the existing architecture.
- Do not generate a new finance app.
- Do not replace the existing storage model.
- Do not invent demo/sample financial data.
- Do not change unrelated modules during feature updates.
- Preserve existing user data and behavior unless an explicit change is requested.

### Current baseline features
- Dashboard
- Accounts
- Transactions / History
- Budgets
- Assets
- Debt / Liabilities
- Financial Commitments and payments
- Recurring & Forecast
- Cash Flow Calendar with projection breakdown
- Financial Health
- Money Simulator / Financial Digital Twin
- Financial Memory / Context
- Temporary Money
- Backup / Restore
- CSV / report export

### Android project
Open the repository root (the directory containing `settings.gradle`) in Android Studio.
