# 🤝 Contributing to ZUNEO

Thanks for considering a contribution to **ZUNEO**.

## 🛠️ Project Layout

ZUNEO is a multi-module Android Studio project:

* **`app/`** — Core application module with Jetpack Compose UI under `ui/`, screens under `ui/screens/`, viewmodels, and ColorOS Aquamorphic design tokens.
* **`innertube/`** — YouTube Music (InnerTube) streaming and search API client.
* **`jiosaavn/`** & **`spotify/`** — Alternative music source and Spotify Web API metadata importer.
* **`lrclib/`**, **`kugou/`**, **`musixmatchlyrics/`** — Synchronized lyrics provider modules.
* **`kizzy/`** & **`lastfm/`** — Discord Rich Presence (RPC) gateway and Last.fm scrobbler.

## 🖥️ Building locally

* **JDK 17 / JDK 21**, latest stable **Android Studio**.
* `compileSdk 37`, `minSdk 26` (Android 8.0+).
* Clone and open the project in Android Studio, let Gradle sync, then run the `app` module (`gmsDebug` or `fossDebug`).

## 🐞 Reporting Bugs

Open an [issue](https://github.com/sudeepdevOPS5/ZUNEO-Beyond-the-beat/issues) with repro steps, device model, and Android OS version.
