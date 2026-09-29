# Stately OHOS 适配

此分支基于上游 `2.1.0`，使用 Kotlin `2.2.21-1.0.0`，发布 JVM（Android 使用）、`iosArm64`、`iosSimulatorArm64`、`iosX64` 和 `ohosArm64` 变体。`-1` 为历史 OHOS 单目标版本；三端共用请使用 `-2`。

```kotlin
// settings.gradle.kts 的 dependencyResolutionManagement.repositories
maven { url = uri("https://gycrosskit.github.io/stately-ohos/maven") }

// commonMain.dependencies（按需选择）
implementation("co.touchlab:stately-concurrency:2.1.0-ohos-2.2.21-2")
implementation("co.touchlab:stately-concurrent-collections:2.1.0-ohos-2.2.21-2")
```

产物包括 `stately-strict`、`stately-concurrency`、`stately-concurrent-collections` 及 Gradle Module Metadata，发布目录为 `docs/maven`。JVM/iOS 沿用上游源码，鸿蒙 `Lock` 复用 POSIX/Linux 实现；Native 线程标注与旧内存模型辅助函数适配 Kotlin 2.2。

```bash
bash gradlew -p stately-probe publishAllPublicationsToGycrosskitRepository
```

三端消费由 `koin-ohos/verification-consumer` 验证：Android 编译、iOS 编译/模拟器 Framework 链接、OHOS 动态库链接及 JVM 注入运行检查。尚未验证 iOS/OHOS 设备运行。
