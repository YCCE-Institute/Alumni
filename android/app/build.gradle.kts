import java.io.FileInputStream
import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    id("com.google.gms.google-services")
}

val localProperties = Properties().apply {
    val localFile = rootProject.file("local.properties")
    if (localFile.exists()) {
        load(FileInputStream(localFile))
    }
}

android {
    namespace = "com.suyogbauskar.yccealumni"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.suyogbauskar.yccealumni"
        minSdk = 26
        targetSdk = 35
        versionCode = 1
        versionName = "1.0.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    signingConfigs {
        create("devRelease") {
            val path = localProperties.getProperty("KEYSTORE_PATH")
                ?: System.getenv("KEYSTORE_PATH")
            if (!path.isNullOrEmpty()) {
                storeFile = file(path)
                storePassword = localProperties.getProperty("KEYSTORE_PASSWORD")
                    ?: System.getenv("KEYSTORE_PASSWORD")
                keyAlias = localProperties.getProperty("KEY_ALIAS_DEV")
                    ?: System.getenv("KEY_ALIAS_DEV")
                keyPassword = localProperties.getProperty("KEY_PASSWORD_DEV")
                    ?: System.getenv("KEY_PASSWORD_DEV")
            } else {
                println("⚠️ prodRelease keystore not configured; using debug signing!")
            }
        }
        create("prodRelease") {
            val path = localProperties.getProperty("KEYSTORE_PATH")
                ?: System.getenv("KEYSTORE_PATH")
            if (!path.isNullOrEmpty()) {
                storeFile = file(path)
                storePassword = localProperties.getProperty("KEYSTORE_PASSWORD")
                    ?: System.getenv("KEYSTORE_PASSWORD")
                keyAlias = localProperties.getProperty("KEY_ALIAS_PROD")
                    ?: System.getenv("KEY_ALIAS_PROD")
                keyPassword = localProperties.getProperty("KEY_PASSWORD_PROD")
                    ?: System.getenv("KEY_PASSWORD_PROD")
            } else {
                println("⚠️ prodRelease keystore not configured; using debug signing!")
            }
        }
    }

    buildTypes {
        getByName("debug") {
            // debug builds use default debug keystore
            isDebuggable = true
        }
        getByName("release") {
            isMinifyEnabled = false
            isDebuggable = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    flavorDimensions += "environment"

    productFlavors {
        create("dev") {
            dimension = "environment"
            applicationIdSuffix = ".dev"
            versionNameSuffix = "-dev"
            signingConfig = signingConfigs.getByName("devRelease")
        }
        create("prod") {
            dimension = "environment"
            signingConfig = signingConfigs.getByName("prodRelease")
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

dependencies {

    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)

    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.analytics)
    implementation(libs.firebase.auth)
    implementation(libs.firebase.firestore)
}