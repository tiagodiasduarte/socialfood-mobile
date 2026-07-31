package pt.socialfood.runner

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain

@OptIn(ExperimentalCoroutinesApi::class)
fun runTestWithMainDispatcher(
    dispatcher: TestDispatcher = StandardTestDispatcher(),
    testBody: suspend TestScope.() -> Unit
) = runTest(dispatcher) {
    Dispatchers.setMain(dispatcher)
    try {
        testBody()
    } finally {
        Dispatchers.resetMain()
    }
}
