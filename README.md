# 📈 Monivue

**Monivue** is a modern, lightweight JavaFX-based stock watchlist app that lets you track real-time stock data through the [Twelve Data API](https://twelvedata.com/). Designed with simplicity and speed in mind, it provides a clutter-free UI, auto-refresh functionality, and persistent token management — all while keeping your data private.

---

## ✨ Features

- 🔍 **Real-time Stock Quotes**  
  Add stock symbols and instantly fetch live quotes including:
  - Symbol
  - Company Name
  - Current Price
  - Percentage Change

- 🕒 **Auto Refresh Support**  
  Choose from different refresh intervals:
  - 30 seconds
  - 1 minute
  - 2 minutes
  - 5 minutes  
  Monivue will auto-update your watchlist at the selected frequency.

- 💾 **Persistent Watchlist**  
  Your stock symbols are saved locally and loaded automatically every time you open the app. No need to re-enter them.

- 🔐 **API Token Prompt**  
  - On first launch, Monivue prompts the user for an API token via a secure dialog.
  - The token is saved using the Java Preferences API (OS-native storage).
  - If the token is reset, the app will prompt again on next launch.

- 🧹 **Reset Token Support**  
  - Easily clear your saved token from the app via the "Settings > Reset Token" menu option.
  - Also clears the watchlist data to prevent accidental use without a token.

- 🧾 **Logging Support**  
  Logs are written to your home directory under `~/MonivueLogs/`:
  - `monivue-error.log`
  - `monivue-output.log`

- 🌐 **Cross-platform**  
  Built with JavaFX, Monivue runs seamlessly on Windows, macOS, and Linux.

---

## 🔐 API Token
Monivue uses the TwelveData API for fetching live stock quotes. You must have a valid API key to use the app.

How the token is handled:
1. On first launch, the app will prompt you to enter your API key.
2. It will be stored securely using Java's Preferences API.
3. The token will persist across sessions until you reset it.

To manually reset the token:
 - Use the "Reset Token" option in the app menu.

---

## 📁 File Locations

| Purpose     | Path                                |
| ----------- | ----------------------------------- |
| Logs        | `~/MonivueLogs/monivue-error.log`   |
| Watchlist   | `~/.monivue/Monivue/watchlist.json` |
| Saved Token | OS-specific Java Preferences path   |


---


## 📂 File Structure Overview

Monivue/
├── src/
│ ├── com.monivue
│ │ └── MonivueApplication.java
│ ├── com.monivue.controller
│ │ └── DashboardController.java
│ ├── com.monivue.service
│ │ └── StockApiService.java
│ ├── com.monivue.model
│ │ └── StockQuote.java
│ ├── com.monivue.persistence
│ │ └── WatchlistPersistence.java
│ └── com.monivue.util
│ └── TokenPersistence.java
├── view/
│ └── dashboard.fxml


---
## 🧹 Reset Functionality
Resetting the token via UI will:

- Remove the stored token from Preferences.
- Clear your watchlist.json so the app starts fresh.
- Trigger the API token prompt on next launch.
---
## 🧠 Future Improvements

- 🔐 Encrypt token storage
- 📱 Export to mobile
- 📊 Enhanced data visualization (opt-in)
- 🧠 AI-based stock insights (beta)

---

## 🤝 License

Monivue is free to use and respects your privacy. Your data stays local — no tracking, no ads.

---

🚀 Built with ❤️ by Sampath Medarametla
