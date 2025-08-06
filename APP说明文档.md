# 写作应用说明文档

## 应用概述

这是一款专为小说创作者和读者设计的Android应用，支持小说的创建、编辑、阅读和管理功能。应用采用现代化的Material Design设计风格，提供作家模式和阅读模式两种不同的使用体验。

## 技术架构

### 开发环境
- Android SDK 36
- Java 11
- Room数据库 2.6.1
- Material Design组件

### 主要技术栈
- **架构模式**: MVVM (Model-View-ViewModel)
- **数据库**: Room持久化库
- **UI框架**: Material Design Components
- **异步处理**: ExecutorService
- **图片处理**: Android Bitmap API

### 项目结构
```
app/
├── src/main/
│   ├── java/com/example/myapplication/
│   │   ├── adapter/           # RecyclerView适配器
│   │   ├── dao/               # 数据访问对象
│   │   ├── database/          # 数据库定义
│   │   ├── model/             # 数据模型
│   │   ├── repository/        # 数据仓库
│   │   ├── util/              # 工具类
│   │   ├── viewmodel/         # 视图模型
│   │   ├── BaseActivity.java      # 所有Activity的基类，用于统一主题和夜间模式管理
│   │   ├── BaseApplication.java   # 应用的Application类，用于应用级初始化和主题设置
│   │   ├── MainActivity.java      # 主界面
│   │   ├── NovelEditActivity.java # 小说编辑界面
│   │   ├── NovelReaderActivity.java # 小说阅读界面
│   │   ├── SettingsActivity.java  # 设置界面
│   │   ├── SettingsFragment.java  # 设置界面的PreferenceFragment
│   │   └── AboutActivity.java     # 关于界面
│   ├── res/
│   │   ├── layout/            # 布局文件
│   │   ├── menu/              # 菜单文件
│   │   ├── drawable/          # 图形资源
│   │   ├── values/            # 字符串、颜色等资源
│   │   ├── values-night/      # 夜间模式资源
│   │   └── xml/               # PreferenceScreen配置
│   └── AndroidManifest.xml    # 应用配置文件
```

## 核心功能模块

### 1. 数据模型 (Model)

#### Novel.java
这是应用的核心数据模型，使用Room数据库注解定义了小说实体：
- **字段**:
  - `id`: 主键，自动生成
  - `title`: 小说标题
  - `description`: 小说简介
  - `content`: 小说内容
  - `coverImagePath`: 封面图片路径
  - `wordCount`: 字数统计
  - `lastUpdated`: 最后更新时间

- **功能**:
  - 自动计算字数
  - 自动更新最后修改时间
  - 提供封面图片路径管理

### 2. 数据访问层 (DAO)

#### NovelDao.java
定义了小说数据的访问接口：
- 查询所有小说 (按更新时间倒序)
- 根据ID查询小说
- 插入、更新、删除小说
- 根据ID删除小说

### 3. 数据库层

#### NovelDatabase.java
Room数据库实现：
- 定义数据库版本为2
- 使用单例模式确保全局唯一数据库实例
- 提供NovelDao访问接口

### 4. 数据仓库层

#### NovelRepository.java
数据仓库负责协调数据的获取和存储：
- 封装了DAO的访问方法
- 使用ExecutorService在后台线程执行数据库操作
- 提供LiveData观察数据变化

### 5. 视图模型层

#### NovelViewModel.java
ViewModel作为UI和数据之间的桥梁：
- 暴露数据给UI层
- 处理UI触发的数据操作
- 确保数据在配置变更时的持久性

### 6. 工具类

#### ImageUtil.java
图片处理工具类：
- 保存封面图片到应用私有目录
- 从URI加载图片
- 从文件路径加载图片

### 7. UI适配器

#### NovelAdapter.java
RecyclerView适配器负责小说列表的显示：
- 支持两种模式：作家模式和阅读模式
- 实现选择模式，支持批量操作
- 处理点击和长按事件
- 动态显示小说信息和封面

#### ChapterAdapter.java
RecyclerView适配器负责章节列表的显示：
- 支持章节的拖拽排序
- 处理章节的点击和长按事件
- 显示章节标题和序号

### 8. 主界面

#### MainActivity.java
应用的主入口，展示小说列表：
- **双模式支持**:
  - 阅读模式：点击进入阅读界面，长按进入编辑界面
  - 作家模式：点击和长按都进入编辑界面
- **选择模式**:
  - 支持批量选择和删除小说
  - 提供确认对话框防止误删
- **浮动按钮**:
  - 快速创建新小说

#### 主要功能:
- 切换作家/阅读模式
- 批量删除小说
- 创建新小说

### 9. 编辑界面

#### NovelEditActivity.java
小说编辑界面：
- 创建和编辑小说
- 支持封面图片选择
- 表单验证
- 保存和删除操作

#### 主要功能:
- 输入小说标题、简介和内容
- 选择封面图片
- 保存小说到数据库
- 删除现有小说

### 10. 阅读界面

#### NovelReaderActivity.java
小说阅读界面：
- 展示小说完整内容
- 显示封面图片
- 提供编辑入口

#### 主要功能:
- 阅读小说内容
- 查看小说信息
- 编辑或删除小说

### 11. 设置界面

#### SettingsActivity.java
应用设置界面，允许用户自定义应用行为：
- **功能**: 
  - 字体大小调整
  - 行间距调整
  - 主题颜色选择（蓝色、绿色、红色、紫色、默认）
  - 夜间模式开关

#### SettingsFragment.java
基于PreferenceFragmentCompat实现，提供设置项的UI和逻辑。

### 12. 关于界面

#### AboutActivity.java
显示应用的基本信息：
- **功能**:
  - 应用名称
  - 版本号
  - 开发者信息
  - 隐私政策等（可扩展）

## UI设计

### 布局文件

#### activity_main.xml
主界面布局：
- 使用CoordinatorLayout作为根布局
- AppBarLayout包含MaterialToolbar
- RecyclerView显示小说列表
- FloatingActionButton用于创建新小说

#### item_novel.xml
小说列表项布局：
- MaterialCardView作为容器
- ImageView显示封面
- TextView显示标题、简介和信息

#### activity_novel_edit.xml
编辑界面布局：
- 使用NestedScrollView支持滚动
- 分别展示封面选择、标题、简介和内容输入区域
- 使用TextInputLayout提供输入提示

#### activity_novel_reader.xml
阅读界面布局：
- 分为小说信息区和内容区
- 使用卡片式设计提升视觉效果

### 菜单文件

#### main_menu.xml
主界面菜单：
- 设置选项
- 删除小说
- 模式切换

#### edit_menu.xml
编辑界面菜单：
- 保存
- 删除

#### reader_menu.xml
阅读界面菜单：
- 编辑
- 删除

### 资源文件

#### themes.xml
应用主题：
- 基于Material3主题
- 无ActionBar设计

#### colors.xml
颜色定义：
- 基础黑白色调

#### strings.xml
字符串资源：
- 应用名称和界面文本
- 提示信息

#### drawable/ic_book_cover_placeholder.xml
占位符图标：
- 简单的书籍图标
- 用作小说封面的默认图片

## 使用说明

### 基本操作流程

1. **创建小说**:
   - 点击主界面右下角的"+"按钮
   - 在编辑界面输入小说信息
   - 选择封面图片（可选）
   - 点击保存

2. **阅读小说**:
   - 在主界面点击小说项（阅读模式下）
   - 查看小说内容

3. **编辑小说**:
   - 在主界面长按小说项（阅读模式下）
   - 或在作家模式下点击小说项
   - 修改小说信息
   - 点击保存

4. **删除小说**:
   - 单个删除：在阅读或编辑界面点击删除按钮
   - 批量删除：在主界面选择小说后点击删除

5. **切换模式**:
   - 在主界面菜单中切换作家/阅读模式
   - 作家模式适合创作和编辑
   - 阅读模式适合浏览和阅读

### 注意事项

- 应用会自动保存小说的最后修改时间
- 字数会根据内容自动计算
- 封面图片保存在应用私有目录中
- 支持从相册选择封面图片
- 所有数据库操作在后台线程执行，不会阻塞UI

## 版本更新记录

### v1.0.1

- **修复闪退问题**：解决了创建小说后立即点击或长按小说项导致应用闪退的问题。通过优化数据库查询线程处理和增加空值检查来提高应用稳定性。
- **修复UI遮挡问题**：解决了小说编辑页面中保存与删除按钮被遮挡的问题。通过调整Toolbar布局设置，确保菜单按钮正确显示。

### v1.0.2

- **主题和夜间模式实时切换**：
  - 引入 `BaseActivity` 作为所有 Activity 的基类，统一处理主题和夜间模式的设置。
  - 在 `BaseApplication` 中进行应用级的主题和夜间模式初始化。
  - `SettingsUtil` 提供了获取和应用主题、夜间模式的方法。
  - 用户在设置界面更改主题或夜间模式后，应用界面能够即时更新，无需重启。
  - 优先应用夜间模式设置，确保用户体验一致性。