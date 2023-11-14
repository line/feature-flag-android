import com.github.benmanes.gradle.versions.updates.DependencyUpdatesTask
import org.jlleitschuh.gradle.ktlint.reporter.ReporterType

plugins {
    alias(libs.plugins.kotlin.jvm)
    `kotlin-dsl`
    alias(libs.plugins.gradle.publish)
    `java-gradle-plugin`
    alias(libs.plugins.ktlint.gradle)
    alias(libs.plugins.ben.manes.gradle)
    `maven-publish`
}

version = libs.versions.feature.flag.get()
group = libs.feature.flag.get().module.group

gradlePlugin {
    plugins {
        register("featureFlagPlugin") {
            id = libs.feature.flag.get().module.name
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
    compileOnly(libs.android.gradle)

    testImplementation(libs.kotlin.test)
    testImplementation(libs.mockk)
    testImplementation(libs.spek2.dsl.jvm)
    testRuntimeOnly(libs.kotlin.reflect)
    testRuntimeOnly(libs.spek2.runner.junit5)
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
            version = libs.versions.feature.flag.get()
        }
    }
}

publishing {
    publications {
        maybeCreate("pluginMaven", MavenPublication::class.java).apply {
            groupId = libs.feature.flag.get().module.group
            artifactId = libs.feature.flag.get().module.name
            version = libs.versions.feature.flag.get()
        }
    }
}
