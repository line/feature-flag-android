buildscript {
    repositories {
        jcenter()
        maven {
            url = uri("https://plugins.gradle.org/m2/")
        }
    }
    dependencies {
        classpath(Dependencies.GradlePlugin.kotlin)
        classpath(Dependencies.GradlePlugin.ktlint)
        classpath(Dependencies.GradlePlugin.gradleVersions)
    }
}

allprojects {
    repositories {
        google()
        jcenter()
    }
}

tasks.create("clean", Delete::class.java) {
    delete(rootProject.buildDir)
}
