# 🤏 Chhotu Gesture

> **Your hands, your commands** — A native Android gesture recognition app

Chhotu Gesture detects hand gestures through your phone's camera and converts them into system-level actions — volume control, media playback, brightness, navigation, and more.

## ✨ Features

- 🖐️ **Real-time Hand Detection** — MediaPipe Hands SDK with GPU acceleration
- 🎵 **System Control** — Volume, brightness, media playback, flashlight
- 🧠 **5 Built-in Gestures** — Open Hand, Fist, Thumbs Up, Peace, Pointing
- 🎯 **Custom Gesture Training** — Train your own gestures with kNN
- 📊 **Dashboard** — Usage statistics and gesture accuracy tracking
- 🔄 **Background Detection** — Foreground service with adaptive FPS
- ♿ **Accessibility Service** — System-wide gesture control
- 🎨 **Material 3** — Dynamic color theming

## 📱 Default Gestures

| Gesture | Action |
|---------|--------|
| 👋 Open Hand | Play/Pause |
| ✊ Fist | Mute |
| 👍 Thumbs Up | Volume Up |
| ✌️ Peace Sign | Next Track |
| 👆 Pointing | Scroll Down |

## 🛠️ Tech Stack

- **Kotlin** + **Jetpack Compose** + **Material 3**
- **MediaPipe Hands** Android SDK (GPU delegate)
- **CameraX** for camera access
- **Hilt** for dependency injection
- **Room** for local database
- **Clean Architecture** + MVVM

## 📦 Install

Download the latest APK from the [Releases](../../releases) page.

**Requirements:**
- Android 8.0+ (API 26)
- Camera required
- Accessibility Service permission (for system-wide control)

## 🏗️ Build from Source

```bash
git clone https://github.com/chhot2u/chhotu-gesture.git
cd chhotu-gesture
./gradlew assembleDebug
```

## 📄 License

MIT License

## 🤝 Credits

Built with ❤️ using CocoIndex-Code MCP planning tools
