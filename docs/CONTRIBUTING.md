# Contributing to QR Code Project

Thank you for contributing! Please follow these guidelines to help maintain a high-quality codebase.

---

## 1. Coding Style
- Follow **Kotlin coding conventions**:
    - Use `camelCase` for variables and functions.
    - Use `PascalCase` for classes and objects.
    - Keep line length under 120 characters.
    - Use meaningful variable and function names.
    - Use `val` whenever possible, minimize `var`.
- Run formatting checks before committing:
  ```bash
  ./gradlew spotlessApply
