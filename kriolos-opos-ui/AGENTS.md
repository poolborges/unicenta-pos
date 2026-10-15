# KriolOS Development Guidelines for AI Agents

## Core Principles
1. **Language & Documentation**:
   - All code, variable names, comments, and documentation (`.adoc`, `.md`) MUST be written in English.
   - Every class, interface, and record must include `@author KriolOS`.
   - Use clean, modern English Javadoc conforming to Java 17 standards (`@param`, `@return`, `@throws`, `@since 1.0.0`).

2. **Best Practices & Code Quality**:
   - **No Magic Numbers or Strings**: Declare explicit `public static final` or `private static final` constants.
   - **LAF Agnostic**: Components and visual effects must seamlessly adapt to any Swing Look-and-Feel (FlatLaf Light, FlatLaf Dark, Nimbus, System). Never hardcode theme colors.
   - **Self-Contained Packages**: The `com.openbravo.ui.animation` package must remain self-contained using `AnimationUtils`.

3. **Automated Testing**:
   - Write comprehensive unit tests whenever possible (JUnit 5 / AssertJ) validating all calculations, converters, builders, data-binding, and animation primitives.

4. **Runtime & Compatibility**:
   - Minimum baseline: **Java 17**.
   - Timing Framework 7.4.3 is used for fluid 60fps animations.
