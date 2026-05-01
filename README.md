# 💰 Utang Tracker

> An Android app for tracking debts and payments with biometric verification, photo receipts, and Pro license support.

![Platform](https://img.shields.io/badge/platform-Android-3DDC84?logo=android&logoColor=white)
![Min SDK](https://img.shields.io/badge/minSdk-23%20(Android%206.0)-blue)
![Language](https://img.shields.io/badge/language-Java-orange?logo=openjdk)
![License](https://img.shields.io/badge/license-MIT-lightgrey)
![Status](https://img.shields.io/badge/status-Active-brightgreen)
![Version](https://img.shields.io/badge/version-2.0.0-purple)

---

## 📱 Overview

**Utang Tracker** is a practical Android app designed for small businesses, sari-sari stores, or anyone who lends money and needs a reliable way to track debts and payments. It prevents forgotten or disputed payments by requiring proof — either a **biometric (fingerprint) verification** or a **photo receipt**.

The app has two tiers:
- 🆓 **Free** — full debt tracking with a 30-second ad screen every 2 hours
- ⭐ **Pro** — no ads, custom store branding, and full delete controls (unlocked with a license key)

---

## ✨ Features

### Free Tier
| Feature | Description |
|---|---|
| 👤 Customer Registration | Register debtors with name, amount, and optional notes |
| 🔏 Fingerprint Enrollment | Scan customer fingerprint at registration |
| 🔵 Biometric Receipt | Fingerprint verification per payment |
| 📷 Photo Receipt | Camera photo of receipt/customer face as proof |
| 🔴 Red = Unpaid | Name stays red while balance remains |
| 🟢 Green = Fully Paid | Name turns green when debt is settled |
| 🔤 Alphabetical Order | Debtors always sorted A–Z |
| 📅 Timestamps | Start date, every payment date, and fully-paid date all stamped |
| 🗑️ Delete (Paid) | Long-press to delete fully-paid debtors |
| 🔍 Search | Filter debtors by name |
| 💾 Offline Storage | Room/SQLite — no internet needed |
| 📺 Ad Screen | 30-second ad every 2 hours (skippable after countdown) |

### Pro Tier (requires license key)
| Feature | Description |
|---|---|
| 🚫 No Ads | Ad screen completely disabled |
| 🗑️ Delete Anyone | Long-press to delete any debtor, paid or not |
| 🏪 Store Name | Custom store name shown in splash and header |
| 🖼️ Store Logo | Custom logo from gallery shown in splash screen |
| ⭐ Pro Badge | PRO badge shown throughout the app |

---

## 🖼️ Screenshots

> *(Add screenshots here as the app develops)*

| Splash | Main List | Debtor Detail | Ad Screen | Settings |
|---|---|---|---|---|
| ![splash](#) | ![main](#) | ![detail](#) | ![ad](#) | ![settings](#) |

---

## 🛠️ Tech Stack

- **Language:** Java
- **Min SDK:** 26 (Android 8.0 Oreo)
- **Target SDK:** 34 (Android 14)
- **Database:** Room (SQLite)
- **Biometrics:** AndroidX Biometric library
- **UI:** Material Components, RecyclerView, CardView, CoordinatorLayout
- **License Validation:** Offline SHA-256 hash check (no server needed)
- **Package:** `com.redlab.utang`

---

## 🚀 Getting Started

### Prerequisites
- Android Studio Hedgehog (2023.1.1) or newer
- Android device/emulator running Android 6.0+
- Fingerprint sensor optional (photo receipt works without one)

### Installation

1. Clone the repo:
   ```bash
   git clone https://github.com/fredh2zycho/utang-tracker.git
   ```

2. Open in Android Studio → File → Open → select `UtangApp`

3. Let Gradle sync, then Run on device or emulator.

---

## 📁 Project Structure

```
UtangApp/
└── app/src/main/java/com/redlab/utang/
    ├── SplashActivity.java          ← Splash screen (store logo/name) → routes to Ad or Main
    ├── AdActivity.java              ← 30-second ad screen (free tier only)
    ├── MainActivity.java            ← Alphabetical debtor list, search, long-press delete
    ├── AddDebtorActivity.java       ← Register new debtor + fingerprint enrollment
    ├── DebtorDetailActivity.java    ← View debt info, record payments (biometric/photo)
    ├── SettingsActivity.java        ← Store name, store logo (Pro), license key input
    ├── adapters/
    │   ├── DebtorAdapter.java       ← RecyclerView with red/green colors + long-click delete
    │   └── PaymentAdapter.java      ← Payment history list with receipt thumbnails
    ├── database/
    │   ├── AppDatabase.java         ← Room database singleton
    │   ├── DebtorDao.java           ← Debtor queries (insert, update, delete, sorted)
    │   └── PaymentRecordDao.java    ← Payment record queries
    ├── models/
    │   ├── Debtor.java              ← Debtor entity
    │   └── PaymentRecord.java       ← Payment entity (amount, date, receipt type, photo path)
    ├── pro/
    │   ├── LicenseManager.java      ← Offline SHA-256 key validation + SharedPrefs storage
    │   └── StoreSettings.java       ← Store name + logo (Base64 PNG) persistence
    └── utils/
        ├── AdManager.java           ← Tracks last ad time, 2-hour interval logic
        ├── BiometricHelper.java     ← Biometric prompt wrapper
        └── DateUtils.java           ← Date formatting utilities

UtangKeygenApp/                      ← Separate companion app (internal use only)
└── app/src/main/java/com/redlab/utangkeygen/
    └── KeygenActivity.java          ← Generates valid Pro license keys
```

---

## 🔐 Permissions

| Permission | Purpose |
|---|---|
| `USE_BIOMETRIC` | Payment fingerprint verification |
| `USE_FINGERPRINT` | Legacy fingerprint (API 23–27) |
| `CAMERA` | Photo receipt capture |
| `READ_MEDIA_IMAGES` | Gallery access for store logo (API 33+) |
| `READ_EXTERNAL_STORAGE` | Gallery access (API 32 and below) |
| `WRITE_EXTERNAL_STORAGE` | Saving photos (API 28 and below) |

---

## ⭐ Pro License System

License keys are **validated offline** using SHA-256.

**Key Format:**
```
UTNG-XXXX-XXXX-XXXX-XXXX
```

**Validation Algorithm:**
1. Strip dashes and uppercase the key
2. Compute `SHA-256("REDLAB_UTANG_SALT_2025" + stripped_key)`
3. Key is valid if the hash starts with `"0a"`

**How to get a key:** Use the companion **Utang Keygen** app (internal tool) to generate valid keys. Each key is unique — distribute one per customer.

**In Settings:**
- Enter the key in the License Key field
- Tap **"I-activate ang Pro License"**
- All Pro features unlock immediately and persist across restarts

---

## 📺 Ad System (Free Tier)

- A 30-second fullscreen ad screen appears every **2 hours** of use
- The countdown cannot be skipped — the "Continue" button is locked until 0
- Back button is disabled during the ad
- The 2-hour timer resets each time the ad is shown
- **Pro users never see ads**

---

## 💡 Usage Guide

### Register a New Debtor
1. Tap **＋** on the main screen
2. Enter name, total debt amount, and optional notes
3. Tap **"I-scan ang Fingerprint"** → customer places finger on sensor
4. Tap **"I-save ang Customer"** — registered with a start date stamp

### Record a Payment

**Biometric Receipt:**
1. Open debtor detail page → tap **"Bayad (Biometric)"**
2. Enter amount → customer scans finger to confirm
3. Payment saved with biometric-verified timestamp

**Photo Receipt:**
1. Open debtor detail page → tap **"Bayad (Photo)"**
2. Enter amount → camera opens
3. Take photo of receipt/customer face
4. Payment saved with photo attached

### Delete a Debtor
- **Long-press** any name in the main list
- Free tier: only fully-paid debtors can be deleted
- Pro tier: any debtor can be deleted (with warning dialog)

### Activate Pro
1. Go to ⚙️ Settings from the top menu
2. Enter your license key: `UTNG-XXXX-XXXX-XXXX-XXXX`
3. Tap **"I-activate ang Pro License"**

---

## 🔑 Keygen App (Internal)

A separate companion app `UtangKeygenApp` is included for generating Pro license keys.

**How it works:**
- Generates random `UTNG-XXXX-XXXX-XXXX-XXXX` keys
- Validates each against the same SHA-256 algorithm used by the main app
- Displays the key and hash proof once a valid one is found
- One-tap copy to clipboard

> ⚠️ **Do not distribute the Keygen APK.** Only distribute the keys it generates.

---

## 📋 Changelog

### v2.0.0
- ✅ Added **Splash Screen** with store name and custom logo support
- ✅ Added **30-second Ad Screen** every 2 hours for free tier
- ✅ Added **Settings Screen** with store name, store logo (Pro), and license key input
- ✅ Added **Pro License System** (offline SHA-256 validation)
- ✅ Added **Long-press Delete** for paid debtors (free) or any debtor (Pro)
- ✅ Added **Separate Keygen App** (`com.redlab.utangkeygen`)
- ✅ Store name now appears dynamically in the main screen header
- ✅ Pro badge shown in splash and settings when activated

### v1.0.0 — Initial Release
- Customer registration with fingerprint enrollment
- Biometric and photo payment receipts
- Alphabetical A–Z debtor sorting
- Red/green color status system
- Fully paid date stamping
- Local Room database storage
- Search by name

---

## 🗺️ Roadmap

- [ ] Export payment history to PDF or CSV
- [ ] SMS/push reminder for overdue debts
- [ ] Interest/penalty calculation option
- [ ] Dark mode support
- [ ] Multiple debt entries per customer
- [ ] Full-screen photo viewer for receipt images
- [ ] PIN lock / app password protection
- [ ] Summary dashboard (total lent, collected, outstanding)
- [ ] Backup and restore database

---

## 🤝 Contributing

Issues and pull requests welcome!

1. Fork the repo
2. Create your branch: `git checkout -b feature/your-feature`
3. Commit: `git commit -m "Add your feature"`
4. Push: `git push origin feature/your-feature`
5. Open a Pull Request

---

## 📄 License

MIT License — see [LICENSE](LICENSE) for details.

---

## 👨‍💻 Author

**RedLab**
- GitHub: [@fredh2zycho](https://github.com/fredh2zycho)

---

> *"Huwag kalimutang bayaran ang utang."* 😄
