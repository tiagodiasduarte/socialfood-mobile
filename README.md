# SocialFood

[![Kotlin](https://img.shields.io/badge/Kotlin-2.3.0-7F52FF?logo=kotlin&logoColor=white)](https://kotlinlang.org)
[![Compose Multiplatform](https://img.shields.io/badge/Compose%20Multiplatform-1.10.0-4285F4)](https://www.jetbrains.com/lp/compose-multiplatform/)
[![CI](https://github.com/tiagodiasduarte/socialfood-mobile/actions/workflows/ci.yml/badge.svg)](https://github.com/tiagodiasduarte/socialfood-mobile/actions/workflows/ci.yml)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](./LICENSE)

A Kotlin Multiplatform app for discovering and sharing restaurant guides, built with a single Compose Multiplatform codebase for **Android** and **iOS**.

📖 Deeper docs (architecture, CI/CD internals, SDK inventory and testing) live in the [project wiki](https://github.com/tiagodiasduarte/socialfood-mobile/wiki).

## Features

- 🔐 Email sign-up/sign-in with code verification, plus Google Sign-In — sessions renew automatically via refresh tokens, so you stay signed in
- 📍 Restaurant guides — browse, search, build your own, and favourite guides and restaurants for quick access
- 🔎 Global search across guides, restaurants, and authors, with top-favorites suggestion shortcuts when the query is empty
- 👥 Authors — browse and search profiles, follow them, and see the guides they've created
- 🗺️ Place search and enrichment when adding restaurants to a guide
- 🖼️ Photo uploads for guides and profiles, served straight to S3
- 👤 Profile management

## Tech Stack

| Layer           | Tech                                                                                                                                                                                                                      |
|-----------------|---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| UI              | [Compose Multiplatform](https://www.jetbrains.com/lp/compose-multiplatform/) 1.10                                                                                                                                         |
| Language        | [Kotlin](https://kotlinlang.org) 2.3 (Multiplatform)                                                                                                                                                                      |
| Networking      | [Ktor](https://ktor.io) 3.4 (OkHttp on Android, Darwin on iOS), with the `Auth` plugin handling access/refresh token attachment and renewal                                                                              |
| DI              | [Koin](https://insert-koin.io) 4.2                                                                                                                                                                                        |
| Navigation      | [JetBrains Navigation 3](https://developer.android.com/guide/navigation/navigation-3)                                                                                                                                     |
| Images          | [Coil](https://coil-kt.github.io/coil/) 3.4                                                                                                                                                                               |
| Serialization   | [kotlinx.serialization](https://github.com/Kotlin/kotlinx.serialization)                                                                                                                                                  |
| Local cache     | [Room](https://developer.android.com/kotlin/multiplatform/room)                                                                                                                                                           |
| Pagination      | [Paging 3](https://developer.android.com/kotlin/multiplatform/paging) 3.5, backed by Room via `RemoteMediator`                                                                                                            |
| Crash reporting | [Firebase Crashlytics](https://firebase.google.com/docs/crashlytics) (native on both platforms)                                                                                                                           |
| Coverage        | [Kover](https://github.com/Kotlin/kotlinx-kover)                                                                                                                                                                          |
| Static analysis | [ktlint](https://pinterest.github.io/ktlint/) + [Detekt](https://detekt.dev)                                                                                                                                              |
| Testing         | [kotlin-test](https://kotlinlang.org/api/latest/kotlin.test/), [kotlinx-coroutines-test](https://github.com/Kotlin/kotlinx.coroutines/tree/master/kotlinx-coroutines-test), [Turbine](https://github.com/cashapp/turbine) |

## Architecture

SocialFood follows Clean Architecture, detailed in full on the [Architecture wiki page](https://github.com/tiagodiasduarte/socialfood-mobile/wiki/Architecture).

## UI

The Screens and UI elements are built entirely using Jetpack Compose. Paginated lists (guides, authors, wishlist, visited restaurants) render through Paging 3's Compose integration — `collectAsLazyPagingItems()` over a `Flow<PagingData<T>>` exposed by the ViewModel — instead of hand-rolled page/loadMore state.

## Testing

To keep components easy to test, SocialFood relies on constructor-based dependency injection with [Koin](https://insert-koin.io) — dependencies are injected via the constructor everywhere, so tests can substitute fakes directly, with no need to spin up a DI framework at all.

No mocking library is used. Instead, dependencies are faked with hand-rolled classes that implement the real interface, and these fakes are shared across every test that needs them. This keeps tests exercising real production code paths and asserting on actual resulting state, rather than just verifying that specific calls were made against a mock.

```bash
# Full build: compiles, lints, and runs tests
./gradlew build

# Shared/unit tests only (Android + iOS simulator)
./gradlew :composeApp:allTests

# Android unit tests only
./gradlew :composeApp:testDebugUnitTest

# Lint (ktlint + detekt) + Android unit tests — run before every commit
./gradlew ktlintCheck detekt :composeApp:testDebugUnitTest

# iOS simulator target — requires Xcode + a simulator, macOS only
./gradlew :composeApp:iosSimulatorArm64Test
```

See the [Testing wiki page](https://github.com/tiagodiasduarte/socialfood-mobile/wiki/Testing) for all the details.

## CI/CD

- **CI** (`ci.yml`) — runs on every PR into `develop`/`main`.
- **Firebase** (`firebase.yml`) — signed release AAB distributed via Firebase App Distribution.
- **TestFlight** (`testflight.yml`) — signed iOS build uploaded via Fastlane.

Full breakdown, including the daily-schedule trigger setup, is in the [CI/CD wiki page](https://github.com/tiagodiasduarte/socialfood-mobile/wiki/CI-CD).

## License
SocialFood is distributed under the terms of the MIT [License](./LICENSE). See the license for more information.
