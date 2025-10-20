# Score Retriever

A modern Android application that fetches and displays score data with elegant visualization. Built with Clean Architecture principles and Jetpack Compose.

## Architecture

### Clean Architecture + MVVM

The application follows **Clean Architecture** with three distinct layers, ensuring separation of concerns, testability, and maintainability:

**Presentation Layer**
- **Pattern**: MVVM (Model-View-ViewModel)
- **Components**: Jetpack Compose UI, ViewModels with StateFlow
- **Responsibility**: User interface, state management, user interactions
- **Key Feature**: Component abstraction pattern for swappable visualizations

**Domain Layer**
- **Components**: Use Cases, Domain Models, Repository Interfaces
- **Responsibility**: Business logic and rules
- **Benefits**: Pure Kotlin, no Android dependencies, easily testable
- **Models**: `Score`, `Result<T>`, `ErrorType` sealed classes

**Data Layer**
- **Components**: Repository implementations, API clients, DTOs
- **Responsibility**: Data fetching, caching, error handling
- **Features**: Retrofit integration, DTO-to-domain mapping, comprehensive error handling

### Key Architectural Decisions

**1. Unidirectional Data Flow**
- ViewModels expose immutable StateFlow
- UI observes state changes reactively
- User actions flow through ViewModel methods

**2. Error Handling Strategy**
- Domain-defined `ErrorType` sealed class (network, server, validation, etc.)
- Repository catches exceptions and maps to `ErrorType`
- ViewModel translates to user-friendly localized messages
- Presentation layer only handles display logic

**3. Component Strategy Pattern**
- `CoinLikeComponent` interface for score visualizations
- `ComponentFactory` creates implementations
- Easy to swap between different visualization types
- Future-ready for 2D, 2.5D, and 3D implementations

**4. Threading Model**
- Retrofit suspend functions use OkHttp's thread pool automatically
- ViewModels use `viewModelScope` (defaults to Main dispatcher)
- Flows provide reactive, lifecycle-aware data streams

## Technology Stack

### Core Technologies
- **Kotlin** - Modern, concise, null-safe language
- **Jetpack Compose** - Declarative UI framework
- **Material 3** - Modern Material Design components

### Architecture Components
- **Hilt** - Dependency injection framework
- **ViewModel** - UI state management
- **StateFlow** - Reactive state holder
- **Lifecycle** - Lifecycle-aware components

### Networking & Data
- **Retrofit** - Type-safe HTTP client
- **OkHttp** - HTTP client with interceptors
- **Gson** - JSON serialization/deserialization

### Asynchronous Programming
- **Kotlin Coroutines** - Structured concurrency
- **Flow** - Reactive streams
- **suspend functions** - Async operations

### Testing (72 tests total)
- **JUnit 4** - Test framework
- **MockK** - Kotlin-first mocking library
- **Turbine** - Flow testing utilities
- **Compose UI Test** - UI testing framework
- **kotlinx-coroutines-test** - Coroutine testing utilities

## UI States

The application implements three primary UI states with consistent positioning and modern design:

### 1. Loading State
**When**: Initial app launch, fetching score data

**Visual Design**:
- Large circular progress indicator and text

**Purpose**: Provides visual feedback during network operations

### 2. Success State
**When**: Score data successfully retrieved

**Visual Design**:
- Circular donut progress indicator 

**Components**:
- Uses Strategy pattern for swappable visualizations
- Current: `PlaceholderComponent` with semitransparent background
- Future: 2D, 2.5D, 3D implementations ready via `ComponentFactory`

### 3. Error State
**When**: Network failure, server error, or data validation issues

**Visual Design**:
- Semi-transparent dark container with Error message and Rounded retry button 

**Error Types Handled**:
- Network errors (connection issues)
- Server errors (4xx, 5xx responses)
- Data format errors (invalid JSON)
- Validation errors (invalid score data)
- Unexpected errors (graceful fallback)

### State Transitions
```
App Launch → Loading State
    ↓
Network Call
    ↓
Success? → Success State (display score)
    ↓
Failure? → Error State (show message + retry button)
    ↓
Retry → Loading State (retry flow)
```

## Project Structure

```
app/src/main/java/com/scoreretriever/
├── data/
│   ├── api/              # Retrofit API interfaces and DTOs
│   ├── repository/       # Repository implementations
│   └── di/               # Hilt dependency injection modules
├── domain/
│   ├── model/            # Domain models (Score, Result, ErrorType)
│   ├── repository/       # Repository interfaces
│   └── usecase/          # Business logic use cases
└── presentation/
    ├── component/        # Score visualization components
    ├── screen/           # Composable screens
    ├── viewmodel/        # ViewModels
    └── state/            # UI state classes

app/src/test/             # Unit tests (65 tests)
app/src/androidTest/      # Instrumented UI tests (7 tests)
```

## Getting Started

### Prerequisites
- Android Studio Hedgehog or newer
- JDK 17 or higher
- Android SDK 26+ (target SDK 35)

### Build & Run
```bash
# Clone the repository
git clone <repository-url>

# Open in Android Studio
# Build and run on device or emulator

# Or via command line:
./gradlew assembleDebug
./gradlew installDebug
```

### Run Tests
```bash
# Unit tests
./gradlew testDebugUnitTest

# Instrumented tests (requires device/emulator)
./gradlew connectedDebugAndroidTest

# All tests
./gradlew test connectedAndroidTest
```

## API Endpoint

```
GET https://android-interview.s3.eu-west-2.amazonaws.com/endpoint.json
```

Response format:
```json
{
  "scoreInfo": {
    "score": 514,
    "maxScoreValue": 700
  }
}
```

## Documentation

Detailed documentation available in `AI-Docs/`:
- **DEVELOPMENT_LOG.md** - Complete development history and decisions
- **README_ARCHITECTURE.md** - Detailed architecture documentation
- **BUILD_VERIFICATION.md** - Build and test verification steps

---

**License**: MIT
**Minimum SDK**: 31 (Android 12.0)
**Target SDK**: 35 (Android 15)
