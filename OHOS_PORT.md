# Stately OHOS 适配

**最终核对入口（2026-10-08）**：[测试源码、执行平台、前轮复用与本轮重跑](docs/功能与平台差异.md#验证结果与测试入口)。下列构建/发布命令与已有版本记录保留原范围，不能套用为候选新执行结果。

2026-10-08 的 Core/CMP/Kuikly 消费映射、候选修复和未验收范围见[功能与平台差异](docs/功能与平台差异.md)；下文已有 tag 的验收保留为历史事实，修复交付版本为 `2.1.0-ohos-2.2.21-11`，发布状态以 Release 为准。

此分支基于上游 `2.1.0`，使用 Kotlin `2.2.21-1.0.0`，发布 JVM（Android 使用）、`iosArm64`、`iosSimulatorArm64`、`iosX64` 和 `ohosArm64` 变体。三端共用请使用 `-11`。

消费者的模块选择、坐标及官方依赖替换见 [README](README.md#安装)。

产物包括 `stately-strict`、`stately-concurrency`、`stately-concurrent-collections` 及 Gradle Module Metadata。iOS KLIB 必须在 macOS 上构建；发布版由 GitHub Release 保存构建包，再由 JitPack 提供 Maven 依赖。JVM/iOS 沿用上游源码，鸿蒙 `Lock` 复用 POSIX/Linux 实现；Native 线程标注与旧内存模型辅助函数适配 Kotlin 2.2。
构建时使用上游 `co.touchlab` 生成 KLIB 身份，Maven Publication 则使用 JitPack 所需的 `com.github.gycrosskit.stately-ohos`；不要把项目 `group` 改为 JitPack 组名，否则官方 Native 依赖的 `depends` 无法正确匹配。

```bash
VERSION=2.1.0-ohos-2.2.21-11 \
  bash gradlew -p stately-probe publishAllPublicationsToGycrosskitRepository \
  -PgycrosskitMavenRepo=/path/to/staging
python3 prepare-jitpack-maven.py /path/to/staging
COPYFILE_DISABLE=1 tar --no-xattrs -czf stately-ohos-maven.tar.gz -C /path/to/staging .
```

`2.1.0-ohos-2.2.21-10` 的真实 JitPack 坐标已由 [Koin 独立消费工程](https://github.com/gycrosskit/koin-ohos/tree/codex/ohos-4.1.1/verification-consumer) 验证：Android 编译、iOS 三目标编译与 arm64/模拟器 Framework 链接、OHOS 动态库链接及 JVM 注入运行检查。官方 Koin Compose 4.1.1 与 fork 在默认 Native 缓存下也已链接。尚未验证 iOS/OHOS 设备运行。

新版本归档已在发布前规范化根 `metadataSourcesElements` 并重算校验和；JitPack 安装器只安装校验后的相同字节。18 个真实远程 publication 的 POM、全部变体文件、大小、四种声明哈希、ZIP CRC、available-at 与内部依赖均通过，Release 重下载 SHA-256 一致。JitPack 额外生成的 root identity redirect 和高阶 sidecar 返回 404，作为渠道边界单独记录；公开 MD5/SHA-1 及所有必要变体引用正常。回归入口：`python3 scripts/test-jitpack-metadata.py`。旧标签和归档未覆盖。
