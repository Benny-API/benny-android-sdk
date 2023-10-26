import org.gradle.api.JavaVersion.VERSION_17

plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
    `maven-publish`
}

val sdkVersion = "1.0.0"

android {
    namespace = "com.bennyapi.benny"
    compileSdk = 34
    defaultConfig {
        minSdk = 24
        aarMetadata {
            minCompileSdk = 24
        }
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
            buildConfigField("String", "VERSION", "\"${sdkVersion}\"")
        }
        debug {
            buildConfigField("String", "VERSION", "\"${sdkVersion}\"")
        }
    }
    compileOptions {
        sourceCompatibility = VERSION_17
        targetCompatibility = VERSION_17
    }
    kotlin {
        jvmToolchain(17)
    }
    publishing {
        singleVariant("release") {
            withSourcesJar()
        }
    }
}

publishing {
    publications {
        register<MavenPublication>("release") {
            groupId = "com.bennyapi"
            artifactId = "benny"
            version = "$sdkVersion-SNAPSHOT"
            afterEvaluate {
                from(components["release"])
            }
        }
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
}