<div align="center">

# Lumen


</div>


<div align="center">

![Lumen Logo](https://img.shields.io/badge/Lumen-Image%20Loader-blue?style=flat)
![Kotlin](https://img.shields.io/badge/Kotlin-2.0.21-purple?style=flat&logo=kotlin)
![Android](https://img.shields.io/badge/Android-API%2021+-green?style=flat&logo=android)
![License](https://img.shields.io/badge/License-Apache%202.0-yellow?style=flat)

**A Kotlin-first Android image loading library for business-friendly, AI scenarios, and list scenarios**

[中文文档](README-zh.md) • [Quick Start](#-quick-start) • [Features](#-features) • [Comparison](#-comparison-with-glide--coil) • [Documentation](#-documentation)

</div>

---

## 🤔 Why

While there are excellent image loading libraries like Glide and Coil in the Android ecosystem, we encountered the following pain points in real-world development:

1. **Opaque State Management**: Difficult to precisely control loading states (Loading / Success / Error / Fallback), insufficient flexibility for custom UI
2. **Black Box Pipeline**: Loading pipeline is not transparent enough, making debugging and customization difficult (e.g., encrypted images, custom decoding)
3. **Insufficient RecyclerView Optimization**: Image flickering and memory leaks are common in list scenarios
4. **Underutilized Kotlin Features**: Existing libraries are mostly Java-designed, not fully leveraging Kotlin's DSL, coroutines, etc.
5. **Insufficient AI Scenario Support**: Not friendly enough for AI-related scenarios requiring decryption, custom decoding, etc.

**Lumen's Positioning**: Not another Glide clone, but a modern Android image loading library designed for "real business + AI scenarios".

---

## ✨ Features

### Core Features

- ✅ **State Control**: Clear loading states (Loading / Success / Error / Fallback) with custom UI support
- ✅ **Transparent Pipeline**: Every step is pluggable (Fetcher → Decryptor → Decoder → Transformer → Cache)
- ✅ **Kotlin-first**: Fully leverages modern Kotlin features like DSL, coroutines, Flow
- ✅ **RecyclerView Optimization**: Automatically cancels loading tasks for recycled views, preventing memory leaks and image flickering
- ✅ **Image Transformations**: Rounded corners, rotation, cropping, blur, etc. (applied directly to Bitmap, not View)
- ✅ **Multiple Data Sources**: Supports URL, File, Uri, Resource ID
- ✅ **Compose Support**: Native Jetpack Compose components and state management
- ✅ **Memory Cache**: Automatic memory cache based on LruCache

### Technical Highlights

- 🔄 **Coroutine-driven**: Based on Kotlin Coroutines and Flow
- 🎭 **State Management**: Sealed Class for loading states
- 🧩 **Modular Design**: Core logic separated from UI (`lumen-core` has no Android UI dependencies)
- 🛡️ **Type Safety**: Fully leverages Kotlin's type system

---

## 🚀 Quick Start

### 1. Add Dependencies

**Simple way (recommended):** Just add one dependency to get all features:

```kotlin
dependencies {
    implementation("com.xichen.lumen:lumen:1.0.0")
}
```

**Modular way (optional):** If you only need specific modules:

```kotlin
dependencies {
    implementation("com.xichen.lumen:lumen-core:1.0.0")      // Core only
    implementation("com.xichen.lumen:lumen-view:1.0.0")      // View support
    implementation("com.xichen.lumen:lumen-transform:1.0.0") // Transform support
}
```

### 2. Add Permissions

```xml
<uses-permission android:name="android.permission.INTERNET" />
```

### 3. Use It (10 lines of code)

```kotlin
Lumen.with(context)
    .load("https://example.com/image.jpg") {
        placeholder(R.drawable.placeholder)
        error(R.drawable.error)
        roundedCorners(12f)
    }
    .into(imageView)
```

**That's it!** 🎉

---

## 📊 Comparison with Glide / Coil

| Feature | Lumen | Glide | Coil |
|---------|-------|-------|------|
| **Kotlin-first** | ✅ Native Kotlin, fully leverages DSL, coroutines | ❌ Java-designed, limited Kotlin extensions | ✅ Kotlin-first |
| **State Transparency** | ✅ Sealed Class, clear and controllable states | ⚠️ States not transparent enough | ⚠️ States not transparent enough |
| **Pluggable Pipeline** | ✅ Every step is customizable | ⚠️ Partially customizable | ⚠️ Partially customizable |
| **RecyclerView Optimization** | ✅ Auto-cancel, prevents flickering | ✅ Supported | ✅ Supported |
| **Transform Applied to Bitmap** | ✅ Directly applied to Bitmap | ❌ Applied to View | ✅ Applied to Bitmap |
| **Compose Support** | ✅ Native support | ⚠️ Requires adaptation | ✅ Native support |
| **Encrypted Image Support** | ✅ Built-in Decryptor interface | ❌ Requires custom implementation | ❌ Requires custom implementation |
| **Learning Curve** | ⭐⭐ Simple and intuitive | ⭐⭐⭐ Complex features | ⭐⭐ Relatively simple |
| **Package Size** | 📦 Small (modular) | 📦📦 Medium | 📦 Small |
| **Maturity** | 🆕 New project | ✅ Very mature | ✅ Mature |

### Recommendation

- **Choose Lumen**: Need state control, transparent pipeline, AI scenario support, Kotlin-first experience
- **Choose Glide**: Need GIF support, very mature ecosystem, many third-party plugins
- **Choose Coil**: Need lightweight, native Compose support, modern Kotlin API

---

## 🎯 Use Cases

### ✅ Suitable Scenarios

1. **Business-friendly Scenarios**
   - Need precise control over loading states (Loading / Success / Error / Fallback)
   - Need custom UI display (e.g., skeleton screens, custom error UI)
   - Need clear error handling and fallback mechanisms

2. **AI Scenarios**
   - Encrypted image loading (built-in Decryptor interface)
   - Custom decoding logic
   - Image preprocessing and post-processing

3. **List Scenarios**
   - Image loading in RecyclerView
   - Need to prevent image flickering and memory leaks
   - Need automatic cancellation of loading tasks for recycled views

4. **Kotlin Projects**
   - Pure Kotlin projects
   - Using Jetpack Compose
   - Need modern Kotlin API (DSL, coroutines, Flow)

5. **Image Transformation Scenarios**
   - Need transformations like rounded corners, rotation, cropping, blur
   - Need transformations applied directly to Bitmap (not View)
   - Need chained transformations

### ❌ Unsuitable Scenarios

1. **GIF / Video Frame**
   - Current version does not support GIF animation
   - Does not support video frame extraction

2. **Complex Animations**
   - Does not support image loading animations (e.g., fade in/out)
   - Does not support transition animations

3. **Automatic Cross-process Cache**
   - Current version only supports memory cache
   - Does not support automatic disk cache (can be implemented manually)

4. **Java Projects**
   - Can be used in Java but experience is not as good as Kotlin
   - Recommend using Glide or Coil

5. **Need Many Third-party Plugins**
   - Relatively new ecosystem, fewer third-party plugins
   - Recommend using Glide if you need a rich ecosystem

---

## 📝 Usage Examples

### Basic Usage

```kotlin
// Simplest usage
Lumen.with(context)
    .load("https://example.com/image.jpg")
    .into(imageView)

// With placeholder and error handling
Lumen.with(context)
    .load("https://example.com/image.jpg") {
        placeholder(R.drawable.placeholder)
        error(R.drawable.error)
    }
    .into(imageView)
```

### Image Transformations

```kotlin
// Rounded corners
Lumen.with(context)
    .load("https://example.com/image.jpg") {
        roundedCorners(20f)
    }
    .into(imageView)

// Chained transformations
Lumen.with(context)
    .load("https://example.com/image.jpg") {
        roundedCorners(30f)
        rotate(90f)
        blur(radius = 15f)
    }
    .into(imageView)
```

### Jetpack Compose

```kotlin
import com.xichen.lumen.view.compose.LumenImage

@Composable
fun ImageScreen() {
    LumenImage(
        url = "https://example.com/image.jpg",
        modifier = Modifier.size(200.dp),
        contentDescription = "Example image",
        block = {
            placeholder(R.drawable.placeholder)
            roundedCorners(20f)
        }
    )
}
```

### RecyclerView Optimization

```kotlin
class ImageAdapter : RecyclerView.Adapter<ImageAdapter.ViewHolder>() {
    
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        // Lumen automatically handles cancellation
        Lumen.with(holder.itemView.context)
            .load(images[position]) {
                roundedCorners(12f)
            }
            .into(holder.imageView)
    }
    
    override fun onViewRecycled(holder: ViewHolder) {
        super.onViewRecycled(holder)
        // Optional: Manual cancellation (Lumen already handles it)
        holder.itemView.cancelLumenLoad()
    }
}
```

### Advanced Usage: Custom Decryptor

```kotlin
class CustomDecryptor : ImageDecryptor {
    override suspend fun decrypt(data: ByteArray): ByteArray {
        // Custom decryption logic
        return decryptedData
    }
}

Lumen.with(context)
    .load("https://example.com/encrypted-image.jpg") {
        decryptor(CustomDecryptor())
    }
    .into(imageView)
```

---

## 🏗️ Architecture

### Core Loading Pipeline

```
ImageRequest
   ↓
Fetcher (Network / File / Uri / Resource)
   ↓
Decryptor (Optional)
   ↓
Decoder (BitmapFactory)
   ↓
Transformer (Optional: rounded corners, rotation, crop, blur, etc.)
   ↓
Cache (Memory Cache)
   ↓
Target (ImageView / Compose / Custom)
```

**Core Principle: Every step is pluggable**

### Module Structure

```
Lumen/
 ├── lumen-core        // Core loading logic (no Android UI dependencies)
 ├── lumen-view        // ImageView / ViewTarget / Compose support
 ├── lumen-transform   // Image transformers (rounded corners, rotation, crop, blur)
 └── sample-app        // Sample project
```

### State Model

```kotlin
sealed class ImageState {
    object Loading : ImageState()
    data class Success(val bitmap: Bitmap) : ImageState()
    data class Error(val throwable: Throwable) : ImageState()
    object Fallback : ImageState()
}
```

---

## 📚 Documentation

### API Documentation

- [Core API](docs/api-core.md)
- [View API](docs/api-view.md)
- [Compose API](docs/api-compose.md)
- [Transform API](docs/api-transform.md)

### More Examples

Check the [sample-app](app/) module for complete example code.

---

## 🤝 Contributing

We welcome all forms of contributions!

### How to Contribute

1. Fork this repository
2. Create a feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit your changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

### Development Guidelines

1. Follow Kotlin coding conventions
2. Add necessary unit tests
3. Update relevant documentation
4. Ensure all tests pass

---

## 📄 License

This project is licensed under the Apache License 2.0. See the [LICENSE](LICENSE) file for details.

---

## 🙏 Acknowledgments

Thanks to all developers who contributed to Lumen!

Special thanks to the Glide and Coil projects for their tremendous contributions to the Android image loading field.

---

## 📞 Contact

- **Issues**: [GitHub Issues](https://github.com/your-username/lumen/issues)
- **Email**: your-email@example.com

---

<div align="center">

**If this project helps you, please give it a ⭐ Star!**

Made with ❤️ by Lumen Team

</div>
