# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

SocialFood is a Kotlin Multiplatform (KMP) app targeting **Android** and **iOS**, using Compose Multiplatform for a shared UI. The backend API is at `api.socialfood.pt`.

## Build Commands

```bash 
# Build Android debug APK
./gradlew :composeApp:assembleDebug

# Run all tests
./gradlew build

# Run tests only (no build)
./gradlew :composeApp:allTests

# Run a specific test class
./gradlew :composeApp:allTests --tests "pt.socialfood.presentation.sign_in.SignInViewModelTest"
```

For iOS: open `iosApp/` in Xcode and run from there.

CI runs `./gradlew build` on PRs targeting `develop`.

## Architecture

Clean Architecture with three layers inside `composeApp/src/commonMain/`:

```
data/          – API clients (Ktor), repository implementations, network models
domain/        – Repository interfaces, use cases, domain models, ErrorEntity
presentation/  – Screens, ViewModels, UI state classes, navigation
mapper/        – Network model → domain model converters
di/            – Koin DI module definitions
```

**Data flow:** `Screen` → `ViewModel` → `UseCase` → `RepositoryImpl` → `Api` (Ktor) → backend

**Result type:** All use cases and repositories return `core.Result<T>` (either `Success(data)` or `Error(ErrorEntity)`). Never throw across layer boundaries — catch in `RepositoryImpl` and convert with `Exception.toErrorEntity()`.

**Naming convention:** Each use case has an interface (`GetGuidesUseCase`) and an `Impl` class (`GetGuidesUseCaseImpl`). Same for repositories.

## Dependency Injection (Koin)

All wiring is in `di/Koin.kt`, split into four modules: `networkModule`, `repositoryModule`, `useCaseModule`, `viewModelModule`. ViewModels that require an ID are registered as `factory { (id: String) -> SomeViewModel(get(), id) }` and retrieved with `parametersOf(id)`.

## Navigation

Uses JetBrains Navigation 3 (`androidx.navigation3`). All routes are defined as `@Serializable` objects/data classes implementing the `Route` sealed interface. The `NavigationRoot` composable hosts a `NavDisplay` with bottom-tab navigation; the bottom bar hides when the back stack depth > 1. Auth flow (Splash → SignIn/SignUp → Home) is handled outside `NavigationRoot` in `App.kt`.

## Session & Auth

`SessionManager` stores the JWT in `multiplatform-settings` (key-value store). On 401 responses, `KtorHttpClient` calls `sessionManager.clear()`, which emits an `unauthorizedEvent` that `App.kt` observes to redirect to the login screen.

## Platform-Specific Code

`androidMain` and `iosMain` contain `expect`/`actual` implementations for:
- `GoogleSignInLauncher` – platform sign-in UI
- `ImagePickerLauncher` / `ImageBitmapDecoder` – photo picking
- `AppVersion` – version string

Photo uploads use a separate `S3HttpClient` (unsigned requests) distinct from the main `KtorHttpClient`.

## Test Conventions

Every test function must follow the Given-When-Then structure:

- **Name:** `` `given <context> when <action> then <expected outcome>` `` 
- **Body:** three labelled comment blocks — `// Given`, `// When`, `// Then`

```kotlin
@Test
fun `given valid credentials when login is called then returns Success`() = runTest {
    // Given
    val api = FakeAuthApi()
    val repo = AuthRepositoryImpl(api)

    // When
    val result = repo.login("user@test.com", "password")

    // Then
    assertIs<Result.Success<*>>(result)
}
```

- Fakes over mocks — hand-rolled `Fake<Dependency>` classes with a `shouldThrow: Boolean` flag.
- Place fakes in `composeApp/src/commonTest/kotlin/pt/socialfood/fakes/` so they are shared across all test files within the module.
- Place test files under `commonTest` mirroring the production package path.
- Use `runTest` + `StandardTestDispatcher` for coroutine tests.

## Key Libraries

| Library                    | Purpose                                        |
|----------------------------|------------------------------------------------|
| Ktor 3.4                   | HTTP client (OkHttp on Android, Darwin on iOS) |
| Koin 4.2                   | Dependency injection                           |
| Kamel 1.0                  | Async image loading                            |
| multiplatform-settings 1.3 | Persistent key-value storage                   |
| kotlinx.serialization      | JSON + route serialization                     |
| Firebase (BOM 34)          | Analytics (Android only)                       |
