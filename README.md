# 小星科创浏览器 (Xiaoxing Tech Browser)

现代科技风与二次元红绯特别版 Android 浏览器。

## 🚀 GitHub Actions 自动云端打包

本项目已完整配置 **GitHub Actions CI/CD** 自动化构建工作流（`.github/workflows/build-apk.yml`）。

### 两种使用方式：

#### 方式一：一键手动打包下载（推荐）
1. 将本项目推送到您的 GitHub 仓库。
2. 打开 GitHub 仓库页面，点击顶部的 **Actions** 标签页。
3. 在左侧选择 **Build Android APK** 工作流。
4. 点击右侧的 **Run workflow** 按钮。
5. 等待 2~3 分钟构建完成后，点击该次运行记录，在底部的 **Artifacts** 区域即可直接下载 `XiaoxingTechBrowser-debug-apk` 安装包。

#### 方式二：打 Release 标签自动发布
每次推送形如 `v1.0.0` 的 git tag，GitHub Actions 会自动编译并创建对应的 GitHub Release 附件供任何人直接下载 APK：
```bash
git tag v1.0.0
git push origin v1.0.0
```

---

## 💻 本地打包运行指南

如果您将项目克隆到本地电脑（Windows / macOS / Linux）：

### 环境需求
- JDK 17 或 JDK 21
- Android Studio Ladybug (或更新版本) / Android SDK API 34+

### 常用构建命令

- **Linux / macOS**:
  ```bash
  # 赋予执行权限
  chmod +x gradlew

  # 编译生成 Debug APK
  ./gradlew assembleDebug
  ```
  生成的 APK 文件位于：
  `app/build/outputs/apk/debug/app-debug.apk`

- **Windows**:
  ```cmd
  gradlew.bat assembleDebug
  ```

- **安装至已连接的手机或模拟器**:
  ```bash
  ./gradlew installDebug
  ```
