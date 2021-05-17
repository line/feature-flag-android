allprojects {
    repositories {
        google()
        mavenCentral()
    }
}

tasks.create("clean", Delete::class.java) {
    delete(rootProject.buildDir)
}
