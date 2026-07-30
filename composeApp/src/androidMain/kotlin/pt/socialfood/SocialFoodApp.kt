package pt.socialfood

import android.app.Application
import com.google.firebase.crashlytics.FirebaseCrashlytics
import org.koin.android.ext.koin.androidContext
import pt.socialfood.di.initKoin

class SocialFoodApp : Application() {

    override fun onCreate() {
        super.onCreate()
        FirebaseCrashlytics.getInstance().setCrashlyticsCollectionEnabled(!BuildConfig.DEBUG)
        initKoin {
            androidContext(this@SocialFoodApp)
        }
    }
}
