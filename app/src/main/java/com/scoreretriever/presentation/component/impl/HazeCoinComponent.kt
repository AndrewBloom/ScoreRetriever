package com.scoreretriever.presentation.component.impl

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import com.scoreretriever.domain.model.Score
import com.scoreretriever.presentation.component.CoinLikeComponent
import com.scoreretriever.presentation.component.ComponentType
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.hazeEffect
import dev.chrisbanes.haze.materials.ExperimentalHazeMaterialsApi
import dev.chrisbanes.haze.materials.HazeMaterials
import kotlin.math.abs
import kotlin.math.*

class HazeCoinComponent(
    private val onClick: (() -> Unit)? = null,
    private val thicknessFactor: Float = 0.9f,
    private val blurAlpha: Float = 0.01f
) : CoinLikeComponent {

    @OptIn(ExperimentalHazeMaterialsApi::class)
    @Composable
    override fun Content(score: Score, modifier: Modifier, hazeState: HazeState) {
        val density = LocalDensity.current.density
        val rotation = remember { Animatable(60f) }

        val coinRadius = 133.dp // Half of 266dp coin diameter
        var focalK by remember { mutableStateOf(20f) }

        // --- Animation ---
        LaunchedEffect(Unit) {
            while (true) {
                rotation.animateTo(
                    targetValue = rotation.value + 360f, // rotate one full turn
                    animationSpec = tween(
                        durationMillis = 4000, // 4 seconds per rotation
                        easing = LinearEasing
                    )
                )
                // loop will continue indefinitely
            }
        }
        Box(
            modifier = Modifier
                .fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            // CoinRimSimulation(slideOffset * 2, rotation, coinRadius)
            // The coin face - rotates and slides horizontally
            Box(
                modifier = Modifier
                    .size(coinRadius * 2)
                    .graphicsLayer {
                        cameraDistance = 12f * density
                        rotationY = rotation.value
                    }
                    .clip(CircleShape)
                    .hazeEffect(state = hazeState, style = HazeMaterials.ultraThin())
                    .drawBehind { drawRect(Color(0x000000FF)) }
                    .background(Color(0xA0202020)),
                contentAlignment = Alignment.Center,
            ) {
                // Content with additional flip when past 90°
                Box(
                    modifier = Modifier
                        .graphicsLayer {
                            rotationY = if (abs(rotation.value) > 90f) 180f else 0f
                        },
                    contentAlignment = Alignment.Center,
                ) {
                    Text("HAZE!")
                }
                RotatedCircleOutline(radiusPx = coinRadius.value * density, rotationYDeg = rotation.value, focalDistancePx = focalK*12f * density)
            }
        }
        Box(contentAlignment = Alignment.BottomCenter, modifier = Modifier.fillMaxSize()) {
            Slider(
                value = focalK,
                onValueChange = { focalK = it.coerceAtLeast(200f) },
                valueRange = 200f..500f,
                steps = 0,
            )
        }
    }

    @Composable
    fun RotatedCircleOutline(
        modifier: Modifier = Modifier,
        radiusPx: Float,
        rotationYDeg: Float,
        focalDistancePx: Float,
        color: Color = Color.Yellow,
        strokeWidth: Float = 3f
    ) {
        Box(modifier = modifier) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val ellipse = projectRotatedCircleCompose(
                    radius = radiusPx,
                    thetaDeg = rotationYDeg,
                    focalDistance = focalDistancePx
                )

                val cx = size.width / 2 + ellipse.centerX
                val cy = size.height / 2
                val left = cx - ellipse.a
                val top = cy - ellipse.b
                val right = cx + ellipse.a
                val bottom = cy + ellipse.b

                drawOval(
                    color = color,
                    topLeft = androidx.compose.ui.geometry.Offset(left, top),
                    size = androidx.compose.ui.geometry.Size(right - left, bottom - top),
                    style = Stroke(width = strokeWidth)
                )
            }
        }
    }

    data class EllipseProjection(
        val centerX: Float,
        val a: Float, // horizontal semi-axis
        val b: Float  // vertical semi-axis
    )

    /**
     * Project a circle of radius `radius` (px) rotated by `thetaDeg` around Y,
     * with perspective focal distance `focalDistance` (px).
     *
     * - radius: circle radius in pixels (e.g. coinRadiusDp.toPx()).
     * - thetaDeg: rotationY in degrees (Compose uses degrees).
     * - focalDistance: camera distance in pixels (e.g. cameraDistance * density).
     *
     * returns EllipseProjection(centerX, a, b) in the same units (px).
     */
    fun projectRotatedCircleEllipse(
        radius: Float,
        thetaDeg: Float,
        focalDistance: Float
    ): EllipseProjection {
        val theta = Math.toRadians(thetaDeg.toDouble())
        val r = radius.toDouble()
        val f = focalDistance.toDouble()

        val sinT = sin(theta)
        val cosT = cos(theta)

        val delta = f * f - r * r * sinT * sinT
        require(delta > 0.0) { "Projection degenerates: f^2 <= r^2 * sin^2(theta). Increase focalDistance or reduce radius/rotation." }

        val a = abs((r * f * f * cosT / delta)).toFloat()              // horizontal semi-axis
        val b = abs(r * f / sqrt(delta)).toFloat()                   // vertical semi-axis
        val centerX = (-r * r * f * sinT * cosT / delta).toFloat()// horizontal center offset

        return EllipseProjection(centerX, a, b)
    }


    /**
     * Compose-style projection of a rotated circle (uses Compose's simplified 4×4 matrix model).
     *
     * cameraDistancePx corresponds to the value you set in graphicsLayer.cameraDistance * density.
     */
    fun projectRotatedCircleCompose(
        radius: Float,
        thetaDeg: Float,
        focalDistance: Float
    ): EllipseProjection {
        val theta = Math.toRadians(thetaDeg.toDouble())
        val r = radius.toDouble()
        val d = focalDistance.toDouble()

        val sinT = sin(theta)
        val cosT = cos(theta)
        val k = r * sinT / d
        require(abs(k) < 1.0) { "Degenerate: r*sin(theta)/d must be < 1" }

        val a = (r * cosT / (1 - k * k)).toFloat()
        val b = r.toFloat()
        val centerX = (-r * r * sinT * cosT / (d * (1 - k * k))).toFloat()

        return EllipseProjection(centerX, a, b)
    }

    override fun getType(): ComponentType = ComponentType.OPENGL_3D
}
