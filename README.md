# Stately：GY CrossKit 分支说明

这是 [Touchlab/Stately](https://github.com/touchlab/Stately) 的分支仓库。Stately 为 Kotlin Multiplatform 提供并发原语与线程安全集合。默认 `main` 跟随上游代码，**不包含** GY CrossKit 的 OpenHarmony 适配；上游英文介绍保存在 [README_EN.md](README_EN.md)。

## OpenHarmony 适配

适配代码与发布配置位于 [`codex/ohos-2.1.0` 分支](https://github.com/gycrosskit/stately-ohos/tree/codex/ohos-2.1.0)。请以该分支的 [OHOS_PORT.md](https://github.com/gycrosskit/stately-ohos/blob/codex/ohos-2.1.0/OHOS_PORT.md) 查看 JitPack 坐标、支持目标和验证范围；不要使用默认 `main` 的构建结果判断鸿蒙版本是否可用。

普通 Stately 用法及上游版本更新请参考 [Touchlab/Stately](https://github.com/touchlab/Stately)。许可证见 [LICENSE.txt](LICENSE.txt)。
