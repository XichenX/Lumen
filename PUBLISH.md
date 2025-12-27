# Lumen 发布指南

本文档说明如何使用 GitHub Actions 自动发布 Lumen 到 Maven Central 和 JitPack。

## 🎯 发布方式

Lumen 支持两种发布方式，都通过 GitHub Actions 自动完成：

1. **Maven Central**（主要发布方式）⭐
   - ✅ 官方 Maven 仓库
   - ✅ GitHub Actions 自动发布
   - ✅ 需要 Sonatype 账号和 GPG 签名

2. **JitPack**（备用发布方式）
   - ✅ 无需配置，自动构建
   - ✅ GitHub Actions 自动通知
   - ✅ 免费使用

## 🚀 快速开始

### 一键发布流程

**方式一：自动递增版本号（推荐）** - 只需打 Tag，自动递增 patch 版本

```bash
# 当前代码库版本：1.0.0
# 打 tag：v1.0.0 或 v1.0.0/develop
# GitHub Actions 会自动：
# 1. 从 Tag 提取基础版本号（如 v1.0.0/develop → 1.0.0）
# 2. 检测到版本相同，自动递增 patch：1.0.0 → 1.0.1
# 3. 更新 gradle.properties 并发布 1.0.1
# 4. 自动提交版本号变更回代码库

git tag -a v1.0.0 -m "Release v1.0.0"
git push origin v1.0.0
# 或
git tag -a v1.0.0/develop -m "Release from develop branch"
git push origin v1.0.0/develop
```

**方式二：指定版本号** - 直接指定要发布的版本

```bash
# 打 tag 指定版本号
git tag -a v1.0.5 -m "Release v1.0.5"
git push origin v1.0.5
# GitHub Actions 会直接使用 1.0.5 版本发布
```

**方式二：完整流程** - 手动更新版本号并提交（可选，用于保持代码库版本号一致）

```bash
# 1. 更新版本号（gradle.properties）
# 编辑 gradle.properties，设置 VERSION_NAME=1.0.0

# 2. 提交代码（可选，但推荐）
git add gradle.properties
git commit -m "Bump version to 1.0.0"
git push origin main

# 3. 打 Tag（自动触发 GitHub Actions）
git tag -a v1.0.0 -m "Release v1.0.0"
git push origin v1.0.0
```

**GitHub Actions 会自动：**
- ✅ 从 Tag 提取版本号（支持 `v1.0.0` 和 `v1.0.0/develop` 格式）
- ✅ **智能版本管理**：
  - 如果 Tag 版本 = 当前版本：自动递增 patch 版本（如 `1.0.0` → `1.0.1`）
  - 如果 Tag 版本 ≠ 当前版本：使用 Tag 指定的版本号
- ✅ 自动更新 `gradle.properties` 中的 VERSION_NAME
- ✅ 构建所有模块
- ✅ 运行测试
- ✅ 发布到 Maven Central（如果配置了）
- ✅ **自动提交版本号变更回代码库**（保持代码库版本号与发布版本一致）
- ✅ 创建 GitHub Release
- ✅ 上传构建产物

**版本管理说明：**
- **自动递增**：当 tag 版本与代码库版本相同时，自动递增 patch 版本，避免版本冲突
- **指定版本**：可以直接在 tag 中指定版本号（如 `v1.0.5`），GitHub Actions 会使用该版本
- **分支关联**：支持 `v1.0.0/develop` 格式，版本号会与分支关联

详细版本管理策略请查看 [VERSION_MANAGEMENT.md](VERSION_MANAGEMENT.md)

## ⚙️ 首次配置

### 1. 配置 GitHub Secrets

进入仓库 **Settings** → **Secrets and variables** → **Actions**，添加以下 Secrets：

#### Maven Central 发布所需

**SONATYPE_USERNAME**
- 名称: `SONATYPE_USERNAME`（保持不变，无需重命名）
- 值: 你的 **Central Publishing Portal 用户名**（短用户名）
- 📝 从 https://central.sonatype.com/ → Profile 获取

**SONATYPE_PASSWORD**
- 名称: `SONATYPE_PASSWORD`（保持不变，无需重命名）
- 值: 你的 **Central Publishing Portal 专属 Token**（虽然变量名叫 PASSWORD，但实际存储的是 Token）
- ⚠️ **重要**：必须使用 Central Token，不能使用密码
- 💡 **提示**：变量名保持 `SONATYPE_PASSWORD` 是为了向后兼容，但实际值应该是 Central Token
- 📝 **获取 Token**：
  1. 登录 https://central.sonatype.com/
  2. 点击右上角用户名 → Profile
  3. 找到 User Token 部分
  4. 点击 Access User Token 或 Generate User Token
  5. 复制生成的 Token（这是你的专属 Central Token）
  6. 将 Token 设置为 `SONATYPE_PASSWORD` 的值
- ⚠️ **注意**：OSSRH 将于 2025-06-30 停止服务，必须使用 Central Publishing Portal

**GPG_PRIVATE_KEY**
- 名称: `GPG_PRIVATE_KEY`
- 值: GPG 私钥的 Base64 编码

**GPG_PASSPHRASE**
- 名称: `GPG_PASSPHRASE`
- 值: GPG 密钥的密码

### 2. 生成 GPG 密钥

```bash
# 1. 生成 GPG 密钥
gpg --full-generate-key

# 选择选项：
# - RSA and RSA (default)
# - 4096 bits
# - 密钥永不过期（或设置过期时间）
# - 输入你的信息

# 2. 查看密钥 ID
gpg --list-secret-keys --keyid-format LONG

# 3. 导出私钥（Base64 编码）
gpg --export-secret-keys --armor YOUR_KEY_ID | base64 -w 0

# 4. 将输出的 Base64 字符串复制到 GitHub Secret: GPG_PRIVATE_KEY
```

**Windows (PowerShell):**
```powershell
# 导出私钥
gpg --export-secret-keys --armor YOUR_KEY_ID | Out-File -Encoding ASCII private.key
# 然后使用在线工具或手动转换为 Base64
```

**macOS/Linux:**
```bash
# 导出并 Base64 编码
gpg --export-secret-keys --armor YOUR_KEY_ID | base64 -w 0
```

### 3. 配置 Sonatype 账号

#### ⚠️ 重要：迁移到 Central Publishing Portal

**OSSRH 将于 2025-06-30 停止服务**，所有项目需要迁移到新的 Central Publishing Portal。

**迁移步骤：**

1. **注册 Central Publishing Portal 账户**
   - 访问 https://central.sonatype.com/
   - 使用 GitHub 账户登录（推荐）

2. **注册命名空间**
   - 登录后，在 Portal 中注册您的命名空间（如 `io.github.xichenx`）
   - 如果通过 GitHub 登录，个人命名空间可能已自动注册

3. **生成 User Token**
   - 在 Portal 中：Profile → User Token
   - 生成新的 User Token
   - 将 Token 设置为 `SONATYPE_PASSWORD` 的值（而不是密码）

4. **验证命名空间**
   - 在 Portal 的命名空间页面确认命名空间已验证

**旧方式（OSSRH，将在 2025-06-30 后停止）：**

1. 访问 https://issues.sonatype.org
2. 创建账号（如果还没有）
3. 创建 Issue 申请 GroupId（使用 `io.github.XichenX`，这是 GitHub 域名格式）
4. 等待审核通过
5. 获取用户名和密码

**注意**: 对于 GitHub 仓库，Maven Central 要求使用 `io.github.用户名` 格式的 GroupId。

## 📦 模块说明

### 独立模块

- **lumen-core**: `io.github.xichenx:lumen-core:1.0.0`
- **lumen-view**: `io.github.xichenx:lumen-view:1.0.0`
- **lumen-transform**: `io.github.xichenx:lumen-transform:1.0.0`

### 聚合模块

- **lumen**: `io.github.xichenx:lumen:1.0.0`
  - 通过 `api` 依赖所有子模块
  - 自动传递依赖
  - 用户只需添加一个依赖即可使用所有功能

### 依赖传递

`lumen` 模块使用 `api` 依赖子模块：

```kotlin
dependencies {
    api(project(":lumen-core"))
    api(project(":lumen-view"))
    api(project(":lumen-transform"))
}
```

这确保了：
- 发布 `lumen` 时，POM 文件会自动包含子模块依赖
- 用户只需添加 `lumen` 依赖即可使用所有功能
- 子模块也可以独立使用

## 🔄 发布流程

### Maven Central 发布流程

1. **打 Tag 触发**
   ```bash
   git tag -a v1.0.0 -m "Release v1.0.0"
   git push origin v1.0.0
   ```

2. **GitHub Actions 自动执行**
   - 自动提取版本号
   - 自动更新 `gradle.properties`
   - 自动构建和测试
   - 自动发布到 Maven Central

3. **完成 Sonatype 发布**
   - 登录 https://s01.oss.sonatype.org
   - 进入 Staging Repositories
   - 找到你的仓库，点击 **Close**
   - 等待验证通过后，点击 **Release**
   - 等待同步到 Maven Central（通常几小时）

### JitPack 发布流程

1. **打 Tag 触发**
   ```bash
   git tag -a v1.0.0 -m "Release v1.0.0"
   git push origin v1.0.0
   ```

2. **JitPack 自动构建**
   - JitPack 自动检测 Tag
   - 自动构建所有模块
   - 自动发布到 JitPack 仓库

3. **查看构建状态**
   - 访问 https://jitpack.io/#MartinLiuMingZhi/Lumen

## 📝 使用方式

### Maven Central

```kotlin
repositories {
    mavenCentral()
}

dependencies {
    // 聚合模块（推荐）- 自动包含所有子模块
    implementation("io.github.xichenx:lumen:1.0.0")
    
    // 或独立模块
    implementation("io.github.xichenx:lumen-core:1.0.0")
    implementation("io.github.xichenx:lumen-view:1.0.0")
    implementation("io.github.xichenx:lumen-transform:1.0.0")
}
```

### JitPack

```kotlin
repositories {
    maven { url = uri("https://jitpack.io") }
}

dependencies {
    // 聚合模块（推荐）- 自动包含所有子模块
    implementation("com.github.XichenX:Lumen:v1.0.0")
    
    // 或独立模块
    implementation("com.github.XichenX:lumen-core:v1.0.0")
    implementation("com.github.XichenX:lumen-view:v1.0.0")
    implementation("com.github.XichenX:lumen-transform:v1.0.0")
}
```

## 🔍 查看发布状态

- **GitHub Actions**: https://github.com/XichenX/Lumen/actions
- **Maven Central**: https://repo1.maven.org/maven2/io/github/xichenx/
- **Sonatype Staging**: https://s01.oss.sonatype.org
- **JitPack**: https://jitpack.io/#XichenX/Lumen

## 🐛 故障排查

### GitHub Actions 失败

**问题**: 构建失败
- 检查 Actions 日志
- 确认 Gradle 版本兼容
- 检查 Java 版本设置

**问题**: GPG 签名失败
- 确认 `GPG_PRIVATE_KEY` 是 Base64 编码
- 确认 `GPG_PASSPHRASE` 正确
- 检查密钥是否过期

**问题**: Sonatype 发布失败（401 错误）
- 确认 `SONATYPE_USERNAME` 和 `SONATYPE_PASSWORD` 正确
- ⚠️ **如果使用 Central Publishing Portal**：确保使用 User Token 而不是密码
- 检查命名空间是否已在 Portal 中注册和验证
- 参考：[Central Publishing Portal 迁移指南](https://central.sonatype.org/news/20250326_ossrh_sunset/)
- 确认 GroupId 已申请并通过审核
- 检查 POM 文件是否正确生成

### JitPack 构建失败

**问题**: JitPack 无法构建
- 检查 `jitpack.yml` 配置
- 查看 JitPack 构建日志
- 确认 Tag 格式正确（`v*`）

## 📋 发布检查清单

发布前请确认：

- [ ] 版本号已更新（`gradle.properties` 中的 `VERSION_NAME`）
- [ ] 所有测试通过
- [ ] README 已更新
- [ ] CHANGELOG 已更新（如果有）
- [ ] 代码已提交并推送
- [ ] Tag 已创建并推送
- [ ] GitHub Secrets 已配置（Maven Central）
- [ ] Sonatype 账号已配置（Maven Central）

## 📚 参考链接

- [GitHub Actions 文档](https://docs.github.com/en/actions)
- [Maven Central 发布指南](https://central.sonatype.org/publish/publish-guide/)
- [JitPack 文档](https://jitpack.io/docs/)
- [Gradle 发布插件文档](https://docs.gradle.org/current/userguide/publishing_maven.html)

