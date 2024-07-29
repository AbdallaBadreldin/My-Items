package com.fstech.myItems

import android.app.Application
import android.content.res.Resources
import com.google.firebase.Firebase
import com.google.firebase.appcheck.appCheck
import com.google.firebase.appcheck.ktx.appCheck
import com.google.firebase.appcheck.playintegrity.PlayIntegrityAppCheckProviderFactory
import com.google.firebase.initialize
import dagger.hilt.android.HiltAndroidApp


@HiltAndroidApp
class Application : Application() {
    companion object {
        lateinit var res: Resources
    }

    override fun onCreate() {
        super.onCreate()
        Firebase.initialize(context = this)
        Firebase.appCheck.installAppCheckProviderFactory(
            PlayIntegrityAppCheckProviderFactory.getInstance(),
        )
//        isAppUpdated()
        res = resources
//        createNotificationChannel()
    }

    /*    private fun isAppUpdated() {
            val localVersionCode = BuildConfig.VERSION_CODE.toLong()

            val remoteConfig: FirebaseRemoteConfig = Firebase.remoteConfig
            remoteConfig.setDefaultsAsync(R.xml.remote_config_defaults)
            val configSettings = remoteConfigSettings {
                minimumFetchIntervalInSeconds = 3600
            }
            remoteConfig.setConfigSettingsAsync(configSettings)
            remoteConfig.fetchAndActivate()
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val remoteVersionCode = remoteConfig.getLong("androidVersionCode")
                        if (remoteVersionCode > localVersionCode) {
                            Toast.makeText(this, getString(R.string.need_update), Toast.LENGTH_LONG)
                                .show()
                            val packageName =
                                this.javaClass.getPackage()?.name ?: "store.msolapps.flamingo"
                            val intent = Intent(Intent.ACTION_VIEW)
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                            intent.data = Uri.parse("market://details?id=$packageName")
                            startActivity(intent)
                        }
                    }
                }
        }*/
//    private fun createNotificationChannel() {
//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            val notificationManager =
//                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
//
//            val channel = NotificationChannel(
//                CHANNEL_ID,
//                getString(R.string.app_name),
//                NotificationManager.IMPORTANCE_LOW
//            )
//            notificationManager.createNotificationChannel(channel)
//
//        }
//    }

}
//listen to token
//    val token:LiveData<String>= LiveData<String>("")

/*    //
//       private val localizationDelegate = LocalizationApplicationDelegate()
    override fun attachBaseContext(base: Context) {
//        localizationDelegate.setDefaultLanguage(base, Locale.ENGLISH)
//        super.attachBaseContext(localizationDelegate.attachBaseContext(base))
    }

    override fun getApplicationContext(): Context {
//        return localizationDelegate.getApplicationContext(super.getApplicationContext())
    }

    override fun getResources(): Resources {
//        return localizationDelegate.getResources(baseContext, super.getResources())
    }*/
