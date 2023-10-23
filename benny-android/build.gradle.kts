import org.gradle.api.JavaVersion.VERSION_1_8

plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
}

val version = "1.0.0"

android {
    namespace = "com.bennyapi.benny"
    compileSdk = 34
    defaultConfig {
        minSdk = 24
    }
    buildFeatures {
        buildConfig = true
    }
    buildTypes {
        release {
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            buildConfigField("String", "VERSION", "\"${version}\"")
        }
        debug {
            buildConfigField("String", "VERSION", "\"${version}\"")
        }
    }
    compileOptions {
        sourceCompatibility = VERSION_1_8
        targetCompatibility = VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
}