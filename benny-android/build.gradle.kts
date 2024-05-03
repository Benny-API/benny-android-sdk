import com.vanniktech.maven.publish.SonatypeHost.Companion.S01
import org.gradle.api.JavaVersion.VERSION_17

plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.maven.publish)
    alias(libs.plugins.molecule)
}

val sdkVersion = "1.1.1"

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
                "proguard-rules.pro",
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
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.13"
    }
    kotlin {
        jvmToolchain(17)
    }
    testOptions {
        unitTests.isReturnDefaultValues = true
    }
}

mavenPublishing {
    publishToMavenCentral(S01)
    signAllPublications()
    coordinates(
        groupId = "com.bennyapi",
        artifactId = "android",
        version = sdkVersion,
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
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.appcompat)
    implementation(libs.kotlinx.datetime)
    implementation(libs.kotlinx.serialization)
    implementation(libs.ktor.client.android)
    implementation(libs.ktor.client.content.negotiation)
    implementation(libs.ktor.client.json.serialization)
    implementation(libs.viewmodel.compose)

    implementation(platform(libs.androidx.compose.bom))

    debugImplementation(libs.androidx.ui.tooling)

    testImplementation(libs.assertk)
    testImplementation(libs.junit)
    testImplementation(libs.turbine)
}
