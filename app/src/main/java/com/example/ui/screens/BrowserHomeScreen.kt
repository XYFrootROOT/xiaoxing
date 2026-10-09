package com.example.ui.screens

import android.app.Activity
import android.content.Intent
import android.speech.RecognizerIntent
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.Language
import androidx.compose.material.icons.outlined.RocketLaunch
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.local.QuickShortcutEntity
import com.example.ui.components.StarryCosmicBackground
import com.example.ui.components.XiaoxingLogoView
import com.example.ui.theme.CrimsonFlame
import com.example.ui.theme.GoldenAmber
import com.example.ui.theme.RubyAccent
import com.example.ui.theme.SakuraBlossom

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun BrowserHomeScreen(
    shortcuts: List<QuickShortcutEntity>,
    enableDynamicBg: Boolean,
    enableAnimations: Boolean,
    onNavigate: (String) -> Unit,
    onAddShortcut: (String, String) -> Unit,
    onUpdateShortcut: (QuickShortcutEntity) -> Unit,
    onDeleteShortcut: (QuickShortcutEntity) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var queryText by remember { mutableStateOf("") }
    var showAddDialog by remember { mutableStateOf(false) }
    var editingShortcut by remember { mutableStateOf<QuickShortcutEntity?>(null) }
    var selectedForMenu by remember { mutableStateOf<QuickShortcutEntity?>(null) }

    val speechRecognizerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val spokenText = result.data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)?.firstOrNull()
            if (!spokenText.isNullOrBlank()) {
                queryText = spokenText
                onNavigate(spokenText)
            }
        }
    }

    Box(modifier = modifier.fillMaxSize()) {
        StarryCosmicBackground(enabled = enableDynamicBg)

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            // 1. Brand Logo & Avatar Header
            XiaoxingLogoView(
                size = 80.dp,
                animated = enableAnimations,
                modifier = Modifier.testTag("home_brand_logo")
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "小星科创",
                fontSize = 26.sp,
                fontWeight = FontWeight.ExtraBold,
                letterSpacing = 2.sp,
                color = MaterialTheme.colorScheme.onBackground
            )

            Text(
                text = "X I A O X I N G   T E C H",
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold,
                letterSpacing = 3.5.sp,
                color = CrimsonFlame
            )

            Spacer(modifier = Modifier.height(22.dp))

            // 2. Central Search Bar with Baidu Integration
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .clip(RoundedCornerShape(28.dp))
                    .border(
                        width = 1.4.dp,
                        brush = Brush.horizontalGradient(
                            colors = listOf(
                                CrimsonFlame.copy(alpha = 0.85f),
                                GoldenAmber.copy(alpha = 0.7f),
                                SakuraBlossom.copy(alpha = 0.85f)
                            )
                        ),
                        shape = RoundedCornerShape(28.dp)
                    ),
                color = MaterialTheme.colorScheme.surface.copy(alpha = 0.90f),
                tonalElevation = 8.dp,
                shadowElevation = 6.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "搜索",
                        tint = CrimsonFlame,
                        modifier = Modifier.size(22.dp)
                    )

                    Spacer(modifier = Modifier.width(10.dp))

                    OutlinedTextField(
                        value = queryText,
                        onValueChange = { queryText = it },
                        placeholder = {
                            Text(
                                "百度一下，你就知道 / 输入网址...",
                                fontSize = 14.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.65f)
                            )
                        },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                        keyboardActions = KeyboardActions(onSearch = {
                            if (queryText.isNotBlank()) onNavigate(queryText)
                        }),
                        colors = androidx.compose.material3.TextFieldDefaults.colors(
                            focusedContainerColor = Color.Transparent,
                            unfocusedContainerColor = Color.Transparent,
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent
                        ),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("home_search_input")
                    )

                    // Clear button
                    if (queryText.isNotEmpty()) {
                        IconButton(
                            onClick = { queryText = "" },
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "清空",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }

                    // Voice search button
                    IconButton(
                        onClick = {
                            try {
                                val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
                                    putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
                                    putExtra(RecognizerIntent.EXTRA_PROMPT, "请说出您要搜索的内容...")
                                }
                                speechRecognizerLauncher.launch(intent)
                            } catch (_: Exception) {
                                Toast.makeText(context, "您的设备不支持语音识别", Toast.LENGTH_SHORT).show()
                            }
                        },
                        modifier = Modifier.size(36.dp).testTag("home_voice_search_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Mic,
                            contentDescription = "语音搜索",
                            tint = CrimsonFlame,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(26.dp))

            // 3. Featured Hero Art Banner Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(20.dp))
                    .border(
                        width = 1.dp,
                        color = CrimsonFlame.copy(alpha = 0.3f),
                        shape = RoundedCornerShape(20.dp)
                    ),
                shape = RoundedCornerShape(20.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Box(modifier = Modifier.fillMaxWidth()) {
                    Image(
                        painter = painterResource(id = R.drawable.redhair_hero),
                        contentDescription = "小星科创插画",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .fillMaxWidth()
                            .aspectRatio(2.1f)
                    )

                    // Gradient overlay with quote
                    Box(
                        modifier = Modifier
                            .matchParentSize()
                            .background(
                                Brush.verticalGradient(
                                    colors = listOf(
                                        Color.Transparent,
                                        Color(0x99140A12),
                                        Color(0xEA140A12)
                                    )
                                )
                            )
                            .padding(horizontal = 14.dp, vertical = 10.dp),
                        contentAlignment = Alignment.BottomStart
                    ) {
                        Column {
                            Text(
                                text = "小星科创 · 红绯樱落特别版",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Text(
                                text = "春风拂樱 · 极速同行 · 纯净浏览探索世界",
                                fontSize = 11.sp,
                                color = SakuraBlossom
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(26.dp))

            // 4. Quick Shortcuts Grid Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Outlined.RocketLaunch,
                        contentDescription = null,
                        tint = CrimsonFlame,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "快捷网站",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                }

                TextButton(
                    onClick = { showAddDialog = true },
                    modifier = Modifier.testTag("add_shortcut_header_button")
                ) {
                    Icon(imageVector = Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = "添加网站", fontSize = 13.sp, color = CrimsonFlame)
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Shortcuts Grid
            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                maxItemsInEachRow = 4
            ) {
                shortcuts.forEach { shortcut ->
                    ShortcutTileItem(
                        shortcut = shortcut,
                        onClick = { onNavigate(shortcut.url) },
                        onLongClick = { selectedForMenu = shortcut }
                    )
                }
            }

            Spacer(modifier = Modifier.height(30.dp))
        }
    }

    // Context Menu for Long-Pressed Shortcut
    selectedForMenu?.let { item ->
        DropdownMenu(
            expanded = true,
            onDismissRequest = { selectedForMenu = null }
        ) {
            DropdownMenuItem(
                text = { Text("编辑快捷方式") },
                onClick = {
                    editingShortcut = item
                    selectedForMenu = null
                }
            )
            DropdownMenuItem(
                text = { Text("删除", color = MaterialTheme.colorScheme.error) },
                onClick = {
                    onDeleteShortcut(item)
                    selectedForMenu = null
                }
            )
        }
    }

    // Add Shortcut Dialog
    if (showAddDialog) {
        ShortcutEditDialog(
            initialTitle = "",
            initialUrl = "https://",
            titleText = "添加快捷网站",
            onDismiss = { showAddDialog = false },
            onConfirm = { title, url ->
                onAddShortcut(title, url)
                showAddDialog = false
            }
        )
    }

    // Edit Shortcut Dialog
    editingShortcut?.let { item ->
        ShortcutEditDialog(
            initialTitle = item.title,
            initialUrl = item.url,
            titleText = "编辑快捷网站",
            onDismiss = { editingShortcut = null },
            onConfirm = { title, url ->
                onUpdateShortcut(item.copy(title = title, url = url))
                editingShortcut = null
            }
        )
    }
}

@OptIn(androidx.compose.foundation.ExperimentalFoundationApi::class)
@Composable
private fun ShortcutTileItem(
    shortcut: QuickShortcutEntity,
    onClick: () -> Unit,
    onLongClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .width(68.dp)
            .combinedClickable(
                onClick = onClick,
                onLongClick = onLongClick
            ),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Surface(
            modifier = Modifier.size(52.dp),
            shape = RoundedCornerShape(16.dp),
            color = MaterialTheme.colorScheme.surfaceVariant,
            tonalElevation = 4.dp,
            shadowElevation = 2.dp
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                val initialLetter = shortcut.title.firstOrNull()?.toString()?.uppercase() ?: "B"
                Text(
                    text = initialLetter,
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    color = CrimsonFlame
                )
            }
        }

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = shortcut.title,
            fontSize = 11.sp,
            fontWeight = FontWeight.Medium,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

@Composable
private fun ShortcutEditDialog(
    initialTitle: String,
    initialUrl: String,
    titleText: String,
    onDismiss: () -> Unit,
    onConfirm: (String, String) -> Unit
) {
    var title by remember { mutableStateOf(initialTitle) }
    var url by remember { mutableStateOf(initialUrl) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = titleText, fontWeight = FontWeight.Bold) },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("网站名称") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(12.dp))
                OutlinedTextField(
                    value = url,
                    onValueChange = { url = it },
                    label = { Text("网址 URL") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (title.isNotBlank() && url.isNotBlank()) {
                        onConfirm(title.trim(), url.trim())
                    }
                }
            ) {
                Text("保存")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("取消")
            }
        }
    )
}
