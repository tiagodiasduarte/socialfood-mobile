# SocialFood

[![CI](https://github.com/tiagodiasduarte/socialfood-mobile/actions/workflows/ci.yml/badge.svg)](https://github.com/tiagodiasduarte/socialfood-mobile/actions/workflows/ci.yml)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](./LICENSE)
[![Kotlin](https://img.shields.io/badge/Kotlin-2.3.0-7F52FF?logo=kotlin&logoColor=white)](https://kotlinlang.org)
[![Compose Multiplatform](https://img.shields.io/badge/Compose%20Multiplatform-1.10.0-4285F4)](https://www.jetbrains.com/lp/compose-multiplatform/)

A Kotlin Multiplatform app for discovering and sharing restaurant guides, built with a single Compose Multiplatform codebase for **Android** and **iOS**.

📖 Deeper docs (architecture, CI/CD internals, SDK inventory, testing conventions) live in the [project wiki](https://github.com/tiagodiasduarte/socialfood-mobile/wiki).

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
| Local cache   | Room                                                               |
| Crash reporting | Firebase Crashlytics (native on both platforms)                 |
| Coverage      | [Kover](https://github.com/Kotlin/kotlinx-kover)                   |
| Static analysis | ktlint + Detekt                                                 |
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

See the [Architecture wiki page](https://github.com/tiagodiasduarte/socialfood-mobile/wiki/Architecture) for the full breakdown (DI modules, the `expect`/`actual` bridge pattern, navigation, session/auth).

## Getting Started

### Prerequisites

- JDK 21
- Android Studio (Narwhal or newer) for Android
- Xcode + [CocoaPods](https://cocoapods.org) for iOS
- A `GOOGLE_CLIENT_ID_WEB` value in `local.properties` or your environment
- `composeApp/google-services.json` and `iosApp/iosApp/GoogleService-Info.plist` (real Firebase project files — copy the tracked `.example` versions and fill in real values for local dev)

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

See the [Testing Conventions wiki page](https://github.com/tiagodiasduarte/socialfood-mobile/wiki/Testing-Conventions) for the Given-When-Then format and fakes-over-mocks pattern.

## CI/CD

- **CI** (`ci.yml`) — runs on every PR into `develop`/`main`: Android build + unit tests (with Kover coverage report/gate), iOS build + unit tests, and static analysis (ktlint + Detekt + Android Lint). Comment `/retest` on a PR to re-run just its failed jobs.
- **Firebase** (`firebase.yml`) — signed release AAB distributed via Firebase App Distribution.
- **TestFlight** (`testflight.yml`) — signed iOS build uploaded via Fastlane.

Full breakdown, including the nightly-schedule trigger setup, is in the [CI/CD wiki page](https://github.com/tiagodiasduarte/socialfood-mobile/wiki/CI-CD).

## Contributing

Branch, commit, and PR conventions live in [`.claude/rules/git-conventions.md`](./.claude/rules/git-conventions.md); test conventions (Given-When-Then, fakes over mocks) live in [`.claude/rules/test-conventions.md`](./.claude/rules/test-conventions.md). `main` and `develop` are protected — all changes go through a PR with passing CI.

## License

MIT — see [LICENSE](./LICENSE).