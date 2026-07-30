package pt.socialfood.core

interface CrashReportingDelegate {
    fun triggerTestCrash()
}

object CrashReportingBridge {
    var delegate: CrashReportingDelegate? = null
}

actual fun triggerTestCrash() {
    CrashReportingBridge.delegate?.triggerTestCrash()
}
