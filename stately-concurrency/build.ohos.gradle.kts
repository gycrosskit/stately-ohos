plugins {
    id("org.jetbrains.kotlin.multiplatform")
}

kotlin {
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
