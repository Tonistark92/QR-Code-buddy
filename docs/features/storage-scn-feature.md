# 🏗️ feature Document
---

## 🔑  State Diagram

```mermaid
stateDiagram
    [*] --> Idle
    Idle --> Loading : OnImageSelected
    Loading --> DisplayResult : OnQrDetected
    Loading --> NoQrFound : OnNoQrFound
    NoQrFound --> Idle : Retry
```

---