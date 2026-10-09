package com.example.util

import android.webkit.URLUtil
import java.util.regex.Pattern

object UrlUtils {
    private val WEB_URL_PATTERN = Pattern.compile(
        "^((https?|ftp)://|(www|ftp)\\.)?[a-z0-9-]+(\\.[a-z0-9-]+)+([/?].*)?$",
        Pattern.CASE_INSENSITIVE
    )

    fun resolveInputToUrl(input: String, searchEngineBaseUrl: String): String {
        val trimmed = input.trim()
        if (trimmed.isEmpty()) return "xiaoxing://home"

        if (trimmed.startsWith("http://") || trimmed.startsWith("https://")) {
            return trimmed
        }

        if (trimmed.startsWith("localhost") || trimmed.startsWith("127.0.0.1") ||
            trimmed.contains(".") && !trimmed.contains(" ") && (WEB_URL_PATTERN.matcher(trimmed).matches() || URLUtil.isValidUrl("https://$trimmed"))
        ) {
            return "https://$trimmed"
        }

        // Otherwise query via search engine
        return searchEngineBaseUrl + java.net.URLEncoder.encode(trimmed, "UTF-8")
    }

    fun getDomain(url: String): String {
        return runCatching {
            val uri = java.net.URI(url)
            uri.host ?: url
        }.getOrDefault(url)
    }

    fun isHttps(url: String): Boolean {
        return url.startsWith("https://", ignoreCase = true)
    }
}
