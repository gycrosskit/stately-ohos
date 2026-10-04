# Stately OHOS 适配

此分支基于上游 `2.1.0`，使用 Kotlin `2.2.21-1.0.0`，发布 JVM（Android 使用）、`iosArm64`、`iosSimulatorArm64`、`iosX64` 和 `ohosArm64` 变体。三端共用请使用 `-10`。

消费者的模块选择、坐标及官方依赖替换见 [README](README.md#安装)。

产物包括 `stately-strict`、`stately-concurrency`、`stately-concurrent-collections` 及 Gradle Module Metadata。iOS KLIB 必须在 macOS 上构建；发布版由 GitHub Release 保存构建包，再由 JitPack 提供 Maven 依赖。JVM/iOS 沿用上游源码，鸿蒙 `Lock` 复用 POSIX/Linux 实现；Native 线程标注与旧内存模型辅助函数适配 Kotlin 2.2。
构建时使用上游 `co.touchlab` 生成 KLIB 身份，Maven Publication 则使用 JitPack 所需的 `com.github.gycrosskit.stately-ohos`；不要把项目 `group` 改为 JitPack 组名，否则官方 Native 依赖的 `depends` 无法正确匹配。

```bash
VERSION=2.1.0-ohos-2.2.21-10 \
  bash gradlew -p stately-probe publishAllPublicationsToGycrosskitRepository \
  -PgycrosskitMavenRepo=/path/to/staging
python3 prepare-jitpack-maven.py /path/to/staging
COPYFILE_DISABLE=1 tar --no-xattrs -czf stately-ohos-maven.tar.gz -C /path/to/staging .
```

三端消费由 [Koin 独立消费工程](https://github.com/gycrosskit/koin-ohos/tree/codex/ohos-4.1.1/verification-consumer) 验证：Android 编译、iOS 编译/模拟器 Framework 链接、OHOS 动态库链接及 JVM 注入运行检查。尚未验证 iOS/OHOS 设备运行。

本次发布准备补齐根 `metadataSourcesElements` 正规化，并在归档前重算已有 `.module` 校验和；JitPack 安装器不再改写归档字节。已发布版本的 API/Native 变体可用，但来源变体 URL 被改写为 API JAR；不能把编译通过视为所有变体正确。新版本须重新构建归档、核对引用/校验值并更新安装器 SHA，不覆盖旧标签或归档。回归入口：`python3 scripts/test-jitpack-metadata.py`。
