# QR Code Generator and Scanner

This Android application allows users to generate and scan QR codes. The app is built using Jetpack Compose for UI, Jetpack Navigation for app navigation, ZXing library for QR code generation and scanning, and CameraX for camera functionalities.

## Features

- Generate QR codes from text input
- Scan QR codes using the device's camera
- Modern UI built with Jetpack Compose
- Navigation between screens using Jetpack Navigation
- Deep link handling for shared qr coe image from other app

## Libraries Used

- [Jetpack Compose](https://developer.android.com/jetpack/compose) - For building native UI.
- [Jetpack Navigation](https://developer.android.com/guide/navigation) - For navigation between screens.
- [ZXing](https://github.com/zxing/zxing) - For generating and decoding QR codes.
- [CameraX](https://developer.android.com/training/camerax) - For camera functionalities.
  - Used `ImageAnalysis.Analyzer` for analyzing the camera frames in real-time to detect QR codes.

## Screenshots
![qr1](https://github.com/user-attachments/assets/5b5c0058-6d5f-4f57-b8b0-7ddcd4707eb2)
![qr2](https://github.com/user-attachments/assets/ddcbf3f9-a117-478e-af83-2b0d4f35f640)
![qr3](https://github.com/Tonistark92/QR-Code-Generator-Scanner/assets/86676102/832695b5-48dc-46fe-8433-afc480a4f6f5)
