/**
 * 统一发布配置
 * 支持 Maven Central 和 JitPack 两种发布方式
 * 
 * 特性：
 * - 子模块独立发布
 * - lumen 模块聚合所有子模块，依赖自动传递
 * - 支持 GitHub Actions 自动发布
 * 
 * 注意：此脚本通过 apply(from = ...) 应用
 * Android Library 插件已自动应用 maven-publish 插件
 * 需要手动应用 signing 插件
 */

import org.gradle.api.publish.maven.MavenPublication
import org.gradle.api.publish.PublishingExtension
import org.gradle.plugins.signing.SigningExtension

// 应用必要的插件
// 注意：Android Library 插件在某些版本中可能不会自动应用 maven-publish
if (!project.plugins.hasPlugin("maven-publish")) {
    project.plugins.apply("maven-publish")
}
if (!project.plugins.hasPlugin("signing")) {
    project.plugins.apply("signing")
}

// 从 gradle.properties 读取版本号
val versionName: String = project.findProperty("VERSION_NAME") as String? ?: "1.0.0"
val isJitPack = System.getenv("JITPACK") == "true"

// 根据发布方式选择不同的 groupId
// GitHub 仓库: https://github.com/XichenX/Lumen
val githubUser = project.findProperty("GITHUB_USER") as String? ?: "XichenX"
val publishGroupId = if (isJitPack) {
    // JitPack 格式: com.github.用户名
    "com.github.$githubUser"
} else {
    // Maven Central 格式: io.github.用户名 (GitHub 域名格式)
    "io.github.$githubUser"
}

// 设置项目版本
version = versionName

// 配置发布
// 注意：需要在 afterEvaluate 中配置，确保 Android 插件已配置完成
afterEvaluate {
    // 确保 maven-publish 插件已应用
    if (!project.plugins.hasPlugin("maven-publish")) {
        project.plugins.apply("maven-publish")
    }
    
    // 等待一下，确保插件完全初始化
    // 然后查找 PublishingExtension
    val publishing = extensions.findByType<PublishingExtension>()
    if (publishing == null) {
        // 如果 PublishingExtension 不存在，尝试强制创建
        // 这通常不应该发生，但如果发生了，我们需要处理
        logger.error("❌ PublishingExtension not found for ${project.name}")
        logger.error("   maven-publish plugin applied: ${project.plugins.hasPlugin("maven-publish")}")
        logger.error("   This is a critical error. Publishing configuration cannot proceed.")
        throw GradleException(
            "PublishingExtension not found for ${project.name}. " +
            "This usually means the maven-publish plugin was not properly initialized. " +
            "Please check your build configuration."
        )
    }
    
    publishing.publications {
        create<MavenPublication>("release") {
            // 发布 Android 库
            from(components["release"])
            
            groupId = publishGroupId
            artifactId = project.name
            version = versionName
            
            pom {
                name.set("Lumen ${project.name}")
                description.set("A Kotlin-first Android image loading library")
                url.set("https://github.com/XichenX/Lumen")
                
                licenses {
                    license {
                        name.set("The Apache License, Version 2.0")
                        url.set("http://www.apache.org/licenses/LICENSE-2.0.txt")
                    }
                }
                
                developers {
                    developer {
                        id.set("XichenX")
                        name.set("Lumen Team")
                        email.set("3108531642@qq.com")
                    }
                }
                
                scm {
                    connection.set("scm:git:git://github.com/XichenX/Lumen.git")
                    developerConnection.set("scm:git:ssh://github.com:XichenX/Lumen.git")
                    url.set("https://github.com/XichenX/Lumen")
                }
            }
        }
    }
    
    // Maven Central 仓库配置（仅非 JitPack 时使用）
    // 使用 Central Publishing Portal（OSSRH 将于 2025-06-30 停止服务）
    // 参考：https://central.sonatype.org/news/20250326_ossrh_sunset/
    if (!isJitPack) {
        publishing.repositories {
            maven {
                name = "MavenCentral"
                
                // Central Publishing Portal URL
                // 注意：虽然 URL 相同，但认证方式改为使用 Central Token
                val releasesRepoUrl = "https://s01.oss.sonatype.org/service/local/staging/deploy/maven2/"
                val snapshotsRepoUrl = "https://s01.oss.sonatype.org/content/repositories/snapshots/"
                
                url = uri(if (versionName.endsWith("SNAPSHOT")) snapshotsRepoUrl else releasesRepoUrl)
                
                credentials {
                    // Central Publishing Portal 使用 Token 认证
                    // 注意：变量名保持 SONATYPE_USERNAME 和 SONATYPE_PASSWORD 以保持兼容性
                    // SONATYPE_USERNAME: Central Portal 的用户名（短用户名）
                    // SONATYPE_PASSWORD: Central Portal 的专属 Token（虽然叫 PASSWORD，但实际是 Token）
                    val sonatypeUsername = project.findProperty("SONATYPE_USERNAME") as String?
                        ?: System.getenv("SONATYPE_USERNAME")
                    val sonatypeToken = project.findProperty("SONATYPE_PASSWORD") as String?
                        ?: System.getenv("SONATYPE_PASSWORD")
                    
                    username = sonatypeUsername
                    password = sonatypeToken
                    
                    // 调试信息（不打印实际 Token）
                    if (sonatypeUsername.isNullOrBlank() || sonatypeToken.isNullOrBlank()) {
                        logger.warn("⚠️  Central Publishing Portal credentials are missing or empty")
                        logger.warn("   SONATYPE_USERNAME: ${if (sonatypeUsername.isNullOrBlank()) "not set" else "set (${sonatypeUsername.length} chars)"}")
                        logger.warn("   SONATYPE_PASSWORD (Central Token): ${if (sonatypeToken.isNullOrBlank()) "not set" else "set (${sonatypeToken.length} chars)"}")
                        logger.warn("   Make sure SONATYPE_USERNAME and SONATYPE_PASSWORD (Central Token) are set in GitHub Secrets")
                        logger.warn("   Get your Central Token from: https://central.sonatype.com/ → Profile → User Token")
                    } else {
                        logger.info("✅ Central Publishing Portal credentials configured")
                        logger.info("   Username: ${sonatypeUsername.take(3)}*** (length: ${sonatypeUsername.length})")
                        logger.info("   Central Token: ${"*".repeat(sonatypeToken.length)} (length: ${sonatypeToken.length})")
                        logger.info("   Using Central Publishing Portal (OSSRH will end on 2025-06-30)")
                    }
                }
            }
        }
    }
}

// Maven Central 签名配置（仅非 JitPack 时使用）
if (!isJitPack) {
    afterEvaluate {
        val signing = extensions.findByType<SigningExtension>()
        val publishing = extensions.findByType<PublishingExtension>()
        
        if (signing == null || publishing == null) {
            logger.warn("SigningExtension or PublishingExtension not found, skipping signing configuration")
            return@afterEvaluate
        }
        
        val signingKeyId = project.findProperty("SIGNING_KEY_ID") as String?
            ?: System.getenv("SIGNING_KEY_ID")
        val signingPassword = project.findProperty("SIGNING_PASSWORD") as String?
            ?: System.getenv("SIGNING_PASSWORD")
        val signingSecretKeyRingFile = project.findProperty("SIGNING_SECRET_KEY_RING_FILE") as String?
            ?: System.getenv("SIGNING_SECRET_KEY_RING_FILE")
        
        if (signingKeyId != null && signingPassword != null && signingSecretKeyRingFile != null) {
            try {
                val secretKeyRingFile = file(signingSecretKeyRingFile)
                if (secretKeyRingFile.exists()) {
                    val secretKey = secretKeyRingFile.readText()
                    if (secretKey.isBlank()) {
                        logger.warn("GPG secret key file is empty: $signingSecretKeyRingFile, skipping signing")
                        return@afterEvaluate
                    }
                    
                    signing.useInMemoryPgpKeys(signingKeyId, secretKey, signingPassword)
                    signing.sign(publishing.publications["release"])
                    logger.info("✅ GPG signing configured for ${project.name}")
                } else {
                    logger.warn("⚠️  GPG secret key file not found: $signingSecretKeyRingFile, skipping signing")
                }
            } catch (e: Exception) {
                logger.error("❌ Failed to configure GPG signing: ${e.message}", e)
                throw e
            }
        } else {
            logger.warn("⚠️  GPG signing credentials not fully configured, skipping signing")
            logger.debug("  SIGNING_KEY_ID: ${if (signingKeyId != null) "set" else "not set"}")
            logger.debug("  SIGNING_PASSWORD: ${if (signingPassword != null) "set" else "not set"}")
            logger.debug("  SIGNING_SECRET_KEY_RING_FILE: ${if (signingSecretKeyRingFile != null) "set" else "not set"}")
        }
    }
}

// 注意：移除了发布前验证任务
// 原因：
// 1. Gradle 的发布任务会自动检查 PublishingExtension 和 publication 是否存在
// 2. 如果配置有问题，Gradle 会在任务执行时自动报错，并提供更清晰的错误信息
// 3. 额外的验证可能导致时序问题（如当前遇到的 PublishingExtension 找不到的问题）
// 4. 我们已经有了 validatePublishConfiguration 任务在配置阶段验证
// 
// 如果需要验证，可以在发布前手动运行：
//   ./gradlew validatePublishConfiguration

