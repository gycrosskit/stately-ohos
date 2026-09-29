plugins {
    id("org.jetbrains.kotlin.multiplatform")
}

kotlin {
    jvm { compilerOptions.jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_1_8) }
    iosArm64()
    iosSimulatorArm64()
    iosX64()
    ohosArm64()
    sourceSets {
        commonMain.dependencies {
            api(project(":stately-concurrency"))
        }
    }
}
