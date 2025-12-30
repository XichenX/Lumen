package com.xichen.lumen.view

import android.content.Context
import android.graphics.drawable.Drawable
import androidx.core.content.ContextCompat
import com.xichen.lumen.core.BitmapTransformer
import com.xichen.lumen.core.ImageData
import com.xichen.lumen.core.ImageDecryptor
import com.xichen.lumen.core.ImageRequest
import com.xichen.lumen.core.Lumen

/**
 * Lumen 请求构建器
 * 
 * 提供 DSL 风格的 API 用于构建图片加载请求
 * 
 * 使用示例：
 * ```
 * Lumen.with(context)
 *     .load("https://example.com/image.jpg") {
 *         placeholder(R.drawable.placeholder)
 *         error(R.drawable.error)
 *         roundedCorners(12.dp)
 *     }
 *     .into(imageView)
 * ```
 */
class RequestBuilder(
    internal val lumen: Lumen,
    private val data: ImageData
) {
    private var placeholder: Drawable? = null
    private var error: Drawable? = null
    private var decryptor: ImageDecryptor? = null
    private val transformers = mutableListOf<BitmapTransformer>()
    private var priority: ImageRequest.Priority = ImageRequest.Priority.NORMAL
    private var progressiveLoading: Boolean = false

    /**
     * 设置占位图
     */
    fun placeholder(drawable: Drawable) {
        this.placeholder = drawable
    }

    /**
     * 设置占位图（资源 ID）
     * 注意：需要在 into() 方法中通过 context 获取 Drawable
     */
    private var placeholderResId: Int? = null
    
    fun placeholder(resId: Int) {
        this.placeholderResId = resId
    }

    /**
     * 设置错误图片
     */
    fun error(drawable: Drawable) {
        this.error = drawable
    }

    /**
     * 设置错误图片（资源 ID）
     * 注意：需要在 into() 方法中通过 context 获取 Drawable
     */
    private var errorResId: Int? = null
    
    fun error(resId: Int) {
        this.errorResId = resId
    }
    
    internal fun getPlaceholderResId(): Int? = placeholderResId
    internal fun getErrorResId(): Int? = errorResId

    /**
     * 设置解密器
     */
    fun decryptor(decryptor: ImageDecryptor) {
        this.decryptor = decryptor
    }

    /**
     * 添加转换器
     */
    fun addTransformer(transformer: BitmapTransformer) {
        this.transformers.add(transformer)
    }

    /**
     * 设置优先级
     */
    fun priority(priority: ImageRequest.Priority) {
        this.priority = priority
    }

    /**
     * 启用渐进式加载
     * 仅对网络图片有效，在图片下载过程中逐步显示从低质量到高质量的预览图
     * 
     * 使用示例：
     * ```
     * Lumen.with(context)
     *     .load("https://example.com/image.jpg") {
     *         progressiveLoading()
     *     }
     *     .into(imageView)
     * ```
     */
    fun progressiveLoading() {
        this.progressiveLoading = true
    }

    /**
     * 构建 ImageRequest
     */
    fun build(context: Context): ImageRequest {
        val finalPlaceholder = placeholder ?: placeholderResId?.let {
            ContextCompat.getDrawable(context, it)
        }
        val finalError = error ?: errorResId?.let {
            ContextCompat.getDrawable(context, it)
        }
        
        return ImageRequest(
            data = data,
            placeholder = finalPlaceholder,
            error = finalError,
            decryptor = decryptor,
            transformers = transformers,
            priority = priority,
            progressiveLoading = progressiveLoading
        )
    }
}

/**
 * RequestBuilder 配置作用域
 */
typealias RequestBuilderScope = RequestBuilder.() -> Unit

