# Credit Score Android App - Phase 1

A production-grade Android application that displays credit score information with a modular, extensible component architecture. This project is being developed in multiple phases, with each phase adding increasingly sophisticated visualization components.

## Overview

This app fetches credit score data from a REST API and displays it using interchangeable visualization components. The architecture is designed to support multiple implementations of the "CoinLikeComponent" - from simple 2D displays to advanced 3D glassmorphic effects.

**Current Status**: Phase 1 - Skeleton App Complete ✅

## Features

### Phase 1 (Current)
- ✅ Fetches credit score from API endpoint
- ✅ Displays score using placeholder component (circular progress)
- ✅ Handles loading, success, and error states
- ✅ Component type selector (UI framework for future implementations)
- ✅ Production-grade architecture with comprehensive testing setup
- ✅ Material 3 design with dynamic theming

### Future Phases
- **Phase 2**: Comprehensive testing suite
- **Phase 3**: Basic 2D donut chart implementation
- **Phase 4**: Enhanced 2.5D glassmorphic coin with gestures
- **Phase 5**: OpenGL 3D coin with advanced effects

## Technical Stack

- **Language**: Kotlin
- **UI Framework**: Jetpack Compose with Material 3
- **Architecture**: Clean Architecture + MVVM
- **Dependency Injection**: Hilt
- **Networking**: Retrofit + OkHttp
- **Async**: Kotlin Coroutines + Flow
- **Testing**: JUnit, MockK, Turbine (to be implemented in Phase 2)

## Architecture

This project follows **Clean Architecture** principles with clear separation of concerns:

```
┌─────────────────────────────────────────┐
│ Presentation Layer (MVVM)               │
│  - Composables                          │
│  - ViewModel + StateFlow                │
│  - Component Abstraction                │
├─────────────────────────────────────────┤
│ Domain Layer (Business Logic)          │
│  - Use Cases                            │
│  - Domain Models                        │
│  - Repository Interfaces                │
├─────────────────────────────────────────┤
│ Data Layer (Data Sources)              │
│  - Repository Implementation            │
│  - API Client (Retrofit)                │
│  - DTOs                                 │
└─────────────────────────────────────────┘
```

### Key Design Patterns

1. **MVVM (Model-View-ViewModel)**
   - Clear separation of UI and business logic
   - StateFlow for reactive state management
   - Lifecycle-aware data flow

2. **Repository Pattern**
   - Abstracts data sources
   - Single source of truth
   - Easy to test and mock

3. **Strategy Pattern (CoinLikeComponent)**
   - Multiple interchangeable implementations
   - Runtime component switching
   - Open for extension, closed for modification

4. **Dependency Injection**
   - Hilt for compile-time DI
   - Easy testing with mock implementations
   - Loosely coupled components

### SOLID Principles

The codebase strictly follows SOLID principles:

- **S**ingle Responsibility: Each class has one reason to change
- **O**pen/Closed: Open for extension via interfaces, closed for modification
- **L**iskov Substitution: All CoinLikeComponent implementations are interchangeable
- **I**nterface Segregation: Minimal, focused interfaces
- **D**ependency Inversion: Depends on abstractions, not concretions

See [README_ARCHITECTURE.md](README_ARCHITECTURE.md) for detailed architecture documentation.

## Project Structure

```
app/src/main/java/com/creditscore/
├── data/
│   ├── api/
│   │   ├── CreditScoreApi.kt
│   │   └── dto/
│   │       ├── CreditReportInfoDto.kt
│   │       └── CreditScoreResponseDto.kt
│   ├── repository/
│   │   └── CreditScoreRepositoryImpl.kt
│   └── di/
│       ├── NetworkModule.kt
│       └── RepositoryModule.kt
├── domain/
│   ├── model/
│   │   ├── CreditScore.kt
│   │   └── Result.kt
│   ├── repository/
│   │   └── CreditScoreRepository.kt
│   └── usecase/
│       └── GetCreditScoreUseCase.kt
├── presentation/
│   ├── component/
│   │   ├── CoinLikeComponent.kt (interface)
│   │   ├── ComponentType.kt
│   │   ├── ComponentFactory.kt
│   │   └── impl/
│   │       └── PlaceholderComponent.kt
│   ├── screen/
│   │   └── CreditScoreScreen.kt
│   ├── viewmodel/
│   │   └── CreditScoreViewModel.kt
│   └── state/
│       └── CreditScoreUiState.kt
├── ui/theme/
│   ├── Color.kt
│   ├── Type.kt
│   └── Theme.kt
├── ScoreRetrieverApp.kt
└── MainActivity.kt
```

## Setup & Running

### Prerequisites

- Android Studio Ladybug | 2024.2.1 or later
- JDK 11 or later
- Android SDK with API level 26+ (minimum) and 35 (target)

### Steps

1. Clone the repository:
   ```bash
   git clone <repository-url>
   cd DriveScore
   ```

2. Open project in Android Studio:
   - File → Open → Select the project directory
   - Wait for Gradle sync to complete

3. Run the app:
   - Click "Run" button or press Shift+F10
   - Select a device or emulator (API 26+)

The app will:
- Fetch credit score data from the API
- Display loading state
- Show the credit score in a circular component
- Allow switching between component types (only Placeholder functional in Phase 1)

## API Endpoint

The app fetches data from:
```
https://android-interview.s3.eu-west-2.amazonaws.com/endpoint.json
```

Response structure:
```json
{
  "creditReportInfo": {
    "score": 514,
    "maxScoreValue": 700,
    ...
  }
}
```

## Testing

**Note**: Comprehensive testing will be implemented in Phase 2.

Testing strategy includes:
- Unit tests for ViewModels
- Unit tests for Repositories
- Unit tests for Use Cases
- Unit tests for Domain models
- Composable UI tests

## Development Roadmap

### ✅ Phase 1: Skeleton App (Completed)
- Clean Architecture setup
- API integration
- Basic UI with placeholder component
- State management
- Component abstraction framework

### 🔄 Phase 2: Testing (Next)
- Unit tests for all layers
- ViewModel tests with StateFlow
- Repository tests with mocked API
- UI tests for different states
- Test coverage reporting

### 📋 Phase 3: Basic 2D Component
- 2D donut chart implementation
- Canvas-based rendering
- Smooth animations
- Percentage-based fill

### 📋 Phase 4: Enhanced 2.5D Component
- Glassmorphic effect (backdrop blur)
- Gesture-based rotation
- Light reflections
- Semi-transparent overlays

### 📋 Phase 5: OpenGL 3D Component
- OpenGL ES implementation
- Full 3D rendering
- Advanced lighting
- Complex animations

## Technical Decisions

### Why Clean Architecture?
- **Testability**: Each layer can be tested independently
- **Flexibility**: Easy to swap implementations (e.g., different data sources)
- **Maintainability**: Clear boundaries and responsibilities
- **Scalability**: Easy to add features without affecting existing code

### Why Jetpack Compose?
- Modern declarative UI framework
- Less boilerplate than XML layouts
- Powerful state management
- Better performance with recomposition
- Future-proof (recommended by Google)

### Why Hilt?
- Compile-time dependency injection
- Less boilerplate than Dagger
- Android-specific optimizations
- Built-in ViewModel support
- Easy testing with mock modules

### Why Flow over RxJava?
- Native Kotlin coroutines support
- Simpler, more intuitive API
- Better performance
- No additional dependencies
- Suspend function integration

### Why Strategy Pattern for Components?
- Supports multiple visualization approaches
- Easy to add new implementations
- Testable in isolation
- Runtime switching capability
- Follows Open/Closed principle

## Known Limitations (Phase 1)

1. **Component implementations**: Only Placeholder component is functional
   - Other component types return placeholder as fallback
   - Will be implemented in later phases

2. **Pull-to-refresh**: Framework present but not implemented
   - Can manually retry on error

3. **Offline support**: No caching mechanism
   - Future enhancement for offline viewing

4. **Icon assets**: Using default launcher icons
   - Custom icons to be added

## License

This project is part of a technical assessment and is for educational purposes.

## Contact

For questions or issues, please create an issue in the repository.

---

**Note**: This is Phase 1 of a multi-phase development project. More features and sophisticated implementations will be added in subsequent phases.
