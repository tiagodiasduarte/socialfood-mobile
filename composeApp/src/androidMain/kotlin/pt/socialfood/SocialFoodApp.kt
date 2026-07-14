package pt.socialfood

import android.app.Application
import org.koin.android.ext.koin.androidContext
import pt.socialfood.di.initKoin

class SocialFoodApp : Application() {

    override fun onCreate() {
        super.onCreate()
        initKoin {
            androidContext(this@SocialFoodApp)
        }
    }
}
