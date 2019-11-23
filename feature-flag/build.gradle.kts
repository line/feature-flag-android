import org.jlleitschuh.gradle.ktlint.reporter.ReporterType

plugins {
    kotlin("jvm")
    `kotlin-dsl`
    id("com.gradle.plugin-publish") version Dependencies.Version.gradlePublishPlugin
    `java-gradle-plugin`
    id("org.jlleitschuh.gradle.ktlint")
    id("com.github.ben-manes.versions")
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
    reporters.set(setOf(ReporterType.PLAIN, ReporterType.CHECKSTYLE))
}

tasks.withType(Test::class.java) {
    @Suppress("UnstableApiUsage")
    useJUnitPlatform {
        includeEngines("spek2")
    }
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
