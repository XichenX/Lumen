# GitHub Actions 自动发布 - 快速开始

## 🎯 一键发布（最简单方式）

### 自动递增版本号（推荐）

**只需打 Tag，GitHub Actions 会自动递增版本号！**

```bash
# 当前代码库版本：1.0.0
# 打 tag：v1.0.0 或 v1.0.0/develop
# GitHub Actions 会自动：
# - 检测到版本相同，自动递增：1.0.0 → 1.0.1
# - 构建和发布 1.0.1
# - 自动提交版本号变更回代码库

git tag -a v1.0.0 -m "Release v1.0.0"
git push origin v1.0.0

# 或从 develop 分支发布
git tag -a v1.0.0/develop -m "Release from develop"
git push origin v1.0.0/develop
```

### 指定版本号

```bash
# 直接指定版本号
git tag -a v1.0.5 -m "Release v1.0.5"
git push origin v1.0.5
# GitHub Actions 会直接使用 1.0.5 版本发布
```

**就这么简单！** 🚀

**智能版本管理：**
- ✅ 自动递增：tag 版本与当前版本相同时，自动递增 patch 版本
- ✅ 指定版本：可以直接在 tag 中指定版本号
- ✅ 分支关联：支持 `v1.0.0/develop` 格式，版本号与分支关联

详细版本管理策略请查看 [VERSION_MANAGEMENT.md](VERSION_MANAGEMENT.md)

### 可选：手动更新版本号（推荐用于正式发布）

如果你想保持代码库中的版本号与发布版本一致：

```bash
# 1. 更新版本号（可选）
# 编辑 gradle.properties，设置 VERSION_NAME=1.0.0

# 2. 提交代码（可选）
git add gradle.properties
git commit -m "Bump version to 1.0.0"
git push origin main

# 3. 打 Tag
git tag -a v1.0.0 -m "Release v1.0.0"
git push origin v1.0.0
```

## ⚙️ 首次配置（只需一次）

### 1. 配置 GitHub Secrets

进入仓库 **Settings** → **Secrets and variables** → **Actions**，添加：

- `SONATYPE_USERNAME` - Sonatype 用户名
- `SONATYPE_PASSWORD` - Sonatype 密码
- `GPG_PRIVATE_KEY` - GPG 私钥（Base64 编码）
- `GPG_PASSPHRASE` - GPG 密钥密码

### 2. 获取 GPG 私钥

```bash
# 导出私钥并 Base64 编码
gpg --export-secret-keys --armor YOUR_KEY_ID | base64 -w 0
```

详细说明请查看 [PUBLISH.md](PUBLISH.md)

## 📦 发布结果

### Maven Central（主要）

发布后，用户可以使用：

```kotlin
repositories {
    mavenCentral()
}

dependencies {
    // 聚合模块（推荐）- 自动包含所有子模块
    implementation("io.github.xichenx:lumen:1.0.0")
}
```

### JitPack（备用）

JitPack 会自动构建，用户可以使用：

```kotlin
repositories {
    maven { url = uri("https://jitpack.io") }
}

dependencies {
    // 聚合模块（推荐）
    implementation("com.github.XichenX:Lumen:v1.0.0")
}
```

## 🔍 查看状态

- **GitHub Actions**: https://github.com/XichenX/Lumen/actions
- **Maven Central**: https://repo1.maven.org/maven2/io/github/xichenx/
- **JitPack**: https://jitpack.io/#XichenX/Lumen

## 📚 更多信息

- [完整发布指南](PUBLISH.md)
- [GitHub Actions 工作流](.github/workflows/)

