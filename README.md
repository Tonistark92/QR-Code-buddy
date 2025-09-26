# QR Code Generator and Scanner

This Android application allows users to generate and scan QR codes. The app is built using Jetpack Compose for UI, Jetpack Navigation for app navigation, ZXing library for QR code generation and scanning, and CameraX for camera functionalities.

## Features

- Generate QR codes from text input
- Scan QR codes using the device's camera
- Modern UI built with Jetpack Compose
- Navigation between screens using Jetpack Navigation
- Deep link handling for shared qr coe image from other app

## 🛠️ Tech Stack
- **Language**: Kotlin
- **UI**: Jetpack Compose
- **Architecture**: [MVI,Clean Architecture]
- **Asynchronous**: Coroutines + Flow
- **Dependency Injection**: [Koin]
- **Database**: Room / DataStore
- **Testing**: JUnit, Espresso, MockK, Compose UI Test
- [Jetpack Compose](https://developer.android.com/jetpack/compose) - For building native UI.
- [Jetpack Navigation](https://developer.android.com/guide/navigation) - For navigation between screens.
- [ZXing](https://github.com/zxing/zxing) - For generating and decoding QR codes.
- [CameraX](https://developer.android.com/training/camerax) - For camera functionalities.
  - Used `ImageAnalysis.Analyzer` for analyzing the camera frames in real-time to detect QR codes.
---

## 📂 Project Structure
```
com.iscoding.qrcode/
│── data/
│ ├── local/ # [TODO: Local DB/DataStore/SharedPref if any]
│ └── repos/ # Data layer implementations
│ ├── MediaRepositoryImpl.kt
│ ├── QrCodeGeneratorImp.kt
│ ├── QrCodeScannerImp.kt
│ └── QrCodeStorageAnalyzerImp.kt
│
├── di/ # Dependency injection modules
│
├── domain/ # Domain layer (business logic contracts)
│ ├── model/ # Domain models
│ └── repos/ # Repository interfaces
│ ├── MediaRepository.kt
│ ├── QrCodeGenerator.kt
│ ├── QrCodeScanner.kt
│ └── QrCodeStorageAnalyzer.kt
│
├── features/ # Feature-based UI modules
│ ├── generate/ # QR code generation feature
│ │ ├── event/
│ │ ├── util/
│ │ ├── widgets/
│ │ │ ├── GenerateQRCodeScreen.kt
│ │ │ ├── GenerateQRCodeState.kt
│ │ │ ├── GenerateQRCodeViewModel.kt
│ │ │ └── MainScreen.kt
│ │
│ ├── mainactivity/ # App entry activity
│
│ ├── scan/ # QR code scanning feature
│ │ ├── camera/ # Live camera scanning
│ │ │ ├── intent/
│ │ │ ├── widgets/
│ │ │ ├── CameraScanScreen.kt
│ │ │ ├── CameraScanUIState.kt
│ │ │ └── CameraScanViewModel.kt
│ │ │
│ │ ├── storage/ # Scan from storage
│ │ │ ├── allimages/ # All storage images
│ │ │ │ ├── intent/
│ │ │ │ ├── widgets/
│ │ │ │ ├── AllStorageImagesScreen.kt
│ │ │ │ ├── AllStorageImagesUIState.kt
│ │ │ │ └── AllStorageImagesViewModel.kt
│ │ │ │
│ │ │ └── details/ # QR details after scanning
│ │ │ ├── intent/
│ │ │ ├── QrDetailsScreen.kt
│ │ │ ├── QrDetailsUIState.kt
│ │ │ └── QrDetailsViewModel.kt
│ │ │
│ │ └── widgets/
│ │ └── AskFromCameraOrStorageScreen.kt
│ │
│ └── util/ # Shared feature utilities
│
├── graph/ # NavGraph
│
├── util/ # App-wide utilities
│
├── MyApp.kt # Application class
│
res/ # Android resources
│ ├── layout/ 
│ ├── drawable/
│ ├── values/
│ └── mipmap/
│
AndroidManifest.xml
```


## ⚙️ Setup & Installation

### Requirements
- Android Studio Giraffe+
- JDK 17 (check with `java -version`)
- Gradle 8.14.3
- Android SDK

### Clone & Build
```bash
git clone https://github.com/[your-username]/[your-repo].git
cd [your-repo]
./gradlew assembleDebug
./gradlew test
./gradlew connectedAndroidTest

## Screenshots
![qr1](https://github.com/user-attachments/assets/5b5c0058-6d5f-4f57-b8b0-7ddcd4707eb2)
![qr2](https://github.com/user-attachments/assets/ddcbf3f9-a117-478e-af83-2b0d4f35f640)
![qr3](https://github.com/Tonistark92/QR-Code-Generator-Scanner/assets/86676102/832695b5-48dc-46fe-8433-afc480a4f6f5)
