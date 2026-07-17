package pt.socialfood.fakes

import pt.socialfood.data.crash.CrashReporter

class FakeCrashReporter : CrashReporter {

    val recordedExceptions = mutableListOf<Throwable>()
    val loggedMessages = mutableListOf<String>()
    val customKeys = mutableMapOf<String, String>()
    var userId: String? = null
        private set

    override fun recordException(throwable: Throwable) {
        recordedExceptions.add(throwable)
    }

    override fun log(message: String) {
        loggedMessages.add(message)
    }

    override fun setCustomKey(key: String, value: String) {
        customKeys[key] = value
    }

    override fun setUserId(userId: String) {
        this.userId = userId
    }
}
