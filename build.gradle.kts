plugins {
    id("com.android.application") version "8.1.2" apply false
    id("org.jetbrains.kotlin.android") version "1.8.10" apply false
    id("com.android.library") version "8.1.2" apply false
    id("com.diffplug.spotless") version "6.22.0" apply true
}

spotless {
    kotlin {
        target("**/src/**/*.kt", "**/src/**/*.kts")
        ktlint("1.0.0")
            .editorConfigOverride(mapOf("ktlint_standard_function-naming" to "disabled"))
            .userData(mapOf("android" to "true"))
    }
    kotlinGradle {
        target("*.gradle.kts")
        ktlint("1.0.0").userData(mapOf("android" to "true"))
    }
}
