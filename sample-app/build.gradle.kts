import org.gradle.api.JavaVersion.VERSION_17

plugins {
    id("com.android.application")
}

android {
    namespace = "com.bennyapi"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.bennyapi"
        minSdk = 30
        versionCode = 1
        versionName = "1.0.0"

        vectorDrawables {
            useSupportLibrary = true
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro",
            )
        }
    }
    compileOptions {
        sourceCompatibility = VERSION_17
        targetCompatibility = VERSION_17
    }
    kotlin {
        jvmToolchain(17)
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {
    implementation(project(":benny-android"))
    implementation(libs.androidx.core.ktx)
    implementation(libs.appcompat)
}
