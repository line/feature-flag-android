import org.jetbrains.changelog.Changelog
import org.jetbrains.changelog.markdownToHTML
import org.jetbrains.intellij.platform.gradle.IntelliJPlatformType
import org.jetbrains.intellij.platform.gradle.TestFrameworkType

plugins {
    id("java")
    alias(libs.plugins.kotlin)
    alias(libs.plugins.intellij.platform)
    alias(libs.plugins.changelog)
    alias(libs.plugins.qodana)
    alias(libs.plugins.kover)
    alias(libs.plugins.ktlint.gradle)
}

group = "com.linecorp.android.featureflag.ij.plugin"
version = libs.versions.feature.flag.intellij.plugin.get()

kotlin {
    jvmToolchain(17)
}

sourceSets["main"].java.srcDirs("src/main/gen")

repositories {
    mavenCentral()
    intellijPlatform {
        defaultRepositories()
    }
}

dependencies {
    intellijPlatform {
        // https://plugins.jetbrains.com/docs/intellij/android-studio-releases-list.html
        // Note: Temporarily using Koala Feature Drop because some resources have been removed from
        // Ladybug and cannot build properly.
        // https://github.com/JetBrains/intellij-platform-gradle-plugin/issues/1738
        androidStudio("2024.1.2.13") // Koala Feature Drop | 2024.1.2 Patch 1
        bundledPlugin("com.intellij.java")
        bundledPlugin("org.jetbrains.kotlin")

        instrumentationTools()
        pluginVerifier()
        zipSigner()

        testFramework(TestFrameworkType.Platform)
        testFramework(TestFrameworkType.Plugin.Java)
    }

    testImplementation(libs.junit)
}

intellijPlatform {
    pluginConfiguration {
        version = libs.versions.feature.flag.intellij.plugin.get()

        description = providers.fileContents(layout.projectDirectory.file("README.md")).asText.map {
            val start = "<!-- Plugin description -->"
            val end = "<!-- Plugin description end -->"

            with(it.lines()) {
                if (!containsAll(listOf(start, end))) {
                    throw GradleException(
                        "Plugin description section not found in README.md:\n$start ... $end"
                    )
                }
                subList(indexOf(start) + 1, indexOf(end)).joinToString("\n").let(::markdownToHTML)
            }
        }

        val changelog = project.changelog // local variable for configuration cache compatibility
        changeNotes = libs.versions.feature.flag.intellij.plugin.map { pluginVersion ->
            with(changelog) {
                renderItem(
                    (getOrNull(pluginVersion) ?: getUnreleased())
                        .withHeader(false)
                        .withEmptySections(false),
                    Changelog.OutputType.HTML,
                )
            }
        }

        ideaVersion {
            // https://plugins.jetbrains.com/docs/intellij/android-studio-releases-list.html
            sinceBuild = "241"
            untilBuild = provider { null } // Accept all versions since `sinceBuild`.
        }
    }

    signing {
        certificateChain = providers.environmentVariable("CERTIFICATE_CHAIN")
        privateKey = providers.environmentVariable("PRIVATE_KEY")
        password = providers.environmentVariable("PRIVATE_KEY_PASSWORD")
    }

    publishing {
        token = providers.environmentVariable("PUBLISH_TOKEN")
        // The pluginVersion is based on the SemVer (https://semver.org) and supports pre-release labels, like 2.1.7-alpha.3
        // Specify pre-release label to publish the plugin in a custom Release Channel automatically. Read more:
        // https://plugins.jetbrains.com/docs/intellij/deployment.html#specifying-a-release-channel
        channels = libs.versions.feature.flag.intellij.plugin
            .map { listOf(it.substringAfter('-', "").substringBefore('.').ifEmpty { "default" }) }
    }

    pluginVerification {
        ides {
            // Only on GitHub CI, verify for 2024.2 fails because there is no java plugin. as with
            // the `intellijPlatform` item, the version is temporarily fixed because it seems to be
            // unstable above 2024.2 now.
            ide(IntelliJPlatformType.AndroidStudio, "2024.1.2.13")
        }
    }

    buildSearchableOptions = false
}

changelog {
    groups.empty()
    repositoryUrl = "https://github.com/line/feature-flag-android"
}

kover {
    reports {
        total {
            xml {
                onCheck = true
            }
        }
    }
}

tasks {
    publishPlugin {
        dependsOn(patchChangelog)
    }
}

intellijPlatformTesting {
    runIde {
        register("runIdeForUiTests") {
            task {
                jvmArgumentProviders += CommandLineArgumentProvider {
                    listOf(
                        "-Drobot-server.port=8082",
                        "-Dide.mac.message.dialogs.as.sheets=false",
                        "-Djb.privacy.policy.text=<!--999.999-->",
                        "-Djb.consents.confirmation.enabled=false",
                    )
                }
            }

            plugins {
                robotServerPlugin()
            }
        }
    }
}
