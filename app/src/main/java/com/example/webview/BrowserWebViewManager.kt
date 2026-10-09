package com.example.webview

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.view.ViewGroup
import android.webkit.CookieManager
import android.webkit.WebChromeClient
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import com.example.data.preferences.BrowserSettings

class BrowserWebViewManager(private val context: Context) {
    private val webViewPool = mutableMapOf<String, WebView>()

    @SuppressLint("SetJavaScriptEnabled")
    fun getOrCreateWebView(
        tabId: String,
        isIncognito: Boolean,
        settings: BrowserSettings,
        onProgressChanged: (Int) -> Unit,
        onPageStarted: (String) -> Unit,
        onPageFinished: (String, String?) -> Unit,
        onReceivedTitle: (String) -> Unit,
        onReceivedFavicon: (Bitmap) -> Unit,
        onErrorReceived: (String) -> Unit,
        onDownloadRequested: (url: String, userAgent: String, contentDisposition: String, mimeType: String) -> Unit
    ): WebView {
        webViewPool[tabId]?.let { wv ->
            applySettings(wv, settings, isIncognito)
            return wv
        }

        val webView = WebView(context).apply {
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )

            isVerticalScrollBarEnabled = true
            isHorizontalScrollBarEnabled = true

            webChromeClient = object : WebChromeClient() {
                override fun onProgressChanged(view: WebView?, newProgress: Int) {
                    onProgressChanged(newProgress)
                }

                override fun onReceivedTitle(view: WebView?, title: String?) {
                    title?.let { onReceivedTitle(it) }
                }

                override fun onReceivedIcon(view: WebView?, icon: Bitmap?) {
                    icon?.let { onReceivedFavicon(it) }
                }
            }

            webViewClient = object : WebViewClient() {
                override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                    super.onPageStarted(view, url, favicon)
                    url?.let { onPageStarted(it) }
                }

                override fun onPageFinished(view: WebView?, url: String?) {
                    super.onPageFinished(view, url)
                    url?.let { onPageFinished(it, view?.title) }
                }

                override fun onReceivedError(
                    view: WebView?,
                    request: WebResourceRequest?,
                    error: WebResourceError?
                ) {
                    super.onReceivedError(view, request, error)
                    if (request?.isForMainFrame == true) {
                        val desc = error?.description?.toString() ?: "网页加载失败"
                        onErrorReceived(desc)
                    }
                }
            }

            setDownloadListener { url, userAgent, contentDisposition, mimetype, _ ->
                onDownloadRequested(url, userAgent, contentDisposition, mimetype)
            }
        }

        applySettings(webView, settings, isIncognito)
        webViewPool[tabId] = webView
        return webView
    }

    @SuppressLint("SetJavaScriptEnabled")
    fun applySettings(webView: WebView, settings: BrowserSettings, isIncognito: Boolean) {
        webView.settings.apply {
            javaScriptEnabled = settings.enableJavaScript
            domStorageEnabled = !isIncognito
            databaseEnabled = !isIncognito
            loadsImagesAutomatically = !settings.blockImagesToSaveData
            useWideViewPort = true
            loadWithOverviewMode = true
            setSupportZoom(true)
            builtInZoomControls = true
            displayZoomControls = false
            allowFileAccess = !isIncognito
            mixedContentMode = WebSettings.MIXED_CONTENT_NEVER_ALLOW

            if (settings.desktopModeDefault) {
                userAgentString = "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36"
            } else {
                userAgentString = null // Default mobile UA
            }
        }

        val cookieManager = CookieManager.getInstance()
        cookieManager.setAcceptCookie(settings.enableCookies)
        if (isIncognito) {
            cookieManager.setAcceptThirdPartyCookies(webView, false)
        } else {
            cookieManager.setAcceptThirdPartyCookies(webView, settings.enableCookies)
        }
    }

    fun setDesktopMode(tabId: String, enable: Boolean) {
        webViewPool[tabId]?.let { wv ->
            if (enable) {
                wv.settings.userAgentString = "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36"
            } else {
                wv.settings.userAgentString = null
            }
            wv.reload()
        }
    }

    fun captureThumbnail(tabId: String): Bitmap? {
        val wv = webViewPool[tabId] ?: return null
        return try {
            val width = wv.width.coerceAtLeast(1)
            val height = wv.height.coerceAtLeast(1)
            val scale = 0.35f
            val thumbWidth = (width * scale).toInt().coerceAtLeast(100)
            val thumbHeight = (height * scale).toInt().coerceAtLeast(100)
            val bitmap = Bitmap.createBitmap(thumbWidth, thumbHeight, Bitmap.Config.ARGB_8888)
            val canvas = android.graphics.Canvas(bitmap)
            canvas.scale(scale, scale)
            wv.draw(canvas)
            bitmap
        } catch (_: Exception) {
            null
        }
    }

    fun removeWebView(tabId: String, isIncognito: Boolean = false) {
        webViewPool.remove(tabId)?.let { wv ->
            if (isIncognito) {
                wv.clearCache(true)
                wv.clearFormData()
                wv.clearHistory()
                CookieManager.getInstance().removeSessionCookies(null)
            }
            wv.stopLoading()
            wv.loadUrl("about:blank")
            (wv.parent as? ViewGroup)?.removeView(wv)
            wv.destroy()
        }
    }

    fun clearAll(isIncognitoOnly: Boolean = false) {
        val keys = webViewPool.keys.toList()
        for (key in keys) {
            if (!isIncognitoOnly) {
                removeWebView(key)
            }
        }
    }
}
