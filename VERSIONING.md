# Lumen 版本管理说明

## 版本管理策略

Lumen 采用 **独立版本 + BOM 协调** 的版本管理策略：

- **各模块独立版本**：每个模块（core、transform、view、compose）可以独立发布版本
- **BOM 版本协调**：通过 `lumen-bom` 模块定义兼容的版本组合
- **用户显式选择**：用户必须显式选择 UI 模块（view 或 compose），不会自动引入

## 模块版本配置

### 版本优先级（从高到低）

1. **模块特定版本属性**：`-PLUMEN_CORE_VERSION=1.2.0`
2. **LIBRARY_VERSION_NAME**：`-PLIBRARY_VERSION_NAME=1.2.0`
3. **VERSION_NAME**：`-PVERSION_NAME=1.2.0`
4. **默认值**：`1.0.0`

### 发布单个模块

```bash
# 只发布 lumen-transform 模块，版本 1.1.4
./gradlew :lumen-transform:publishToMavenCentral -PLUMEN_TRANSFORM_VERSION=1.1.4

# 然后更新 BOM，版本 1.2.1（只更新约束）
./gradlew :lumen:publishToMavenCentral -PVERSION_NAME=1.2.1
```

### 发布所有模块（统一版本）

```bash
# 所有模块使用相同版本
./gradlew publishAllToMavenCentral -PVERSION_NAME=1.2.0
```

### 发布所有模块（独立版本）

```bash
# 各模块使用不同版本
./gradlew publishAllToMavenCentral \
  -PLUMEN_CORE_VERSION=1.2.0 \
  -PLUMEN_TRANSFORM_VERSION=1.1.3 \
  -PLUMEN_VIEW_VERSION=1.0.5 \
  -PLUMEN_COMPOSE_VERSION=1.0.2 \
  -PVERSION_NAME=1.2.0
```

## BOM 配置

BOM 模块（`lumen`）通过 `dependencies.constraints` 定义兼容的版本组合：

```kotlin
dependencies {
    constraints {
        api("io.github.xichenx:lumen-core:$coreVersion")
        api("io.github.xichenx:lumen-transform:$transformVersion")
        api("io.github.xichenx:lumen-view:$viewVersion")
        api("io.github.xichenx:lumen-compose:$composeVersion")
    }
}
```

用户使用 BOM 时，会自动应用这些版本约束：

```kotlin
dependencies {
    implementation(platform("io.github.xichenx:lumen-bom:1.2.0"))
    implementation("io.github.xichenx:lumen-core")      // 自动使用 1.2.0
    implementation("io.github.xichenx:lumen-view")      // 自动使用 1.0.5
}
```

## 版本演进示例

### 场景 A：只修改 transform 模块

```text
发布：
- lumen-transform:1.1.4
- lumen-bom:1.2.1（更新约束）
```

用户更新：
```kotlin
implementation(platform("io.github.xichenx:lumen-bom:1.2.1"))
```

### 场景 B：core breaking change

```text
发布：
- lumen-core:2.0.0
- lumen-transform:2.0.0
- lumen-view:2.0.0
- lumen-compose:2.0.0
- lumen-bom:2.0.0
```

## 模块依赖关系

```
lumen-core (基础)

lumen-transform → lumen-core

lumen-view → lumen-core + lumen-transform

lumen-compose → lumen-core + lumen-view

lumen-bom (版本协调，不包含代码)
```

## 用户使用方式

### XML 项目

```kotlin
dependencies {
    implementation(platform("io.github.xichenx:lumen-bom:1.2.0"))
    implementation("io.github.xichenx:lumen-core")
    implementation("io.github.xichenx:lumen-view")
}
```

### Compose 项目

```kotlin
dependencies {
    implementation(platform("io.github.xichenx:lumen-bom:1.2.0"))
    implementation("io.github.xichenx:lumen-core")
    implementation("io.github.xichenx:lumen-compose")
}
```

## 注意事项

1. **UI 模块必须显式选择**：不能同时引入 `lumen-view` 和 `lumen-compose`
2. **BOM 版本独立**：BOM 版本可以与各模块版本不同，用于版本协调
3. **向后兼容**：小版本更新（1.1.x → 1.2.x）应保持 API 兼容
4. **Breaking Change**：主版本更新（1.x.x → 2.x.x）可以包含 breaking changes

