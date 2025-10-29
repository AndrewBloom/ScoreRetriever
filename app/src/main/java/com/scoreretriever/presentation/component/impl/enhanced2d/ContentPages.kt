package com.scoreretriever.presentation.component.impl.enhanced2d

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.scoreretriever.R
import com.scoreretriever.domain.model.Score
import kotlin.math.roundToInt


@Composable
public fun ScoreFace(radius: Dp, score: Score) {
    val progressAnimation = remember { Animatable(0f) }
    val scoreAnimation = remember { Animatable(0f) }

    LaunchedEffect(score.score) {
        // Animate progress bar
        progressAnimation.animateTo(
            targetValue = score.percentage,
            animationSpec = tween(durationMillis = 1500, easing = FastOutSlowInEasing)
        )
    }

    LaunchedEffect(score.score) {
        // Animate number rolling
        scoreAnimation.animateTo(
            targetValue = score.score.toFloat(),
            animationSpec = tween(durationMillis = 1200, easing = FastOutSlowInEasing)
        )
    }

    Box(
        modifier = Modifier.size(radius * 2),
        contentAlignment = Alignment.Center
    ) {
        // Donut progress indicator with animation
        Canvas(modifier = Modifier.size(250.dp)) {
            val strokeWidth = 12.dp.toPx()
            val diameter = size.minDimension - strokeWidth

            // Background circle (dark gray)
            drawCircle(
                color = Color(0xFF505050),
                radius = diameter / 2,
                style = Stroke(width = strokeWidth)
            )

            // Animated progress arc (gold)
            val sweepAngle = 360f * progressAnimation.value
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

            // Animated rolling number
            Text(
                text = scoreAnimation.value.roundToInt().toString(),
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
public fun PercentageFace(radius: Dp, score: Score) {
    val percentageAnimation = remember { Animatable(0f) }
    val scaleAnimation = remember { Animatable(0.5f) }

    LaunchedEffect(score.percentage) {
        // Animate percentage counting up
        percentageAnimation.animateTo(
            targetValue = score.percentage * 100f,
            animationSpec = tween(durationMillis = 1500, easing = FastOutSlowInEasing)
        )
    }

    LaunchedEffect(Unit) {
        // Scale up animation
        scaleAnimation.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 600, easing = FastOutSlowInEasing)
        )
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier.scale(scaleAnimation.value)
    ) {
        Text(
            text = "${percentageAnimation.value.roundToInt()}%",
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
fun DetailsFace(radius: Dp, score: Score) {
    val titleAlpha = remember { Animatable(0f) }
    val row1Alpha = remember { Animatable(0f) }
    val row2Alpha = remember { Animatable(0f) }
    val row3Alpha = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        titleAlpha.animateTo(1f, animationSpec = tween(400))
        row1Alpha.animateTo(1f, animationSpec = tween(400, delayMillis = 200))
        row2Alpha.animateTo(1f, animationSpec = tween(400, delayMillis = 400))
        row3Alpha.animateTo(1f, animationSpec = tween(400, delayMillis = 600))
    }

    Column(
        modifier = Modifier.padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "SCORE DETAILS",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White,
            modifier = Modifier.alpha(titleAlpha.value)
        )
        Spacer(modifier = Modifier.height(24.dp))
        DetailRow("Current", score.score.toString(), Modifier.alpha(row1Alpha.value))
        Spacer(modifier = Modifier.height(12.dp))
        DetailRow("Maximum", score.maxScore.toString(), Modifier.alpha(row2Alpha.value))
        Spacer(modifier = Modifier.height(12.dp))
        DetailRow("Progress", "${(score.percentage * 100).toInt()}%", Modifier.alpha(row3Alpha.value))
    }
}

@Composable
public fun DetailRow(label: String, value: String, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier.padding(vertical = 4.dp),
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
public fun InfoFace(radius: Dp, score: Score) {
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.15f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse scale"
    )

    val fadeInAlpha = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        fadeInAlpha.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 600, easing = LinearEasing)
        )
    }

    Column(
        modifier = Modifier
            .padding(32.dp)
            .alpha(fadeInAlpha.value),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "\ud83c\udfaf",
            fontSize = 64.sp,
            modifier = Modifier.scale(pulseScale)
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
