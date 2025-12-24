package com.xichen.lumen

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.xichen.lumen.ui.theme.LumenTheme
import com.xichen.lumen.ui.screens.DemoScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            LumenTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MainScreen()
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen() {
    val context = LocalContext.current
    
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
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "选择演示方式",
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.padding(bottom = 32.dp)
            )
            
            Button(
                onClick = {
                    context.startActivity(Intent(context, ComposeDemoActivity::class.java))
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            ) {
                Text("Compose UI 示例")
            }
            
            Button(
                onClick = {
                    context.startActivity(Intent(context, XmlDemoActivity::class.java))
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            ) {
                Text("XML 布局示例")
            }
            
            Button(
                onClick = {
                    context.startActivity(Intent(context, GifVideoDemoActivity::class.java))
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            ) {
                Text("GIF 和视频帧示例 (Compose)")
            }
            
            Button(
                onClick = {
                    context.startActivity(Intent(context, GifVideoXmlDemoActivity::class.java))
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            ) {
                Text("GIF 和视频帧示例 (XML)")
            }
            
            Button(
                onClick = {
                    context.startActivity(Intent(context, ProgressiveLoadingXmlDemoActivity::class.java))
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            ) {
                Text("渐进式加载示例 (XML)")
            }
        }
    }
}
