package com.scoreretriever.presentation.component.impl

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationVector1D
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.scoreretriever.domain.model.Score
import com.scoreretriever.presentation.component.CoinLikeComponent
import com.scoreretriever.presentation.component.ComponentType
import com.scoreretriever.presentation.component.impl.enhanced2d.CircularInt
import com.scoreretriever.presentation.component.impl.enhanced2d.CircularNumber
import com.scoreretriever.presentation.component.impl.enhanced2d.DetailsFace
import com.scoreretriever.presentation.component.impl.enhanced2d.InfoFace
import com.scoreretriever.presentation.component.impl.enhanced2d.PagesIndicatorComponent
import com.scoreretriever.presentation.component.impl.enhanced2d.PercentageFace
import com.scoreretriever.presentation.component.impl.enhanced2d.ScoreFace
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.hazeEffect
import dev.chrisbanes.haze.materials.ExperimentalHazeMaterialsApi
import dev.chrisbanes.haze.materials.HazeMaterials
import kotlinx.coroutines.launch
import kotlin.math.abs
import kotlin.math.ceil
import kotlin.math.cos
import kotlin.math.floor
import kotlin.math.sin

/**
 * Configuration parameters for the Enhanced2D coin component.
 *
 * This class provides a single source of truth for all visual and behavioral
 * parameters of the coin component.
 *
 * @param coinRadius The radius of the coin (half of diameter)
 * @param coinThickness The thickness of the coin edge
 * @param coinSideColor The base color for the main coin faces
 * @param coinRimColor The base color for the coin rim/edge
 * @param containerSize The size of the interactive container around the coin
 * @param cameraDistance The 3D camera distance for perspective (multiplied by density)
 * @param dragSensitivity Factor to control how responsive drag gestures are (higher = less sensitive)
 * @param perspectiveBeta Coefficient for 3D perspective shift calculation
 * @param totalPages Number of virtual pages/faces the coin can display
 * @param springDampingRatio Damping ratio for snap-back animations
 * @param springStiffness Stiffness for snap-back animations
 */
data class CoinParameters(
    val coinRadius: Dp = 133.dp,
    val coinThickness: Dp = 60.dp,
    val coinSideColor: Color = Color(0x20202020),
    val coinRimColor: Color = Color(0x40202020),
    val containerSize: Dp = 320.dp,
    val cameraDistance: Float = 12f,
    val dragSensitivity: Float = 5f,
    val perspectiveBeta: Float = 0.1518f,
    val totalPages: Int = 4,
    val springDampingRatio: Float = Spring.DampingRatioMediumBouncy,
    val springStiffness: Float = Spring.StiffnessMedium
) {
    /** Half of the coin thickness */
    val halfThickness: Dp get() = coinThickness / 2

    /** Diameter of the coin */
    val coinDiameter: Dp get() = coinRadius * 2
}

/**
 * Geometry calculator for coin transformations.
 *
 * This class encapsulates all geometric calculations based on the current rotation
 * state, providing a single source of truth for derived values.
 *
 * @param rotation The current rotation state
 * @param params The coin parameters
 * @param density The display density for pixel conversions
 */
class CoinGeometry(
    private val rotation: Animatable<Float, AnimationVector1D>,
    private val params: CoinParameters,
    private val density: Density
) {
    /** Current rotation angle in radians */
    val rotationRadians: Double get() = Math.toRadians(rotation.value.toDouble())

    /** Current rotation modulo 180 degrees */
    val rotationMod180: Double get() = rotation.value.toDouble() % 180

    /** Coin radius in pixels */
    val radiusPixels: Float get() = params.coinRadius.value * density.density

    /** Horizontal scale factor based on rotation (cosine of angle) */
    val horizontalScale: Float get() = abs(cos(rotationRadians)).toFloat()

    /**
     * Mirror factor: returns 1f when past 90°, -1f otherwise.
     * Used to flip the rim horizontally.
     */
    val mirrorFactor: Float get() {
        val angle = ((rotation.value % 360f + 360f) % 360f) % 180f
        return if (angle > 90f) 1f else -1f
    }

    /**
     * Horizontal slide offset for coin faces during rotation.
     * - 0° to 90°: slides from 0 to +halfThickness
     * - At 90°: snaps to -halfThickness
     * - 90° to 180°: slides from -halfThickness to 0
     */
    val slideOffset: Float get() {
        val sinValue = sin(Math.toRadians(rotationMod180)).toFloat()
        return if (abs(rotationMod180) < 90) {
            // Before 90°: slide in direction of rotation
            sinValue * params.halfThickness.value
        } else {
            // After 90°: flip to other side and slide back
            -sinValue * params.halfThickness.value
        }
    }

    /**
     * Center shift for 3D perspective effect.
     * This accounts for the visual displacement of the coin's center
     * due to perspective projection.
     */
    val centerShift: Float get() {
        return (params.perspectiveBeta * radiusPixels *
                sin(rotationRadians) * cos(rotationRadians)).toFloat()
    }

    /**
     * Center shift for rim (with mirror factor applied).
     */
    val rimCenterShift: Float get() {
        return (-mirrorFactor * params.perspectiveBeta * radiusPixels *
                sin(rotationRadians) * cos(rotationRadians)).toFloat()
    }

    /**
     * Determines if the coin face should be flipped (past 90° rotation).
     */
    val shouldFlipContent: Boolean get() {
        return (abs(rotation.value) + 90) % 360 > 180
    }

    /**
     * Calculate the target snap rotation when drag ends.
     * Snaps to the nearest 0° or 180° position.
     */
    fun calculateSnapTarget(currentRotation: Float): Float {
        val rvMod = currentRotation % 180
        return when {
            rvMod > -90 && rvMod < 90 -> currentRotation - rvMod
            rvMod < -90 -> currentRotation + (-180 - rvMod)
            else -> currentRotation + (180 - rvMod)
        }
    }

    /**
     * Calculate which page should be displayed based on rotation angle.
     */
    fun getDisplayPage(currentPage: CircularNumber<Int>, angle: Float): CircularNumber<Int> {
        return when {
            angle > 90f -> {
                CircularInt(currentPage.value + 1 + ((angle - 90) / 180).toInt(), currentPage.length)
            }
            angle < -90f -> {
                CircularInt(currentPage.value - (1 + ((-angle - 90) / 180).toInt()), currentPage.length)
            }
            else -> currentPage
        }
    }
}

/**
 * Enhanced 2D component with glassmorphic blur effects using Haze library.
 *
 * This refactored version:
 * - Centralizes all parameters in CoinParameters data class
 * - Uses CoinGeometry for all geometric calculations (single source of truth)
 * - Eliminates duplicated formulas
 * - Makes the component more configurable and maintainable
 *
 * Features:
 * - Glassmorphic coin with blur effects on background image
 * - 4 virtual faces showing different information
 * - 3D coin rotation around vertical axis with visible thickness/edge
 * - Swipe gestures for navigation between faces
 * - Content switches at 90 degrees during rotation (like a real coin flip)
 * - Smooth spring-based animations with proper 3D perspective
 * - Page indicator dots
 * - Toroidal navigation (wraps around from 4 to 1)
 */
class Enhanced2DComponent(
    private val params: CoinParameters = CoinParameters()
) : CoinLikeComponent {
    private val pagesIndicator = PagesIndicatorComponent(params.totalPages)

    @OptIn(ExperimentalHazeMaterialsApi::class)
    @Composable
    override fun Content(score: Score, modifier: Modifier, hazeState: HazeState) {
        var currentPage by remember { mutableStateOf(CircularInt(0, params.totalPages)) }
        val rotation = remember { Animatable(0f) }
        val scope = rememberCoroutineScope()
        var isAnimating by remember { mutableStateOf(false) }

        // Determine which page to show based on rotation angle
        var displayPage by remember { mutableStateOf(currentPage) }

        // Get current density
        val density = androidx.compose.ui.platform.LocalDensity.current

        // Create geometry calculator with current state
        val geometry = remember(rotation.value) {
            CoinGeometry(rotation, params, density)
        }

        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Container for coin - handles gestures but doesn't rotate
            Box(
                modifier = Modifier
                    .size(params.containerSize)
                    .pointerInput(Unit) {
                        detectHorizontalDragGestures(
                            onDragEnd = {
                                if (!isAnimating) {
                                    // Snap to closest value when dragging ends
                                    isAnimating = true
                                    scope.launch {
                                        val targetRotation = geometry.calculateSnapTarget(rotation.value)

                                        rotation.animateTo(
                                            targetValue = targetRotation,
                                            animationSpec = spring(
                                                dampingRatio = params.springDampingRatio,
                                                stiffness = params.springStiffness
                                            )
                                        )

                                        // Reset rotation
                                        currentPage = geometry.getDisplayPage(currentPage, rotation.value)
                                        displayPage = currentPage
                                        rotation.snapTo(0f)
                                        isAnimating = false
                                    }
                                }
                            }
                        ) { change, dragAmount ->
                            if (!isAnimating) {
                                change.consume()
                                // Update rotation based on drag
                                scope.launch {
                                    val newRotation = rotation.value + dragAmount / params.dragSensitivity
                                    rotation.snapTo(newRotation)
                                    displayPage = geometry.getDisplayPage(currentPage, newRotation)
                                }
                            }
                        }
                    },
                contentAlignment = Alignment.Center
            ) {
                // Coin side (main face)
                Box(
                    Modifier
                        .size(width = params.coinRadius * 2 * geometry.horizontalScale,
                              height = params.coinDiameter)
                        .graphicsLayer { translationX = geometry.slideOffset - geometry.centerShift }
                        .clip(CoinSideShape(geometry))
                        .hazeEffect(state = hazeState, style = HazeMaterials.ultraThin())
                        .background(params.coinSideColor)
                )

                // The coin face content - rotates and slides horizontally
                Box(
                    modifier = Modifier
                        .size(params.coinDiameter)
                        .graphicsLayer {
                            cameraDistance = params.cameraDistance * density.density
                            rotationY = rotation.value +
                                if (geometry.shouldFlipContent) 180f else 0f
                            translationX = geometry.slideOffset
                        },
                    contentAlignment = Alignment.Center,
                ) {
                    CoinFaceContent(
                        radius = params.coinRadius,
                        page = displayPage.value,
                        score = score
                    )
                }

                // Rim (edge of the coin)
                Box(
                    modifier = Modifier
                        .size(params.coinDiameter, params.coinDiameter)
                        .graphicsLayer {
                            scaleX = geometry.mirrorFactor
                            cameraDistance = params.cameraDistance * density.density
                            translationX = geometry.slideOffset
                        }
                        .offset(params.coinRadius, 0.dp)
                        .clip(RimShape(geometry))
                        .hazeEffect(state = hazeState, style = HazeMaterials.ultraThin())
                        .background(params.coinRimColor)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Page indicator dots
            pagesIndicator.Content(currentPage = displayPage.value)
        }
    }

    @Composable
    private fun CoinFaceContent(page: Int, score: Score, radius: Dp) {
        when (page) {
            0 -> ScoreFace(radius, score)
            1 -> PercentageFace(radius, score)
            2 -> DetailsFace(radius, score)
            3 -> InfoFace(radius, score)
        }
    }

    /**
     * The Shape used to draw the coin surface. It mimics the transformation
     * used by compose with RotationY. The Haze library for blurring does not work
     * with rotationY, so we need to apply the transformation manually.
     *
     * This refactored version uses CoinGeometry as the single source of truth
     * for all geometric calculations.
     */
    class CoinSideShape(
        private val geometry: CoinGeometry
    ) : Shape {
        override fun createOutline(
            size: Size,
            layoutDirection: LayoutDirection,
            density: Density
        ): Outline {
            val r = geometry.radiusPixels
            val horizontalScale = geometry.horizontalScale

            // Create oval representing the rotated coin face
            val path = Path().apply {
                val innerLeft = 0f
                // Truncate with ceil and floor to reduce flickering
                val innerRect = Rect(
                    left = floor(innerLeft) - 0.5f,
                    top = 0f,
                    right = ceil(innerLeft + 2f * r * horizontalScale) + 0.5f,
                    bottom = 2f * r
                )
                addOval(innerRect)
            }

            return Outline.Generic(path)
        }
    }

    /**
     * This class computes the rim shape of a coin, composed of a crescent shape.
     *
     * When a rotation around the Y-axis is applied, we see the coin's edge/thickness,
     * which is limited on one side by the back face of the coin. This creates
     * a crescent-shaped visible area.
     *
     * This refactored version uses CoinGeometry as the single source of truth.
     */
    class RimShape(
        private val geometry: CoinGeometry
    ) : Shape {
        override fun createOutline(
            size: Size,
            layoutDirection: LayoutDirection,
            density: Density
        ): Outline {
            val hso = abs(geometry.slideOffset * 2f)
            val r = geometry.radiusPixels
            val horizontalScale = geometry.horizontalScale
            val centerShift = geometry.rimCenterShift

            val crescentPath = Path().apply {
                // Outer oval (visible edge of the coin)
                val outerLeft = hso + centerShift - r * horizontalScale
                val outerRect = Rect(
                    left = ceil(outerLeft),
                    top = 0f,
                    right = floor(outerLeft + 2f * r * horizontalScale),
                    bottom = 2f * r
                )

                // Inner oval (clipping boundary - back face)
                val innerLeft = centerShift - r * horizontalScale
                val innerRect = Rect(
                    left = ceil(innerLeft) + 0.5f,
                    top = 0f,
                    right = floor(innerLeft + 2f * r * horizontalScale) - 0.5f,
                    bottom = 2f * r
                )

                // Calculate center points
                val outerTopX = (outerRect.left + outerRect.right) / 2f
                val innerTopX = (innerRect.left + innerRect.right) / 2f

                // Build the crescent path
                moveTo(0f, 0f)
                lineTo(outerTopX, 0f)

                // Outer arc from top to bottom (right side)
                arcTo(
                    outerRect,
                    startAngleDegrees = -90f,
                    sweepAngleDegrees = 180f,
                    forceMoveTo = false
                )

                // Bottom edge
                lineTo(innerTopX, 2f * r)

                // Inner arc from bottom to top (right side, backwards)
                arcTo(
                    innerRect,
                    startAngleDegrees = 90f,
                    sweepAngleDegrees = -180f,
                    forceMoveTo = false
                )

                // Close back to start
                lineTo(0f, 0f)
                close()
            }

            return Outline.Generic(crescentPath)
        }
    }

    override fun getType(): ComponentType = ComponentType.ENHANCED_2D
}
