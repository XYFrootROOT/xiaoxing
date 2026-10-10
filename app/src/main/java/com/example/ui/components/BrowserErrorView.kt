package com.example.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.filled.WifiOff
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.CoolBrandCrimson
import com.example.ui.theme.LiquidGlassButton
import com.example.ui.theme.liquidGlassSurface

@Composable
fun BrowserErrorView(
    url: String,
    errorMessage: String,
    onRetry: () -> Unit,
    onGoHome: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isDark = isSystemInDarkTheme()

    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .liquidGlassSurface(
                    shape = RoundedCornerShape(22.dp),
                    refractionStrength = 16f,
                    dispersion = 0.35f,
                    isDark = isDark
                )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(28.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .liquidGlassSurface(
                            shape = CircleShape,
                            refractionStrength = 10f,
                            dispersion = 0.25f,
                            isDark = isDark,
                            hasSheen = false
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (errorMessage.contains("net", ignoreCase = true) || errorMessage.contains("connect", ignoreCase = true)) {
                            Icons.Default.WifiOff
                        } else {
                            Icons.Default.Warning
                        },
                        contentDescription = "加载错误",
                        tint = MaterialTheme.colorScheme.error,
                        modifier = Modifier.size(32.dp)
                    )
                }

                Spacer(modifier = Modifier.height(18.dp))

                Text(
                    text = "页面连接中断",
                    fontSize = 19.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = errorMessage,
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = url,
                    fontSize = 12.sp,
                    maxLines = 2,
                    color = CoolBrandCrimson,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(22.dp))

                LiquidGlassButton(
                    onClick = onRetry,
                    isPrimary = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(46.dp)
                        .testTag("retry_load_button")
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Default.Refresh, contentDescription = null, tint = Color.White, modifier = Modifier.size(17.dp))
                        Spacer(modifier = Modifier.size(6.dp))
                        Text(text = "重新尝试", color = Color.White, fontWeight = FontWeight.SemiBold)
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(44.dp)
                        .liquidGlassSurface(
                            shape = RoundedCornerShape(16.dp),
                            refractionStrength = 6f,
                            dispersion = 0.15f,
                            isDark = isDark,
                            hasSheen = false,
                            borderWidth = 0.8.dp
                        )
                        .clickable { onGoHome() }
                        .testTag("go_home_button"),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = "返回小星首页", color = MaterialTheme.colorScheme.onSurface, fontSize = 13.sp)
                }
            }
        }
    }
}
