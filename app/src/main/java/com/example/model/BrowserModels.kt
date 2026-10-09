package com.example.model

import android.graphics.Bitmap

data class BrowserTab(
    val id: String,
    val title: String = "小星科创",
    val url: String = "",
    val isIncognito: Boolean = false,
    val isLoading: Boolean = false,
    val progress: Int = 0,
    val canGoBack: Boolean = false,
    val canGoForward: Boolean = false,
    val isDesktopMode: Boolean = false,
    val favicon: Bitmap? = null,
    val thumbnail: Bitmap? = null,
    val errorMessage: String? = null
) {
    val isHome: Boolean
        get() = url.isBlank() || url == "about:blank" || url == "xiaoxing://home"
}

enum class ActiveScreen {
    BROWSER,
    TABS_OVERVIEW,
    BOOKMARKS,
    HISTORY,
    DOWNLOADS,
    SETTINGS
}

data class DownloadItem(
    val id: Long,
    val fileName: String,
    val url: String,
    val mimeType: String,
    val sizeBytes: Long = 0,
    val progressPercent: Int = 100,
    val isCompleted: Boolean = true,
    val filePath: String = "",
    val timestamp: Long = System.currentTimeMillis()
)
