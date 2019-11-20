object Dependencies {
    object GradlePlugin {
        const val kotlin = "org.jetbrains.kotlin:kotlin-gradle-plugin:${Version.kotlin}"
        const val android = "com.android.tools.build:gradle:${Version.androidPlugin}"
        const val ktlint = "org.jlleitschuh.gradle:ktlint-gradle:${Version.ktlintGradle}"
        const val gradleVersions =
            "com.github.ben-manes:gradle-versions-plugin:${Version.gradleVersionsPlugin}"
    }

    object Library {
        object Kotlin {
            const val test = "org.jetbrains.kotlin:kotlin-test:${Version.kotlin}"
            const val reflect = "org.jetbrains.kotlin:kotlin-reflect:${Version.kotlin}"
        }

        object Spek2 {
            const val dslJvm = "org.spekframework.spek2:spek-dsl-jvm:${Version.spek}"
            const val runnerJunit5 = "org.spekframework.spek2:spek-runner-junit5:${Version.spek}"
        }

        const val mockk = "io.mockk:mockk:${Version.mockk}"
    }

    object Version {
        // Language
        const val kotlin = "1.3.50"

        // Plugin
        const val androidPlugin = "3.5.0"
        const val gradlePublishPlugin = "0.10.1"
        const val ktlintGradle = "7.4.0"
        const val gradleVersionsPlugin = "0.20.0"

        // Library
        const val spek = "2.0.6"
        const val mockk = "1.9.3"
    }
}
