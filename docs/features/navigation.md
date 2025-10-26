```markdown
# 🏗️ Navigation Document
---

## 🔑  State Diagram

```mermaid
stateDiagram
    [*] --> Idle
    Idle --> Loading : OnGenerateClicked
    Loading --> DisplayQR : QRGenerated
    DisplayQR --> Sharing : OnShareClicked
    DisplayQR --> Idle : OnTextChanged
```

---

