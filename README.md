# 💰 Utang Tracker

> An Android app for tracking debts and payments with biometric verification and photo receipts.

![Platform](https://img.shields.io/badge/platform-Android-3DDC84?logo=android&logoColor=white)
![Min SDK](https://img.shields.io/badge/minSdk-23%20(Android%206.0)-blue)
![Language](https://img.shields.io/badge/language-Java-orange?logo=openjdk)
![License](https://img.shields.io/badge/license-MIT-lightgrey)
![Status](https://img.shields.io/badge/status-Active-brightgreen)

---

## 📱 Overview

**Utang Tracker** is a simple but practical Android app designed for small businesses, sari-sari stores, or anyone who lends money and needs a reliable way to track who owes them and who has already paid. It solves the common problem of forgotten or disputed payments by requiring proof of payment — either a **biometric (fingerprint) verification** or a **photo of the receipt and the customer's face**.

---

## ✨ Features

| Feature | Description |
|---|---|
| 👤 Customer Registration | Register a debtor with their name, debt amount, and optional notes |
| 🔏 Fingerprint Enrollment | Scan the customer's fingerprint at the time of registration |
| 🔵 Biometric Receipt | Customer verifies payment with their fingerprint — acts as a digital receipt |
| 📷 Photo Receipt | Take a photo of the payment slip and/or customer's face as proof |
| 🔴 Red = Unpaid | Customer names appear in **red** while they still have an outstanding balance |
| 🟢 Green = Fully Paid | Customer names turn **green** once the debt is completely paid off |
| 🔤 Alphabetical Order | All debtors are automatically sorted A–Z by name |
| 📅 Debt Start Stamp | Records the exact date and time the debt was registered |
| 📅 Payment Stamps | Every payment is timestamped with date and time |
| 🏆 Fully Paid Stamp | Records the exact date and time the debt was fully settled |
| 🔍 Search | Quickly find a customer by name |
| 💾 Offline Storage | All data is stored locally using Room (SQLite) — no internet required |

---

## 🖼️ Screenshots

> *(Coming soon — add screenshots here as the app develops)*

| Main List | Debtor Detail | Add Debtor |
|---|---|---|
| ![main](#) | ![detail](#) | ![add](#) |

---

## 🛠️ Tech Stack

- **Language:** Java
- **Min SDK:** 23 (Android 6.0 Marshmallow)
- **Target SDK:** 34 (Android 14)
- **Database:** Room (SQLite)
- **Biometrics:** AndroidX Biometric library
- **UI:** Material Components, RecyclerView, CardView
- **Architecture:** Activity-based with LiveData observers
- **Package:** `com.redlab.utang`

---

## 🚀 Getting Started

### Prerequisites

- Android Studio Hedgehog (2023.1.1) or newer
- Android device or emulator running Android 6.0+
- A device with a fingerprint sensor (optional — photo receipt works without one)

### Installation

1. Clone the repository:
   ```bash
   git clone https://github.com/fredh2zycho/utang-tracker.git
   ```

2. Open the project in Android Studio:
   - File → Open → select the `UtangApp` folder

3. Let Gradle sync automatically.

4. Run on a device or emulator:
   - Click ▶ **Run** or press `Shift+F10`

> **Note:** For biometric features to work during testing, use a physical device or an AVD (emulator) that has fingerprint enrollment configured.

---

## 📁 Project Structure

```
app/src/main/
├── java/com/redlab/utang/
│   ├── MainActivity.java              # Alphabetical debtor list with search
│   ├── AddDebtorActivity.java         # Register new debtor + fingerprint enrollment
│   ├── DebtorDetailActivity.java      # View debt info, record payments
│   ├── adapters/
│   │   ├── DebtorAdapter.java         # RecyclerView adapter (red/green color logic)
│   │   └── PaymentAdapter.java        # Payment history list with receipt thumbnails
│   ├── database/
│   │   ├── AppDatabase.java           # Room database singleton
│   │   ├── DebtorDao.java             # Debtor queries (insert, update, get all sorted)
│   │   └── PaymentRecordDao.java      # Payment record queries
│   ├── models/
│   │   ├── Debtor.java                # Debtor entity (name, amount, dates, fingerprint key)
│   │   └── PaymentRecord.java         # Payment entity (amount, date, receipt type, photo path)
│   └── utils/
│       ├── BiometricHelper.java       # Biometric prompt wrapper
│       └── DateUtils.java             # Date formatting utilities
└── res/
    ├── layout/                        # All XML layouts
    ├── drawable/                      # Shapes, icons
    ├── values/                        # Colors, strings, themes
    └── mipmap-*/                      # Launcher icons (all densities)
```

---

## 🔐 Permissions

The app requests the following permissions:

| Permission | Purpose |
|---|---|
| `USE_BIOMETRIC` | Fingerprint verification for payment receipts |
| `USE_FINGERPRINT` | Legacy fingerprint support (API 23–27) |
| `CAMERA` | Taking photo receipts |
| `WRITE_EXTERNAL_STORAGE` | Saving receipt photos (Android 8 and below only) |

---

## 💡 How It Works

### Registering a New Debtor
1. Tap the **＋** button on the main screen.
2. Enter the customer's name, total debt amount, and optional notes.
3. Tap **"I-scan ang Fingerprint"** and have the customer place their finger on the sensor.
4. Tap **"I-save ang Customer"** — the debt is registered with a timestamp.

### Recording a Payment

**Option A — Biometric Receipt:**
1. Open the debtor's detail page.
2. Tap **"Bayad (Biometric)"** and enter the payment amount.
3. The customer places their finger on the sensor to confirm.
4. Payment is saved with a verified biometric stamp.

**Option B — Photo Receipt:**
1. Open the debtor's detail page.
2. Tap **"Bayad (Photo)"** and enter the payment amount.
3. The camera opens — take a photo of the receipt or the customer.
4. Payment is saved with the photo attached.

### Status Colors
- 🔴 **Red name** — customer still has a remaining balance
- 🟢 **Green name** — customer is fully paid, debt is settled

---

## 📋 Changelog

### v1.0.0 — Initial Release
- Customer registration with name, debt amount, notes, and start date stamp
- Biometric (fingerprint) enrollment at registration
- Biometric payment verification with timestamp receipt
- Photo receipt capture using device camera
- Alphabetical sorting of all debtors (A–Z)
- Red/green color coding based on payment status
- Fully paid date stamp
- Payment history log per debtor
- Local offline storage with Room database
- Search bar to filter debtors by name

---

## 🗺️ Roadmap

- [ ] Export payment history to PDF or CSV
- [ ] Backup and restore database
- [ ] SMS/notification reminder for overdue debts
- [ ] Interest/penalty calculation option
- [ ] Dark mode support
- [ ] Multiple debt entries per customer
- [ ] Photo viewer for stored receipt images (full screen)
- [ ] PIN lock / app password protection
- [ ] Summary dashboard (total lent, total collected, total outstanding)

---

## 🤝 Contributing

Contributions, issues, and feature requests are welcome! Feel free to open an issue or submit a pull request.

1. Fork the repository
2. Create your feature branch: `git checkout -b feature/your-feature`
3. Commit your changes: `git commit -m "Add your feature"`
4. Push to the branch: `git push origin feature/your-feature`
5. Open a Pull Request

---

## 📄 License

This project is licensed under the MIT License — see the [LICENSE](LICENSE) file for details.

---

## 👨‍💻 Author

**RedLab**
- GitHub: [@fredh2zycho](https://github.com/fredh2zycho)

---

> *"Huwag kalimutang bayaran ang utang."* 😄
