# SocialFood

[![CI Tests](https://github.com/tiagodiasduarte/socialfood-mobile/actions/workflows/tests.yml/badge.svg)](https://github.com/tiagodiasduarte/socialfood-mobile/actions/workflows/tests.yml)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](./LICENSE)
[![Kotlin](https://img.shields.io/badge/Kotlin-2.3.0-7F52FF?logo=kotlin&logoColor=white)](https://kotlinlang.org)
[![Compose Multiplatform](https://img.shields.io/badge/Compose%20Multiplatform-1.10.0-4285F4)](https://www.jetbrains.com/lp/compose-multiplatform/)

A Kotlin Multiplatform app for discovering and sharing restaurant guides, built with a single Compose Multiplatform codebase for **Android** and **iOS**.

## Features

- 🔐 Email sign-up/sign-in with code verification, plus Google Sign-In
- 📍 Restaurant guides — browse, search, and build your own
- 🗺️ Place search and enrichment when adding restaurants to a guide
- 🖼️ Photo uploads for guides and profiles, served straight to S3
- 👤 Profile management
- 🌓 Shared UI, shared business logic, native performance on both platforms

## Tech Stack

| Layer         | Tech                                                              |
|---------------|--------------------------------------------------------------------|
| UI            | [Compose Multiplatform](https://www.jetbrains.com/lp/compose-multiplatform/) 1.10 |
| Language      | Kotlin 2.3 (Multiplatform)                                        |
| Networking    | [Ktor](https://ktor.io) 3.4 (OkHttp on Android, Darwin on iOS)     |
| DI            | [Koin](https://insert-koin.io) 4.2                                |
| Navigation    | JetBrains Navigation 3                                            |
| Images        | [Coil](https://coil-kt.github.io/coil/) 3.4                       |
| Serialization | kotlinx.serialization                                             |
| Analytics     | Firebase (Android)                                                |
| Testing       | kotlin-test, kotlinx-coroutines-test, [Turbine](https://github.com/cashapp/turbine) |

## Architecture

Clean Architecture, three layers, all living inside `composeApp/src/commonMain/`:

```
Screen ──▶ ViewModel ──▶ UseCase ──▶ RepositoryImpl ──▶ Api (Ktor) ──▶ backend
```

```
data/          API clients, repository implementations, network models
domain/        Repository interfaces, use cases, domain models, ErrorEntity
presentation/  Screens, ViewModels, UI state, navigation
mapper/        Network model → domain model converters
di/            Koin module definitions
```

Every use case and repository returns `core.Result<T>` — `Success(data)` or `Error(ErrorEntity)`. Exceptions are caught at the repository boundary and never thrown across layers.

Platform-specific code (`androidMain` / `iosMain`) covers things like Google Sign-In, image picking, app versioning, and local settings storage (Jetpack DataStore on Android, `NSUserDefaults` on iOS).

## Getting Started

### Prerequisites

- JDK 21
- Android Studio (Narwhal or newer) for Android
- Xcode for iOS
- A `GOOGLE_CLIENT_ID_WEB` value in `local.properties` or your environment

### Build & Run — Android

```bash
./gradlew :composeApp:assembleDebug
```

Or run the `composeApp` configuration from Android Studio.

### Build & Run — iOS

Open `iosApp/` in Xcode and run from there.

### Tests

```bash
# All tests (Android + build)
./gradlew build

# Shared/unit tests only
./gradlew :composeApp:allTests

# A single test class
./gradlew :composeApp:testDebugUnitTest --tests "pt.socialfood.presentation.sign_in.SignInViewModelTest"

# iOS simulator target
./gradlew :composeApp:iosSimulatorArm64Test
```

## CI/CD

- **CI Tests** — runs on every PR into `develop`: Android unit tests + debug assemble, and iOS unit tests on the simulator target.
- **TestFlight** — nightly build uploaded to TestFlight via Fastlane.
- **Firebase** — Android distribution workflow.

## Contributing

Branch, commit, and PR conventions live in [`.claude/rules/git-conventions.md`](./.claude/rules/git-conventions.md); test conventions (Given-When-Then, fakes over mocks) live in [`.claude/rules/test-conventions.md`](./.claude/rules/test-conventions.md). `main` and `develop` are protected — all changes go through a PR with passing CI.

## License

MIT — see [LICENSE](./LICENSE).