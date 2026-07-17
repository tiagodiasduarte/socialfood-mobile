package pt.socialfood.data.crash

import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.crashlytics.crashlytics

class CrashReporterImpl : CrashReporter {

    private val crashlytics get() = Firebase.crashlytics

    override fun recordException(throwable: Throwable) {
        try {
            crashlytics.recordException(throwable)
        } catch (e: Exception) {
            // swallow — crash reporting must never itself crash the app
        }
    }

    override fun log(message: String) {
        try {
            crashlytics.log(message)
        } catch (e: Exception) {
            // swallow — crash reporting must never itself crash the app
        }
    }

    override fun setCustomKey(key: String, value: String) {
        try {
            crashlytics.setCustomKey(key, value)
        } catch (e: Exception) {
            // swallow — crash reporting must never itself crash the app
        }
    }

    override fun setUserId(userId: String) {
        try {
            crashlytics.setUserId(userId)
        } catch (e: Exception) {
            // swallow — crash reporting must never itself crash the app
        }
    }
}
