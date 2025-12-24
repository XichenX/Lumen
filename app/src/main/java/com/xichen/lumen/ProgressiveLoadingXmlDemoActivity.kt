package com.xichen.lumen

import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.xichen.lumen.core.Lumen
import com.xichen.lumen.view.*

/**
 * 渐进式加载 XML 布局示例 Activity
 * 
 * 展示在传统 XML 布局中使用渐进式加载的最佳实践
 * 
 * 最佳实践要点：
 * 1. 渐进式加载仅对网络图片有效
 * 2. 适合大图片或网络较慢的场景
 * 3. 可以与转换器、占位图等配合使用
 * 4. 在列表中使用时注意内存管理
 */
class ProgressiveLoadingXmlDemoActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_progressive_loading_xml_demo)
        
        setupBasicProgressiveLoading()
        setupProgressiveWithTransformers()
        setupProgressiveWithPlaceholder()
        setupProgressiveInList()
        setupPerformanceTips()
    }
    
    /**
     * 基本渐进式加载示例
     * 
     * 最佳实践：
     * - 使用 progressiveLoading() 启用渐进式加载
     * - 适合加载大图片或网络较慢的场景
     * - 用户可以看到图片逐步清晰的过程，提升体验
     */
    private fun setupBasicProgressiveLoading() {
        // 基本渐进式加载
        Lumen.with(this)
            .load("https://image.liumingzhi.cn/file/4c26ab1b462e8dd701151.png") {
                progressiveLoading()  // 启用渐进式加载
            }
            .into(findViewById<ImageView>(R.id.imageViewBasicProgressive))
        
        // 对比：普通加载（无渐进式）
        Lumen.with(this)
            .load("https://image.liumingzhi.cn/file/4832421ab2eb68fa542e2.png")
            .into(findViewById<ImageView>(R.id.imageViewNormal))
    }
    
    /**
     * 渐进式加载 + 转换器示例
     * 
     * 最佳实践：
     * - 渐进式加载可以与所有转换器配合使用
     * - 转换器会在最终图片加载完成后应用
     * - 预览图会先显示，最终图片应用转换后替换
     */
    private fun setupProgressiveWithTransformers() {
        // 渐进式加载 + 圆角
        Lumen.with(this)
            .load("https://image.liumingzhi.cn/file/4c26ab1b462e8dd701151.png") {
                progressiveLoading()
                roundedCorners(20f)
            }
            .into(findViewById<ImageView>(R.id.imageViewProgressiveRounded))
        
        // 渐进式加载 + 链式转换
        Lumen.with(this)
            .load("https://image.liumingzhi.cn/file/4832421ab2eb68fa542e2.png") {
                progressiveLoading()
                roundedCorners(30f)
                blur(radius = 10f, scale = 0.8f)
            }
            .into(findViewById<ImageView>(R.id.imageViewProgressiveChained))
    }
    
    /**
     * 渐进式加载 + 占位图示例
     * 
     * 最佳实践：
     * - 占位图会在加载开始时立即显示
     * - 渐进式预览图会逐步替换占位图
     * - 最终图片会替换预览图
     * - 错误图片会在加载失败时显示
     */
    private fun setupProgressiveWithPlaceholder() {
        // 渐进式加载 + 占位图 + 错误处理
        Lumen.with(this)
            .load("https://image.liumingzhi.cn/file/4c26ab1b462e8dd701151.png") {
                progressiveLoading()
                placeholder(android.R.drawable.ic_menu_gallery)
                error(android.R.drawable.ic_dialog_alert)
            }
            .into(findViewById<ImageView>(R.id.imageViewProgressivePlaceholder))
        
        // 渐进式加载 + 自定义占位图
        Lumen.with(this)
            .load("https://image.liumingzhi.cn/file/4832421ab2eb68fa542e2.png") {
                progressiveLoading()
                placeholder(R.drawable.pikaqi)  // 使用本地资源作为占位图
                error(android.R.drawable.ic_dialog_alert)
            }
            .into(findViewById<ImageView>(R.id.imageViewProgressiveCustomPlaceholder))
    }
    
    /**
     * 在列表中使用渐进式加载
     * 
     * 最佳实践：
     * - RecyclerView 中自动处理取消逻辑
     * - 建议在快速滚动时禁用渐进式加载以节省资源
     * - 对于小图片，普通加载可能更合适
     */
    private fun setupProgressiveInList() {
        // 模拟列表项加载
        val imageUrls = listOf(
            "https://image.liumingzhi.cn/file/4c26ab1b462e8dd701151.png",
            "https://image.liumingzhi.cn/file/4832421ab2eb68fa542e2.png",
            "https://image.liumingzhi.cn/file/4c26ab1b462e8dd701151.png"
        )
        
        val imageViews = listOf(
            findViewById<ImageView>(R.id.imageViewListItem1),
            findViewById<ImageView>(R.id.imageViewListItem2),
            findViewById<ImageView>(R.id.imageViewListItem3)
        )
        
        imageUrls.forEachIndexed { index, url ->
            Lumen.with(this)
                .load(url) {
                    progressiveLoading()
                    roundedCorners(12f)
                }
                .into(imageViews[index])
        }
    }
    
    /**
     * 性能优化建议示例
     * 
     * 最佳实践：
     * - 小图片（< 100KB）不需要渐进式加载
     * - 大图片（> 500KB）或网络慢时使用渐进式加载
     * - 列表中的缩略图建议使用普通加载
     * - 详情页大图建议使用渐进式加载
     */
    private fun setupPerformanceTips() {
        // 大图使用渐进式加载（推荐）
        Lumen.with(this)
            .load("https://image.liumingzhi.cn/file/4c26ab1b462e8dd701151.png") {
                progressiveLoading()  // 大图推荐使用
                roundedCorners(16f)
            }
            .into(findViewById<ImageView>(R.id.imageViewLargeProgressive))
        
        // 小图使用普通加载（推荐）
        Lumen.with(this)
            .load("https://image.liumingzhi.cn/file/4832421ab2eb68fa542e2.png") {
                // 小图不需要渐进式加载
                roundedCorners(16f)
            }
            .into(findViewById<ImageView>(R.id.imageViewSmallNormal))
    }
}

