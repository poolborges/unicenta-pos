---
name: quarkus-ddd-modular-monolith
description: "Design and implement Java 17 Quarkus applications using Domain-Driven Design and modular monolith architecture. Use for bounded-context analysis, domain modeling, project structure, APIs, persistence, architecture reviews, and implementation planning."
argument-hint: "Describe the business capability, domain boundaries, constraints, and target deployment"
user-invocable: true
disable-model-invocation: false
---

# Quarkus, DDD, and Modular Monolith Architecture

## When to Use
- Design a new Quarkus service or module from a business problem.
- Refactor an existing application toward a modular monolith.
- Define bounded contexts, aggregates, entities, and value objects.
- Decide package structure, persistence strategy, and application services.
- Review architecture for cohesion, coupling, testability, and delivery speed.
- Implement Java 17 business logic using Quarkus patterns and DDD principles.

## Core Principles
- Start from the business language, not from database tables or frameworks.
- Keep each module cohesive and loosely coupled.
- Model invariants and business rules in the domain, not in controllers or repositories.
- Separate domain logic from infrastructure, APIs, and orchestration.
- Prefer explicit boundaries and clear ownership over clever abstractions.
- Design for incremental delivery, operational clarity, and measurable business value.

## Procedure

### 1. Clarify the Business Outcome
- Identify the user problem, business capability, and success criteria.
- Capture functional requirements, non-functional constraints, and business rules.
- Define the target deployment shape: monolith, module boundaries, external integrations, and expected scale.
- Determine whether the work is greenfield, modernization, or incremental refactoring.

### 2. Define the Ubiquitous Language
- Translate business concepts into a shared terminology.
- Describe key concepts in domain terms: order, payment, inventory, customer, shipment, etc.
- Capture aliases, synonyms, and terminology conflicts across teams.
- Use the domain vocabulary consistently in code names, APIs, tests, and documentation.

### 3. Identify Bounded Contexts and Responsibilities
- Split the system by business capability, not by technical layers.
- Group domain concepts by ownership and consistency boundaries.
- Identify contexts with different business semantics or lifecycle rules.
- Decide whether cross-context interactions are synchronous, asynchronous, or event-driven.

### 4. Design the Domain Model
- Identify aggregates, entities, value objects, and domain services.
- Explicitly model invariants, state transitions, and business constraints.
- Keep aggregates small and consistent with transactional boundaries.
- Make domain events visible when state changes matter across contexts.
- Avoid mixing persistence logic or HTTP concerns into the domain model.

### 5. Define the Application Layer
- Create application services or use-case orchestration for workflows.
- Keep commands and queries explicit and task-oriented.
- Use ports for persistence, messaging, and external systems.
- Put transaction boundaries at the application or use-case layer.
- Ensure use cases are easy to test without starting the whole application stack.

### 6. Choose the Architectural Shape
- Use a modular monolith when the team wants strong cohesion with low operational complexity.
- Define modules or feature packages around business capabilities, not technical roles.
- Enforce dependency direction: domain -> application -> infrastructure -> interface adapters.
- Avoid circular dependencies; if they appear, revisit boundaries.
- Keep integration points explicit through interfaces and adapters.

### 7. Design Quarkus-Specific Implementation Details
- Use CDI, RESTEasy, and Quarkus-native conventions for application surfaces.
- Prefer lightweight, explicit configuration and environment-driven integration.
- Design repository interfaces and use data-access adapters that isolate persistence details.
- Choose a persistence strategy that fits the aggregate structure: JPA/Hibernate, Panache, or repository abstractions.
- Keep infrastructure concerns outside the domain model and use-case logic.

### 8. Define APIs and Contracts
- Define command and query contracts in terms of business intent.
- Make DTOs and REST resources thin adapters, not domain containers.
- Prefer clear validation rules and explicit error semantics.
- Keep API responses consistent with the business language and operational contract.
- Ensure contract changes are deliberate and versioned when necessary.

### 9. Plan for Quality and Delivery
- Design tests around business behavior: domain tests, application/use-case tests, and integration tests.
- Add architecture checks to preserve module boundaries and dependency rules.
- Keep operational concerns visible: observability, health checks, tracing, logging, and security.
- Break work into small, releasable slices aligned with business capabilities.
- Favor iterative validation over large speculative designs.

## Decision Heuristics
- If the domain is rich and complex, favor DDD with explicit bounded contexts and aggregates.
- If the team is small or the product is still evolving, use a modular monolith before splitting into microservices.
- If the business rule is central to the aggregate, keep it in the domain model.
- If the concern is external system integration, isolate it behind ports/adapters.
- If the dependency flow is hard to reason about, redesign the module boundaries before adding more code.

## Architecture Review Checklist
- [ ] The business language is clear and used consistently.
- [ ] Bounded contexts are identified and ownership is explicit.
- [ ] Aggregates and invariants are modeled around business rules.
- [ ] Domain logic is not mixed with infrastructure or HTTP concerns.
- [ ] Modules have a clear purpose and stable dependency direction.
- [ ] Persistence and external integrations are behind explicit adapters.
- [ ] The application layer coordinates use cases without becoming a dumping ground.
- [ ] Testing strategy covers business behavior and integration boundaries.
- [ ] Security, observability, and reliability are built into the design.
- [ ] The architecture can evolve without forcing a rewrite.

## Output Expectations
When this skill is used, it should produce one or more of the following:
- a domain model with aggregates, entities, value objects, and invariants
- bounded-context and module boundaries for a modular monolith
- a Quarkus project structure aligned with DDD principles
- application/use-case flows and command/query design
- technology choices, persistence approach, and integration patterns
- an implementation plan with realistic milestones and quality gates

## Example Prompts
- "Design a modular monolith for an e-commerce domain using Quarkus and Java 17."
- "Map the bounded contexts for a retail fulfillment system and propose the module structure."
- "Create a DDD domain model for billing and invoicing with clear aggregates and invariants."
- "Refactor this service toward Quarkus + DDD while preserving current APIs and data contracts."
- "Review this architecture for coupling, boundaries, and Java 17 implementation quality."

## Related Customizations to Create Next
- a Quarkus backend architecture prompt for API design and security defaults
- a DDD review checklist for domain modeling and aggregate boundaries
- a Java 17 coding standards instruction set for Quarkus projects
- a modular monolith dependency policy to enforce package and layer boundaries
- a project template for Quarkus REST modules with DDD folder structure
