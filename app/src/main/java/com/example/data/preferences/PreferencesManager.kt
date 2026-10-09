package com.example.data.preferences

import android.content.Context
import android.content.SharedPreferences

enum class SearchEngine(val displayName: String, val searchUrl: String) {
    BAIDU("百度 (Baidu)", "https://www.baidu.com/s?wd="),
    BING("必应 (Bing)", "https://www.bing.com/search?q="),
    GOOGLE("Google", "https://www.google.com/search?q="),
    DUCKDUCKGO("DuckDuckGo", "https://duckduckgo.com/?q=")
}

enum class ThemeMode {
    SYSTEM,
    LIGHT,
    DARK
}

data class BrowserSettings(
    val searchEngine: SearchEngine = SearchEngine.BAIDU,
    val themeMode: ThemeMode = ThemeMode.SYSTEM,
    val desktopModeDefault: Boolean = false,
    val enableJavaScript: Boolean = true,
    val enableCookies: Boolean = true,
    val enableDynamicBackground: Boolean = true,
    val enableAnimations: Boolean = true,
    val blockImagesToSaveData: Boolean = false,
    val hardwareAcceleration: Boolean = true
)

class PreferencesManager(context: Context) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences("xiaoxing_browser_prefs", Context.MODE_PRIVATE)

    fun getSettings(): BrowserSettings {
        val engineName = prefs.getString("search_engine", SearchEngine.BAIDU.name) ?: SearchEngine.BAIDU.name
        val engine = runCatching { SearchEngine.valueOf(engineName) }.getOrDefault(SearchEngine.BAIDU)

        val themeName = prefs.getString("theme_mode", ThemeMode.SYSTEM.name) ?: ThemeMode.SYSTEM.name
        val theme = runCatching { ThemeMode.valueOf(themeName) }.getOrDefault(ThemeMode.SYSTEM)

        return BrowserSettings(
            searchEngine = engine,
            themeMode = theme,
            desktopModeDefault = prefs.getBoolean("desktop_mode", false),
            enableJavaScript = prefs.getBoolean("enable_js", true),
            enableCookies = prefs.getBoolean("enable_cookies", true),
            enableDynamicBackground = prefs.getBoolean("dynamic_bg", true),
            enableAnimations = prefs.getBoolean("enable_animations", true),
            blockImagesToSaveData = prefs.getBoolean("save_data_images", false),
            hardwareAcceleration = prefs.getBoolean("hw_accel", true)
        )
    }

    fun updateSettings(settings: BrowserSettings) {
        prefs.edit()
            .putString("search_engine", settings.searchEngine.name)
            .putString("theme_mode", settings.themeMode.name)
            .putBoolean("desktop_mode", settings.desktopModeDefault)
            .putBoolean("enable_js", settings.enableJavaScript)
            .putBoolean("enable_cookies", settings.enableCookies)
            .putBoolean("dynamic_bg", settings.enableDynamicBackground)
            .putBoolean("enable_animations", settings.enableAnimations)
            .putBoolean("save_data_images", settings.blockImagesToSaveData)
            .putBoolean("hw_accel", settings.hardwareAcceleration)
            .apply()
    }
}
