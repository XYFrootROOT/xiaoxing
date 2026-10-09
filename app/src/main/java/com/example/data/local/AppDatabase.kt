package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        BookmarkEntity::class,
        HistoryEntity::class,
        QuickShortcutEntity::class,
        TabEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun bookmarkDao(): BookmarkDao
    abstract fun historyDao(): HistoryDao
    abstract fun quickShortcutDao(): QuickShortcutDao
    abstract fun tabDao(): TabDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "xiaoxing_browser_database"
                )
                    .addCallback(object : Callback() {
                        override fun onCreate(db: SupportSQLiteDatabase) {
                            super.onCreate(db)
                            // Populate default shortcuts
                            CoroutineScope(Dispatchers.IO).launch {
                                val shortcutDao = getDatabase(context).quickShortcutDao()
                                val defaultShortcuts = listOf(
                                    QuickShortcutEntity(
                                        title = "小星科创",
                                        url = "https://www.google.com/search?q=Xiaoxing+Tech",
                                        iconKey = "star",
                                        sortOrder = 0
                                    ),
                                    QuickShortcutEntity(
                                        title = "Google",
                                        url = "https://www.google.com",
                                        iconKey = "google",
                                        sortOrder = 1
                                    ),
                                    QuickShortcutEntity(
                                        title = "Gemini",
                                        url = "https://gemini.google.com",
                                        iconKey = "gemini",
                                        sortOrder = 2
                                    ),
                                    QuickShortcutEntity(
                                        title = "YouTube",
                                        url = "https://www.youtube.com",
                                        iconKey = "youtube",
                                        sortOrder = 3
                                    ),
                                    QuickShortcutEntity(
                                        title = "GitHub",
                                        url = "https://github.com",
                                        iconKey = "github",
                                        sortOrder = 4
                                    ),
                                    QuickShortcutEntity(
                                        title = "ChatGPT",
                                        url = "https://chatgpt.com",
                                        iconKey = "chatgpt",
                                        sortOrder = 5
                                    ),
                                    QuickShortcutEntity(
                                        title = "维基百科",
                                        url = "https://zh.wikipedia.org",
                                        iconKey = "wiki",
                                        sortOrder = 6
                                    ),
                                    QuickShortcutEntity(
                                        title = "哔哩哔哩",
                                        url = "https://www.bilibili.com",
                                        iconKey = "bilibili",
                                        sortOrder = 7
                                    )
                                )
                                shortcutDao.insertAll(defaultShortcuts)
                            }
                        }
                    })
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
