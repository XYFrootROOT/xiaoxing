package com.example

import android.content.Intent
import android.os.Bundle
import android.webkit.WebView
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.model.ActiveScreen
import com.example.ui.components.BrowserAddressBar
import com.example.ui.components.BrowserBottomBar
import com.example.ui.components.BrowserErrorView
import com.example.ui.screens.BookmarksScreen
import com.example.ui.screens.BrowserHomeScreen
import com.example.ui.screens.DownloadsScreen
import com.example.ui.screens.HistoryScreen
import com.example.ui.screens.SettingsScreen
import com.example.ui.screens.TabsOverviewScreen
import com.example.ui.theme.XiaoxingBrowserTheme
import com.example.viewmodel.BrowserViewModel

class MainActivity : ComponentActivity() {
    private val viewModel: BrowserViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            val settings by viewModel.settings.collectAsStateWithLifecycle()

            XiaoxingBrowserTheme(themeMode = settings.themeMode) {
                androidx.compose.runtime.CompositionLocalProvider(
                    com.example.ui.theme.LocalAnimationsEnabled provides settings.enableAnimations
                ) {
                    MainBrowserApp(
                        viewModel = viewModel,
                        onShareUrl = { url, title ->
                            val sendIntent = Intent(Intent.ACTION_SEND).apply {
                                type = "text/plain"
                                putExtra(Intent.EXTRA_TEXT, "$title\n$url")
                            }
                            startActivity(Intent.createChooser(sendIntent, "分享网页链接"))
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun MainBrowserApp(
    viewModel: BrowserViewModel,
    onShareUrl: (String, String) -> Unit
) {
    val activeScreen by viewModel.activeScreen.collectAsStateWithLifecycle()
    val tabs by viewModel.tabs.collectAsStateWithLifecycle()
    val currentTabId by viewModel.currentTabId.collectAsStateWithLifecycle()
    val currentTab = viewModel.currentTab
    val isBookmarked by viewModel.isCurrentBookmarked.collectAsStateWithLifecycle()
    val shortcuts by viewModel.shortcuts.collectAsStateWithLifecycle()
    val settings by viewModel.settings.collectAsStateWithLifecycle()
    val bookmarks by viewModel.bookmarks.collectAsStateWithLifecycle()
    val history by viewModel.history.collectAsStateWithLifecycle()
    val downloads by viewModel.downloadController.downloads.collectAsStateWithLifecycle()

    var activeWebViewRef by remember { mutableStateOf<WebView?>(null) }

    when (activeScreen) {
        ActiveScreen.BROWSER -> {
            BackHandler(enabled = true) {
                if (currentTab != null && !currentTab.isHome) {
                    viewModel.navigateBack(activeWebViewRef)
                } else if (tabs.size > 1) {
                    viewModel.closeTab(currentTabId)
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background)
            ) {
                // Address Bar with Status Bar insets
                BrowserAddressBar(
                    tab = currentTab,
                    onNavigate = { input -> viewModel.openUrl(currentTabId, input) },
                    onReloadOrStop = { viewModel.reloadOrStop(activeWebViewRef) },
                    modifier = Modifier.windowInsetsPadding(WindowInsets.statusBars)
                )

                // Main Content (Home vs WebView vs Error)
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                ) {
                    if (currentTab?.isHome == true) {
                        BrowserHomeScreen(
                            shortcuts = shortcuts,
                            enableDynamicBg = settings.enableDynamicBackground,
                            enableAnimations = settings.enableAnimations,
                            onNavigate = { input -> viewModel.openUrl(currentTabId, input) },
                            onAddShortcut = { title, url -> viewModel.addShortcut(title, url) },
                            onUpdateShortcut = { shortcut -> viewModel.updateShortcut(shortcut) },
                            onDeleteShortcut = { shortcut -> viewModel.deleteShortcut(shortcut) }
                        )
                    } else if (currentTab?.errorMessage != null) {
                        BrowserErrorView(
                            url = currentTab.url,
                            errorMessage = currentTab.errorMessage,
                            onRetry = {
                                viewModel.updateTab(currentTabId) { it.copy(errorMessage = null) }
                                activeWebViewRef?.reload()
                            },
                            onGoHome = {
                                viewModel.updateTab(currentTabId) { it.copy(url = "", errorMessage = null) }
                            }
                        )
                    } else {
                        currentTab?.let { tab ->
                            AndroidView(
                                factory = { ctx ->
                                    viewModel.webViewManager.getOrCreateWebView(
                                        tabId = tab.id,
                                        isIncognito = tab.isIncognito,
                                        settings = settings,
                                        onProgressChanged = { progress ->
                                            viewModel.updateTab(tab.id) {
                                                it.copy(
                                                    progress = progress,
                                                    isLoading = progress in 1..99
                                                )
                                            }
                                        },
                                        onPageStarted = { url ->
                                            viewModel.updateTab(tab.id) {
                                                it.copy(
                                                    url = url,
                                                    isLoading = true,
                                                    canGoBack = activeWebViewRef?.canGoBack() ?: false,
                                                    canGoForward = activeWebViewRef?.canGoForward() ?: false,
                                                    errorMessage = null
                                                )
                                            }
                                        },
                                        onPageFinished = { url, title ->
                                            viewModel.updateTab(tab.id) {
                                                it.copy(
                                                    url = url,
                                                    title = title ?: it.title,
                                                    isLoading = false,
                                                    canGoBack = activeWebViewRef?.canGoBack() ?: false,
                                                    canGoForward = activeWebViewRef?.canGoForward() ?: false
                                                )
                                            }
                                            viewModel.addHistory(title ?: url, url)
                                        },
                                        onReceivedTitle = { title ->
                                            viewModel.updateTab(tab.id) { it.copy(title = title) }
                                        },
                                        onReceivedFavicon = { icon ->
                                            viewModel.updateTab(tab.id) { it.copy(favicon = icon) }
                                        },
                                        onErrorReceived = { error ->
                                            viewModel.updateTab(tab.id) { it.copy(errorMessage = error, isLoading = false) }
                                        },
                                        onDownloadRequested = { url, userAgent, contentDisposition, mimeType ->
                                            viewModel.downloadController.handleDownload(
                                                url,
                                                userAgent,
                                                contentDisposition,
                                                mimeType
                                            )
                                        }
                                    ).also { wv ->
                                        activeWebViewRef = wv
                                        if (tab.url.isNotBlank() && wv.url != tab.url) {
                                            wv.loadUrl(tab.url)
                                        }
                                    }
                                },
                                update = { wv ->
                                    activeWebViewRef = wv
                                    if (tab.url.isNotBlank() && wv.url != tab.url) {
                                        wv.loadUrl(tab.url)
                                    }
                                },
                                modifier = Modifier.fillMaxSize()
                            )
                        }
                    }
                }

                // Bottom Navigation Bar
                BrowserBottomBar(
                    tab = currentTab,
                    tabCount = tabs.size,
                    isBookmarked = isBookmarked,
                    onBack = { viewModel.navigateBack(activeWebViewRef) },
                    onForward = { viewModel.navigateForward(activeWebViewRef) },
                    onNewTab = { viewModel.openNewTab(isIncognito = false) },
                    onToggleBookmark = { viewModel.toggleBookmarkCurrent() },
                    onOpenTabsOverview = { viewModel.setActiveScreen(ActiveScreen.TABS_OVERVIEW) },
                    onOpenBookmarks = { viewModel.setActiveScreen(ActiveScreen.BOOKMARKS) },
                    onOpenHistory = { viewModel.setActiveScreen(ActiveScreen.HISTORY) },
                    onOpenDownloads = { viewModel.setActiveScreen(ActiveScreen.DOWNLOADS) },
                    onOpenSettings = { viewModel.setActiveScreen(ActiveScreen.SETTINGS) },
                    onToggleIncognito = {
                        val currentIncognito = currentTab?.isIncognito ?: false
                        if (currentIncognito) {
                            viewModel.closeTab(currentTabId)
                        } else {
                            viewModel.openNewTab(isIncognito = true)
                        }
                    },
                    onToggleDesktopMode = { viewModel.toggleDesktopMode() },
                    onShare = {
                        if (currentTab != null && !currentTab.isHome) {
                            onShareUrl(currentTab.url, currentTab.title)
                        }
                    }
                )
            }
        }

        ActiveScreen.TABS_OVERVIEW -> {
            TabsOverviewScreen(
                tabs = tabs,
                currentTabId = currentTabId,
                onSelectTab = { viewModel.selectTab(it) },
                onCloseTab = { viewModel.closeTab(it) },
                onCloseAll = { isIncognito -> viewModel.closeAllTabs(isIncognitoOnly = isIncognito) },
                onNewTab = { isIncognito -> viewModel.openNewTab(isIncognito = isIncognito) },
                onBackToBrowser = { viewModel.setActiveScreen(ActiveScreen.BROWSER) }
            )
        }

        ActiveScreen.BOOKMARKS -> {
            BookmarksScreen(
                bookmarks = bookmarks,
                onSelectBookmark = { url ->
                    viewModel.openUrl(currentTabId, url)
                },
                onDeleteBookmark = { viewModel.deleteBookmark(it) },
                onUpdateBookmark = { viewModel.updateBookmark(it) },
                onBack = { viewModel.setActiveScreen(ActiveScreen.BROWSER) }
            )
        }

        ActiveScreen.HISTORY -> {
            HistoryScreen(
                historyList = history,
                onSelectHistory = { url ->
                    viewModel.openUrl(currentTabId, url)
                },
                onDeleteHistoryItem = { viewModel.deleteHistoryItem(it) },
                onClearAllHistory = { viewModel.clearAllHistory() },
                onBack = { viewModel.setActiveScreen(ActiveScreen.BROWSER) }
            )
        }

        ActiveScreen.DOWNLOADS -> {
            DownloadsScreen(
                downloads = downloads,
                onRemoveDownload = { viewModel.downloadController.removeDownload(it) },
                onBack = { viewModel.setActiveScreen(ActiveScreen.BROWSER) }
            )
        }

        ActiveScreen.SETTINGS -> {
            SettingsScreen(
                settings = settings,
                onUpdateSettings = { viewModel.updateSettings(it) },
                onClearData = { cache, cookies, historyData ->
                    viewModel.clearBrowsingData(cache, cookies, historyData)
                },
                onBack = { viewModel.setActiveScreen(ActiveScreen.BROWSER) }
            )
        }
    }
}
