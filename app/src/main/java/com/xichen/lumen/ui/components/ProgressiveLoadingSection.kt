package com.xichen.lumen.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import com.xichen.lumen.compose.LumenImage
import com.xichen.lumen.view.*

/**
 * 渐进式加载 Compose 示例组件
 * 
 * 展示在 Jetpack Compose 中使用渐进式加载的最佳实践
 */
@Composable
fun ProgressiveLoadingSection() {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        BasicProgressiveExample()
        
        ProgressiveWithTransformersExample()
        
        ProgressiveWithPlaceholderExample()
        
        ProgressiveInListExample()
        
        PerformanceTipsExample()
    }
}

/**
 * 基本渐进式加载示例
 */
@Composable
private fun BasicProgressiveExample() {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "基本渐进式加载",
                style = androidx.compose.material3.MaterialTheme.typography.titleMedium
            )
            
            Text(
                text = "左侧：渐进式加载（逐步清晰）\n右侧：普通加载（一次性显示）",
                style = androidx.compose.material3.MaterialTheme.typography.bodySmall,
                color = androidx.compose.ui.graphics.Color.Gray
            )
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // 渐进式加载
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    LumenImage(
                        url = "https://image.liumingzhi.cn/file/4c26ab1b462e8dd701151.png",
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp),
                        contentScale = ContentScale.Crop,
                        block = {
                            progressiveLoading()  // 启用渐进式加载
                        }
                    )
                    Text(
                        text = "渐进式加载",
                        style = androidx.compose.material3.MaterialTheme.typography.labelSmall,
                        color = androidx.compose.ui.graphics.Color(0xFF4CAF50),
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
                
                // 普通加载
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    LumenImage(
                        url = "https://image.liumingzhi.cn/file/4832421ab2eb68fa542e2.png",
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp),
                        contentScale = ContentScale.Crop
                    )
                    Text(
                        text = "普通加载",
                        style = androidx.compose.material3.MaterialTheme.typography.labelSmall,
                        color = androidx.compose.ui.graphics.Color.Gray,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            }
            
            Text(
                text = "代码：\nprogressiveLoading()  // 启用渐进式加载",
                style = androidx.compose.material3.MaterialTheme.typography.bodySmall,
                color = androidx.compose.ui.graphics.Color.Gray,
                fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace
            )
        }
    }
}

/**
 * 渐进式加载 + 转换器示例
 */
@Composable
private fun ProgressiveWithTransformersExample() {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "渐进式加载 + 转换器",
                style = androidx.compose.material3.MaterialTheme.typography.titleMedium
            )
            
            LumenImage(
                url = "https://image.liumingzhi.cn/file/4c26ab1b462e8dd701151.png",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp),
                contentScale = ContentScale.Crop,
                block = {
                    progressiveLoading()
                    roundedCorners(20f)
                }
            )
            
            Text(
                text = "渐进式加载 + 圆角",
                style = androidx.compose.material3.MaterialTheme.typography.bodySmall,
                color = androidx.compose.ui.graphics.Color.Gray
            )
            
            LumenImage(
                url = "https://image.liumingzhi.cn/file/4832421ab2eb68fa542e2.png",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp),
                contentScale = ContentScale.Crop,
                block = {
                    progressiveLoading()
                    roundedCorners(30f)
                }
            )
            
            Text(
                text = "渐进式加载 + 圆角（链式转换）",
                style = androidx.compose.material3.MaterialTheme.typography.bodySmall,
                color = androidx.compose.ui.graphics.Color.Gray
            )
        }
    }
}

/**
 * 渐进式加载 + 占位图示例
 */
@Composable
private fun ProgressiveWithPlaceholderExample() {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "渐进式加载 + 占位图",
                style = androidx.compose.material3.MaterialTheme.typography.titleMedium
            )
            
            Text(
                text = "占位图 → 渐进式预览 → 最终图片",
                style = androidx.compose.material3.MaterialTheme.typography.bodySmall,
                color = androidx.compose.ui.graphics.Color.Gray
            )
            
            LumenImage(
                url = "https://image.liumingzhi.cn/file/4c26ab1b462e8dd701151.png",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp),
                contentScale = ContentScale.Crop,
                block = {
                    progressiveLoading()
                    placeholder(android.R.drawable.ic_menu_gallery)
                    error(android.R.drawable.ic_dialog_alert)
                }
            )
            
            LumenImage(
                url = "https://image.liumingzhi.cn/file/4832421ab2eb68fa542e2.png",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp),
                contentScale = ContentScale.Crop,
                block = {
                    progressiveLoading()
                    placeholder(android.R.drawable.ic_menu_gallery)
                    error(android.R.drawable.ic_dialog_alert)
                }
            )
        }
    }
}

/**
 * 在列表中使用渐进式加载示例
 */
@Composable
private fun ProgressiveInListExample() {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "在列表中使用渐进式加载",
                style = androidx.compose.material3.MaterialTheme.typography.titleMedium
            )
            
            Text(
                text = "适合大图场景，Compose 自动处理重组和取消",
                style = androidx.compose.material3.MaterialTheme.typography.bodySmall,
                color = androidx.compose.ui.graphics.Color.Gray
            )
            
            val imageUrls = listOf(
                "https://image.liumingzhi.cn/file/4c26ab1b462e8dd701151.png",
                "https://image.liumingzhi.cn/file/4832421ab2eb68fa542e2.png",
                "https://image.liumingzhi.cn/file/4c26ab1b462e8dd701151.png"
            )
            
            imageUrls.forEach { url ->
                LumenImage(
                    url = url,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(150.dp),
                    contentScale = ContentScale.Crop,
                    block = {
                        progressiveLoading()
                        roundedCorners(12f)
                    }
                )
            }
        }
    }
}

/**
 * 性能优化建议示例
 */
@Composable
private fun PerformanceTipsExample() {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "性能优化建议",
                style = androidx.compose.material3.MaterialTheme.typography.titleMedium
            )
            
            Column(
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = "✓ 大图（> 500KB）或网络慢时使用渐进式加载",
                    style = androidx.compose.material3.MaterialTheme.typography.bodySmall,
                    color = androidx.compose.ui.graphics.Color.Gray
                )
                Text(
                    text = "✓ 小图（< 100KB）使用普通加载",
                    style = androidx.compose.material3.MaterialTheme.typography.bodySmall,
                    color = androidx.compose.ui.graphics.Color.Gray
                )
                Text(
                    text = "✓ 列表缩略图建议普通加载",
                    style = androidx.compose.material3.MaterialTheme.typography.bodySmall,
                    color = androidx.compose.ui.graphics.Color.Gray
                )
                Text(
                    text = "✓ 详情页大图建议渐进式加载",
                    style = androidx.compose.material3.MaterialTheme.typography.bodySmall,
                    color = androidx.compose.ui.graphics.Color.Gray
                )
            }
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // 大图使用渐进式加载
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    LumenImage(
                        url = "https://image.liumingzhi.cn/file/4c26ab1b462e8dd701151.png",
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp),
                        contentScale = ContentScale.Crop,
                        block = {
                            progressiveLoading()
                            roundedCorners(16f)
                        }
                    )
                    Text(
                        text = "大图：使用渐进式加载 ✓",
                        style = androidx.compose.material3.MaterialTheme.typography.labelSmall,
                        color = androidx.compose.ui.graphics.Color(0xFF4CAF50),
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
                
                // 小图使用普通加载
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    LumenImage(
                        url = "https://image.liumingzhi.cn/file/4832421ab2eb68fa542e2.png",
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp),
                        contentScale = ContentScale.Crop,
                        block = {
                            roundedCorners(16f)
                        }
                    )
                    Text(
                        text = "小图：使用普通加载 ✓",
                        style = androidx.compose.material3.MaterialTheme.typography.labelSmall,
                        color = androidx.compose.ui.graphics.Color(0xFF4CAF50),
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            }
        }
    }
}

