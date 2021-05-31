import com.github.benmanes.gradle.versions.updates.DependencyUpdatesTask
import org.jlleitschuh.gradle.ktlint.reporter.ReporterType

plugins {
    kotlin("jvm") version Dependencies.Version.kotlin
    `kotlin-dsl`
    id("com.gradle.plugin-publish") version Dependencies.Version.gradlePublishPlugin
    `java-gradle-plugin`
    id("org.jlleitschuh.gradle.ktlint") version Dependencies.Version.ktlintGradlePlugin
    id("com.github.ben-manes.versions") version Dependencies.Version.gradleVersionsPlugin
    `maven-publish`
}

version = ModuleConfig.version
group = ModuleConfig.groupId

gradlePlugin {
    plugins {
        register("featureFlagPlugin") {
            id = ModuleConfig.pluginId
            implementationClass = "com.linecorp.android.featureflag.FeatureFlagPlugin"
        }
    }
}

ktlint {
    reporters {
        reporter(ReporterType.PLAIN)
        reporter(ReporterType.CHECKSTYLE)
    }
}

tasks.withType(Test::class.java) {
    useJUnitPlatform {
        includeEngines("spek2")
    }
}

fun isStable(version: String): Boolean {
    val hasStableKeyword = setOf("RELEASE", "FINAL", "GA").any {
        version.contains(version, ignoreCase = true)
    }
    val regex = """^[0-9,.v-]+(-r)?$""".toRegex()
    return hasStableKeyword || regex.matches(version)
}

tasks.withType<DependencyUpdatesTask> {
    resolutionStrategy {
        componentSelection {
            all {
                if (!isStable(candidate.version) && isStable(currentVersion)) {
                    reject("Release candidate")
                }
            }
        }
    }
    checkForGradleUpdate = true
}

dependencies {
    compileOnly(Dependencies.GradlePlugin.android)

    testImplementation(Dependencies.Library.Kotlin.test)
    testImplementation(Dependencies.Library.mockk)
    testImplementation(Dependencies.Library.Spek2.dslJvm)
    testRuntimeOnly(Dependencies.Library.Kotlin.reflect)
    testRuntimeOnly(Dependencies.Library.Spek2.runnerJunit5)
}

pluginBundle {
    website = "https://github.com/line/feature-flag-android"
    vcsUrl = "https://github.com/line/feature-flag-android.git"
    description =
        "A Gradle plugin to achieve feature flag based development for Android applications."
    (plugins) {
        "featureFlagPlugin" {
            displayName = "feature-flag-android"
            tags = listOf("android", "feature-flag", "feature-toggle")
            version = ModuleConfig.version
        }
    }
}

publishing {
    publications {
        maybeCreate("pluginMaven", MavenPublication::class.java).apply {
            groupId = ModuleConfig.groupId
            artifactId = ModuleConfig.pluginId
            version = ModuleConfig.version
        }
    }
}
