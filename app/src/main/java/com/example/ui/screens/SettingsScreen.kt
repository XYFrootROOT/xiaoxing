package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CleaningServices
import androidx.compose.material.icons.filled.DesktopWindows
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Javascript
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Stars
import androidx.compose.material3.Checkbox
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.preferences.BrowserSettings
import com.example.data.preferences.SearchEngine
import com.example.data.preferences.ThemeMode
import com.example.ui.components.StarryCosmicBackground
import com.example.ui.theme.CoolBrandCrimson
import com.example.ui.theme.LiquidGlassDialog
import com.example.ui.theme.liquidGlassElevation
import com.example.ui.theme.liquidGlassSurface

@Composable
fun SettingsScreen(
    settings: BrowserSettings,
    onUpdateSettings: (BrowserSettings) -> Unit,
    onClearData: (clearCache: Boolean, clearCookies: Boolean, clearHistory: Boolean) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    BackHandler { onBack() }
    val isDark = isSystemInDarkTheme()

    var showEngineDialog by remember { mutableStateOf(false) }
    var showThemeDialog by remember { mutableStateOf(false) }
    var showClearDataDialog by remember { mutableStateOf(false) }

    Box(
        modifier = modifier
            .fillMaxSize()
            .windowInsetsPadding(WindowInsets.statusBars)
    ) {
        StarryCosmicBackground(enabled = true)

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            // Liquid Glass Header with Elevation
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp, vertical = 8.dp)
                    .liquidGlassElevation(
                        elevation = 8.dp,
                        shape = RoundedCornerShape(18.dp),
                        shadowColor = Color(0x40000000)
                    )
                    .liquidGlassSurface(
                        shape = RoundedCornerShape(18.dp),
                        refractionStrength = 12f,
                        dispersion = 0.30f,
                        isDark = isDark
                    )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = onBack) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "返回")
                    }
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "浏览器设置",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                }
            }

            SettingsSectionHeader(title = "通用与内核")
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp)
                    .liquidGlassElevation(
                        elevation = 8.dp,
                        shape = RoundedCornerShape(16.dp),
                        shadowColor = Color(0x35000000)
                    )
                    .liquidGlassSurface(
                        shape = RoundedCornerShape(16.dp),
                        refractionStrength = 8f,
                        dispersion = 0.25f,
                        isDark = isDark,
                        borderWidth = 0.8.dp
                    )
            ) {
                Column {
                    SettingsClickableItem(
                        icon = Icons.Default.Search,
                        title = "默认搜索引擎",
                        subtitle = settings.searchEngine.displayName,
                        onClick = { showEngineDialog = true }
                    )
                    HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp), thickness = 0.5.dp, color = Color.White.copy(alpha = 0.10f))
                    SettingsSwitchItem(
                        icon = Icons.Default.DesktopWindows,
                        title = "默认桌面版网站",
                        subtitle = "新标签页默认请求桌面版网页标识",
                        checked = settings.desktopModeDefault,
                        onCheckedChange = { onUpdateSettings(settings.copy(desktopModeDefault = it)) }
                    )
                    HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp), thickness = 0.5.dp, color = Color.White.copy(alpha = 0.10f))
                    SettingsSwitchItem(
                        icon = Icons.Default.Javascript,
                        title = "启用 JavaScript",
                        subtitle = "允许网页执行交互式脚本",
                        checked = settings.enableJavaScript,
                        onCheckedChange = { onUpdateSettings(settings.copy(enableJavaScript = it)) }
                    )
                    HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp), thickness = 0.5.dp, color = Color.White.copy(alpha = 0.10f))
                    SettingsSwitchItem(
                        icon = Icons.Default.Security,
                        title = "接受 Cookie",
                        subtitle = "保存网站登录与偏好状态",
                        checked = settings.enableCookies,
                        onCheckedChange = { onUpdateSettings(settings.copy(enableCookies = it)) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            SettingsSectionHeader(title = "酷安质感 × Liquid Glass 视觉")
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp)
                    .liquidGlassElevation(
                        elevation = 8.dp,
                        shape = RoundedCornerShape(16.dp),
                        shadowColor = Color(0x35000000)
                    )
                    .liquidGlassSurface(
                        shape = RoundedCornerShape(16.dp),
                        refractionStrength = 8f,
                        dispersion = 0.25f,
                        isDark = isDark,
                        borderWidth = 0.8.dp
                    )
            ) {
                Column {
                    SettingsClickableItem(
                        icon = Icons.Default.Palette,
                        title = "主题外观",
                        subtitle = when (settings.themeMode) {
                            ThemeMode.SYSTEM -> "跟随系统"
                            ThemeMode.LIGHT -> "纯净晶莹浅色模式"
                            ThemeMode.DARK -> "黑曜深邃夜空模式"
                        },
                        onClick = { showThemeDialog = true }
                    )
                    HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp), thickness = 0.5.dp, color = Color.White.copy(alpha = 0.10f))
                    SettingsSwitchItem(
                        icon = Icons.Default.Stars,
                        title = "首页动态空间折射背景",
                        subtitle = "低饱和蓝紫色环境光与细腻微粒",
                        checked = settings.enableDynamicBackground,
                        onCheckedChange = { onUpdateSettings(settings.copy(enableDynamicBackground = it)) }
                    )
                    HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp), thickness = 0.5.dp, color = Color.White.copy(alpha = 0.10f))
                    SettingsSwitchItem(
                        icon = Icons.Default.Speed,
                        title = "流体物理光泽与动效",
                        subtitle = "高光掠影及液态按钮弹性回弹",
                        checked = settings.enableAnimations,
                        onCheckedChange = { onUpdateSettings(settings.copy(enableAnimations = it)) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            SettingsSectionHeader(title = "隐私与性能")
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp)
                    .liquidGlassElevation(
                        elevation = 8.dp,
                        shape = RoundedCornerShape(16.dp),
                        shadowColor = Color(0x35000000)
                    )
                    .liquidGlassSurface(
                        shape = RoundedCornerShape(16.dp),
                        refractionStrength = 8f,
                        dispersion = 0.25f,
                        isDark = isDark,
                        borderWidth = 0.8.dp
                    )
            ) {
                Column {
                    SettingsSwitchItem(
                        icon = Icons.Default.Image,
                        title = "无图省流模式",
                        subtitle = "阻止网页自动加载图片，大幅节省流量",
                        checked = settings.blockImagesToSaveData,
                        onCheckedChange = { onUpdateSettings(settings.copy(blockImagesToSaveData = it)) }
                    )
                    HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp), thickness = 0.5.dp, color = Color.White.copy(alpha = 0.10f))
                    SettingsClickableItem(
                        icon = Icons.Default.CleaningServices,
                        title = "清除浏览数据",
                        subtitle = "清理缓存、Cookie 以及历史记录",
                        onClick = { showClearDataDialog = true }
                    )
                }
            }

            Spacer(modifier = Modifier.height(26.dp))

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "小星科创浏览器",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "CoolApk × iOS 26 Liquid Glass Edition · v1.3.0",
                    fontSize = 12.sp,
                    color = CoolBrandCrimson
                )
                Text(
                    text = "AGSL 物理折射与色散材质系统 · 原生 WebView 内核",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.70f)
                )
            }

            Spacer(modifier = Modifier.height(40.dp))
        }
    }

    if (showEngineDialog) {
        LiquidGlassDialog(
            onDismissRequest = { showEngineDialog = false },
            title = "选择默认搜索引擎",
            isDark = isDark,
            confirmText = "完成",
            onConfirm = { showEngineDialog = false }
        ) {
            Column {
                SearchEngine.entries.forEach { engine ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                onUpdateSettings(settings.copy(searchEngine = engine))
                                showEngineDialog = false
                            }
                            .padding(vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = settings.searchEngine == engine,
                            onClick = {
                                onUpdateSettings(settings.copy(searchEngine = engine))
                                showEngineDialog = false
                            }
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = engine.displayName, fontSize = 15.sp)
                    }
                }
            }
        }
    }

    if (showThemeDialog) {
        LiquidGlassDialog(
            onDismissRequest = { showThemeDialog = false },
            title = "选择外观主题",
            isDark = isDark,
            confirmText = "完成",
            onConfirm = { showThemeDialog = false }
        ) {
            Column {
                listOf(
                    ThemeMode.SYSTEM to "跟随系统",
                    ThemeMode.LIGHT to "纯净晶莹浅色模式",
                    ThemeMode.DARK to "黑曜深邃夜空模式"
                ).forEach { (mode, title) ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                onUpdateSettings(settings.copy(themeMode = mode))
                                showThemeDialog = false
                            }
                            .padding(vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = settings.themeMode == mode,
                            onClick = {
                                onUpdateSettings(settings.copy(themeMode = mode))
                                showThemeDialog = false
                            }
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = title, fontSize = 15.sp)
                    }
                }
            }
        }
    }

    if (showClearDataDialog) {
        var clearCache by remember { mutableStateOf(true) }
        var clearCookies by remember { mutableStateOf(true) }
        var clearHistory by remember { mutableStateOf(true) }

        LiquidGlassDialog(
            onDismissRequest = { showClearDataDialog = false },
            title = "清除浏览数据",
            isDark = isDark,
            confirmText = "立即清除",
            dismissText = "取消",
            onDismiss = { showClearDataDialog = false },
            onConfirm = {
                onClearData(clearCache, clearCookies, clearHistory)
                showClearDataDialog = false
            }
        ) {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth().clickable { clearCache = !clearCache },
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(checked = clearCache, onCheckedChange = { clearCache = it })
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("清空网页缓存与临时文件")
                }
                Row(
                    modifier = Modifier.fillMaxWidth().clickable { clearCookies = !clearCookies },
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(checked = clearCookies, onCheckedChange = { clearCookies = it })
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("清除网站 Cookie 及登录状态")
                }
                Row(
                    modifier = Modifier.fillMaxWidth().clickable { clearHistory = !clearHistory },
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(checked = clearHistory, onCheckedChange = { clearHistory = it })
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("清空网页历史记录")
                }
            }
        }
    }
}

@Composable
private fun SettingsSectionHeader(title: String) {
    Text(
        text = title,
        fontSize = 13.sp,
        fontWeight = FontWeight.Bold,
        color = CoolBrandCrimson,
        modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp)
    )
}

@Composable
private fun SettingsClickableItem(
    icon: ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = CoolBrandCrimson,
            modifier = Modifier.size(21.dp)
        )
        Spacer(modifier = Modifier.width(14.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                fontSize = 15.sp,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = subtitle,
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun SettingsSwitchItem(
    icon: ImageVector,
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = CoolBrandCrimson,
            modifier = Modifier.size(21.dp)
        )
        Spacer(modifier = Modifier.width(14.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                fontSize = 15.sp,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = subtitle,
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Spacer(modifier = Modifier.width(8.dp))
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(checkedThumbColor = CoolBrandCrimson)
        )
    }
}
