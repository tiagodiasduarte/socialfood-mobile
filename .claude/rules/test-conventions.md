# Test Conventions

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
- Place shared fakes in `core/testing/src/commonMain/kotlin/pt/socialfood/fakes/` (random data generators are in `pt.socialfood.random`, same module). Every module built with a convention plugin gets `:core:testing` on its `commonTest` classpath. A fake of something that only one module can see (e.g. `FakeImageCache` for `core:ui`'s `ImageCache`) lives in that module's `commonTest/.../fakes/`.
- Place test files in the `commonTest` of the module that owns the production code, mirroring its package path.
- Use `runTest` + `StandardTestDispatcher` for coroutine tests.