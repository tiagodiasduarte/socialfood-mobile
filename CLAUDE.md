# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

SocialFood is a Kotlin Multiplatform (KMP) app targeting **Android** and **iOS**, using Compose Multiplatform for a shared UI. The backend API is at `api.socialfood.pt`.

## Build Commands

```bash
# Build Android debug APK
./gradlew :composeApp:assembleDebug

# Run all tests (Android + iOS simulator), every module
./gradlew allTests

# Run Android unit tests only: :composeApp uses testDebugUnitTest, library modules use testAndroidHostTest
./gradlew :composeApp:testDebugUnitTest testAndroidHostTest

# Run a specific test class (in the module that owns it)
./gradlew :feature:auth:testAndroidHostTest --tests "pt.socialfood.presentation.signin.SignInViewModelTest"

# Coverage verification across all modules (aggregated into :composeApp)
./gradlew :composeApp:koverVerify

# Lint (ktlint + detekt) — must pass before every commit
./gradlew ktlintCheck detekt

# Full build (compiles, lints, tests) — closest to what CI runs end-to-end
./gradlew build
```

For iOS: open `iosApp/` in Xcode and run from there.

CI (`.github/workflows/ci.yml`) runs on PRs targeting `develop` or `main`, as parallel jobs: Android build, Android unit tests + Kover coverage verification (`koverVerify`), iOS build, iOS unit tests, and Static Analysis (`ktlintCheck detekt checkModuleGraph :composeApp:lintDebug`). iOS unit tests are split into four shards (`core`, `features-1`, `features-2`, `app`) because each module links its own Kotlin/Native test binary; `scripts/ios-test-shard.sh` assigns every `iosSimulatorArm64Test` task to exactly one shard, so new modules are picked up automatically. Only the 🏁 Finish job is a required check. Release builds/distribution are handled separately by `firebase.yml` (Firebase App Distribution) and `testflight.yml` (TestFlight).

## Architecture

Clean Architecture split into Gradle modules. Package names didn't change when code moved into modules (e.g. use cases are still `pt.socialfood.domain.usecase.*`).

```
composeApp/                 – app shell: App.kt, NavigationRoot + bottom bar, route serializers, Koin (di/),
                              sync, MainActivity / MainViewController. Builds the iOS ComposeApp framework.
feature/<name>/api          – the feature's @Serializable routes (sealed <Name>Route : NavKey)
feature/<name>/impl         – screens, ViewModels, UI state, and <name>Entries(...) entry builders
                              (home, guide, restaurant, map, author, profile, favourite, search)
feature/auth, feature/settings – splash/sign-in/sign-up/validate-code, and drawer/theme (no api: hosted by App.kt)
core/common                 – Result, DataError/ErrorCode, AppConfig
core/model                  – domain models
core/domain                 – repository interfaces, use cases, SessionManager
core/network                – Ktor/S3/Coil clients, *Api, network models, safeApiCall, ConnectivityObserver
core/database               – Room database, DAOs, entities (schemas/ lives here)
core/datastore              – SettingsRepositoryImpl + token storage (DataStore / NSUserDefaults + Keychain)
core/data                   – repository implementations, mappers, paging mediators
core/designsystem           – AppTheme, components, drawables, fonts, and strings shared by several modules
core/ui                     – shared domain-aware UI (guide/restaurant cards), DataErrorMessages, image picker
core/maps                   – Google Maps / MapKit views
core/navigation             – Navigator, NavigationState, transition metadata
core/testing                – fakes, Random generators, runTestWithMainDispatcher (test-only dependency)
build-logic/                – convention plugins: socialfood.kmp.library / .compose / .feature / .room
```

**Compose resources:** each module has its own generated `Res` class (`pt.socialfood.<module path>.generated.resources`). A string used by only one feature lives in that feature's `src/commonMain/composeResources/values{,-pt}/strings.xml`; strings shared by several modules, plus all drawables and fonts, live in `core/designsystem`. A file that needs both imports the feature's class as `Res` and the design system's as `import pt.socialfood.core.designsystem.generated.resources.Res as DesignSystemRes`. Always add a new string to both `values` and `values-pt`.

**Module rules** (enforced by `./gradlew checkModuleGraph`, which CI runs in Static Analysis): a feature `impl` (and `:feature:auth`/`:feature:settings`) may depend on other features' `api` modules (to navigate) but never on another feature's implementation. Feature `api` modules only depend on `:core:navigation`/`:core:model`/`:core:common`. Features use `core:domain` interfaces and never see `core:data`/`core:network`/`core:database`/`core:datastore`; `:composeApp` binds implementations in Koin. Core modules never depend on features, `:core:designsystem` depends on no other module (callers pass in what it needs, e.g. `AppTheme(darkTheme = ...)`), and non-UI core modules never depend on the Compose ones (`designsystem`, `ui`, `maps`, `navigation`). The rules live in `build-logic/.../ModuleGraphCheck.kt`. New modules apply a convention plugin instead of configuring KMP/Android/lint by hand. Every module has to be listed in `settings.gradle.kts` and in `:composeApp`'s `kover(...)` dependencies.

**Data flow:** `Screen` → `ViewModel` → (`UseCase` →) `RepositoryImpl` → `Api` (Ktor) → backend

**Result type:** All use cases and repositories return `core.Result<T>` (either `Success(data)` or `Failure(DataError)`). Never throw across layer boundaries — `safeApiCall` (`core/network`, `domain/error/SafeApiCall.kt`) wraps Ktor calls and converts exceptions via `Throwable.toDataError()` (`data/network/extensions/ThrowableExceptions.kt`) into `DataError.Known` (structured backend error carrying an `ErrorCode`), `DataError.Unknown` (unparsed HTTP error), or `DataError.Network` (connectivity/IO failure).

**Error messages:** Resolve error copy in Compose, not in the ViewModel — store `ErrorCode` in UI state (via `DataError.toErrorCode()`) and resolve the string at render time with `stringResource(errorCode.stringResource())` (`core/ui`, `presentation/error/DataErrorMessages.kt`). This keeps ViewModel tests on plain JVM without needing Robolectric, since no Android resource APIs are touched outside `@Composable` scope. Every ViewModel that surfaces a `DataError` follows this pattern.

**Use cases only when they add logic:** add a use case when it does more than forward one repository call, e.g. it combines several repositories or managers, validates or transforms data, holds a business rule, or orchestrates a flow such as login. Otherwise the ViewModel depends on the repository interface (in `:core:domain`) directly and its tests use the `Fake*Repository` from `:core:testing`.

**Naming convention:** Each use case has an interface (`CreateGuideUseCase`) and an `Impl` class (`CreateGuideUseCaseImpl`). Same for repositories. Packages are camelCase, never snake_case (e.g. `presentation/signin`, `domain/usecase` — not `sign_in`/`use_case`).

**Lint:** `@Composable` functions are allowed PascalCase names (exempted via `ktlint_function_naming_ignore_when_annotated_with = Composable` in `.editorconfig`); everything else follows standard ktlint naming rules. Pre-existing ktlint violations not yet fixed are tracked per module in `<module>/ktlint-baseline.xml`, keyed by exact line/column, so an edit that shifts lines in a baselined file needs the corresponding entry updated (or that module's file regenerated via `./gradlew :<module>:ktlintGenerateBaseline` if drift is large). Detekt's equivalent is `<module>/config/detekt/baseline.xml` (`:<module>:detektBaseline`); the shared detekt config is `config/detekt/detekt.yml`. Moving a file to another module means moving its baseline entries too.

## Dependency Injection (Koin)

Each feature module declares its own ViewModel bindings in a `di/<Feature>FeatureModule.kt` (e.g. `guideFeatureModule` in `:feature:guide:impl`), so adding or changing a ViewModel doesn't touch `:composeApp`. The data layer does the same: `coreNetworkModule` (`:core:network`: APIs, HTTP clients, `ConnectivityObserver`), `coreDatabaseModule` (`:core:database`: `AppDatabase`), `coreDatastoreModule` (`:core:datastore`: `SettingsRepository`) and `coreDataModule` (`:core:data`: repositories), each in a `pt.socialfood.core.<module>.di` package. Platform-specific bindings use `expect`/`actual` inside those modules. Because the bindings live next to the classes, the `*ApiImpl`, `*RepositoryImpl`, `*UseCaseImpl`, `SettingsRepositoryImpl` and `ConnectivityObserverImpl` classes are `internal`, so keep new implementations `internal` and expose only the interface. Use cases and `SessionManager` are bound in `coreDomainModule` (`:core:domain`). What's left in `:composeApp`'s `di/Koin.kt` is app-level: `platformModule` (`AppConfig`, `GoogleSignInConfig`), `appModule` (image loader, `ImageCache`) and `viewModelModule` (`SyncViewModel`). Tests in other modules use the `Fake*UseCase` fakes from `:core:testing`, never a real `*UseCaseImpl`. `initKoin` includes all of them, and a new module must be added there and to `KoinModulesTest`. ViewModels that require an ID are registered as `factory { (id: String) -> SomeViewModel(get(), id) }` and retrieved with `parametersOf(id)`.

`KoinModulesTest` (`:composeApp` Android unit tests) runs Koin's `verify()` over every module, so a missing binding fails a test instead of crashing at runtime. Types that are provided outside Koin definitions (Android `Context`, runtime parameters) are listed there as `extraTypes`.

## Navigation

Uses JetBrains Navigation 3 (`androidx.navigation3`). Each feature defines its routes as a `@Serializable sealed interface <Feature>Route : NavKey` in its `api` module, and registers screens through an `EntryProviderScope<NavKey>.<feature>Entries(...)` builder in its `impl` module. `serializersConfig` (`:composeApp`, `NavKeySerializers.kt`) registers every sealed route interface in `featureRouteSerializers` with `subclassesOfSealed`, so a new feature only has to add its `<Feature>Route.serializer()` to that list; new routes inside an existing feature are picked up automatically. `NavKeySerializationTest` checks that every subclass of every listed interface resolves, and round-trips every bottom-bar tab. A screen that returns a value doesn't take a callback through navigation: the restaurant picker (`RestaurantRoute.PickRestaurant(requestKey)`) publishes the picked restaurant to `RestaurantPickerResults` (`:feature:restaurant:api`, a Koin single), and the caller's ViewModel collects `results(requestKey)` with a stable key of its own (e.g. `edit-guide:<id>`), so the result still arrives if that ViewModel is recreated. The `NavigationRoot` composable hosts a `NavDisplay` with bottom-tab navigation; the bottom bar hides when the back stack depth > 1. Auth flow (Splash → SignIn/SignUp → ValidateCode → Home) is handled outside `NavigationRoot` in `App.kt`. Unverified users are routed to `ValidateCode` from both Splash (existing session, unverified) and SignUp (new registration).

## Session & Auth

`SessionManager` stores the JWT via `SettingsRepository` (Jetpack DataStore on Android, `NSUserDefaults` on iOS). On 401 responses, `KtorHttpClient` calls `sessionManager.clear()`, which emits an `unauthorizedEvent` that `App.kt` observes to redirect to the login screen.

## Platform-Specific Code

`androidMain` and `iosMain` contain `expect`/`actual` implementations for:
- `GoogleSignInLauncher` – platform sign-in UI
- `ImagePickerLauncher` / `ImageBitmapDecoder` – photo picking
- `AppConfig` (version name, build date) – bound per platform in `platformModule`, from `BuildConfig` on Android and `NSBundle` on iOS. Only `:composeApp` can read `BuildConfig`, so library modules get build values through Koin (e.g. `GoogleSignInConfig`)
- `platformModule` (in `di/Koin.kt`) – Koin module binding `SettingsRepository`: `SettingsRepositoryImpl` is backed by Jetpack DataStore on Android and `NSUserDefaults` on iOS (these two `SettingsRepositoryImpl` classes aren't `expect`/`actual` themselves, just independently implemented per platform and wired in through `platformModule`)

Swift implements delegates declared in Kotlin `*Bridge` objects (image picker, Google Sign-In, alert dialogs). The modules that declare them (`core:ui`, `feature:auth`, `feature:guide:impl`, `feature:settings`) are `api` dependencies of `:composeApp` and `export(...)`ed from its iOS framework so Swift sees unprefixed names. A new bridge in another module needs the same treatment.

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
