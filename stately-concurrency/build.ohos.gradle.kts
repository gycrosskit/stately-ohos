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
            api(project(":stately-strict"))
        }
        ohosArm64Main {
            // OHOS 提供 POSIX pthread，沿用已有 Linux Lock 实现。
            kotlin.srcDir("src/linuxMain/kotlin")
        }
    }
}
