plugins {
    id("org.jetbrains.kotlin.multiplatform")
}

kotlin {
    ohosArm64()
    sourceSets {
        commonMain.dependencies {
            api(project(":stately-concurrency"))
        }
    }
}
