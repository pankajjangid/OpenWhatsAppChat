package com.whatsappdirect.direct_chat

import android.app.Application
import com.google.android.gms.ads.MobileAds
// Uncomment after adding correct google-services.json
// import com.google.firebase.FirebaseApp
// import com.google.firebase.crashlytics.FirebaseCrashlytics
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class WhatsAppDirectApp : Application() {
    
    override fun onCreate() {
        super.onCreate()
        
        // Initialize Firebase - Uncomment after adding correct google-services.json
        // FirebaseApp.initializeApp(this)
        // FirebaseCrashlytics.getInstance().apply {
        //     setCrashlyticsCollectionEnabled(!BuildConfig.DEBUG)
        // }
        
        // Initialize Mobile Ads SDK
        MobileAds.initialize(this) {}
    }
}
