package com.example.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import com.example.ui.theme.CrimsonFlame
import com.example.ui.theme.DarkCherryNight
import com.example.ui.theme.GoldenAmber
import com.example.ui.theme.ObsidianCard
import com.example.ui.theme.RubyAccent
import com.example.ui.theme.SakuraBlossom
import kotlin.random.Random

private data class SakuraPetal(
    val x: Float,
    val y: Float,
    val radius: Float,
    val speed: Float,
    val alphaBase: Float,
    val color: Color
)

@Composable
fun StarryCosmicBackground(
    enabled: Boolean = true,
    modifier: Modifier = Modifier
) {
    val isDark = isSystemInDarkTheme() || MaterialTheme.colorScheme.background == DarkCherryNight

    if (!enabled) {
        Box(
            modifier = modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
        )
        return
    }

    val infiniteTransition = rememberInfiniteTransition(label = "PetalTransition")
    val animProgress by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 16000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "PetalMovement"
    )

    val petals = remember {
        val rand = Random(42)
        List(45) {
            val pickColor = when (rand.nextInt(3)) {
                0 -> CrimsonFlame
                1 -> SakuraBlossom
                else -> GoldenAmber
            }
            SakuraPetal(
                x = rand.nextFloat(),
                y = rand.nextFloat(),
                radius = rand.nextFloat() * 2.8f + 1.2f,
                speed = rand.nextFloat() * 0.45f + 0.2f,
                alphaBase = rand.nextFloat() * 0.45f + 0.2f,
                color = pickColor
            )
        }
    }

    val bgBrush = if (isDark) {
        Brush.verticalGradient(
            colors = listOf(
                DarkCherryNight,
                ObsidianCard,
                Color(0xFF1F0D1B)
            )
        )
    } else {
        Brush.verticalGradient(
            colors = listOf(
                Color(0xFFFFF0F5),
                Color(0xFFFFE3EC),
                Color(0xFFFFD6E0)
            )
        )
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(bgBrush)
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val width = size.width
            val height = size.height

            // Warm radiant aura from cherry blossom sun flare
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        CrimsonFlame.copy(alpha = if (isDark) 0.12f else 0.08f),
                        GoldenAmber.copy(alpha = if (isDark) 0.05f else 0.04f),
                        Color.Transparent
                    ),
                    center = Offset(width * 0.5f, height * 0.2f),
                    radius = width * 0.75f
                )
            )

            // Flowing glowing petals / embers
            petals.forEach { p ->
                val currentY = (p.y + animProgress * p.speed) % 1.0f
                val driftX = (p.x + kotlin.math.sin((animProgress * 2f + p.y) * Math.PI.toFloat()) * 0.05f).mod(1.0f)
                val alpha = (p.alphaBase + kotlin.math.sin((animProgress + p.x) * Math.PI.toFloat() * 2) * 0.2f)
                    .coerceIn(0.1f, 0.85f) * (if (isDark) 1.0f else 0.6f)

                drawCircle(
                    color = p.color.copy(alpha = alpha),
                    radius = p.radius,
                    center = Offset(driftX * width, currentY * height)
                )
            }
        }
    }
}
