# Stately OpenHarmony 适配

本分支基于上游 Stately 2.1.0，使用 Kotlin `2.2.21-1.0.0`，为 Android、iOS、OpenHarmony 的 KMP 共享模块提供并发原语与线程安全集合。详细构建与验证说明见 [OHOS_PORT.md](OHOS_PORT.md)；上游英文介绍保存在 [README_EN.md](README_EN.md)。

## 引入依赖

在 `settings.gradle.kts` 的 `dependencyResolutionManagement.repositories` 中加入 JitPack：

```kotlin
maven { url = uri("https://jitpack.io") }
```

在共享模块中按需引入：

```kotlin
commonMain.dependencies {
    implementation("com.github.gycrosskit.stately-ohos:stately-concurrency:2.1.0-ohos-2.2.21-7")
    implementation("com.github.gycrosskit.stately-ohos:stately-concurrent-collections:2.1.0-ohos-2.2.21-7")
}
```

此版本还发布 `stately-strict`，提供 JVM（供 Android 使用）、`iosArm64`、`iosSimulatorArm64`、`iosX64` 和 `ohosArm64` 变体。iOS KLIB 在 macOS 构建，版本化归档由 GitHub Release 保存，再经 JitPack 提供 Maven 依赖。

## 验证范围

三端消费通过 Koin 仓库的独立验证工程检查：Android 编译、iOS 编译与模拟器 Framework 链接、OHOS 动态库链接，以及 JVM 注入运行检查。iOS/OHOS 设备运行尚未验证。

许可证见 [LICENSE.txt](LICENSE.txt)。
