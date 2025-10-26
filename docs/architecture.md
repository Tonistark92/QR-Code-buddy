```markdown
# 🏗️ Architecture Documentation

This project follows **Clean Architecture + MVI** with feature-based packaging.  
The diagrams below describe the relationships between layers and the main data flows.

---

## 🔑 High-Level UML Diagram

```mermaid
classDiagram
    class MediaRepository {
        <<interface>>
        +saveImage(bitmap)
    }

    class QrCodeGenerator {
        <<interface>>
        +generate(text): Bitmap
    }

    class QrCodeScanner {
        <<interface>>
        +scanFromCamera(): String
    }

    class QrCodeStorageAnalyzer {
        <<interface>>
        +analyzeFromStorage(image): String
    }

    class MediaRepositoryImpl {
        +saveImage(bitmap)
    }

    class QrCodeGeneratorImp {
        +generate(text): Bitmap
    }

    class QrCodeScannerImp {
        +scanFromCamera(): String
    }

    class QrCodeStorageAnalyzerImp {
        +analyzeFromStorage(image): String
    }

    class GenerateQRCodeViewModel {
        -state: GenerateQRCodeState
        +onEvent(event)
    }

    class CameraScanViewModel {
        -state: CameraScanUIState
        +onEvent(event)
    }

    class AllStorageImagesViewModel {
        -state: AllStorageImagesUIState
        +onEvent(event)
    }

    class QrDetailsViewModel {
        -state: QrDetailsUIState
        +onEvent(event)
    }

    MediaRepository <|.. MediaRepositoryImpl
    QrCodeGenerator <|.. QrCodeGeneratorImp
    QrCodeScanner <|.. QrCodeScannerImp
    QrCodeStorageAnalyzer <|.. QrCodeStorageAnalyzerImp

    GenerateQRCodeViewModel --> QrCodeGenerator
    CameraScanViewModel --> QrCodeScanner
    AllStorageImagesViewModel --> QrCodeStorageAnalyzer
    QrDetailsViewModel --> MediaRepository
```

---

## 📱 Sequence Diagrams

### QR Code Generation
```mermaid
sequenceDiagram
    User->>GenerateQRCodeScreen: Enter text + click Generate
    GenerateQRCodeScreen->>GenerateQRCodeViewModel: onEvent(Generate)
    GenerateQRCodeViewModel->>QrCodeGenerator: generate(text)
    QrCodeGenerator->>QrCodeGeneratorImp: Generate bitmap
    QrCodeGeneratorImp-->>GenerateQRCodeViewModel: Return Bitmap
    GenerateQRCodeViewModel-->>GenerateQRCodeScreen: Update state with bitmap
    GenerateQRCodeScreen-->>User: Show QR code
```

### Camera Scan
```mermaid
sequenceDiagram
    User->>CameraScanScreen: Open screen
    CameraScanScreen->>CameraScanViewModel: Request scan
    CameraScanViewModel->>QrCodeScanner: scanFromCamera()
    QrCodeScanner->>QrCodeScannerImp: Access CameraX
    QrCodeScannerImp-->>QrCodeScanner: Decoded text
    QrCodeScanner-->>CameraScanViewModel: Result
    CameraScanViewModel-->>CameraScanScreen: Update UI with result
    CameraScanScreen-->>User: Display scanned text
```

### Storage Image Scan
```mermaid
sequenceDiagram
    User->>AllStorageImagesScreen: Select image
    AllStorageImagesScreen->>AllStorageImagesViewModel: onEvent(ImageSelected)
    AllStorageImagesViewModel->>QrCodeStorageAnalyzer: analyzeFromStorage(image)
    QrCodeStorageAnalyzer->>QrCodeStorageAnalyzerImp: Decode QR from bitmap
    QrCodeStorageAnalyzerImp-->>QrCodeStorageAnalyzer: Return text
    QrCodeStorageAnalyzer-->>AllStorageImagesViewModel: Result
    AllStorageImagesViewModel-->>QrDetailsViewModel: Pass decoded text
    QrDetailsViewModel-->>QrDetailsScreen: Update state
    QrDetailsScreen-->>User: Show QR details
```

---