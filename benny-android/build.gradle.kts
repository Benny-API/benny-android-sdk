import com.vanniktech.maven.publish.SonatypeHost.Companion.S01
import org.gradle.api.JavaVersion.VERSION_17

plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
    alias(libs.plugins.maven.publish)
    kotlin("plugin.serialization")
}

val sdkVersion = "1.1.0"

android {
    namespace = "com.bennyapi.android"
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
}

mavenPublishing {
    publishToMavenCentral(S01)
    signAllPublications()
    coordinates(
        groupId = "com.bennyapi",
        artifactId = "android",
        version = "$sdkVersion"
    )
    pom {
        name.set("Benny Android SDK")
        description.set("Benny Android SDK")
        inceptionYear.set("2023")
        url.set("https://github.com/Benny-API/benny-android-sdk")

        licenses {
            license {
                name.set("MIT License")
                url.set("https://github.com/Benny-API/benny-android-sdk/blob/main/LICENSE")
                distribution.set("https://github.com/Benny-API/benny-android-sdk/blob/main/LICENSE")
            }
        }
        developers {
            developer {
                id.set("Benny-API")
                name.set("Benny-API")
                url.set("https://github.com/Benny-API")
            }
        }
        scm {
            url.set("https://github.com/Benny-API/benny-android-sdk")
            connection.set("scm:git://github.com/Benny-API/benny-android-sdk.git")
            developerConnection.set("scm:git:ssh://git@github.com/Benny-API/benny-android-sdk.git")
        }
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.kotlin.serialization)
}
