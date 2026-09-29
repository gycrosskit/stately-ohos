# Stately OHOS 适配

此分支基于上游 `2.1.0`，使用 Kotlin `2.2.21-1.0.0`，发布 JVM（Android 使用）、`iosArm64`、`iosSimulatorArm64`、`iosX64` 和 `ohosArm64` 变体。三端共用请使用 `-8`。

```kotlin
// settings.gradle.kts 的 dependencyResolutionManagement.repositories
maven { url = uri("https://jitpack.io") }

// commonMain.dependencies（按需选择）
implementation("com.github.gycrosskit.stately-ohos:stately-concurrency:2.1.0-ohos-2.2.21-8")
implementation("com.github.gycrosskit.stately-ohos:stately-concurrent-collections:2.1.0-ohos-2.2.21-8")
```

产物包括 `stately-strict`、`stately-concurrency`、`stately-concurrent-collections` 及 Gradle Module Metadata。iOS KLIB 必须在 macOS 上构建；发布版由 GitHub Release 保存构建包，再由 JitPack 提供 Maven 依赖。JVM/iOS 沿用上游源码，鸿蒙 `Lock` 复用 POSIX/Linux 实现；Native 线程标注与旧内存模型辅助函数适配 Kotlin 2.2。
构建时使用上游 `co.touchlab` 生成 KLIB 身份，Maven Publication 则使用 JitPack 所需的 `com.github.gycrosskit.stately-ohos`；不要把项目 `group` 改为 JitPack 组名，否则官方 Native 依赖的 `depends` 无法正确匹配。

```bash
VERSION=2.1.0-ohos-2.2.21-8 \
  bash gradlew -p stately-probe publishAllPublicationsToGycrosskitRepository \
  -PgycrosskitMavenRepo=/path/to/staging
```

三端消费由 `koin-ohos/verification-consumer` 验证：Android 编译、iOS 编译/模拟器 Framework 链接、OHOS 动态库链接及 JVM 注入运行检查。尚未验证 iOS/OHOS 设备运行。
