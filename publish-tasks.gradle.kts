/**
 * 发布任务优化配置
 * 创建聚合任务以优化发布流程
 * 
 * 优化策略：
 * 1. 使用 Gradle 的并行执行（--parallel）
 * 2. 利用任务依赖系统自动处理依赖顺序
 * 3. 构建阶段完全并行，发布阶段在满足依赖的前提下并行
 */

// 创建发布所有模块的聚合任务
tasks.register("publishAllToMavenCentral") {
    group = "publishing"
    description = "Publish all modules to Maven Central with optimized parallel execution"
    
    // 依赖所有发布任务
    // Gradle 会根据项目依赖关系自动处理执行顺序
    dependsOn(
        ":lumen-core:publishReleasePublicationToMavenCentralRepository",
        ":lumen-transform:publishReleasePublicationToMavenCentralRepository",
        ":lumen-view:publishReleasePublicationToMavenCentralRepository",
        ":lumen:publishReleasePublicationToMavenCentralRepository"
    )
    
    // 明确指定执行顺序，确保依赖关系
    // 注意：这些约束只在并行执行时生效
    val publishCore = tasks.named(":lumen-core:publishReleasePublicationToMavenCentralRepository")
    val publishTransform = tasks.named(":lumen-transform:publishReleasePublicationToMavenCentralRepository")
    val publishView = tasks.named(":lumen-view:publishReleasePublicationToMavenCentralRepository")
    val publishLumen = tasks.named(":lumen:publishReleasePublicationToMavenCentralRepository")
    
    // lumen-transform 必须在 lumen-core 之后
    publishTransform.configure {
        mustRunAfter(publishCore)
    }
    
    // lumen-view 必须在 lumen-core 和 lumen-transform 之后
    publishView.configure {
        mustRunAfter(publishCore, publishTransform)
    }
    
    // lumen 聚合模块必须在所有子模块之后
    publishLumen.configure {
        mustRunAfter(publishCore, publishTransform, publishView)
    }
}

// 创建本地发布的聚合任务（用于测试）
tasks.register("publishAllToMavenLocal") {
    group = "publishing"
    description = "Publish all modules to local Maven repository"
    
    dependsOn(
        ":lumen-core:publishToMavenLocal",
        ":lumen-transform:publishToMavenLocal",
        ":lumen-view:publishToMavenLocal",
        ":lumen:publishToMavenLocal"
    )
}

