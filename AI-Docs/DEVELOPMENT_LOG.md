# Development Log - Score Retriever App

## Project Overview
Android app that fetches and displays score data using Clean Architecture + MVVM pattern.

**Tech Stack:**
- Kotlin + Jetpack Compose
- Hilt (Dependency Injection)
- Retrofit + OkHttp (Networking)
- Coroutines + Flow (Async)
- Material 3 Design
- JUnit + MockK + Turbine (Testing)

**Architecture:** Clean Architecture with 3 layers:
- **Presentation**: ViewModels, Composables, UI State
- **Domain**: Use Cases, Domain Models, Repository Interfaces
- **Data**: Repository Implementation, API Client, DTOs

---

## Development Timeline

### Initial Setup
- Created project with Android Studio
- Package: `com.scoreretriever`
- Min SDK: 26, Target SDK: 35
- App Name: "Score Retriever"

### Phase 1: Skeleton App ✅ (Completed)

**Commit 1: Initial commit - Phase 1 Skeleton**
- Implemented Clean Architecture structure
- Created all layers with proper separation
- API endpoint: `https://android-interview.s3.eu-west-2.amazonaws.com/endpoint.json`

**Key Components Created:**

**Domain Layer:**
- `Score.kt` - Domain model with validation (score, maxScore, percentage)
- `Result.kt` - Sealed class for Success/Error states
- `ErrorType.kt` - Sealed class for different error types
- `ScoreRepository.kt` - Repository interface
- `GetScoreUseCase.kt` - Use case for fetching scores

**Data Layer:**
- `ScoreApi.kt` - Retrofit interface
- `ScoreInfoDto.kt` - DTO for score info
- `ScoreResponseDto.kt` - DTO for API response with `toDomainModel()`
- `ScoreRepositoryImpl.kt` - Repository implementation with error handling
- `NetworkModule.kt` - Hilt module for Retrofit/OkHttp
- `RepositoryModule.kt` - Hilt module for repository binding

**Presentation Layer:**
- `ScoreViewModel.kt` - ViewModel with StateFlow
- `ScoreUiState.kt` - Sealed interface (Loading, Success, Error)
- `ScoreScreen.kt` - Main composable screen
- `CoinLikeComponent.kt` - Component interface (Strategy pattern)
- `PlaceholderComponent.kt` - Phase 1 implementation
- `ComponentType.kt` - Enum for component types
- `ComponentFactory.kt` - Factory for creating components

**Error Handling:**
- IOException → NetworkError
- HttpException → ServerError
- JsonSyntaxException → DataFormatError
- IllegalArgumentException → ValidationError
- Other Exception → UnexpectedError
- ViewModel maps ErrorType to localized strings

**Refactoring - String Externalization:**

**Commit 2: Refactor - File Renames**
- Renamed all files from `CreditScore*` to `Score*` using `git mv`
- 100% git similarity tracking preserved

**Commit 3: Refactor - Content Changes**
- Updated all class names, variables, functions
- Removed "credit" references throughout codebase
- Updated string resources:
  - `credit_score_label` → `score_label`
  - `loading_credit_score` → `loading_score`
- Preserved API field names (match JSON structure)
- Build: ✅ SUCCESSFUL

**Key Architectural Decisions:**
1. ErrorType in domain layer (no Android dependencies)
2. ViewModel maps ErrorType to localized strings (presentation layer)
3. Repository handles all error types and emits ErrorType
4. Clean separation: domain/data layers have no string resources

### Phase 2: Comprehensive Testing ✅ (Completed)

**Commit 4: Phase 2 - Testing Suite**

**Test Coverage: 65 unit tests**

**Domain Model Tests (32 tests):**
- `ScoreTest.kt` - 13 tests
  - Valid score creation
  - Percentage calculation (0.0 to 1.0)
  - Validation (negative scores, zero maxScore, score > maxScore)
  - Edge cases (0/1, 700/700)
  - Data class functions (copy, equals, toString)

- `ResultTest.kt` - 10 tests
  - Success state with data
  - Error state with ErrorType
  - Helper properties (isSuccess, isError)
  - getOrNull() behavior
  - Pattern matching in when expressions

- `ErrorTypeTest.kt` - 9 tests
  - All error type variants (Network, Server, DataFormat)
  - Data types (ValidationError, UnexpectedError)
  - Equality and pattern matching

**Data Layer Tests (10 tests):**
- `ScoreRepositoryImplTest.kt` - 10 tests
  - Successful API call → Success with mapped domain model
  - IOException → NetworkError
  - HttpException → ServerError
  - JsonSyntaxException → DataFormatError
  - Invalid domain data → ValidationError (e.g., score > maxScore)
  - Other exceptions → UnexpectedError
  - DTO-to-domain mapping correctness
  - Flow emission behavior
  - Used MockK for API mocking
  - Used Turbine for Flow testing

**Domain Use Case Tests (8 tests):**
- `GetScoreUseCaseTest.kt` - 8 tests
  - Success/Error propagation from repository
  - All error types (Network, Server, DataFormat, Validation, Unexpected)
  - Repository method invocation
  - Multiple invocations
  - Flow collection

**Presentation Layer Tests (18 tests):**
- `ScoreViewModelTest.kt` - 18 tests
  - Initial state is Loading
  - Success state emission with correct score
  - Error state emissions for all ErrorType variants
  - Error message mapping (ErrorType → localized string)
  - Component type selection
  - Retry functionality
  - StateFlow behavior
  - Loading → Success/Error transitions
  - Used TestDispatcher for coroutine testing
  - Used Turbine for StateFlow testing
  - Mocked Application context for string resources

**UI/Composable Tests (9 tests):**
- `ScoreScreenTest.kt` - 9 instrumented tests
  - Loading state shows progress indicator
  - Success state displays score and "out of X"
  - Component type selector shows all options
  - Clicking component type calls ViewModel
  - Error state shows "Oops!" and error message
  - Retry button interaction
  - Different score values display correctly
  - Used Compose UI Test framework
  - MockK for ViewModel mocking

**Testing Technologies:**
- JUnit 4 - Test framework
- MockK - Mocking (better than Mockito for Kotlin)
- Turbine - Flow/StateFlow testing
- kotlinx-coroutines-test - TestDispatcher, runTest
- Compose UI Test - Instrumented UI testing

**Build Result:** ✅ BUILD SUCCESSFUL - 65 tests, 0 failures

---

## Current Project State

### File Structure
```
app/src/main/java/com/scoreretriever/
├── data/
│   ├── api/
│   │   ├── ScoreApi.kt
│   │   └── dto/
│   │       ├── ScoreInfoDto.kt
│   │       └── ScoreResponseDto.kt
│   ├── repository/
│   │   └── ScoreRepositoryImpl.kt
│   └── di/
│       ├── NetworkModule.kt
│       └── RepositoryModule.kt
├── domain/
│   ├── model/
│   │   ├── Score.kt
│   │   ├── Result.kt
│   │   └── ErrorType.kt
│   ├── repository/
│   │   └── ScoreRepository.kt
│   └── usecase/
│       └── GetScoreUseCase.kt
├── presentation/
│   ├── component/
│   │   ├── CoinLikeComponent.kt
│   │   ├── ComponentType.kt
│   │   ├── ComponentFactory.kt
│   │   └── impl/
│   │       └── PlaceholderComponent.kt
│   ├── screen/
│   │   └── ScoreScreen.kt
│   ├── viewmodel/
│   │   └── ScoreViewModel.kt
│   └── state/
│       └── ScoreUiState.kt
├── ui/theme/
│   ├── Color.kt
│   ├── Type.kt
│   └── Theme.kt
├── MainActivity.kt
└── ScoreRetrieverApp.kt

app/src/test/java/com/scoreretriever/
├── domain/
│   ├── model/
│   │   ├── ScoreTest.kt
│   │   ├── ResultTest.kt
│   │   └── ErrorTypeTest.kt
│   └── usecase/
│       └── GetScoreUseCaseTest.kt
├── data/
│   └── repository/
│       └── ScoreRepositoryImplTest.kt
└── presentation/
    └── viewmodel/
        └── ScoreViewModel Test.kt

app/src/androidTest/java/com/scoreretriever/
└── presentation/
    └── screen/
        └── ScoreScreenTest.kt
```

### Git Commit History
```
7db668e Phase 2: Comprehensive Testing Suite
8cbc222 Refactor: Update all CreditScore references to Score (content changes)
652e905 Refactor: Rename CreditScore to Score (file renames only)
1639714 Initial commit: Phase 1 - Skeleton App Complete
```

---

## Key Technical Decisions & Patterns

### 1. Threading Strategy
- **Retrofit**: Uses suspend functions, automatically switches to OkHttp thread pool
- **ViewModel**: `viewModelScope.launch` defaults to Dispatchers.Main
- **Repository**: No explicit `withContext(Dispatchers.IO)` needed (Retrofit handles it)
- **Conclusion**: Current implementation is correct and efficient

### 2. Error Handling Architecture
- **Domain Layer**: Defines `ErrorType` sealed class (no Android dependencies)
- **Data Layer**: Catches exceptions, maps to `ErrorType`, emits `Result.Error`
- **Presentation Layer**: ViewModel maps `ErrorType` to localized strings
- **Benefits**: Clean separation, testable, no Android deps in domain/data

### 3. Validation Strategy
- **Domain Model**: `Score` has `init` block with `require()` statements
- **Repository**: Catches `IllegalArgumentException` → `ValidationError`
- **ViewModel**: Maps to user-friendly message
- **Result**: Validation errors are caught and handled gracefully

### 4. Component Strategy Pattern
- **Interface**: `CoinLikeComponent`
- **Factory**: `ComponentFactory` creates implementations
- **Enum**: `ComponentType` with string resource references
- **Current**: Only `PlaceholderComponent` implemented
- **Future**: BASIC_2D, ENHANCED_2D, OPENGL_3D (Phases 3-5)

---

## Next Steps (Future Phases)

### Phase 3: Basic 2D Component (Not Started)
- Implement `Basic2DComponent.kt`
- 2D donut chart with Canvas API
- Smooth animations
- Percentage-based arc rendering
- Match wireframe from PDF spec

### Phase 4: Enhanced 2.5D Component (Not Started)
- Implement `Enhanced2DComponent.kt`
- Glassmorphic effect (backdrop blur)
- Gesture-based rotation
- Light reflections
- Semi-transparent overlays

### Phase 5: OpenGL 3D Component (Not Started)
- Implement `OpenGL3DComponent.kt`
- OpenGL ES implementation
- Full 3D rendering
- Advanced lighting effects
- Complex animations

### Potential Additions
- **Hilt Integration Tests**: Test with real dependency injection
- **DTO Mapping Tests**: Ensure API responses map correctly
- **Component Rendering Tests**: Test individual composables
- **Pull-to-refresh**: Add swipe-to-refresh functionality
- **Offline support**: Cache scores locally
- **Test Coverage Reporting**: Jacoco or Kover

---

## Important Notes

### Questions & Answers

**Q: What happens if Score validation fails?**
A: The `init` block throws `IllegalArgumentException`, caught by repository, mapped to `ValidationError`, shown to user as localized error message.

**Q: Is threading handled correctly?**
A: Yes. Retrofit suspend functions use OkHttp's thread pool. No need for explicit `withContext(Dispatchers.IO)`.

**Q: Why MockK instead of Mockito?**
A: MockK is better for Kotlin:
- Native Kotlin support
- Supports suspend functions natively
- Better syntax for Kotlin idioms
- Industry standard for Kotlin testing

### Build Commands
```bash
# Compile
./gradlew compileDebugKotlin

# Run unit tests
./gradlew test

# Run instrumented tests (requires device/emulator)
./gradlew connectedAndroidTest

# Clean build
./gradlew clean build
```

### Testing Notes
- All 65 unit tests pass
- MockK used for mocking dependencies
- Turbine used for Flow/StateFlow testing
- TestDispatcher used for coroutine testing
- Instrumented tests for UI (Compose Test)

---

## Context for Future Development

When resuming development:
1. Read this log to understand what's been done
2. Check git log: `git log --oneline`
3. Review test coverage: All layers have comprehensive tests
4. Next phase: Implement Basic 2D Component (Phase 3)
5. All refactoring complete: "CreditScore" → "Score" throughout

**Current state: Ready for Phase 3 implementation!**
