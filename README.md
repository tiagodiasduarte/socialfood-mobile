# SocialFood

[![CI](https://github.com/tiagodiasduarte/socialfood-mobile/actions/workflows/ci.yml/badge.svg)](https://github.com/tiagodiasduarte/socialfood-mobile/actions/workflows/ci.yml)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](./LICENSE)
[![Kotlin](https://img.shields.io/badge/Kotlin-2.3.0-7F52FF?logo=kotlin&logoColor=white)](https://kotlinlang.org)
[![Compose Multiplatform](https://img.shields.io/badge/Compose%20Multiplatform-1.10.0-4285F4)](https://www.jetbrains.com/lp/compose-multiplatform/)

A Kotlin Multiplatform app for discovering and sharing restaurant guides, built with a single Compose Multiplatform codebase for **Android** and **iOS**.

📖 Deeper docs (architecture, CI/CD internals, SDK inventory, testing conventions) live in the [project wiki](https://github.com/tiagodiasduarte/socialfood-mobile/wiki).

## Features

- 🔐 Email sign-up/sign-in with code verification, plus Google Sign-In
- 📍 Restaurant guides — browse, search, build your own, and favourite guides and restaurants for quick access
- 👥 Authors — browse and search profiles, follow them, and see the guides they've created
- 🗺️ Place search and enrichment when adding restaurants to a guide
- 🖼️ Photo uploads for guides and profiles, served straight to S3
- 👤 Profile management

## Tech Stack

| Layer           | Tech                                                                                |
|-----------------|-------------------------------------------------------------------------------------|
| UI              | [Compose Multiplatform](https://www.jetbrains.com/lp/compose-multiplatform/) 1.10   |
| Language        | Kotlin 2.3 (Multiplatform)                                                          |
| Networking      | [Ktor](https://ktor.io) 3.4 (OkHttp on Android, Darwin on iOS)                      |
| DI              | [Koin](https://insert-koin.io) 4.2                                                  |
| Navigation      | JetBrains Navigation 3                                                              |
| Images          | [Coil](https://coil-kt.github.io/coil/) 3.4                                         |
| Serialization   | kotlinx.serialization                                                               |
| Local cache     | Room                                                                                |
| Crash reporting | Firebase Crashlytics (native on both platforms)                                     |
| Coverage        | [Kover](https://github.com/Kotlin/kotlinx-kover)                                    |
| Static analysis | ktlint + Detekt                                                                     |
| Testing         | kotlin-test, kotlinx-coroutines-test, [Turbine](https://github.com/cashapp/turbine) |

## Architecture

SocialFood follows Clean Architecture, detailed in full on the [Architecture wiki page](https://github.com/tiagodiasduarte/socialfood-mobile/wiki/Architecture).

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

## License

MIT — see [LICENSE](./LICENSE).
