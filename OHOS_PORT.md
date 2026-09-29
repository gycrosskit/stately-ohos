# Stately OHOS 适配

此分支基于上游 `2.1.0`，仅发布 `ohosArm64` KMP 变体，供 Koin Core 鸿蒙适配使用。使用 Kotlin `2.2.21-1.0.0`。Android/iOS 继续使用上游正式版本。

```kotlin
// settings.gradle.kts 的 dependencyResolutionManagement.repositories
maven { url = uri("https://gycrosskit.github.io/stately-ohos/maven") }

// commonMain.dependencies（按需选择）
implementation("co.touchlab:stately-concurrency:2.1.0-ohos-2.2.21-1")
implementation("co.touchlab:stately-concurrent-collections:2.1.0-ohos-2.2.21-1")
```

产物包括 `stately-strict`、`stately-concurrency`、`stately-concurrent-collections` 及其 Gradle Module Metadata，发布目录为 `docs/maven`。鸿蒙 `Lock` 使用现有 POSIX/Linux 实现；Native 线程标注和冻结辅助函数按当前 Kotlin/Native 编译器调整。

```bash
bash ../koin-ohos/projects/gradlew -p stately-probe :stately-concurrent-collections:compileKotlinOhosArm64
bash ../koin-ohos/projects/gradlew -p stately-probe :stately-strict:publishAllPublicationsToGycrosskitRepository :stately-concurrency:publishAllPublicationsToGycrosskitRepository :stately-concurrent-collections:publishAllPublicationsToGycrosskitRepository
```

尚未验证设备运行或 Android/iOS 变体。本分支只保证上述 OHOS 产物的编译与依赖解析。
