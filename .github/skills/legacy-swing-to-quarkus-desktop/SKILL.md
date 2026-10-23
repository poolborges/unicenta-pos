---
name: legacy-swing-to-quarkus-desktop
description: "Migrate legacy Swing desktop applications to Quarkus desktop architecture. Use for replacing custom containers, BeanShell scripts, Velocity templates, UI wiring, and legacy runtime assumptions with Arc, QuickJS, Qute, and a maintainable modular structure."
argument-hint: "Describe the legacy Swing application, the current runtime/container, scripting/template usage, and the target Quarkus desktop architecture."
user-invocable: true
disable-model-invocation: false
---

# Legacy Swing to Quarkus Desktop Migration

## When to Use
- Modernize a legacy Swing or desktop Java application to a Quarkus-based runtime.
- Replace a proprietary or in-house dependency injection/container model with Quarkus Arc.
- Migrate custom scripting such as BeanShell to QuickJS.
- Replace Apache Velocity rendering with Qute templates.
- Break a monolithic Swing application into a clearer domain, services, and UI boundaries.
- Reduce technical debt while preserving user-visible behavior and business rules.

## Core Principles
- Preserve business behavior first; architecture refactoring comes second.
- Treat the Swing UI as a thin presentation layer, not the system of record.
- Move state, rules, and orchestration into explicit domain/application services.
- Replace runtime-specific magic with explicit Quarkus idioms: CDI, producers, events, configuration, and lifecycle.
- Prefer incremental migration with validation at each step over a risky big-bang rewrite.
- Keep the migration reversible and testable.

## Procedure

### 1. Baseline the Current System
- Inventory the Swing screens, business flows, and integration points.
- Document the current runtime lifecycle: startup sequence, container wiring, thread model, and resource management.
- Identify entry points, configuration, global services, and custom frameworks.
- Capture the current behavior in tests or scripted validations before any migration work begins.

### 2. Map the Legacy Architecture
- Separate UI concerns from domain logic, persistence, and infrastructure.
- Identify where the legacy container or runtime performs instantiation, lifecycle management, or lookup.
- Locate script execution, dynamic configuration, and templated rendering.
- Mark hidden dependencies such as singleton state, thread-local usage, reflection, and manual resource cleanup.

### 3. Define the Target Quarkus Desktop Shape
- Decide the business boundaries and module structure for the migrated app.
- Choose explicit Quarkus application services, configuration, persistence, and integration points.
- Define how Swing screens interact with the application layer and which objects are UI-only.
- Keep data and rules inside durable application/domain structures rather than in Swing components.

### 4. Replace the Legacy Container with Arc
- Map every custom registration, lookup, and lifecycle hook to CDI concepts.
- Replace ad-hoc singleton/service locator patterns with `@ApplicationScoped`, `@Singleton`, `@RequestScoped`, or producer methods as appropriate.
- Convert custom wiring to beans, producers, interceptors, events, and configuration properties.
- Ensure lifecycle and disposal semantics are explicit and deterministic.
- If a legacy component is stateful, keep that state behind a well-defined bean boundary instead of spreading it through the UI.

### 5. Extract and Harden the Domain and Application Layers
- Move business rules outside the Swing widgets and action listeners.
- Create use-case services for workflows that currently live inside buttons and dialogs.
- Keep the UI as a view of the state, not as the owner of the state.
- Validate invariants at the service layer and keep UI validation only as a thin interaction aid.

### 6. Migrate Scripting from BeanShell to QuickJS
- Inventory scripts, dynamic expressions, and logic fragments currently interpreted by BeanShell.
- Classify each script as: pure logic, UI behavior, data transformation, or configuration evaluation.
- Replace script execution with explicit Java wrappers and a fixed, safe QuickJS context.
- Preserve script contracts carefully: input objects, output values, and error handling semantics.
- Add safeguards for sandboxing, restricted access, and deterministic execution.
- Keep a migration table mapping old script entry points to new Java/QuickJS equivalents.

### 7. Migrate Templates from Apache Velocity to Qute
- Identify all templates used for text generation, reports, UI fragments, or configuration output.
- Convert Velocity constructs to Qute syntax, sections, and parameter binding.
- Replace implicit context objects with explicit model types and isolated namespaces.
- Split rendering logic from business rules so templates remain presentation-focused.
- Preserve formatting and conditional output with explicit tests for representative documents.

### 8. Decouple the Swing UI from the Runtime
- Replace direct global access patterns with injected services and explicit state models.
- Prefer event-driven updates and commands rather than deep object graphs in the UI.
- Move screen-level logic into controllers or presenters to keep `JFrame`/`JPanel` classes simpler.
- Keep the UI responsive by isolating long-running work behind background execution, task coordination, or async boundaries.

### 9. Validate Incrementally
- Run existing functional tests, smoke checks, and business validation after each migration slice.
- Verify edge cases in startup, shutdown, dialogs, state persistence, and display refresh.
- Confirm that legacy compile-time assumptions are no longer required at runtime.
- Keep migration checkpoints visible: container migration, script migration, template migration, UI decoupling.

### 10. Harden the New Runtime
- Add health/status checks, structured logging, configuration validation, and observability.
- Ensure resources are started and cleaned up in a predictable way.
- Audit access control, thread boundaries, and cross-module dependencies.
- Document the new architecture, migration decisions, and remaining technical debt.

## Decision Heuristics
- If the app relies on custom service lookup or lifecycle hooks, prioritize Arc migration before deeper refactoring.
- If the app uses BeanShell for dynamic logic, convert those flows to explicit Java services first and leave the script layer only where there is a real requirement for scripting.
- If templates are used for reporting or output rendering, move them to Qute and make the view model explicit.
- If Swing components own business state, extract that state into a domain/application model before adding Quarkus services.
- If a feature is still evolving quickly, favor a thin migration over a perfect architecture so teams can ship safely.

## Common Migration Patterns

### Legacy Container to Arc
- Replace custom bean factory and manual registration with CDI beans and producers.
- Replace manual lifecycle event wiring with Quarkus startup hooks and `@Observes` events.
- Keep the legacy object graph but move it behind application services and bean boundaries.

### BeanShell to QuickJS
- Map script variables to Java objects and plain data structures.
- Convert dynamic methods to explicit Java interfaces or adapter layers.
- Use QuickJS for execution but keep business rules outside the script engine when possible.
- Treat scripting as a compatibility layer, not as the primary platform logic.

### Apache Velocity to Qute
- Convert namespaces, templates, and macros into Qute templates and sections.
- Replace implicit context lookups with strongly typed view models.
- Use template composition and parameter binding to maintain readability and testability.

## Quality Criteria
- [ ] The legacy app still boots and behaves correctly on the target runtime.
- [ ] Custom container logic is replaced by Arc or explicit architecture boundaries.
- [ ] BeanShell or equivalent scripting has a clear migration path and safe execution model.
- [ ] Velocity templates have been ported to Qute or deliberately replaced with explicit rendering code.
- [ ] Swing widgets no longer own domain logic or business state.
- [ ] Services and use cases are tested independently of the UI.
- [ ] Logs, configuration, and lifecycle behavior are observable and stable.
- [ ] The codebase is easier to maintain than the original legacy architecture.

## Output Expectations
When this skill is used, it should produce one or more of the following:
- a migration plan for the legacy Swing desktop app to Quarkus
- a target architecture with Arc, domain/application services, and UI decoupling
- a mapping from legacy container patterns to CDI and Quarkus lifecycle
- a BeanShell-to-QuickJS migration strategy and safety model
- a Velocity-to-Qute conversion plan for templates and rendering
- a phased rollout plan with checkpoints, validation gates, and rollback considerations

## Example Prompts
- "Migrate this legacy Swing application to a Quarkus desktop architecture without breaking business flows."
- "Replace the in-house runtime container with Arc and keep the current startup behavior stable."
- "Convert the BeanShell scripting layer to QuickJS and preserve script behavior safely."
- "Port the Apache Velocity templates to Qute while keeping the generated output equivalent."
- "Refactor the Swing UI so domain logic and state live in application services instead of UI components."
- "Plan the incremental migration from legacy desktop Java to Quarkus with validation checkpoints."

## Related Customizations to Create Next
- a Quarkus desktop architecture prompt for Swing modernization and service boundaries
- an Arc migration checklist for replacing custom containers and lifecycle patterns
- a QuickJS migration instruction set for BeanShell compatibility and sandboxing
- a Qute template migration prompt for Velocity conversion and rendering parity
- a validation checklist for desktop application regression testing and rollout readiness
