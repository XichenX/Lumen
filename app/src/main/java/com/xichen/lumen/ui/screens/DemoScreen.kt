package com.xichen.lumen.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.xichen.lumen.ui.components.*
import com.xichen.lumen.ui.components.GifAndVideoSection

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DemoScreen() {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Lumen 图片加载库 Demo") }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // 基本使用示例
            BasicUsageSection()
            
            Divider()
            
            // 转换器示例
            TransformSection()
            
            Divider()
            
            // RecyclerView 示例
            RecyclerViewSection()
            
            Divider()
            
            // Compose 示例
            ComposeSection()
            
            Divider()
            
            // GIF 和视频帧示例
            GifAndVideoSection()
            
            Divider()
            
            // 渐进式加载示例
            ProgressiveLoadingSection()
        }
    }
}

