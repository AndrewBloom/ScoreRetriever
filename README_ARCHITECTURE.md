# Architecture Documentation

This document explains the architectural principles and patterns used in this Android application.

## SOLID Principles

### S - Single Responsibility Principle
Each class has ONE reason to change.

**In our implementation:**
- `CreditScoreApi` - only handles API calls
- `CreditScoreRepository` - only handles data access logic
- `GetCreditScoreUseCase` - only handles business logic
- `CreditScoreViewModel` - only handles UI state management
- Each component is focused on a single concern

### O - Open/Closed Principle
Open for extension, closed for modification.

**In our implementation:**
- `CoinLikeComponent` interface allows new implementations without changing existing code
- `ComponentFactory` can support new component types by adding cases, not modifying existing components
- Can add new use cases without touching repository

### L - Liskov Substitution Principle
Subtypes must be substitutable for their base types.

**In our implementation:**
- Any `CoinLikeComponent` implementation can replace another
- `PlaceholderComponent`, `Basic2DComponent`, `OpenGL3DComponent` all work interchangeably
- Repository interface can be swapped (e.g., mock for testing)

### I - Interface Segregation Principle
Clients shouldn't depend on interfaces they don't use.

**In our implementation:**
- `CreditScoreRepository` interface has only methods needed
- `CoinLikeComponent` interface is minimal (just render method)
- No "fat" interfaces with unused methods

### D - Dependency Inversion Principle
Depend on abstractions, not concretions.

**In our implementation:**
- ViewModel depends on `GetCreditScoreUseCase` interface (not implementation)
- UseCase depends on `CreditScoreRepository` interface (not implementation)
- Hilt provides concrete implementations at runtime
- UI depends on `CoinLikeComponent` abstraction (not specific implementation)

## Clean Architecture

### What is it?
Architecture pattern by Robert C. Martin (Uncle Bob) that separates code into concentric layers, where dependencies point INWARD (outer layers depend on inner, never the reverse).

### Layers (from inside out):

#### 1. Domain/Core Layer (innermost - no dependencies)
- Business logic and rules
- Domain models (pure Kotlin classes)
- Use cases (business operations)
- Repository interfaces (contracts)

**Our implementation:** `domain/` package with models, use cases, repository interfaces

#### 2. Data Layer (depends on Domain)
- Data sources (API, database, cache)
- Repository implementations
- DTOs (Data Transfer Objects)

**Our implementation:** `data/` package with API client, DTOs, repository implementation

#### 3. Presentation Layer (depends on Domain, NOT on Data)
- UI components
- ViewModels
- UI state

**Our implementation:** `presentation/` package with ViewModels, Composables, UI state

### Key Benefits:
- **Testable**: Domain logic has no Android dependencies
- **Flexible**: Can swap data sources, UI frameworks
- **Independent layers**: Each layer can be developed and tested separately
- **Business logic isolated**: Framework-agnostic core logic

### Dependency Rule:
```
Presentation Layer ──→ Domain Layer ←── Data Layer
    (UI/ViewModel)    (Business Logic)    (API/DB)
```

## MVVM (Model-View-ViewModel)

### What is it?
Architectural pattern for separating UI from business logic using a ViewModel as intermediary.

### Components:

#### 1. Model
**Our implementation:** Domain models (`CreditScore`), Repository, Use Cases
- Represents data and business logic
- Independent of UI

#### 2. View
**Our implementation:** Composable functions (`CreditScoreScreen`, `CoinLikeComponent` implementations)
- UI layer that displays data
- Observes ViewModel state
- Sends user actions to ViewModel

#### 3. ViewModel
**Our implementation:** `CreditScoreViewModel`
- Holds UI state (`StateFlow<CreditScoreUiState>`)
- Handles user actions
- Communicates with Model layer (via Use Cases)
- Survives configuration changes

### Data Flow:
```
User Action → View (Composable) → ViewModel → UseCase → Repository → API
                ↑                      ↓
                └── StateFlow ←────────┘
```

## How They Work Together

```
┌─────────────────────────────────────────────────────┐
│ PRESENTATION LAYER (View + ViewModel - MVVM)       │
│  - CreditScoreScreen (View - Composable)            │
│  - CreditScoreViewModel (StateFlow)                 │
│  - CoinLikeComponent interface (ISP, OCP, DIP)     │
├─────────────────────────────────────────────────────┤
│ DOMAIN LAYER (Model - Clean Architecture Core)     │
│  - CreditScore (domain model)                       │
│  - GetCreditScoreUseCase (SRP)                      │
│  - CreditScoreRepository interface (DIP)            │
├─────────────────────────────────────────────────────┤
│ DATA LAYER (Model - Clean Architecture)            │
│  - CreditScoreRepositoryImpl (SRP, LSP)             │
│  - CreditScoreApi (SRP)                             │
│  - DTOs (SRP)                                        │
└─────────────────────────────────────────────────────┘
```

## Project Structure

```
app/
├── data/
│   ├── api/
│   │   ├── CreditScoreApi.kt (Retrofit interface)
│   │   └── dto/ (Response DTOs)
│   ├── repository/
│   │   └── CreditScoreRepositoryImpl.kt
│   └── di/ (Hilt modules)
├── domain/
│   ├── model/
│   │   ├── CreditScore.kt (domain model)
│   │   └── Result.kt (sealed class)
│   ├── repository/
│   │   └── CreditScoreRepository.kt (interface)
│   └── usecase/
│       └── GetCreditScoreUseCase.kt
├── presentation/
│   ├── component/
│   │   ├── CoinLikeComponent.kt (interface)
│   │   ├── ComponentType.kt (enum)
│   │   ├── ComponentFactory.kt
│   │   └── impl/
│   │       └── PlaceholderComponent.kt (Phase 1)
│   ├── screen/
│   │   └── CreditScoreScreen.kt
│   ├── viewmodel/
│   │   └── CreditScoreViewModel.kt
│   └── state/
│       └── CreditScoreUiState.kt (sealed interface)
└── ui/theme/
```

## Technology Stack

- **Language**: Kotlin
- **UI Framework**: Jetpack Compose with Material 3
- **Dependency Injection**: Hilt
- **Networking**: Retrofit + OkHttp
- **Async**: Kotlin Coroutines + Flow
- **Testing**: JUnit, MockK, Turbine

## Summary

- ✅ **SOLID**: Every class follows all 5 principles
- ✅ **Clean Architecture**: 3 distinct layers with proper dependency direction
- ✅ **MVVM**: Clear separation of View (Compose) → ViewModel → Model (Domain/Data)
