# KriolOS POS UI — Agent Coding Standards & Rules

## 1. Language & Documentation
- **English Everywhere**: All generated code, class names, method names, variable names, code comments, logs, and documentation (`.adoc`, `.md`) MUST strictly be written in standard, professional English, even when user prompts or discussions are conducted in Portuguese or other languages.
- **Author Tag**: Every Java class, interface, enum, and record MUST include the `@author KriolOS` Javadoc tag.
- **Modern Javadoc**: Include comprehensive English Javadoc on all public and protected types, methods, and constants, conforming to Java 17 standards (`@param`, `@return`, `@throws`, `@since 1.0.0`).

## 2. Best Coding Practices
- **Constants Over Magic Values**: Always use `public static final` or `private static final` constants. Avoid magic numbers (e.g. durations, alphas, radii, offsets, dimension limits) and magic string literals (e.g. UIManager keys, format patterns).
- **Self-Contained Packages**: The `com.openbravo.ui.animation` package must remain completely self-contained. All common color analysis, contrast calculation, luminance checks, and mathematical easing must reside in `com.openbravo.ui.animation.AnimationUtils`.
- **Look-and-Feel (LAF) Agnostic**:
  - Components and animations MUST be 100% agnostic to Look-and-Feel (FlatLaf Light, FlatLaf Dark, Nimbus, Metal, System).
  - Never hardcode fixed dark or light themes or assume a specific LAF.
  - Query `UIManager.getColor(...)` dynamically and provide clean runtime support for `updateUI()` switches.
- **Java 17 Baseline**: Use modern Java features (records, enhanced `switch`, pattern matching `instanceof`, standard stream APIs, `java.time`, etc.).

## 3. Automated Testing
- **Unit Tests Everywhere**: Write comprehensive unit tests whenever possible (using JUnit 5 / AssertJ) covering mathematical interpolations, color luminance/contrast calculations, builders, data-binding pipelines, and component behaviors.
