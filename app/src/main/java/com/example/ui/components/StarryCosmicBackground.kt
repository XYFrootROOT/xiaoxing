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
import com.example.ui.theme.CausticAmber
import com.example.ui.theme.CoolBrandCrimson
import com.example.ui.theme.CoolObsidianBase
import com.example.ui.theme.LocalAnimationsEnabled
import com.example.ui.theme.PrismaticCyan
import kotlin.random.Random

private data class CosmicParticle(
    val x: Float,
    val y: Float,
    val radius: Float,
    val speed: Float,
    val baseAlpha: Float,
    val color: Color
)

@Composable
fun StarryCosmicBackground(
    enabled: Boolean = true,
    modifier: Modifier = Modifier
) {
    val isDark = isSystemInDarkTheme() || MaterialTheme.colorScheme.background == CoolObsidianBase
    val animationsEnabled = LocalAnimationsEnabled.current && enabled

    // Serene multi-stop celestial background gradient (deep space navy into indigo-sapphire slate)
    val bgBrush = if (isDark) {
        Brush.verticalGradient(
            colors = listOf(
                Color(0xFF070A10), // Ultra deep cosmic void
                Color(0xFF0C111C), // Deep celestial navy
                Color(0xFF0F1526), // Indigo slate
                Color(0xFF131828)  // Grounded calm cosmic night
            )
        )
    } else {
        Brush.verticalGradient(
            colors = listOf(
                Color(0xFFF8FAFC),
                Color(0xFFF1F5F9),
                Color(0xFFE2E8F0)
            )
        )
    }

    if (!enabled) {
        Box(
            modifier = modifier
                .fillMaxSize()
                .background(bgBrush)
        )
        return
    }

    val infiniteTransition = rememberInfiniteTransition(label = "CosmicAmbientTransition")
    val animProgress by if (animationsEnabled) {
        infiniteTransition.animateFloat(
            initialValue = 0f,
            targetValue = 1f,
            animationSpec = infiniteRepeatable(
                animation = tween(durationMillis = 28000, easing = LinearEasing),
                repeatMode = RepeatMode.Restart
            ),
            label = "CausticDrift"
        )
    } else {
        remember { androidx.compose.animation.core.Animatable(0.2f) }.asState()
    }

    val particles = remember {
        val rand = Random(42)
        List(28) { index ->
            val color = when (index % 4) {
                0 -> Color(0xFF818CF8).copy(alpha = 0.55f) // Soft periwinkle indigo
                1 -> PrismaticCyan.copy(alpha = 0.45f)      // Optical cyan
                2 -> CoolBrandCrimson.copy(alpha = 0.50f)   // Brand rose accent
                else -> Color(0xFFA78BFA).copy(alpha = 0.40f) // Gentle violet
            }
            CosmicParticle(
                x = rand.nextFloat(),
                y = rand.nextFloat(),
                radius = rand.nextFloat() * 1.8f + 0.8f,
                speed = rand.nextFloat() * 0.18f + 0.08f,
                baseAlpha = rand.nextFloat() * 0.30f + 0.12f,
                color = color
            )
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(bgBrush)
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val width = size.width
            val height = size.height

            // 1. Low-saturation blue-violet environmental caustics (depth without distracting from glass UI)
            // Top-left calming cosmic indigo glow
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        Color(0xFF4F46E5).copy(alpha = if (isDark) 0.07f else 0.04f),
                        Color.Transparent
                    ),
                    center = Offset(width * 0.18f, height * 0.22f),
                    radius = width * 0.75f
                )
            )

            // Top-right restrained rose-crimson brand glow
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        CoolBrandCrimson.copy(alpha = if (isDark) 0.06f else 0.035f),
                        Color.Transparent
                    ),
                    center = Offset(width * 0.82f, height * 0.16f),
                    radius = width * 0.65f
                )
            )

            // Bottom-center celestial cyan rim glow
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        PrismaticCyan.copy(alpha = if (isDark) 0.045f else 0.03f),
                        Color.Transparent
                    ),
                    center = Offset(width * 0.50f, height * 0.85f),
                    radius = width * 0.80f
                )
            )

            // 2. Slow drifting stardust particles
            particles.forEach { p ->
                val currentY = if (animationsEnabled) {
                    (p.y + animProgress * p.speed) % 1.0f
                } else {
                    p.y
                }
                val driftX = if (animationsEnabled) {
                    (p.x + kotlin.math.sin((animProgress * 1.6f + p.y) * Math.PI.toFloat()) * 0.03f).mod(1.0f)
                } else {
                    p.x
                }
                val alpha = if (animationsEnabled) {
                    (p.baseAlpha + kotlin.math.sin((animProgress + p.x) * Math.PI.toFloat() * 2) * 0.12f)
                        .coerceIn(0.06f, 0.55f) * (if (isDark) 0.85f else 0.50f)
                } else {
                    p.baseAlpha * 0.8f
                }

                drawCircle(
                    color = p.color.copy(alpha = alpha),
                    radius = p.radius,
                    center = Offset(driftX * width, currentY * height)
                )
            }
        }
    }
}
