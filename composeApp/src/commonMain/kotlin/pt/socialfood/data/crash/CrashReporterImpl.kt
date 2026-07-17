package pt.socialfood.data.crash

import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.crashlytics.crashlytics

class CrashReporterImpl : CrashReporter {

    private val crashlytics get() = Firebase.crashlytics

    override fun recordException(throwable: Throwable) {
        crashlytics.recordException(throwable)
    }

    override fun log(message: String) {
        crashlytics.log(message)
    }

    override fun setCustomKey(key: String, value: String) {
        crashlytics.setCustomKey(key, value)
    }

    override fun setUserId(userId: String) {
        crashlytics.setUserId(userId)
    }
}
