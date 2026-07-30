package pt.socialfood.core

import com.google.firebase.crashlytics.FirebaseCrashlytics

actual fun triggerTestCrash() {
    FirebaseCrashlytics.getInstance().log("Manual test crash triggered from Profile screen")
    throw RuntimeException("Test Crash - SocialFood Profile debug trigger")
}
