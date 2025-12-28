plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.hilt)
    alias(libs.plugins.ksp)
    // Uncomment after adding correct google-services.json with package: com.whatsappdirect.direct_cha
    // alias(libs.plugins.google.services)
    // alias(libs.plugins.firebase.crashlytics)
}

android {
    namespace = "com.whatsappdirect.direct_cha"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.whatsappdirect.direct_cha"
        minSdk = 24
        targetSdk = 35
        versionCode = 20
        versionName = "3.2"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        vectorDrawables {
            useSupportLibrary = true
        }
    }

    signingConfigs {
        create("release") {
            storeFile = file("keystore/direct_chat_open.jks")
            storePassword = "Pankaj@1611"
            keyAlias = "key0"
            keyPassword = "Pankaj@1611"
        }
    }

    buildTypes {
        debug {
            isDebuggable = true
            buildConfigField("boolean", "DEVELOPER_MODE", "true")
        }
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            signingConfig = signingConfigs.getByName("release")
            buildConfigField("boolean", "DEVELOPER_MODE", "false")
            ndk {
                debugSymbolLevel = "FULL"
            }
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = "17"
    }

    buildFeatures {
        compose = true
        buildConfig = true
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {
    // AndroidX Core
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.androidx.lifecycle.runtime.compose)
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.splashscreen)

    // Compose
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.material.icons.extended)

    // Navigation
    implementation(libs.androidx.navigation.compose)

    // Hilt
    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)
    implementation(libs.hilt.navigation.compose)

    // Room
    implementation(libs.room.runtime)
    implementation(libs.room.ktx)
    ksp(libs.room.compiler)

    // DataStore
    implementation(libs.datastore.preferences)

    // Coroutines
    implementation(libs.kotlinx.coroutines.android)

    // Image Loading
    implementation(libs.coil.compose)
    implementation(libs.coil.video)

    // Phone Number Validation
    implementation(libs.libphonenumber)

    // Animation
    implementation(libs.lottie.compose)

    // Ads
    implementation(libs.play.services.ads)

    // Firebase - Uncomment after adding correct google-services.json
    // implementation(platform(libs.firebase.bom))
    // implementation(libs.firebase.crashlytics)
    // implementation(libs.firebase.analytics)

    // QR Code
    implementation(libs.zxing.core)

    // CameraX (for QR scanning)
    implementation(libs.camera.core)
    implementation(libs.camera.camera2)
    implementation(libs.camera.lifecycle)
    implementation(libs.camera.view)
    implementation("com.google.guava:guava:32.1.3-android")

    // Permissions
    implementation(libs.accompanist.permissions)
    
    // Biometric
    implementation(libs.biometric)
    
    // DocumentFile for SAF
    implementation(libs.documentfile)
    
    // Video processing - using native Android APIs

    // Testing
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
}
