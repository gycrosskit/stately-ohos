pluginManagement {
    repositories {
        maven("https://maven.eazytec-cloud.com/nexus/repository/maven-public/")
        maven("https://mirrors.tencent.com/nexus/repository/maven-public/")
        gradlePluginPortal()
        mavenCentral()
    }
}

dependencyResolutionManagement {
    repositories {
        maven("https://maven.eazytec-cloud.com/nexus/repository/maven-public/")
        maven("https://mirrors.tencent.com/nexus/repository/maven-public/")
        mavenCentral()
    }
}

rootProject.name = "stately-ohos-probe"
listOf("stately-strict", "stately-concurrency", "stately-concurrent-collections").forEach { name ->
    include(":$name")
    project(":$name").projectDir = file("../$name")
    project(":$name").buildFileName = "build.ohos.gradle.kts"
}
