plugins {
    id("org.jetbrains.kotlin.multiplatform") version "2.2.21-1.0.0" apply false
}

subprojects {
    // Native KLIB names must match upstream dependencies compiled into Koin Compose.
    group = "co.touchlab"
    version = providers.environmentVariable("VERSION").orElse("2.1.0-ohos-2.2.21-10").get()
    plugins.apply("maven-publish")
    extensions.configure<org.gradle.api.publish.PublishingExtension> {
        publications.withType<org.gradle.api.publish.maven.MavenPublication>().configureEach {
            groupId = "com.github.gycrosskit.stately-ohos"
            pom {
                name.set(project.name)
                description.set("OpenHarmony-compatible upstream library")
                url.set("https://github.com/gycrosskit/stately-ohos")
                licenses {
                    license {
                        name.set("Apache License, Version 2.0")
                        url.set("https://www.apache.org/licenses/LICENSE-2.0.txt")
                        distribution.set("repo")
                    }
                }
            }
        }
        repositories.maven {
            name = "gycrosskit"
            url = uri(providers.gradleProperty("gycrosskitMavenRepo").orElse(rootProject.file("../build/release-maven").absolutePath).get())
        }
    }
}
