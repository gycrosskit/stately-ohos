plugins {
    id("org.jetbrains.kotlin.multiplatform") version "2.2.21-1.0.0" apply false
}

subprojects {
    group = "co.touchlab"
    version = "2.1.0-ohos-2.2.21-2"
    plugins.apply("maven-publish")
    extensions.configure<org.gradle.api.publish.PublishingExtension> {
        repositories.maven {
            name = "gycrosskit"
            url = uri(providers.gradleProperty("gycrosskitMavenRepo").orElse(rootProject.file("../docs/maven").absolutePath).get())
        }
    }
}
