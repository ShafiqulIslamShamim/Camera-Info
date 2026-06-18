# 📷 Camera Info App

<div align="center">

  <img src="assets/logo.png" alt="Camera-Info Logo" width="120" height="120" />
  <br/>
  <br/>
  
**Detailed Camera & System Specs for Android Geeks**
  
  [![Platform](https://img.shields.io/badge/Platform-Android-3DDC84?style=flat-square&logo=android)](https://www.android.com/)
  [![License](https://img.shields.io/badge/License-Apache-blue?style=flat-square)]()

</div>

A lightweight and powerful Android application that provides in-depth details about your device’s camera hardware and system environment. Perfect for developers, testers, and power users who need quick and reliable camera diagnostics.  

---

## 🚀 Features  

- **Customizable App Theme**  
  - Added new options under **Settings > System**: choose between **Follow System**, **Dark Mode**, or **Light Mode**.
  - Switch between diverse hand-picked colors, light/dark modes, AMOLED black mode, and dynamic accent colors.
  - Reconstructed the style selection view with responsive rounded segmented buttons, active scheme floating badges, color accent previews, and reactive checkmarks.

- **Info Selection Made Easy**  
  - Redesigned the "Select Info Type" menu to provide seamless accessibility to different camera diagnostic reporting modules:  
    - **Basic Info** – essential device and camera details.  
    - **All Info** – a full list of keys, environment variables, and system data.  
    - **Camcorder Profile Info** – video-specific capabilities and profiles.  

- **Advanced Data Views**  
  - Expanded reporting to include details like:  
    - System security patch level  
    - SDK version details  
    - Supported processor ABIs  
    - Complete list of **Camera2 API** native characteristics, requests, and result keys  

- **Integrated Sharing**  
  - Added a dedicated share icon to quickly export and send camera reports instantly.  

---

## 🔧 Technical Improvements  

- **Google Play In-App Updates Integration**  
  - Replaced the custom self-hosted checker with the official **Google Play In-App Updates SDK** to check for and apply updates securely.
  - Failures in non-Play environments (e.g. debug, side-loaded build) are caught gracefully with user instructions and direct shortcut support to open the Play Store.

- **Logcat Control**  
  - Added an **Enable Logcat logging** toggle in Settings > System for advanced troubleshooting.  

- **UI & Container Styling Updates**  
  - Refreshed **Settings** and **System** menus for a cleaner experience.  

- **Optimizations**  
  - Faster info retrieval and improved stability across different Android devices.  

---

## 🐞 Bug Fixes  

- Fixed an issue where container borders and rounded shapes failed to adapt dynamically to chosen color schemas.
- Fixed truncation of certain resolution values in the report view.  
- Resolved long load times when selecting **Camcorder Profile Info**.

---

## 🖼️ Screenshots

| ![ss-1] | ![ss-2] | ![ss-3] |
| ------- | ------- | ------- |
| ![ss-4] | ![ss-5] | ![ss-6] |
| ![ss-7] |

[ss-1]: screenshots/1.jpg
[ss-2]: screenshots/2.jpg
[ss-3]: screenshots/3.jpg
[ss-4]: screenshots/4.jpg
[ss-5]: screenshots/5.jpg
[ss-6]: screenshots/6.jpg
[ss-7]: screenshots/7.jpg

---

## 🔧 Tech Stack

- **Language:** Java
- **Platform:** Android  
- **Min SDK:** *26*

---

## 📫 Feedback & Support

- Encountered an issue? Enable **Logging Mode** and send the log file.
- Suggestions or feature requests? [Open an issue](https://github.com/ShafiqulIslamShamim/Camera-Info/issues) or contact the developer.

---

## 🛡️ License

This project is licensed under the [Apache License](LICENSE)

---

## 🙌 Contributions

Pull requests are welcome!  
Fork the repo, make your changes, and submit a PR.  
Let’s make **Camera Info** even more powerful together.

---

## Get it from

<div align="center">

<a href="https://play.google.com/store/apps/details?id=com.shamim.camerainfo"><img src="assets/play.png" height="50"></a>

</div>

---