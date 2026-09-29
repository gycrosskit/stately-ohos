plugins {
    id("org.jetbrains.kotlin.multiplatform") version "2.2.21-1.0.0" apply false
}

subprojects {
    // Native KLIB names must match upstream dependencies compiled into Koin Compose.
    group = "co.touchlab"
    version = providers.environmentVariable("VERSION").orElse("2.1.0-ohos-2.2.21-9").get()
    plugins.apply("maven-publish")
    extensions.configure<org.gradle.api.publish.PublishingExtension> {
        publications.withType<org.gradle.api.publish.maven.MavenPublication>().configureEach {
            groupId = "com.github.gycrosskit.stately-ohos"
        }
        repositories.maven {
            name = "gycrosskit"
            url = uri(providers.gradleProperty("gycrosskitMavenRepo").orElse(rootProject.file("../build/release-maven").absolutePath).get())
        }
    }
}
