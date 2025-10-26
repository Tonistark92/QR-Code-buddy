# 🏗️ feature Document
---

## 🔑  State Diagram

```mermaid
stateDiagram
    [*] --> WaitingPermission
    WaitingPermission --> CameraPreview : PermissionGranted
    WaitingPermission --> PermissionDialog : PermissionDenied
    CameraPreview --> DisplayResult : OnQrDetected
    DisplayResult --> CameraPreview : ScanAgain
```

---