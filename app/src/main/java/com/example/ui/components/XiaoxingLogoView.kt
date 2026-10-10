package com.example.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.R
import com.example.ui.theme.CausticAmber
import com.example.ui.theme.CoolBrandCrimson
import com.example.ui.theme.PrismaticCyan

@Composable
fun XiaoxingLogoView(
    size: Dp = 76.dp,
    animated: Boolean = true,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "AuraTransition")
    val rotationAngle by if (animated) {
        infiniteTransition.animateFloat(
            initialValue = 0f,
            targetValue = 360f,
            animationSpec = infiniteRepeatable(
                animation = tween(12000, easing = LinearEasing),
                repeatMode = RepeatMode.Restart
            ),
            label = "Rotation"
        )
    } else {
        infiniteTransition.animateFloat(
            initialValue = 0f,
            targetValue = 0f,
            animationSpec = infiniteRepeatable(tween(1)),
            label = "Static"
        )
    }

    Box(
        modifier = modifier.size(size),
        contentAlignment = Alignment.Center
    ) {
        // High-end crystalline ring: crisp white specular with subtle optical refraction flare
        Box(
            modifier = Modifier
                .size(size)
                .rotate(rotationAngle)
                .border(
                    width = 1.6.dp,
                    brush = Brush.sweepGradient(
                        listOf(
                            Color.White.copy(alpha = 0.85f),
                            PrismaticCyan.copy(alpha = 0.50f),
                            CausticAmber.copy(alpha = 0.40f),
                            CoolBrandCrimson.copy(alpha = 0.60f),
                            Color.White.copy(alpha = 0.85f)
                        )
                    ),
                    shape = CircleShape
                )
        )

        // Character Avatar with smooth circle cut & inner glass specular
        Image(
            painter = painterResource(id = R.drawable.redhair_avatar),
            contentDescription = "小星科创头像",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(size - 8.dp)
                .clip(CircleShape)
        )
    }
}
