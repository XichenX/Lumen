package com.xichen.lumen.core

import android.content.Context
import android.net.Uri
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileInputStream
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL

/**
 * Lumen 数据获取器接口
 * 
 * 负责从不同数据源获取原始字节数据
 */
interface Fetcher {
    /**
     * 获取数据
     * @return 原始字节数组
     */
    suspend fun fetch(): ByteArray

    /**
     * 流式获取数据（用于渐进式加载）
     * @param onProgress 进度回调，参数为当前已读取的字节数组和进度（0.0-1.0）
     * @return 完整的原始字节数组
     */
    suspend fun fetchStream(onProgress: suspend (ByteArray, Float) -> Unit): ByteArray {
        // 默认实现：直接获取所有数据，然后调用回调
        val data = fetch()
        onProgress(data, 1.0f)
        return data
    }

    /**
     * 缓存 Key
     */
    val key: String
}

/**
 * Lumen 网络数据获取器
 * 
 * 从网络 URL 获取图片数据
 */
class NetworkFetcher(
    private val url: String,
    private val connectTimeout: Int = 10000,
    private val readTimeout: Int = 10000
) : Fetcher {
    override val key: String = "network:$url"

    override suspend fun fetch(): ByteArray = withContext(Dispatchers.IO) {
        val connection = URL(url).openConnection() as HttpURLConnection
        try {
            connection.connectTimeout = connectTimeout
            connection.readTimeout = readTimeout
            connection.requestMethod = "GET"
            
            // 设置 User-Agent，避免某些服务器拒绝请求
            connection.setRequestProperty("User-Agent", "Lumen-ImageLoader/1.0")
            connection.setRequestProperty("Accept", "image/*")
            
            // 允许重定向
            connection.instanceFollowRedirects = true
            
            connection.connect()

            val responseCode = connection.responseCode
            if (responseCode !in 200..299) {
                val errorMessage = try {
                    connection.errorStream?.bufferedReader()?.use { it.readText() } ?: "Unknown error"
                } catch (e: Exception) {
                    "Failed to read error stream"
                }
                throw IllegalStateException("HTTP error code: $responseCode, message: $errorMessage")
            }

            connection.inputStream.use { input ->
                input.readBytes()
            }
        } catch (e: Exception) {
            android.util.Log.e("Lumen", "Failed to fetch image from $url", e)
            throw e
        } finally {
            connection.disconnect()
        }
    }

    override suspend fun fetchStream(onProgress: suspend (ByteArray, Float) -> Unit): ByteArray = withContext(Dispatchers.IO) {
        val connection = URL(url).openConnection() as HttpURLConnection
        try {
            connection.connectTimeout = connectTimeout
            connection.readTimeout = readTimeout
            connection.requestMethod = "GET"
            
            // 设置 User-Agent，避免某些服务器拒绝请求
            connection.setRequestProperty("User-Agent", "Lumen-ImageLoader/1.0")
            connection.setRequestProperty("Accept", "image/*")
            
            // 允许重定向
            connection.instanceFollowRedirects = true
            
            connection.connect()

            val responseCode = connection.responseCode
            if (responseCode !in 200..299) {
                val errorMessage = try {
                    connection.errorStream?.bufferedReader()?.use { it.readText() } ?: "Unknown error"
                } catch (e: Exception) {
                    "Failed to read error stream"
                }
                throw IllegalStateException("HTTP error code: $responseCode, message: $errorMessage")
            }

            // 获取内容长度（如果可用）
            val contentLength = connection.contentLengthLong
            val buffer = mutableListOf<Byte>()
            val chunkSize = 8192 // 8KB 块大小
            val tempBuffer = ByteArray(chunkSize)
            var totalBytesRead = 0L
            var bytesRead: Int

            connection.inputStream.use { input ->
                while (input.read(tempBuffer).also { bytesRead = it } != -1) {
                    buffer.addAll(tempBuffer.sliceArray(0 until bytesRead).toList())
                    totalBytesRead += bytesRead
                    
                    // 计算进度并回调
                    val progress = if (contentLength > 0) {
                        (totalBytesRead.toFloat() / contentLength).coerceIn(0f, 1f)
                    } else {
                        // 如果不知道总大小，使用已读取的字节数估算（假设最大 10MB）
                        (totalBytesRead.toFloat() / (10 * 1024 * 1024)).coerceIn(0f, 0.95f)
                    }
                    
                    onProgress(buffer.toByteArray(), progress)
                }
            }
            
            buffer.toByteArray()
        } catch (e: Exception) {
            android.util.Log.e("Lumen", "Failed to fetch image from $url", e)
            throw e
        } finally {
            connection.disconnect()
        }
    }
}

/**
 * Lumen 文件数据获取器
 * 
 * 从本地文件系统获取图片数据
 */
class FileFetcher(
    private val file: File
) : Fetcher {
    override val key: String = "file:${file.absolutePath}"

    override suspend fun fetch(): ByteArray = withContext(Dispatchers.IO) {
        if (!file.exists()) {
            throw IllegalStateException("File not found: ${file.absolutePath}")
        }
        FileInputStream(file).use { input ->
            input.readBytes()
        }
    }
}

/**
 * Lumen Uri 数据获取器
 * 
 * 从 Android ContentProvider Uri 获取图片数据
 */
class UriFetcher(
    private val context: Context,
    private val uri: Uri
) : Fetcher {
    override val key: String = "uri:$uri"

    override suspend fun fetch(): ByteArray = withContext(Dispatchers.IO) {
        context.contentResolver.openInputStream(uri)?.use { input ->
            input.readBytes()
        } ?: throw IllegalStateException("Cannot open URI: $uri")
    }
}

/**
 * Lumen 资源数据获取器
 * 
 * 从 Android 资源文件获取图片数据
 */
class ResourceFetcher(
    private val context: Context,
    private val resId: Int
) : Fetcher {
    override val key: String = "res:$resId"

    override suspend fun fetch(): ByteArray = withContext(Dispatchers.IO) {
        context.resources.openRawResource(resId).use { input ->
            input.readBytes()
        }
    }
}

/**
 * Lumen Fetcher 工厂
 * 
 * 根据 ImageData 类型创建对应的 Fetcher 实例
 * 注意：视频数据源（Video、VideoUri）不需要 Fetcher，直接使用 VideoFrameExtractor
 */
object FetcherFactory {
    fun create(context: Context, data: ImageData): Fetcher? {
        return when (data) {
            is ImageData.Url -> NetworkFetcher(data.url)
            is ImageData.File -> FileFetcher(data.file)
            is ImageData.Uri -> UriFetcher(context, data.uri)
            is ImageData.Resource -> ResourceFetcher(context, data.resId)
            is ImageData.Video -> null // 视频使用 VideoFrameExtractor，不需要 Fetcher
            is ImageData.VideoUri -> null // 视频使用 VideoFrameExtractor，不需要 Fetcher
        }
    }
}

