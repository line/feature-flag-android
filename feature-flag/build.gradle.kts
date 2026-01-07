import com.github.benmanes.gradle.versions.updates.DependencyUpdatesTask
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jlleitschuh.gradle.ktlint.reporter.ReporterType

plugins {
    `java-library`
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.gradle.publish)
    `java-gradle-plugin`
    alias(libs.plugins.ktlint.gradle)
    alias(libs.plugins.ben.manes.gradle)
    `maven-publish`
}

version = libs.versions.feature.flag.get()
group = libs.feature.flag.get().module.group

gradlePlugin {
    website.set("https://github.com/line/feature-flag-android")
    vcsUrl.set("https://github.com/line/feature-flag-android.git")
    plugins {
        register("featureFlagPlugin") {
            id = libs.feature.flag.get().module.name
            implementationClass = "com.linecorp.android.featureflag.FeatureFlagPlugin"
            displayName = "feature-flag-android"
            description = "A Gradle plugin to achieve feature flag based development for Android applications."
            tags.set(listOf("android", "feature-flag", "feature-toggle"))
        }
    }
}

kotlin {
    compilerOptions {
        jvmTarget.set(JvmTarget.JVM_17)
    }
}

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

ktlint {
    reporters {
        reporter(ReporterType.PLAIN)
        reporter(ReporterType.CHECKSTYLE)
    }
}

tasks.withType(Test::class.java) {
    useJUnitPlatform()
}

fun isStable(version: String): Boolean {
    val hasStableKeyword = setOf("RELEASE", "FINAL", "GA").any { version.contains(version, ignoreCase = true) }
    val regex = """^[0-9,.v-]+(-r)?$""".toRegex()
    return hasStableKeyword || regex.matches(version)
}

tasks.withType<DependencyUpdatesTask> {
    rejectVersionIf {
        !isStable(candidate.version)
    }
    checkForGradleUpdate = true
}

dependencies {
    compileOnly(libs.android.gradle)
    implementation(libs.jsemver)

    testImplementation(libs.kotlin.test)
    testImplementation(libs.mockk)
    testImplementation(libs.kotest.runner.junit5)
    testImplementation(libs.kotest.framework.engine)
    testImplementation(libs.kotest.assertions.core)
    testRuntimeOnly(libs.kotlin.reflect)
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
