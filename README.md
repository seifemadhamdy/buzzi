# 📰 Buzzi

Buzzi is a modern, offline-first Android news application designed to provide users with the latest headlines and a seamless reading experience. Built entirely with Kotlin and Jetpack Compose, the app focuses on a highly responsive UI, robust state management, and network resilience.

## 📱 Screenshots

<table style="width: 100%; text-align: center;">
  <tr>
    <th colspan="2">☀️ Light</th>
    <th colspan="2">🌙 Dark</th>
  </tr>
  <tr>
    <td width="25%">
      <img src="https://github.com/user-attachments/assets/62422c59-56e9-406a-ad49-2700c2eae908" alt="Top Headlines Screen (Light)" width="100%"><br>
      <strong>Top Headlines</strong>
    </td>
    <td width="25%">
      <img src="https://github.com/user-attachments/assets/d59276cd-7012-4992-bb58-90fee1872484" alt="Article Details Screen (Light)" width="100%"><br>
      <strong>Article Details</strong>
    </td>
    <td width="25%">
      <img src="https://github.com/user-attachments/assets/23cca009-8307-419a-8f03-1df5b0dd28cf" alt="Top Headlines Screen (Dark)" width="100%"><br>
      <strong>Top Headlines</strong>
    </td>
    <td width="25%">
      <img src="https://github.com/user-attachments/assets/fd06ac43-e2b2-4682-9e19-55d1d9e89d11" alt="Article Details Screen (Dark)" width="100%"><br>
      <strong>Article Details</strong>
    </td>
  </tr>
</table>

### 🛡️ Resilient Error States

<table style="width: 100%; text-align: center;">
  <tr>
    <th colspan="3">☀️ Light</th>
    <th colspan="3">🌙 Dark</th>
  </tr>
  <tr>
    <td width="16%">
      <img src="https://github.com/user-attachments/assets/47e03404-32a0-4f63-95f8-0888f34970a8" alt="Network Error Screen (Light)" width="100%"><br>
      <sub>Network Error</sub>
    </td>
    <td width="16%">
      <img src="https://github.com/user-attachments/assets/87765ef9-d1ca-4add-bf71-1e8b348d968f" alt="No Results Screen (Light)" width="100%"><br>
      <sub>No Results</sub>
    </td>
    <td width="16%">
      <img src="https://github.com/user-attachments/assets/a4f7e19e-3c24-48ee-9898-806a09ed1dfd" alt="No Headlines Screen (Light)" width="100%"><br>
      <sub>No Headlines</sub>
    </td>
    <td width="16%">
      <img src="https://github.com/user-attachments/assets/f9b299ee-1af2-4fbe-86e0-7cae29fc3323" alt="Network Error Screen (Dark)" width="100%"><br>
      <sub>Network Error</sub>
    </td>
    <td width="16%">
      <img src="https://github.com/user-attachments/assets/85db0ae4-7089-4279-befb-728404be0cdf" alt="No Results Screen (Dark)" width="100%"><br>
      <sub>No Results</sub>
    </td>
    <td width="16%">
      <img src="https://github.com/user-attachments/assets/1343c773-824b-46d4-b712-b6040edb744e" alt="No Headlines Screen (Dark)" width="100%"><br>
      <sub>No Headlines</sub>
    </td>
  </tr>
</table>

## ✨ Features

* **Real-time Headlines:** Fetches and seamlessly merges the latest news feeds from multiple sources into a single, smooth, scrollable list.
* **Dynamic Search:** Instantly filter and discover specific topics using the powerful integrated search functionality.
* **Immersive Reading Experience:** Tapping an article opens a detailed view containing the full title, high-quality imagery, author information, and an in-depth description.
* **Adaptive Theming (Palette API):** The app's color scheme dynamically adapts to the prominent colors found in article images, providing a deeply integrated and visually stunning experience.
* **Offline-First Capabilities:** Never lose access to the news. Buzzi caches articles locally using Room Database, allowing users to browse previously loaded content even when they lose their internet connection.

## 🧠 Architecture & State Handling

Buzzi is engineered for scale and predictability, utilizing the **MVI (Model-View-Intent)** architecture pattern.

* **Predictable UI States:** The UI strictly observes a single source of truth for its state (Loading, Success, Empty, Error). This guarantees that the screen always accurately reflects the background data processes.
* **Robust Error Management:** Gracefully handles network failures, empty search results, and API limits. Instead of crashes or blank screens, Buzzi provides clear, actionable UI states to keep the user informed.
* **Unidirectional Data Flow:** User actions (Intents) are processed by the ViewModel, which updates a centralized State flow, ensuring a clean separation of concerns and highly testable code.

## 🛠 Tech Stack

Buzzi adheres strictly to modern Android development best practices:

* **UI Framework:** [Jetpack Compose](https://developer.android.com/jetpack/compose) for a declarative, reactive, and modern user interface.
* **Navigation:** [Compose Navigation](https://developer.android.com/jetpack/compose/navigation) for type-safe routing and seamless transitions between screens.
* **Networking:** [Retrofit](https://square.github.io/retrofit/) & OkHttp for efficient REST API communication.
* **Asynchronous Programming:** [Kotlin Coroutines](https://kotlinlang.org/docs/coroutines-overview.html) & [Flow](https://kotlinlang.org/docs/flow.html) for responsive background processing and reactive data streams.
* **Dependency Injection:** [Hilt](https://dagger.dev/hilt/) for scalable and boilerplate-free DI.
* **Local Database:** [Room Database](https://developer.android.com/training/data-storage/room) serving as the single source of truth for offline caching.
* **Image Loading & Theming:** [Coil](https://coil-kt.github.io/coil/) for asynchronous image loading, paired with AndroidX Palette KTX for dynamic color extraction.
* **Jetpack Components:** ViewModel and Lifecycle extensions.

## 🚀 API Details

The app consumes data from [NewsAPI](https://newsapi.org/), utilizing the following endpoints:
* **Top Headlines:** `https://newsapi.org/v2/top-headlines?country=us` (Handles pagination seamlessly).
* **Search:** `https://newsapi.org/v2/top-headlines?country=us&q={keyword}`

## 🛠️ Setup & Installation

To run this project locally, you need to provide your own NewsAPI key.

**1. Clone the repository**
```bash
git clone https://github.com/seifemadhamdy/buzzi.git
```

**2. Open the project in Android Studio**

**3. Add your API Key**
Open the `local.properties` file located in the root directory of the project and add the following line. Replace `{YOUR_API_KEY}` with your actual key from [NewsAPI](https://newsapi.org/):
```properties
NEWS_API_KEY="{YOUR_API_KEY}"
```

**4. Build and run the project** on an emulator or physical device.

## 👨‍💻 Author

**Seif Abu El-Ela**
