# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

SocialFood is a Kotlin Multiplatform (KMP) app targeting **Android** and **iOS**, using Compose Multiplatform for a shared UI. The backend API is at `api.socialfood.pt`.

## Build Commands

```bash
# Build Android debug APK
./gradlew :composeApp:assembleDebug

# Run all tests (Android + iOS simulator)
./gradlew :composeApp:allTests

# Run Android unit tests only
./gradlew :composeApp:testDebugUnitTest

# Run a specific test class
./gradlew :composeApp:testDebugUnitTest --tests "pt.socialfood.presentation.signin.SignInViewModelTest"

# Lint (ktlint + detekt) — must pass before every commit
./gradlew ktlintCheck detekt

# Full build (compiles, lints, tests) — closest to what CI runs end-to-end
./gradlew build
```

For iOS: open `iosApp/` in Xcode and run from there.

CI (`.github/workflows/ci.yml`) runs on PRs targeting `develop` or `main`, as five parallel jobs: Android build, Android unit tests + Kover coverage verification (`koverVerify`), iOS build, iOS unit tests, and Static Analysis (`ktlintCheck detekt :composeApp:lintDebug`). Release builds/distribution are handled separately by `firebase.yml` (Firebase App Distribution) and `testflight.yml` (TestFlight).

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

**Naming convention:** Each use case has an interface (`GetGuidesUseCase`) and an `Impl` class (`GetGuidesUseCaseImpl`). Same for repositories. Packages are camelCase, never snake_case (e.g. `presentation/signin`, `domain/usecase` — not `sign_in`/`use_case`).

**Lint:** `@Composable` functions are allowed PascalCase names (exempted via `ktlint_function_naming_ignore_when_annotated_with = Composable` in `.editorconfig`); everything else follows standard ktlint naming rules. Pre-existing ktlint violations not yet fixed are tracked in `composeApp/ktlint-baseline.xml`, keyed by exact line/column, so an edit that shifts lines in a baselined file needs the corresponding entry updated (or the whole file regenerated via `./gradlew ktlintGenerateBaseline` if drift is large). Detekt's equivalent baseline is `composeApp/config/detekt/baseline.xml`.

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

## Claude Code Tooling

- `.claude/rules/git-conventions.md` — branch naming, commit message, and PR title/description conventions (loaded automatically as project instructions).
- `.claude/rules/test-conventions.md` — Given-When-Then test structure, fakes-over-mocks, where fakes/random-data generators live (loaded automatically as project instructions).
- `.claude/agents/` — a Jira-integrated pipeline (`jira-planner` → `coder` → `code-reviewer`, orchestrated by `pipeline`) plus `jira-refine` for backlog grooming. Invoke with `@pipeline` (optionally `--ticket <ID>`) or run an individual agent directly; Jira operations go through `scripts/jira.sh`.
