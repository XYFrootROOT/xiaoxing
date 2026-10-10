package com.example.ui.screens

import android.app.Activity
import android.content.Intent
import android.speech.RecognizerIntent
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.isSystemInDarkTheme
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
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.RocketLaunch
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
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
import com.example.ui.theme.CoolBrandCrimson
import com.example.ui.theme.LiquidGlassButton
import com.example.ui.theme.LiquidGlassDefaults
import com.example.ui.theme.LiquidGlassDialog
import com.example.ui.theme.LocalAnimationsEnabled
import com.example.ui.theme.liquidGlassElevation
import com.example.ui.theme.liquidGlassSurface

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
    val isDark = isSystemInDarkTheme()
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

    CompositionLocalProvider(LocalAnimationsEnabled provides enableAnimations) {
        Box(modifier = modifier.fillMaxSize()) {
            StarryCosmicBackground(enabled = enableDynamicBg)

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 18.dp, vertical = 20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(14.dp))

                // 1. Brand Logo & Avatar Header
                XiaoxingLogoView(
                    size = 80.dp,
                    animated = enableAnimations,
                    modifier = Modifier.testTag("home_brand_logo")
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "小星科创",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.6.sp,
                    color = MaterialTheme.colorScheme.onBackground
                )

                Text(
                    text = "X I A O X I N G   T E C H",
                    fontSize = 10.5.sp,
                    fontWeight = FontWeight.SemiBold,
                    letterSpacing = 2.8.sp,
                    color = CoolBrandCrimson
                )

                Spacer(modifier = Modifier.height(22.dp))

                // 2. Liquid Glass Capsule Search Bar with Floating Elevation
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(58.dp)
                        .liquidGlassElevation(
                            elevation = 10.dp,
                            shape = RoundedCornerShape(LiquidGlassDefaults.pillCornerRadius),
                            shadowColor = Color(0x55000000)
                        )
                        .liquidGlassSurface(
                            shape = RoundedCornerShape(LiquidGlassDefaults.pillCornerRadius),
                            refractionStrength = 16f,
                            dispersion = 0.35f,
                            isDark = isDark,
                            hasSheen = true,
                            borderWidth = 1.0.dp
                        )
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
                            tint = CoolBrandCrimson,
                            modifier = Modifier.size(20.dp)
                        )

                        Spacer(modifier = Modifier.width(10.dp))

                        OutlinedTextField(
                            value = queryText,
                            onValueChange = { queryText = it },
                            placeholder = {
                                Text(
                                    "百度一下，你就知道 / 输入网址...",
                                    fontSize = 14.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.70f)
                                )
                            },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                            keyboardActions = KeyboardActions(onSearch = {
                                if (queryText.isNotBlank()) onNavigate(queryText)
                            }),
                            colors = TextFieldDefaults.colors(
                                focusedContainerColor = Color.Transparent,
                                unfocusedContainerColor = Color.Transparent,
                                focusedIndicatorColor = Color.Transparent,
                                unfocusedIndicatorColor = Color.Transparent,
                                focusedTextColor = MaterialTheme.colorScheme.onSurface,
                                unfocusedTextColor = MaterialTheme.colorScheme.onSurface
                            ),
                            modifier = Modifier
                                .weight(1f)
                                .testTag("home_search_input")
                        )

                        if (queryText.isNotEmpty()) {
                            IconButton(
                                onClick = { queryText = "" },
                                modifier = Modifier.size(36.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "清空",
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }

                        IconButton(
                            onClick = {
                                try {
                                    val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
                                        putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
                                        putExtra(RecognizerIntent.EXTRA_PROMPT, "请说出您要搜索的内容...")
                                    }
                                    speechRecognizerLauncher.launch(intent)
                                } catch (_: Exception) {
                                    Toast.makeText(context, "您的设备暂不支持语音识别", Toast.LENGTH_SHORT).show()
                                }
                            },
                            modifier = Modifier.size(40.dp).testTag("home_voice_search_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Mic,
                                contentDescription = "语音搜索",
                                tint = CoolBrandCrimson,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // 3. Featured Hero Art Banner in Pure Crystalline Liquid Glass Frame
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .liquidGlassElevation(
                            elevation = 12.dp,
                            shape = RoundedCornerShape(22.dp),
                            shadowColor = Color(0x60000000)
                        )
                        .liquidGlassSurface(
                            shape = RoundedCornerShape(22.dp),
                            refractionStrength = 14f,
                            dispersion = 0.32f,
                            isDark = isDark,
                            borderWidth = 1.0.dp
                        )
                ) {
                    Box(modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(22.dp))) {
                        Image(
                            painter = painterResource(id = R.drawable.redhair_hero),
                            contentDescription = "小星科创插画",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .fillMaxWidth()
                                .aspectRatio(2.18f)
                        )

                        // Calm crystalline gradient caption overlay
                        Box(
                            modifier = Modifier
                                .matchParentSize()
                                .background(
                                    Brush.verticalGradient(
                                        colors = listOf(
                                            Color.Transparent,
                                            Color(0x400A0D15),
                                            Color(0xE60A0D15)
                                        )
                                    )
                                )
                                .padding(horizontal = 16.dp, vertical = 12.dp),
                            contentAlignment = Alignment.BottomStart
                        ) {
                            Column {
                                Text(
                                    text = "小星科创 · 酷安质感 Liquid Glass 专属版",
                                    fontSize = 13.5.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                                Text(
                                    text = "纯净透光 · 真实曲面折射 · 丝滑液态弹性交互",
                                    fontSize = 11.sp,
                                    color = Color(0xFFCBD5E1)
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(26.dp))

                // 4. Quick Shortcuts Header with Spring Action Button
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Outlined.RocketLaunch,
                            contentDescription = null,
                            tint = CoolBrandCrimson,
                            modifier = Modifier.size(19.dp)
                        )
                        Spacer(modifier = Modifier.width(7.dp))
                        Text(
                            text = "快捷网站",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                    }

                    LiquidGlassButton(
                        onClick = { showAddDialog = true },
                        isPrimary = true,
                        modifier = Modifier.testTag("add_shortcut_header_button")
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(imageVector = Icons.Default.Add, contentDescription = null, tint = Color.White, modifier = Modifier.size(15.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(text = "添加网站", fontSize = 12.sp, color = Color.White, fontWeight = FontWeight.SemiBold)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // 5. Shortcuts Grid with Tactile Liquid Spring Pedestals
                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    maxItemsInEachRow = 4
                ) {
                    shortcuts.forEach { shortcut ->
                        ShortcutTileItem(
                            shortcut = shortcut,
                            isDark = isDark,
                            enableAnimations = enableAnimations,
                            onClick = { onNavigate(shortcut.url) },
                            onLongClick = { selectedForMenu = shortcut }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(36.dp))
            }
        }
    }

    // Context Menu for shortcuts
    selectedForMenu?.let { item ->
        DropdownMenu(
            expanded = true,
            onDismissRequest = { selectedForMenu = null }
        ) {
            DropdownMenuItem(
                text = { Text("编辑快捷方式") },
                leadingIcon = { Icon(Icons.Default.Edit, contentDescription = null, tint = CoolBrandCrimson) },
                onClick = {
                    editingShortcut = item
                    selectedForMenu = null
                }
            )
            DropdownMenuItem(
                text = { Text("删除", color = MaterialTheme.colorScheme.error) },
                leadingIcon = { Icon(Icons.Default.Delete, contentDescription = null, tint = MaterialTheme.colorScheme.error) },
                onClick = {
                    onDeleteShortcut(item)
                    selectedForMenu = null
                }
            )
        }
    }

    // Add Shortcut Dialog with Liquid Glass Container
    if (showAddDialog) {
        ShortcutEditDialog(
            initialTitle = "",
            initialUrl = "https://",
            titleText = "添加快捷网站",
            isDark = isDark,
            onDismiss = { showAddDialog = false },
            onConfirm = { title, url ->
                onAddShortcut(title, url)
                showAddDialog = false
            }
        )
    }

    // Edit Shortcut Dialog with Liquid Glass Container
    editingShortcut?.let { item ->
        ShortcutEditDialog(
            initialTitle = item.title,
            initialUrl = item.url,
            titleText = "编辑快捷网站",
            isDark = isDark,
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
    isDark: Boolean,
    enableAnimations: Boolean,
    onClick: () -> Unit,
    onLongClick: () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val scale by animateFloatAsState(
        targetValue = if (isPressed && enableAnimations) 0.92f else 1.0f,
        animationSpec = spring(dampingRatio = 0.62f, stiffness = 480f),
        label = "TileScale"
    )

    Column(
        modifier = Modifier
            .width(68.dp)
            .scale(scale)
            .combinedClickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick,
                onLongClick = onLongClick
            ),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Crystalline liquid pedestal with ambient occlusion shadow
        Box(
            modifier = Modifier
                .size(54.dp)
                .liquidGlassElevation(
                    elevation = 6.dp,
                    shape = RoundedCornerShape(LiquidGlassDefaults.shortcutCornerRadius),
                    shadowColor = Color(0x45000000)
                )
                .liquidGlassSurface(
                    shape = RoundedCornerShape(LiquidGlassDefaults.shortcutCornerRadius),
                    refractionStrength = 10f,
                    dispersion = 0.28f,
                    isDark = isDark,
                    borderWidth = 0.9.dp
                ),
            contentAlignment = Alignment.Center
        ) {
            val initialLetter = shortcut.title.firstOrNull()?.toString()?.uppercase() ?: "B"
            Text(
                text = initialLetter,
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
                color = CoolBrandCrimson
            )
        }

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = shortcut.title,
            fontSize = 11.5.sp,
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
    isDark: Boolean,
    onDismiss: () -> Unit,
    onConfirm: (String, String) -> Unit
) {
    var title by remember { mutableStateOf(initialTitle) }
    var url by remember { mutableStateOf(initialUrl) }

    LiquidGlassDialog(
        onDismissRequest = onDismiss,
        title = titleText,
        isDark = isDark,
        confirmText = "保存",
        dismissText = "取消",
        onDismiss = onDismiss,
        onConfirm = {
            if (title.isNotBlank() && url.isNotBlank()) {
                onConfirm(title.trim(), url.trim())
            }
        }
    ) {
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
    }
}
