package com.xichen.lumen.core

import android.graphics.Bitmap
import android.graphics.drawable.Drawable

/**
 * Lumen 图片加载状态模型
 * 
 * 用于表示图片加载过程中的不同状态，支持 AI UI、多状态渲染和调试
 */
sealed class ImageState {
    /**
     * 加载中状态
     */
    object Loading : ImageState()

    /**
     * 渐进式加载中间状态（显示低质量预览图）
     * @param bitmap 当前已解码的预览 Bitmap（可能是不完整的）
     * @param progress 加载进度（0.0 - 1.0）
     */
    data class Progressive(val bitmap: Bitmap, val progress: Float = 0f) : ImageState()

    /**
     * 加载成功状态（静态图片）
     * @param bitmap 加载成功的 Bitmap
     */
    data class Success(val bitmap: Bitmap) : ImageState()

    /**
     * 加载成功状态（GIF 动画）
     * @param drawable 动画 Drawable（如 AnimatedImageDrawable）
     */
    data class SuccessAnimated(val drawable: Drawable) : ImageState()

    /**
     * 加载失败状态
     * @param throwable 失败原因（可选）
     */
    data class Error(val throwable: Throwable? = null) : ImageState()

    /**
     * 兜底状态（当加载失败时使用备用方案）
     */
    object Fallback : ImageState()
}

