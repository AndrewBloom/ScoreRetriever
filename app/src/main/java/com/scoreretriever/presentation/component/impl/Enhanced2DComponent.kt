package com.scoreretriever.presentation.component.impl

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.scoreretriever.R
import com.scoreretriever.domain.model.Score
import com.scoreretriever.presentation.component.CoinLikeComponent
import com.scoreretriever.presentation.component.ComponentType
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.hazeSource
import dev.chrisbanes.haze.hazeEffect
import dev.chrisbanes.haze.materials.ExperimentalHazeMaterialsApi
import dev.chrisbanes.haze.materials.HazeMaterials
import kotlinx.coroutines.launch
import kotlin.math.abs

/**
 * Enhanced 2D component with glassmorphic blur effects using Haze library.
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
class Enhanced2DComponent : CoinLikeComponent {

    @OptIn(ExperimentalHazeMaterialsApi::class)
    @Composable
    override fun Content(score: Score, modifier: Modifier) {
        val hazeState = remember { HazeState() }
        var currentPage by remember { mutableIntStateOf(0) }
        val rotation = remember { Animatable(0f) }
        val scale = remember { Animatable(1f) }
        val scope = rememberCoroutineScope()
        var isAnimating by remember { mutableStateOf(false) }

        // Determine which page to show based on rotation angle
        val displayPage = remember(currentPage, rotation.value) {
            if (abs(rotation.value) > 90f) {
                // Past 90 degrees, show the next/previous page
                if (rotation.value > 0) {
                    (currentPage - 1 + 4) % 4
                } else {
                    (currentPage + 1) % 4
                }
            } else {
                currentPage
            }
        }

        // Calculate edge visibility based on rotation angle
        // Edge is most visible at 90°, invisible at 0° and 180°
        val edgeVisibility = remember(rotation.value) {
            val normalizedRotation = abs(rotation.value) % 180f
            // Use sine wave: max at 90°, min at 0° and 180°
            abs(kotlin.math.sin(Math.toRadians(normalizedRotation.toDouble()))).toFloat()
        }

        // Calculate edge width based on rotation angle
        val edgeWidth = remember(rotation.value) {
            // Max width of 20dp at 90°, scales down to near 0 at 0° and 180°
            20.dp * edgeVisibility
        }

        Box(
            modifier = modifier
                .fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            // Background layer that will be blurred - mark it as haze source
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .hazeSource(state = hazeState)
            )

            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                // The glassmorphic coin - entire coin rotates as one unit
                Box(
                    modifier = Modifier
                        .size(320.dp)
                        .pointerInput(Unit) {
                            detectHorizontalDragGestures(
                                onDragEnd = {
                                    if (abs(rotation.value) > 90f && !isAnimating) {
                                        // Complete the rotation to 180 degrees and switch page
                                        isAnimating = true
                                        scope.launch {
                                            val targetRotation = if (rotation.value > 0) 180f else -180f
                                            rotation.animateTo(
                                                targetValue = targetRotation,
                                                animationSpec = spring(
                                                    dampingRatio = Spring.DampingRatioMediumBouncy,
                                                    stiffness = Spring.StiffnessMedium
                                                )
                                            )
                                            // updates current page so that snapTo(0) has no effect
                                            currentPage = if (rotation.value > 0) {
                                                (currentPage - 1 + 4) % 4
                                            } else {
                                                (currentPage + 1) % 4
                                            }
//                                            // Reset rotation
                                            rotation.snapTo(0f)
//                                            scale.snapTo(1f)
                                            isAnimating = false
                                        }
                                    } else {
                                        // Snap back to original position
                                        isAnimating = true
                                        scope.launch {
                                            rotation.animateTo(
                                                targetValue = 0f,
                                                animationSpec = spring(
                                                    dampingRatio = Spring.DampingRatioMediumBouncy,
                                                    stiffness = Spring.StiffnessMedium
                                                )
                                            )
                                            scale.animateTo(
                                                targetValue = 1f,
                                                animationSpec = spring(
                                                    dampingRatio = Spring.DampingRatioMediumBouncy,
                                                    stiffness = Spring.StiffnessMedium
                                                )
                                            )
                                            isAnimating = false
                                        }
                                    }
                                }
                            ) { change, dragAmount ->
                                if (!isAnimating) {
                                    change.consume()
                                    // Update rotation based on drag (more responsive)
                                    scope.launch {
                                        val newRotation = (rotation.value + dragAmount / 5f).coerceIn(-180f, 180f)
                                        rotation.snapTo(newRotation)

                                        // Scale down slightly during rotation
                                        //val scaleFactor = 1f - (abs(newRotation) / 180f) * 0.15f
                                        //scale.snapTo(scaleFactor)
                                    }
                                }
                            }
                        }
                        .graphicsLayer {
                            // Both background and content rotate together
                            // Set camera distance for proper 3D perspective
                            cameraDistance = 12f * density
                            rotationY = rotation.value
                            scaleX = scale.value
                            scaleY = scale.value
                        },
                    contentAlignment = Alignment.Center
                ) {
                    // Coin edge/thickness - visible at 90 degrees, scaled and faded based on rotation
                    if (edgeWidth > 1.dp) {  // Only render if visible
                        Box(
                            modifier = Modifier
                                .size(width = edgeWidth, height = 266.dp)
                                .alpha(edgeVisibility)
                                .background(
                                    Color(0xFF303030),
                                    shape = androidx.compose.foundation.shape.RoundedCornerShape(edgeWidth / 2)
                                )
                        )
                    }

                    // Semi-transparent circular background with blur effect
                    Box(
                        modifier = Modifier
                            .size(266.dp)
                            .clip(CircleShape)
                            .hazeEffect(state = hazeState)
                            .background(Color(0x90202020))
                    )

                    // The coin content - show based on rotation angle
                    Box(
                        modifier = Modifier
                            .size(266.dp)
                            .graphicsLayer {
                                // Flip content horizontally when past 90 degrees to appear correct
                                cameraDistance = 12f * density
                                rotationY = if (abs(rotation.value) > 90f) 180f else 0f
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        // Content based on current page
                        CoinFaceContent(
                            page = displayPage,
                            score = score
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Page indicator dots
                PageIndicator(
                    totalPages = 4,
                    currentPage = displayPage
                )
            }
        }
    }

    @Composable
    private fun CoinFaceContent(page: Int, score: Score) {
        when (page) {
            0 -> ScoreFace(score)
            1 -> PercentageFace(score)
            2 -> DetailsFace(score)
            3 -> InfoFace(score)
        }
    }

    @Composable
    private fun ScoreFace(score: Score) {
        Box(
            modifier = Modifier.size(266.dp),
            contentAlignment = Alignment.Center
        ) {
            // Donut progress indicator
            Canvas(modifier = Modifier.size(250.dp)) {
                val strokeWidth = 12.dp.toPx()
                val diameter = size.minDimension - strokeWidth

                // Background circle (dark gray)
                drawCircle(
                    color = Color(0xFF505050),
                    radius = diameter / 2,
                    style = Stroke(width = strokeWidth)
                )

                // Progress arc (gold)
                val sweepAngle = 360f * score.percentage
                val startAngle = -90f // Start from top

                drawArc(
                    color = Color(0xFFFFB800),
                    startAngle = startAngle,
                    sweepAngle = sweepAngle,
                    useCenter = false,
                    topLeft = Offset(strokeWidth / 2, strokeWidth / 2),
                    size = Size(diameter, diameter),
                    style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                )
            }

            // Center content
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = stringResource(R.string.score_label),
                    fontSize = 16.sp,
                    color = Color.White.copy(alpha = 0.9f)
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = score.score.toString(),
                    fontSize = 80.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFFFB800)
                )

                Text(
                    text = stringResource(id = R.string.out_of_max_score, score.maxScore),
                    fontSize = 18.sp,
                    color = Color.White.copy(alpha = 0.8f)
                )
            }
        }
    }

    @Composable
    private fun PercentageFace(score: Score) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "${(score.percentage * 100).toInt()}%",
                fontSize = 72.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFFFFB800)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "ACHIEVEMENT",
                fontSize = 16.sp,
                color = Color.White.copy(alpha = 0.9f)
            )
        }
    }

    @Composable
    private fun DetailsFace(score: Score) {
        Column(
            modifier = Modifier.padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "SCORE DETAILS",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            Spacer(modifier = Modifier.height(24.dp))
            DetailRow("Current", score.score.toString())
            Spacer(modifier = Modifier.height(12.dp))
            DetailRow("Maximum", score.maxScore.toString())
            Spacer(modifier = Modifier.height(12.dp))
            DetailRow("Progress", "${(score.percentage * 100).toInt()}%")
        }
    }

    @Composable
    private fun DetailRow(label: String, value: String) {
        Row(
            modifier = Modifier.padding(vertical = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "$label:",
                fontSize = 16.sp,
                color = Color.White.copy(alpha = 0.7f),
                modifier = Modifier.weight(1f)
            )
            Text(
                text = value,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFFFFB800)
            )
        }
    }

    @Composable
    private fun InfoFace(score: Score) {
        Column(
            modifier = Modifier.padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "\ud83c\udfaf",
                fontSize = 64.sp
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "ENHANCED 2D",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Swipe to explore",
                fontSize = 14.sp,
                color = Color.White.copy(alpha = 0.7f)
            )
        }
    }

    @Composable
    private fun PageIndicator(totalPages: Int, currentPage: Int) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            repeat(totalPages) { index ->
                Box(
                    modifier = Modifier
                        .size(if (index == currentPage) 12.dp else 8.dp)
                        .background(
                            color = if (index == currentPage) {
                                Color(0xFFFFB800)
                            } else {
                                Color.White.copy(alpha = 0.3f)
                            },
                            shape = CircleShape
                        )
                )
            }
        }
    }

    override fun getType(): ComponentType = ComponentType.ENHANCED_2D
}
