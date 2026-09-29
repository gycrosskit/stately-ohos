plugins {
    id("org.jetbrains.kotlin.multiplatform") version "2.2.21-1.0.0" apply false
}

subprojects {
    group = providers.environmentVariable("RELEASE_GROUP").orElse("co.touchlab").get()
    version = providers.environmentVariable("VERSION").orElse("2.1.0-ohos-2.2.21-3").get()
    plugins.apply("maven-publish")
    extensions.configure<org.gradle.api.publish.PublishingExtension> {
        repositories.maven {
            name = "gycrosskit"
            url = uri(providers.gradleProperty("gycrosskitMavenRepo").orElse(rootProject.file("../docs/maven").absolutePath).get())
        }
    }
}
