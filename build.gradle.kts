import com.github.benmanes.gradle.versions.updates.DependencyUpdatesTask

plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.android.library) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.kotlin.serialization) apply false
    alias(libs.plugins.spotless)
    alias(libs.plugins.version.catalog.update)
    alias(libs.plugins.versions)
}

allprojects {
    apply(plugin = "com.diffplug.spotless")
}

subprojects {
    apply(plugin = "org.jetbrains.kotlin.android")
}

spotless {
    kotlin {
        ktlint("1.2.1")
            .editorConfigOverride(
                mapOf(
                    "android" to "true",
                    "ktlint_function_naming_ignore_when_annotated_with" to "Composable",
                ),
            )
        target("**/*.kt")
        targetExclude("build/generated/**")
        endWithNewline()
    }
    kotlinGradle {
        ktlint("1.2.1").editorConfigOverride(mapOf("android" to "true"))
        target("**/*.kts")
        endWithNewline()
    }
}

tasks.withType<DependencyUpdatesTask> {
    rejectVersionIf {
        isNonStable(candidate.version)
    }
}

/**
 * Exclude non-stable dependency versions from update check.
 */
fun isNonStable(version: String): Boolean {
    val stableKeyword = listOf("RELEASE", "FINAL", "GA").any { version.uppercase().contains(it) }
    val regex = "^[0-9,.v-]+(-r)?$".toRegex()
    val isStable = stableKeyword || regex.matches(version)
    return isStable.not()
}
