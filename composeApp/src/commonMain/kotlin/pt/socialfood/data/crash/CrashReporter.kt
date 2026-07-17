package pt.socialfood.data.crash

/**
 * Cross-cutting abstraction over Firebase Crashlytics so the rest of the app
 * (use cases, repositories, ViewModels) never talks to the GitLive SDK directly.
 */
interface CrashReporter {

    /**
     * Records a non-fatal error, including its full stack trace, to Crashlytics.
     */
    fun recordException(throwable: Throwable)

    /**
     * Adds a breadcrumb log message that is attached to subsequent crash/non-fatal reports.
     */
    fun log(message: String)

    /**
     * Attaches a custom key/value pair to subsequent crash/non-fatal reports.
     */
    fun setCustomKey(key: String, value: String)

    /**
     * Associates an identifier with subsequent crash/non-fatal reports.
     */
    fun setUserId(userId: String)
}
