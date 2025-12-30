// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.android.library) apply false
    alias(libs.plugins.maven.publish) apply false
}

// 注意：各模块独立管理版本和发布
// 每个模块可以通过以下方式设置版本（优先级从高到低）：
// 1. 模块特定版本属性（如 LUMEN_CORE_VERSION）
// 2. LIBRARY_VERSION_NAME 属性（用于指定特定模块版本）
// 3. VERSION_NAME 属性（用于统一版本）
// 4. 默认值 "1.0.0"
// 
// BOM 模块（lumen）用于版本协调，通过 constraints 定义兼容的版本组合

// 应用发布任务优化配置
apply(from = "publish-tasks.gradle.kts")