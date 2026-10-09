package com.example.viewmodel

import android.app.Application
import android.graphics.Bitmap
import android.webkit.WebView
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.AppDatabase
import com.example.data.local.BookmarkEntity
import com.example.data.local.HistoryEntity
import com.example.data.local.QuickShortcutEntity
import com.example.data.local.TabEntity
import com.example.data.preferences.BrowserSettings
import com.example.data.preferences.PreferencesManager
import com.example.download.DownloadController
import com.example.model.ActiveScreen
import com.example.model.BrowserTab
import com.example.util.UrlUtils
import com.example.webview.BrowserWebViewManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.UUID

class BrowserViewModel(application: Application) : AndroidViewModel(application) {
    private val db = AppDatabase.getDatabase(application)
    private val prefManager = PreferencesManager(application)
    val webViewManager = BrowserWebViewManager(application)
    val downloadController = DownloadController(application)

    val bookmarks: StateFlow<List<BookmarkEntity>> = db.bookmarkDao().getAllBookmarks()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val history: StateFlow<List<HistoryEntity>> = db.historyDao().getAllHistory()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val shortcuts: StateFlow<List<QuickShortcutEntity>> = db.quickShortcutDao().getAllShortcuts()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _settings = MutableStateFlow(prefManager.getSettings())
    val settings: StateFlow<BrowserSettings> = _settings.asStateFlow()

    private val _tabs = MutableStateFlow<List<BrowserTab>>(listOf(createInitialTab()))
    val tabs: StateFlow<List<BrowserTab>> = _tabs.asStateFlow()

    private val _currentTabId = MutableStateFlow(_tabs.value.first().id)
    val currentTabId: StateFlow<String> = _currentTabId.asStateFlow()

    private val _activeScreen = MutableStateFlow(ActiveScreen.BROWSER)
    val activeScreen: StateFlow<ActiveScreen> = _activeScreen.asStateFlow()

    private val _isCurrentBookmarked = MutableStateFlow(false)
    val isCurrentBookmarked: StateFlow<Boolean> = _isCurrentBookmarked.asStateFlow()

    val currentTab: BrowserTab?
        get() = _tabs.value.find { it.id == _currentTabId.value } ?: _tabs.value.firstOrNull()

    init {
        // Restore non-incognito tabs if any were saved
        viewModelScope.launch(Dispatchers.IO) {
            db.tabDao().getSavedNormalTabs().collect { savedTabs ->
                if (savedTabs.isNotEmpty() && _tabs.value.size == 1 && _tabs.value.first().isHome) {
                    val restored = savedTabs.map { entity ->
                        BrowserTab(
                            id = entity.tabId,
                            title = entity.title,
                            url = entity.url,
                            isIncognito = false
                        )
                    }
                    _tabs.value = restored
                    _currentTabId.value = restored.first().id
                }
            }
        }
    }

    private fun createInitialTab(isIncognito: Boolean = false): BrowserTab {
        return BrowserTab(
            id = UUID.randomUUID().toString(),
            title = if (isIncognito) "无痕新标签页" else "小星科创",
            url = "",
            isIncognito = isIncognito
        )
    }

    fun openUrl(tabId: String = _currentTabId.value, input: String) {
        val targetUrl = UrlUtils.resolveInputToUrl(input, _settings.value.searchEngine.searchUrl)
        updateTab(tabId) {
            it.copy(
                url = targetUrl,
                title = if (targetUrl.startsWith("http")) UrlUtils.getDomain(targetUrl) else "小星科创",
                errorMessage = null
            )
        }
        _activeScreen.value = ActiveScreen.BROWSER
        checkIsCurrentBookmarked()
    }

    fun openNewTab(isIncognito: Boolean = false, initialUrl: String = "") {
        val newTab = createInitialTab(isIncognito).copy(
            url = initialUrl,
            title = if (initialUrl.isNotBlank()) UrlUtils.getDomain(initialUrl) else if (isIncognito) "无痕新标签页" else "小星科创"
        )
        _tabs.value = _tabs.value + newTab
        _currentTabId.value = newTab.id
        _activeScreen.value = ActiveScreen.BROWSER
        saveTabsToDb()
    }

    fun selectTab(tabId: String) {
        // Capture thumbnail of currently displayed tab before switching
        captureCurrentThumbnail()
        _currentTabId.value = tabId
        _activeScreen.value = ActiveScreen.BROWSER
        checkIsCurrentBookmarked()
    }

    fun closeTab(tabId: String) {
        val currentList = _tabs.value
        val tabToClose = currentList.find { it.id == tabId }
        webViewManager.removeWebView(tabId, tabToClose?.isIncognito ?: false)

        val updated = currentList.filter { it.id != tabId }
        if (updated.isEmpty()) {
            val freshTab = createInitialTab(tabToClose?.isIncognito ?: false)
            _tabs.value = listOf(freshTab)
            _currentTabId.value = freshTab.id
        } else {
            _tabs.value = updated
            if (_currentTabId.value == tabId) {
                _currentTabId.value = updated.last().id
            }
        }
        viewModelScope.launch(Dispatchers.IO) {
            db.tabDao().deleteTab(tabId)
        }
        checkIsCurrentBookmarked()
    }

    fun closeAllTabs(isIncognitoOnly: Boolean = false) {
        val toRemove = if (isIncognitoOnly) _tabs.value.filter { it.isIncognito } else _tabs.value
        toRemove.forEach {
            webViewManager.removeWebView(it.id, it.isIncognito)
        }
        val remaining = if (isIncognitoOnly) _tabs.value.filter { !it.isIncognito } else emptyList()
        if (remaining.isEmpty()) {
            val fresh = createInitialTab(false)
            _tabs.value = listOf(fresh)
            _currentTabId.value = fresh.id
        } else {
            _tabs.value = remaining
            _currentTabId.value = remaining.first().id
        }
        viewModelScope.launch(Dispatchers.IO) {
            if (!isIncognitoOnly) {
                db.tabDao().clearAllTabs()
            }
        }
    }

    fun updateTab(tabId: String, transform: (BrowserTab) -> BrowserTab) {
        _tabs.value = _tabs.value.map {
            if (it.id == tabId) transform(it) else it
        }
        if (tabId == _currentTabId.value) {
            checkIsCurrentBookmarked()
        }
    }

    fun captureCurrentThumbnail() {
        val tabId = _currentTabId.value
        val thumb = webViewManager.captureThumbnail(tabId)
        if (thumb != null) {
            updateTab(tabId) { it.copy(thumbnail = thumb) }
        }
    }

    fun navigateBack(webView: WebView?) {
        if (webView?.canGoBack() == true) {
            webView.goBack()
        } else {
            // If at first page, go back to home screen
            updateTab(_currentTabId.value) { it.copy(url = "", title = "小星科创") }
        }
    }

    fun navigateForward(webView: WebView?) {
        if (webView?.canGoForward() == true) {
            webView.goForward()
        }
    }

    fun reloadOrStop(webView: WebView?) {
        val tab = currentTab ?: return
        if (tab.isLoading) {
            webView?.stopLoading()
            updateTab(tab.id) { it.copy(isLoading = false) }
        } else {
            webView?.reload()
        }
    }

    fun toggleDesktopMode(tabId: String = _currentTabId.value) {
        val tab = _tabs.value.find { it.id == tabId } ?: return
        val newDesktopState = !tab.isDesktopMode
        updateTab(tabId) { it.copy(isDesktopMode = newDesktopState) }
        webViewManager.setDesktopMode(tabId, newDesktopState)
    }

    fun toggleBookmarkCurrent() {
        val tab = currentTab ?: return
        if (tab.isHome || tab.url.isBlank()) {
            Toast.makeText(getApplication(), "无法收藏主页", Toast.LENGTH_SHORT).show()
            return
        }
        viewModelScope.launch(Dispatchers.IO) {
            val exists = db.bookmarkDao().isBookmarked(tab.url)
            if (exists) {
                db.bookmarkDao().deleteBookmarkByUrl(tab.url)
                _isCurrentBookmarked.value = false
                launch(Dispatchers.Main) {
                    Toast.makeText(getApplication(), "已取消收藏", Toast.LENGTH_SHORT).show()
                }
            } else {
                db.bookmarkDao().insertBookmark(
                    BookmarkEntity(
                        title = tab.title.ifBlank { tab.url },
                        url = tab.url
                    )
                )
                _isCurrentBookmarked.value = true
                launch(Dispatchers.Main) {
                    Toast.makeText(getApplication(), "已添加至书签", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun checkIsCurrentBookmarked() {
        val tab = currentTab ?: return
        if (tab.isHome || tab.url.isBlank()) {
            _isCurrentBookmarked.value = false
            return
        }
        viewModelScope.launch(Dispatchers.IO) {
            _isCurrentBookmarked.value = db.bookmarkDao().isBookmarked(tab.url)
        }
    }

    fun addHistory(title: String, url: String) {
        val tab = currentTab
        if (tab?.isIncognito == true) return // Do not record incognito
        if (url.isBlank() || url.startsWith("about:") || url.startsWith("xiaoxing:")) return
        viewModelScope.launch(Dispatchers.IO) {
            db.historyDao().insertHistory(
                HistoryEntity(
                    title = title.ifBlank { url },
                    url = url
                )
            )
        }
    }

    fun deleteBookmark(bookmark: BookmarkEntity) {
        viewModelScope.launch(Dispatchers.IO) {
            db.bookmarkDao().deleteBookmark(bookmark)
            checkIsCurrentBookmarked()
        }
    }

    fun updateBookmark(bookmark: BookmarkEntity) {
        viewModelScope.launch(Dispatchers.IO) {
            db.bookmarkDao().updateBookmark(bookmark)
        }
    }

    fun deleteHistoryItem(id: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            db.historyDao().deleteHistoryById(id)
        }
    }

    fun clearAllHistory() {
        viewModelScope.launch(Dispatchers.IO) {
            db.historyDao().clearAllHistory()
        }
    }

    fun addShortcut(title: String, url: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val nextSort = db.quickShortcutDao().getCount()
            db.quickShortcutDao().insertShortcut(
                QuickShortcutEntity(
                    title = title,
                    url = if (url.startsWith("http://") || url.startsWith("https://")) url else "https://$url",
                    sortOrder = nextSort
                )
            )
        }
    }

    fun updateShortcut(shortcut: QuickShortcutEntity) {
        viewModelScope.launch(Dispatchers.IO) {
            db.quickShortcutDao().updateShortcut(shortcut)
        }
    }

    fun deleteShortcut(shortcut: QuickShortcutEntity) {
        viewModelScope.launch(Dispatchers.IO) {
            db.quickShortcutDao().deleteShortcut(shortcut)
        }
    }

    fun setActiveScreen(screen: ActiveScreen) {
        if (screen == ActiveScreen.TABS_OVERVIEW) {
            captureCurrentThumbnail()
        }
        _activeScreen.value = screen
    }

    fun updateSettings(newSettings: BrowserSettings) {
        _settings.value = newSettings
        prefManager.updateSettings(newSettings)
    }

    fun clearBrowsingData(clearCache: Boolean, clearCookies: Boolean, clearHistoryData: Boolean) {
        viewModelScope.launch(Dispatchers.IO) {
            if (clearHistoryData) {
                db.historyDao().clearAllHistory()
            }
            launch(Dispatchers.Main) {
                if (clearCache) {
                    android.webkit.WebStorage.getInstance().deleteAllData()
                }
                if (clearCookies) {
                    android.webkit.CookieManager.getInstance().removeAllCookies(null)
                }
                Toast.makeText(getApplication(), "浏览数据清理完成", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun saveTabsToDb() {
        viewModelScope.launch(Dispatchers.IO) {
            val normalTabs = _tabs.value.filter { !it.isIncognito }
            normalTabs.forEach {
                db.tabDao().insertTab(
                    TabEntity(
                        tabId = it.id,
                        title = it.title,
                        url = it.url,
                        isIncognito = false
                    )
                )
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        webViewManager.clearAll()
    }
}
