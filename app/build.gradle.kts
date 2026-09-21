plugins {
    id("com.android.application")
}

val localDebugKeystore = rootProject.file("debug.keystore")

android {
    namespace = "com.nenolink.gmailcatch"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.nenolink.gmailcatch"
        minSdk = 26
        targetSdk = 36
        versionCode = 2
        versionName = "0.1.1"
    }

    if (localDebugKeystore.exists()) signingConfigs {
        getByName("debug") {
            storeFile = localDebugKeystore
            storePassword = "android"
            keyAlias = "androiddebugkey"
            keyPassword = "android"
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

}

dependencies {
    implementation("androidx.core:core-ktx:1.17.0")
    implementation("androidx.appcompat:appcompat:1.7.1")
    testImplementation("junit:junit:4.13.2")
}
