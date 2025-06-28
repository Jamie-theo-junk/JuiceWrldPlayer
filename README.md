# Juice WRLD Player 

**Juice WRLD Player** is a native Android app dedicated entirely to streaming and playing songs by the legendary artist **Juice WRLD**. No distractions, no other artists — just 100% Juice WRLD.

---

## 📱 Features

- 🎵 **Stream Juice WRLD songs** from a custom backend or local library
- 🎤 Song info, album art
- 💾 Offline playback (download support)
- 🌙 Dark mode UI for smooth nighttime listening
- 🔍 Search songs by title, album, or mood
- 🧠 Built using a clean MVVM architecture

---

## 🛠 Tech Stack

- **Language:** Kotlin
- **UI:** Android XML layouts, Material Design
- **Audio Engine:**  `MediaPlayer`

---

## 🔧 Installation

1. **Clone the repository**
   ```bash
   git clone https://github.com/Jamie-theo-junk/JuiceWrldPlayer.git

2. **Open the project in Android Studio**
3. **Build and Run the app on your emulator or device**

   ---

## Dependencies

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    implementation("com.google.code.gson:gson:2.8.9")
