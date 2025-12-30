package com.xichen.lumen.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.xichen.lumen.compose.LumenImage

@Composable
fun ComposeSection() {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "Compose 示例",
                style = androidx.compose.material3.MaterialTheme.typography.titleMedium
            )
            
            LumenImage(
                url = "https://image.liumingzhi.cn/file/4c26ab1b462e8dd701151.png",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp),
                contentDescription = "示例图片"
            )
            
            Text(
                text = "代码：\nLumenImage(\n    url = \"https://...\",\n    modifier = Modifier.size(200.dp)\n)",
                style = androidx.compose.material3.MaterialTheme.typography.bodySmall
            )
        }
    }
}

