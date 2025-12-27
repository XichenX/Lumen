/**
 * 简化的发布配置
 * 使用 com.vanniktech.maven.publish 插件的标准 DSL
 * 
 * 参考: https://vanniktech.github.io/gradle-maven-publish-plugin/central/
 * 
 * 使用方法：
 * 1. 在子模块的 build.gradle.kts 中应用插件：alias(libs.plugins.maven.publish)
 * 2. 应用此脚本：apply(from = rootProject.file("publish.gradle.kts"))
 * 3. 配置凭证（通过环境变量或 gradle.properties）：
 *    - mavenCentralUsername: Sonatype 用户名（插件自动读取）
 *    - mavenCentralPassword: Sonatype 密码（插件自动读取）
 *    - signingInMemoryKey: GPG 密钥内容（插件自动读取）
 *    - signingInMemoryKeyId: GPG 密钥 ID（插件自动读取）
 *    - signingInMemoryKeyPassword: GPG 密钥密码（插件自动读取）
 *    或者通过 SIGNING_SECRET_KEY_RING_FILE 指定密钥文件路径（脚本会自动读取并设置）
 */

// 统一版本号管理：确保 JitPack 和 Maven Central 使用相同的版本号
// 优先使用 LIBRARY_VERSION_NAME（组件版本），如果没有则使用 VERSION_NAME
// 这与 build.gradle.kts 中的版本管理保持一致
val versionName: String = project.findProperty("LIBRARY_VERSION_NAME") as String?
    ?: project.findProperty("VERSION_NAME") as String?
    ?: "1.0.0"
val isJitPack = System.getenv("JITPACK") == "true"

// 根据发布方式选择不同的 groupId
val githubUser = project.findProperty("GITHUB_USER") as String? ?: "xichenx"
val publishGroupId = if (isJitPack) {
    "com.github.$githubUser"
} else {
    "io.github.$githubUser"
}

// 设置项目版本（JitPack 和 Maven Central 使用相同的版本号）
version = versionName

// 配置发布（仅在非 JitPack 时使用 Maven Central）
if (!isJitPack && project.plugins.hasPlugin("com.vanniktech.maven.publish")) {
    // 注意：com.vanniktech.maven.publish 插件会自动从以下位置读取凭证：
    // 1. gradle.properties 文件中的 mavenCentralUsername 和 mavenCentralPassword
    // 2. 环境变量 mavenCentralUsername 和 mavenCentralPassword
    // 我们不需要手动设置这些属性，插件会自动读取
    
    // 配置 GPG 签名（使用内存密钥）
    // 插件可以从环境变量或系统属性读取 signingInMemoryKey、signingInMemoryKeyId、signingInMemoryKeyPassword
    // 如果提供了密钥文件路径，我们读取文件内容并设置到系统属性
    val signingKeyFile = System.getenv("SIGNING_SECRET_KEY_RING_FILE")
        ?: project.findProperty("SIGNING_SECRET_KEY_RING_FILE") as String?
    
    if (signingKeyFile != null) {
        try {
            val keyFile = file(signingKeyFile)
            if (keyFile.exists()) {
                val keyContent = keyFile.readText().trim()
                if (keyContent.isNotBlank()) {
                    // 设置系统属性，插件会自动读取
                    System.setProperty("signingInMemoryKey", keyContent)
                    logger.info("✅ GPG key loaded from file: $signingKeyFile")
                }
            }
        } catch (e: Exception) {
            logger.warn("⚠️  Failed to read GPG key file: ${e.message}")
        }
    }
    
    // 注意：signingInMemoryKeyId 和 signingInMemoryKeyPassword 应该通过环境变量传递
    // 插件会自动从环境变量读取，无需手动设置
    
    // 配置 mavenPublishing（使用 afterEvaluate 确保插件已初始化）
    afterEvaluate {
        // 使用简化的配置方式
        val mavenPublishing = extensions.findByName("mavenPublishing")
        if (mavenPublishing != null) {
            try {
                // 设置坐标
                mavenPublishing.javaClass.getMethod(
                    "coordinates",
                    String::class.java,
                    String::class.java,
                    String::class.java
                ).invoke(mavenPublishing, publishGroupId, project.name, versionName)
                
                // 配置 Maven Central
                mavenPublishing.javaClass.getMethod("publishToMavenCentral").invoke(mavenPublishing)
                
                // 启用签名
                mavenPublishing.javaClass.getMethod("signAllPublications").invoke(mavenPublishing)
                
                logger.info("✅ mavenPublishing configured for ${project.name}")
            } catch (e: Exception) {
                logger.warn("⚠️  Could not configure mavenPublishing: ${e.message}")
            }
        }
    }
} else if (isJitPack) {
    // JitPack 模式：使用标准的 maven-publish
    // 注意：版本号与 Maven Central 保持一致（使用相同的 versionName）
    if (!project.plugins.hasPlugin("maven-publish")) {
        project.plugins.apply("maven-publish")
    }
    
    afterEvaluate {
        extensions.configure<org.gradle.api.publish.PublishingExtension>("publishing") {
            publications {
                create<org.gradle.api.publish.maven.MavenPublication>("release") {
                    from(components["release"])
                    groupId = publishGroupId
                    artifactId = project.name
                    // 使用与 Maven Central 相同的版本号
                    version = versionName
                }
            }
        }
    }
} else {
    // 非 JitPack 但插件未应用，只记录警告
    logger.warn("⚠️  com.vanniktech.maven.publish plugin not applied to ${project.name}, skipping Maven Central configuration")
}
