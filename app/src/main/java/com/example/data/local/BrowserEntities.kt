package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "bookmarks")
data class BookmarkEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val url: String,
    val folder: String = "默认收藏",
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "history")
data class HistoryEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val url: String,
    val visitedAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "quick_shortcuts")
data class QuickShortcutEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val url: String,
    val iconKey: String = "",
    val sortOrder: Int = 0
)

@Entity(tableName = "browser_tabs")
data class TabEntity(
    @PrimaryKey val tabId: String,
    val title: String,
    val url: String,
    val isIncognito: Boolean = false,
    val lastAccessed: Long = System.currentTimeMillis()
)
