package com.scoreretriever.presentation.component.impl

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.rememberSplineBasedDecay
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.scoreretriever.domain.model.Score
import com.scoreretriever.presentation.component.CoinLikeComponent
import com.scoreretriever.presentation.component.ComponentType
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.haze
import dev.chrisbanes.haze.materials.ExperimentalHazeMaterialsApi
import kotlinx.coroutines.launch
import kotlin.math.abs

class HazeCoinComponent(
    private val onClick: (() -> Unit)? = null,
    private val thicknessFactor: Float = 0.9f,
    private val blurAlpha: Float = 0.01f
) : CoinLikeComponent {

    @OptIn(ExperimentalHazeMaterialsApi::class)
    @Composable
    override fun Content(score: Score, modifier: Modifier) {
        val hazeState = remember { HazeState() }
        val density = LocalDensity.current.density
        val rotation = remember { Animatable(0f) }
        val scope = rememberCoroutineScope()
        val decay = rememberSplineBasedDecay<Float>()
        var currentPage by remember { mutableStateOf(0) }
        var isAnimating by remember { mutableStateOf(false) }

        // Determine which page to show
        val displayPage = remember(rotation.value, currentPage) {
            if (abs(rotation.value) > 90f) {
                if (rotation.value > 0) (currentPage - 1 + 4) % 4 else (currentPage + 1) % 4
            } else currentPage
        }

        Column(
            modifier = modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(320.dp)
                    .pointerInput(Unit) {
                        detectHorizontalDragGestures(
                            onHorizontalDrag = { _, dragAmount ->
                                if (!isAnimating) {
                                    scope.launch { rotation.snapTo(rotation.value + dragAmount / 5f) }
                                }
                            },
                            onDragEnd = {
                                scope.launch {
                                    val targetPage = if (abs(rotation.value) > 45f) {
                                        if (rotation.value > 0) (currentPage - 1 + 4) % 4 else (currentPage + 1) % 4
                                    } else currentPage
                                    val targetRotation = ((targetPage - currentPage) * 180f).toFloat()
                                    isAnimating = true
                                    rotation.animateTo(
                                        targetRotation,
                                        animationSpec = spring(
                                            dampingRatio = Spring.DampingRatioMediumBouncy,
                                            stiffness = Spring.StiffnessMedium
                                        )
                                    )
                                    currentPage = targetPage
                                    rotation.snapTo(0f)
                                    isAnimating = false
                                }
                            }
                        )
                    }
                    .graphicsLayer {
                        rotationY = rotation.value
                        cameraDistance = 12 * density
                    },
                contentAlignment = Alignment.Center
            ) {
                // Rim shading
                Canvas(Modifier.matchParentSize()) {
                    val radius = size.minDimension / 2f
                    drawCircle(
                        brush = androidx.compose.ui.graphics.Brush.radialGradient(
                            listOf(Color(0xFFBBBBBB), Color(0xFF444444)),
                            center = center,
                            radius = radius * 0.95f
                        ),
                        radius = radius * 0.95f,
                        alpha = 0.3f + thicknessFactor * 0.5f
                    )
                }

                // Blurred background
                Box(
                    modifier = Modifier
                        .fillMaxSize(0.9f)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = blurAlpha))
                        .haze(hazeState),
                    contentAlignment = Alignment.Center
                ) {
                    CoinFaceContent(displayPage, score)
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Page indicators
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                repeat(4) { index ->
                    Box(
                        modifier = Modifier
                            .size(if (index == currentPage) 12.dp else 8.dp)
                            .clip(CircleShape)
                            .background(
                                if (index == currentPage) Color(0xFFFFB800) else Color.White.copy(alpha = 0.3f)
                            )
                    )
                }
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
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Canvas(modifier = Modifier.size(250.dp)) {
                val stroke = 12.dp.toPx()
                val diameter = size.minDimension - stroke
                // Background circle
                drawCircle(Color(0xFF505050), diameter / 2, style = Stroke(stroke))
                // Progress arc
                val sweep = 360f * score.percentage
                drawArc(
                    color = Color(0xFFFFB800),
                    startAngle = -90f,
                    sweepAngle = sweep,
                    useCenter = false,
                    topLeft = androidx.compose.ui.geometry.Offset(stroke / 2, stroke / 2),
                    size = androidx.compose.ui.geometry.Size(diameter, diameter),
                    style = Stroke(width = stroke, cap = StrokeCap.Round)
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            Text(score.score.toString(), fontSize = 80.sp, fontWeight = FontWeight.Bold, color = Color(0xFFFFB800))
            Text("Out of ${score.maxScore}", fontSize = 18.sp, color = Color.White.copy(alpha = 0.8f))
        }
    }

    @Composable
    private fun PercentageFace(score: Score) {
        Text("${(score.percentage * 100).toInt()}%", fontSize = 72.sp, fontWeight = FontWeight.Bold, color = Color(0xFFFFB800))
    }

    @Composable
    private fun DetailsFace(score: Score) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("Score Details", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color.White)
            Spacer(modifier = Modifier.height(8.dp))
            Text("Current: ${score.score}", color = Color.White)
            Text("Max: ${score.maxScore}", color = Color.White)
            Text("Progress: ${(score.percentage * 100).toInt()}%", color = Color.White)
        }
    }

    @Composable
    private fun InfoFace(score: Score) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("\uD83C\uDFAF", fontSize = 48.sp)
            Text("3D Coin", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color.White)
            Text("Swipe to explore", color = Color.White.copy(alpha = 0.7f))
        }
    }

    override fun getType(): ComponentType = ComponentType.OPENGL_3D
}
