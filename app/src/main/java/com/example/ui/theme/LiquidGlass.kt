package com.example.ui.theme

import android.graphics.RenderEffect
import android.graphics.RuntimeShader
import android.os.Build
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.asComposeRenderEffect
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import org.intellij.lang.annotations.Language

/**
 * CompositionLocal to respect Android system reduce-motion or user settings.
 */
val LocalAnimationsEnabled = compositionLocalOf { true }

/**
 * iOS 26 × CoolApk Pure Liquid Glass AGSL Shader for Android 13+ (API 33+).
 * Features:
 * - Convex Lens Refraction with edge-normal displacement
 * - Optical Chromatic Dispersion (prism RGB split)
 * - Restrained dynamic specular sheen wave
 * - Top-edge crystal rim specular & grazing Fresnel rim
 */
@Language("AGSL")
private const val PURE_LIQUID_GLASS_SHADER_SRC = """
    uniform shader composable;
    uniform float2 size;
    uniform float refractionStrength;
    uniform float dispersion;
    uniform float sheenProgress;
    uniform float isDark;

    half4 main(float2 fragCoord) {
        if (size.x <= 0.0 || size.y <= 0.0) {
            return composable.eval(fragCoord);
        }
        float2 uv = fragCoord / size;
        float2 p = uv * 2.0 - 1.0;
        float dist = length(p);

        // Convex lens refraction curve concentrated towards rounded boundaries
        float edgeFactor = smoothstep(0.38, 1.0, dist);
        float2 normal = normalize(p + float2(0.0001, 0.0001)) * edgeFactor;
        float2 offset = normal * (refractionStrength / size);

        // Chromatic dispersion (RGB separate refraction index)
        float2 rOffset = offset * (1.0 + dispersion);
        float2 gOffset = offset;
        float2 bOffset = offset * (1.0 - dispersion);

        half4 rCol = composable.eval(fragCoord + rOffset);
        half4 gCol = composable.eval(fragCoord + gOffset);
        half4 bCol = composable.eval(fragCoord - bOffset);
        half4 baseColor = half4(rCol.r, gCol.g, bCol.b, (rCol.a + gCol.a + bCol.a) / 3.0);

        // Pure crystal translucent tinted body (crystal clear, optical clarity)
        half4 tint = isDark > 0.5 ? half4(0.07, 0.09, 0.15, 0.38) : half4(0.98, 0.99, 1.0, 0.42);
        baseColor = mix(baseColor, tint, 0.32);

        // Specular dynamic sheen ray sweep (soft diagonal travelling light)
        float diag = (uv.x + uv.y) * 0.5;
        float sheenDist = abs(diag - sheenProgress);
        float sheen = smoothstep(0.12, 0.0, sheenDist) * 0.16;
        baseColor.rgb += half3(sheen, sheen * 0.96, sheen * 1.04);

        // Crystal top edge reflection & Fresnel specular rim
        float topHighlight = smoothstep(0.10, 0.0, uv.y) * 0.22;
        float rim = pow(edgeFactor, 2.8) * 0.26;
        half3 rimCol = isDark > 0.5 ? half3(0.95, 0.98, 1.0) : half3(1.0, 1.0, 1.0);
        baseColor.rgb += rimCol * (rim + topHighlight);

        return baseColor;
    }
"""

object LiquidGlassDefaults {
    val cornerRadius = 22.dp
    val cardCornerRadius = 18.dp
    val buttonCornerRadius = 16.dp
    val pillCornerRadius = 28.dp
    val dialogCornerRadius = 24.dp
    val shortcutCornerRadius = 16.dp

    // Multi-layer specular crystal border (Top light catching rim fading into subtle prismatic cyan/amber caustics)
    val crystalBorderBrushDark = Brush.verticalGradient(
        colors = listOf(
            Color.White.copy(alpha = 0.38f),       // Crisp top rim specular
            Color(0xFF94A3B8).copy(alpha = 0.16f), // Mid soft refraction
            Color(0xFF38BDF8).copy(alpha = 0.15f), // Subtle prismatic cyan caustics
            Color.White.copy(alpha = 0.08f)        // Bottom ground rim
        )
    )

    val crystalBorderBrushLight = Brush.verticalGradient(
        colors = listOf(
            Color.White.copy(alpha = 0.88f),
            Color(0xFFCBD5E1).copy(alpha = 0.45f),
            Color(0xFF38BDF8).copy(alpha = 0.22f),
            Color.White.copy(alpha = 0.40f)
        )
    )

    // Active accent border for selected tabs / buttons
    val activeBorderBrush = Brush.sweepGradient(
        listOf(
            CoolBrandCrimson.copy(alpha = 0.90f),
            PrismaticCyan.copy(alpha = 0.70f),
            CausticAmber.copy(alpha = 0.60f),
            CoolBrandCrimson.copy(alpha = 0.90f)
        )
    )
}

/**
 * Ambient occlusion shadow for physical depth separation.
 * Ensures the liquid glass surface floats cleanly above backgrounds and web content.
 */
fun Modifier.liquidGlassElevation(
    elevation: Dp = 8.dp,
    shape: Shape = RoundedCornerShape(LiquidGlassDefaults.cardCornerRadius),
    shadowColor: Color = Color(0x4D000000)
): Modifier = this.drawBehind {
    drawIntoCanvas { canvas ->
        val paint = Paint()
        val frameworkPaint = paint.asFrameworkPaint()
        frameworkPaint.color = shadowColor.toArgb()
        frameworkPaint.setShadowLayer(
            elevation.toPx(),
            0f,
            elevation.toPx() * 0.45f,
            shadowColor.toArgb()
        )
        canvas.drawRoundRect(
            left = 0f,
            top = 0f,
            right = size.width,
            bottom = size.height,
            radiusX = 18.dp.toPx(),
            radiusY = 18.dp.toPx(),
            paint = paint
        )
    }
}

/**
 * Liquid Glass Surface Modifier:
 * - On Android 13+ (API 33+), executes AGSL RuntimeShader for physical lens refraction & chromatic dispersion.
 * - Across all versions, applies multilayer specular refraction gradient, top hairline highlight, and travelling sheen.
 */
fun Modifier.liquidGlassSurface(
    shape: Shape = RoundedCornerShape(LiquidGlassDefaults.cornerRadius),
    refractionStrength: Float = 14f,
    dispersion: Float = 0.32f,
    isDark: Boolean = true,
    hasSheen: Boolean = true,
    borderWidth: Dp = 1.0.dp
): Modifier = composed {
    val animationsEnabled = LocalAnimationsEnabled.current
    val infiniteTransition = rememberInfiniteTransition(label = "LiquidSheen")
    val sheenProgress by if (hasSheen && animationsEnabled) {
        infiniteTransition.animateFloat(
            initialValue = -0.3f,
            targetValue = 1.3f,
            animationSpec = infiniteRepeatable(
                animation = tween(durationMillis = 6400, easing = LinearEasing),
                repeatMode = RepeatMode.Restart
            ),
            label = "SheenSweep"
        )
    } else {
        remember { Animatable(0.5f) }.asState()
    }

    val borderBrush = if (isDark) LiquidGlassDefaults.crystalBorderBrushDark else LiquidGlassDefaults.crystalBorderBrushLight
    
    // Multi-stop optical refraction gradient fill (ensures crystalline depth rather than flat grey)
    val bodyGradient = if (isDark) {
        Brush.verticalGradient(
            colors = listOf(
                Color(0x38FFFFFF),       // Subtle top specular haze
                Color(0xD9101524),       // Crystalline translucent body
                Color(0xEB0A0D16)        // Grounded deep obsidian floor
            )
        )
    } else {
        Brush.verticalGradient(
            colors = listOf(
                Color(0xFAFFFFFF),
                Color(0xE6F8FAFC),
                Color(0xD9EEF2F6)
            )
        )
    }

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        val shader = remember { RuntimeShader(PURE_LIQUID_GLASS_SHADER_SRC) }
        this
            .clip(shape)
            .graphicsLayer {
                if (size.width > 0f && size.height > 0f) {
                    shader.setFloatUniform("size", size.width, size.height)
                    shader.setFloatUniform("refractionStrength", refractionStrength)
                    shader.setFloatUniform("dispersion", dispersion)
                    shader.setFloatUniform("sheenProgress", sheenProgress)
                    shader.setFloatUniform("isDark", if (isDark) 1.0f else 0.0f)
                    renderEffect = RenderEffect.createRuntimeShaderEffect(shader, "composable").asComposeRenderEffect()
                }
            }
            .background(bodyGradient, shape)
            .drawWithContent {
                drawContent()
                val w = size.width
                val h = size.height
                if (w > 20f && h > 20f) {
                    // Crisp internal top specular ridge
                    drawLine(
                        color = Color.White.copy(alpha = if (isDark) 0.35f else 0.60f),
                        start = Offset(14f, 1.2f),
                        end = Offset(w - 14f, 1.2f),
                        strokeWidth = 1.2f
                    )
                }
            }
            .border(borderWidth, borderBrush, shape)
    } else {
        // High-fidelity multi-layer fallback
        this
            .clip(shape)
            .background(bodyGradient, shape)
            .drawWithContent {
                drawContent()
                val w = size.width
                val h = size.height

                if (w > 20f && h > 20f) {
                    // Top hairline specular highlight
                    drawLine(
                        color = Color.White.copy(alpha = if (isDark) 0.32f else 0.60f),
                        start = Offset(14f, 1.2f),
                        end = Offset(w - 14f, 1.2f),
                        strokeWidth = 1.2f
                    )

                    // Traveling specular sheen wave
                    if (animationsEnabled && hasSheen) {
                        val startX = (sheenProgress * (w + h)) - h
                        drawLine(
                            brush = Brush.linearGradient(
                                colors = listOf(
                                    Color.Transparent,
                                    Color.White.copy(alpha = if (isDark) 0.12f else 0.22f),
                                    Color.Transparent
                                ),
                                start = Offset(startX, 0f),
                                end = Offset(startX + 80f, h)
                            ),
                            start = Offset(startX, 0f),
                            end = Offset(startX + 80f, h),
                            strokeWidth = 60f
                        )
                    }
                }
            }
            .border(borderWidth, borderBrush, shape)
    }
}

/**
 * Reusable Liquid Glass Button:
 * CoolApk refined feel & iOS 26 fluid spring reaction:
 * - Idle: Crystal translucent liquid base with fine specular border
 * - Pressed: Spring scale bounce (0.94x), caustic flare & active accent response
 */
@Composable
fun LiquidGlassButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    isPrimary: Boolean = true,
    shape: Shape = RoundedCornerShape(LiquidGlassDefaults.buttonCornerRadius),
    content: @Composable BoxScope.() -> Unit
) {
    val animationsEnabled = LocalAnimationsEnabled.current
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    // Smooth physics-based spring scale
    val scale by animateFloatAsState(
        targetValue = if (isPressed && animationsEnabled) 0.94f else 1.0f,
        animationSpec = spring(dampingRatio = 0.64f, stiffness = 500f),
        label = "LiquidButtonScale"
    )

    val backgroundBrush = if (isPrimary) {
        if (isPressed) {
            Brush.linearGradient(
                colors = listOf(
                    CoolBrandCrimson.copy(alpha = 0.95f),
                    Color(0xFFE11D48).copy(alpha = 0.90f)
                )
            )
        } else {
            Brush.linearGradient(
                colors = listOf(
                    CoolBrandCrimson.copy(alpha = 0.85f),
                    CoolBrandCrimsonLight.copy(alpha = 0.70f)
                )
            )
        }
    } else {
        if (isPressed) {
            Brush.linearGradient(
                colors = listOf(
                    Color.White.copy(alpha = 0.28f),
                    Color.White.copy(alpha = 0.16f)
                )
            )
        } else {
            Brush.linearGradient(
                colors = listOf(
                    Color.White.copy(alpha = 0.18f),
                    Color.White.copy(alpha = 0.09f)
                )
            )
        }
    }

    val borderBrush = if (isPrimary) {
        Brush.sweepGradient(
            listOf(
                Color.White.copy(alpha = 0.75f),
                PrismaticCyan.copy(alpha = 0.45f),
                CausticAmber.copy(alpha = 0.50f),
                Color.White.copy(alpha = 0.75f)
            )
        )
    } else {
        LiquidGlassDefaults.crystalBorderBrushDark
    }

    Box(
        modifier = modifier
            .scale(scale)
            .clip(shape)
            .background(backgroundBrush, shape)
            .border(1.0.dp, borderBrush, shape)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                enabled = enabled,
                role = Role.Button,
                onClick = onClick
            )
            .padding(horizontal = 16.dp, vertical = 9.dp),
        contentAlignment = Alignment.Center,
        content = content
    )
}

/**
 * Liquid Glass Icon Button:
 * Minimum 48x48dp touch target, tactile spring feedback, and crystal translucent styling.
 */
@Composable
fun LiquidGlassIconButton(
    icon: ImageVector,
    contentDescription: String?,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    tint: Color = Color.White,
    iconSize: Dp = 20.dp,
    badgeText: String? = null
) {
    val animationsEnabled = LocalAnimationsEnabled.current
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val scale by animateFloatAsState(
        targetValue = if (isPressed && animationsEnabled) 0.90f else 1.0f,
        animationSpec = spring(dampingRatio = 0.65f, stiffness = 520f),
        label = "IconButtonScale"
    )

    Box(
        modifier = modifier
            .size(48.dp)
            .scale(scale)
            .clip(CircleShape)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                enabled = enabled,
                role = Role.Button,
                onClick = onClick
            ),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = contentDescription,
            tint = if (enabled) tint else tint.copy(alpha = 0.35f),
            modifier = Modifier.size(iconSize)
        )

        if (!badgeText.isNullOrEmpty()) {
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(top = 4.dp, end = 4.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(CoolBrandCrimson)
                    .padding(horizontal = 4.dp, vertical = 1.dp)
            ) {
                Text(
                    text = badgeText,
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        }
    }
}

/**
 * Full Liquid Glass Dialog:
 * Replaces standard flat AlertDialog with a floating liquid glass modal card,
 * translucent crystal blur, and smooth entrance.
 */
@Composable
fun LiquidGlassDialog(
    onDismissRequest: () -> Unit,
    title: String,
    onConfirm: () -> Unit,
    confirmText: String = "确定",
    dismissText: String = "取消",
    onDismiss: (() -> Unit)? = null,
    isDark: Boolean = true,
    content: @Composable () -> Unit
) {
    Dialog(
        onDismissRequest = onDismissRequest,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(0.92f)
                .liquidGlassElevation(elevation = 16.dp, shape = RoundedCornerShape(LiquidGlassDefaults.dialogCornerRadius))
                .liquidGlassSurface(
                    shape = RoundedCornerShape(LiquidGlassDefaults.dialogCornerRadius),
                    refractionStrength = 16f,
                    dispersion = 0.35f,
                    isDark = isDark,
                    borderWidth = 1.2.dp
                )
                .padding(22.dp)
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = title,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isDark) Color.White else Color(0xFF0F172A)
                )

                Spacer(modifier = Modifier.height(16.dp))

                content()

                Spacer(modifier = Modifier.height(20.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = androidx.compose.foundation.layout.Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (onDismiss != null) {
                        TextButton(onClick = onDismiss) {
                            Text(
                                text = dismissText,
                                color = if (isDark) Color(0xFF94A3B8) else Color(0xFF64748B),
                                fontSize = 14.sp
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                    }

                    LiquidGlassButton(
                        onClick = onConfirm,
                        isPrimary = true
                    ) {
                        Text(
                            text = confirmText,
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                    }
                }
            }
        }
    }
}
