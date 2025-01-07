import java.io.FileInputStream
import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.kotlin.kapt)
    alias(libs.plugins.dagger.hilt.android)
}

val keystoreProperties = Properties()

keystoreProperties.load(FileInputStream(rootProject.file("keystore.properties")))

android {
    namespace = "us.huseli.kiddo"
    compileSdk = 35

    signingConfigs {
        create("release") {
            storeFile = file(keystoreProperties["storeFile"] as String)
            storePassword = keystoreProperties["storePassword"] as String
            keyAlias = keystoreProperties["keyAlias"] as String
            keyPassword = keystoreProperties["keyPassword"] as String
        }
    }

    defaultConfig {
        applicationId = "us.huseli.kiddo"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "0.1.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        debug {
            isDebuggable = true
            applicationIdSuffix = ".debug"
        }
        release {
            isDebuggable = false
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            signingConfig = signingConfigs.getByName("release")
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
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
    implementation(libs.androidx.preference)

    // Compose:
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.activity.compose)

    // Material:
    implementation(libs.androidx.material3)
    implementation(libs.androidx.material.icons.extended)

    // Navigation:
    implementation(libs.androidx.hilt.navigation.compose)
    implementation(libs.androidx.navigation.compose)

    // Hilt:
    implementation(libs.hilt.android)
    kapt(libs.hilt.compiler)

    // Own stuff:
    implementation(libs.retain.theme)

    // Gson:
    implementation(libs.gson)

    // Ktor for websockets:
    implementation(libs.ktor.websockets)
    implementation(libs.ktor.cio)

    // Reorderable:
    implementation(libs.reorderable)

    // Just for ListItemAll.toString()
    implementation(libs.kotlin.reflect)

    // Splashscreen:
    implementation(libs.androidx.core.splashscreen)
}
