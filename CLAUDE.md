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
./gradlew :composeApp:testDebugUnitTest --tests "pt.socialfood.presentation.signin.SignInViewModelTest"
```

For iOS: open `iosApp/` in Xcode and run from there.

CI runs `./gradlew build` on PRs targeting `develop`.

## Architecture

Clean Architecture with three layers inside `composeApp/src/commonMain/`:

```
data/          – API clients (Ktor), repository implementations, network models
domain/        – Repository interfaces, use cases, domain models, DataError/ErrorCode
presentation/  – Screens, ViewModels, UI state classes, navigation
mapper/        – Network model → domain model converters
di/            – Koin DI module definitions
```

**Data flow:** `Screen` → `ViewModel` → `UseCase` → `RepositoryImpl` → `Api` (Ktor) → backend

**Result type:** All use cases and repositories return `core.Result<T>` (either `Success(data)` or `Failure(DataError)`). Never throw across layer boundaries — `safeApiCall` (`domain/error/safeApiCall.kt`) wraps Ktor calls and converts exceptions via `Throwable.toDataError()` (`data/network/extensions/ThrowableExceptions.kt`) into `DataError.Known` (structured backend error carrying an `ErrorCode`), `DataError.Unknown` (unparsed HTTP error), or `DataError.Network` (connectivity/IO failure).

**Error messages:** Resolve error copy in Compose, not in the ViewModel — store `ErrorCode` in UI state (via `DataError.toErrorCode()`) and resolve the string at render time with `stringResource(errorCode.stringResource())` (`presentation/error/DataErrorMessages.kt`). This keeps ViewModel tests on plain JVM without needing Robolectric, since no Android resource APIs are touched outside `@Composable` scope. `SignInViewModel`, `EditProfileViewModel`, `FavouriteGuidesViewModel`, and `FavouriteRestaurantsViewModel` follow this pattern; other ViewModels still use the older suspend `DataError.displayMessage()`, which resolves the string inside the ViewModel and hasn't been migrated yet.

**Naming convention:** Each use case has an interface (`GetGuidesUseCase`) and an `Impl` class (`GetGuidesUseCaseImpl`). Same for repositories.

## Dependency Injection (Koin)

All wiring is in `di/Koin.kt`, split into five modules: `platformModule` (`expect`/`actual`, binds `SettingsRepository` per platform), `networkModule`, `repositoryModule`, `useCaseModule`, `viewModelModule`. ViewModels that require an ID are registered as `factory { (id: String) -> SomeViewModel(get(), id) }` and retrieved with `parametersOf(id)`.

## Navigation

Uses JetBrains Navigation 3 (`androidx.navigation3`). All routes are defined as `@Serializable` objects/data classes implementing the `Route` sealed interface. The `NavigationRoot` composable hosts a `NavDisplay` with bottom-tab navigation; the bottom bar hides when the back stack depth > 1. Auth flow (Splash → SignIn/SignUp → ValidateCode → Home) is handled outside `NavigationRoot` in `App.kt`. Unverified users are routed to `ValidateCode` from both Splash (existing session, unverified) and SignUp (new registration).

## Session & Auth

`SessionManager` stores the JWT via `SettingsRepository` (Jetpack DataStore on Android, `NSUserDefaults` on iOS). On 401 responses, `KtorHttpClient` calls `sessionManager.clear()`, which emits an `unauthorizedEvent` that `App.kt` observes to redirect to the login screen.

## Platform-Specific Code

`androidMain` and `iosMain` contain `expect`/`actual` implementations for:
- `GoogleSignInLauncher` – platform sign-in UI
- `ImagePickerLauncher` / `ImageBitmapDecoder` – photo picking
- `AppVersion` – version string
- `platformModule` (in `di/Koin.kt`) – Koin module binding `SettingsRepository`: `SettingsRepositoryImpl` is backed by Jetpack DataStore on Android and `NSUserDefaults` on iOS (these two `SettingsRepositoryImpl` classes aren't `expect`/`actual` themselves, just independently implemented per platform and wired in through `platformModule`)

Photo uploads use a separate `S3HttpClient` (unsigned requests) distinct from the main `KtorHttpClient`.

## Key Libraries

| Library                    | Purpose                                        |
|----------------------------|------------------------------------------------|
| Ktor 3.4                   | HTTP client (OkHttp on Android, Darwin on iOS) |
| Koin 4.2                   | Dependency injection                           |
| Coil 3.4                   | Async image loading                            |
| kotlinx.serialization      | JSON + route serialization                     |
| Firebase (BOM 34)          | Analytics (Android only)                       |
