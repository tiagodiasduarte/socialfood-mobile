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
- Place fakes in `composeApp/src/commonTest/kotlin/pt/socialfood/fakes/` so they are shared across all test files within the module.
- Place test files under `commonTest` mirroring the production package path.
- Use `runTest` + `StandardTestDispatcher` for coroutine tests.