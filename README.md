# 📈 Monivue – A Minimalist, Beautiful Stock Tracker

**Monivue** is a modern, lightweight JavaFX-based stock watchlist app that lets you track real-time stock data through the [Twelve Data API](https://twelvedata.com/). Designed with simplicity and speed in mind, it provides a clutter-free UI, auto-refresh functionality, and persistent token management — all while keeping your data private. 
Monivue is a privacy-respecting desktop app built with JavaFX that allows you to track stocks in real time, without distractions, logins, or bloatware.

---
<p align="center">
  <img src="https://github.com/iamSampath/Monivue/blob/b505c7bc5a91d70ac5eec69785b6ef3dd81f4eb6/src/main/resources/icon/monivue-icon.png" alt="Monivue Logo" width="300"/>
</p>

---

## ✨ Features

- 🌓 Light/Dark Mode Toggle
- 🎨 Sleek Finance-inspired Theme
- 🔒 No data collection. 100% local.
- 🖥️ Built for macOS (Intel & ARM) and Windows

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

  #### 🔁 Auto-Refresh with Interval Control
- Choose refresh intervals (OFF / 15s / 30s / 1 min)
- Option to disable API calls when OFF
- Status bar shows last updated time

#### 🎯 Conditional Formatting
- Green for gains, red for losses
- Grey for inactive or offline data
- Helps you scan critical changes instantly

#### 🧭 Column Sorting and Search
- Click to sort by Price, % Change, Volume, etc.
- Search bar to find symbols instantly

#### ⭐ Mark Favorites
- Clickable star icon to highlight key stocks
- Favorites stay sticky even after refresh

#### 📋 Row Context Menu
- Right-click a stock to:
  - View Details
  - Remove from Watchlist
  - Open in external site

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
  Built with JavaFX, Monivue runs seamlessly on Windows, and macOS.

  

    | Platform    | Status                                |
    | ----------- | ----------------------------------- |
    | 🪟 Windows  |  ✅ Supported  |
    | 🍎 macOS   | ✅ Supported    |


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
## 🧹 Reset Functionality
Resetting the token via UI will:

- Remove the stored token from Preferences.
- Clear your watchlist.json so the app starts fresh.
- Trigger the API token prompt on next launch.
---
## 🚀 Installation (End Users)

### ✅ Option 1: Download from [Releases](https://github.com/iamSampath/Monivue/releases)

1. Go to the [**Releases**](https://github.com/iamSampath/Monivue/releases) page.  
2. Download the installer for your operating system:
   - **🪟 Windows**: `Monivue-1.0.msi`
   - **🍎 macOS (Intel)**: `Monivue-1.0.pkg`
   - **🍎 macOS (Apple Silicon)**: `Monivue-Asc-1.0.pkg`
3. Launch the installer:
   - On **Windows**, double-click the `.msi` file.
   - On **macOS**, double-click the `.pkg` file and follow the installation wizard.
4. Follow the installation prompts:
   - Choose install location (Windows/macOS)
   - (Optional) Create a desktop shortcut (Windows only)

---

## 🚀 RoadMap: Future Features (Prioritized by Impact)

### ✅ High Impact, Low Effort



---

### 🔄 Medium Impact, Medium Effort

#### 🛠️ Inline Editable Columns
- Edit target price, notes, or priority tag
- Changes saved locally

#### 📤 Export to CSV
- One-click export of current table to `.csv`
- Includes applied filters and sort order

#### 📦 Offline Resilience
- Keeps last known values cached locally
- Displays “Offline” flag during API outages

#### 🔔 Smart Alerts (coming soon)
- Get desktop notifications when:
  - Price crosses target
  - % change exceeds threshold
  - Volume spikes

---

### 🧠 High Impact, Higher Effort

#### 📊 Inline Mini Charts
- Sparkline-style price trend per row
- Compact and visually intuitive

#### 📂 Grouping and Collapsibles
- Organize stocks by:
  - Sector
  - Market Cap
  - Custom tags
- Collapse/expand each section

#### 🧩 Drag and Drop Row Reordering
- Reorder rows manually
- Prioritize your view based on your interest

#### ⌨️ Keyboard Shortcuts
- ↑ ↓ — Navigate rows
- ⏎ — View details
- ⌫ — Delete row
- ⌘E / Ctrl+E — Edit notes
---

## 📦 Installation

Monivue is available for:
- Windows (`.msi`)
- macOS Intel (`.pkg`)
- macOS Apple Silicon (`.pkg`)

Installers are available on the [Releases](https://github.com/your-repo/monivue/releases) page.

---
## 🤝 License

Monivue is licensed under the MIT License. It’s free to use and respects your privacy — all data stays local with no tracking, no ads, and no data collection.

---

🚀 Built with ❤️ by Sampath Medarametla
