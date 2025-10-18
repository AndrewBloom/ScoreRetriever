package com.scoreretriever.presentation.component.impl

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.scoreretriever.R
import com.scoreretriever.domain.model.CreditScore
import com.scoreretriever.presentation.component.CoinLikeComponent
import com.scoreretriever.presentation.component.ComponentType

/**
 * Simple placeholder implementation of CoinLikeComponent for Phase 1.
 *
 * This component provides a basic visualization of the credit score:
 * - Circular progress indicator showing score percentage
 * - Score value displayed in the center
 * - Max score value shown below
 * - Simple, clean design matching the wireframe concept
 *
 * This serves as:
 * - A working implementation for Phase 1
 * - A reference for future complex implementations
 * - A fallback when other components are not yet implemented
 *
 * Future phases will add:
 * - Phase 3: Basic2DComponent (2D donut chart with animations)
 * - Phase 4: Enhanced2DComponent (2.5D glassmorphic coin)
 * - Phase 5: OpenGL3DComponent (full 3D rendering)
 */
class PlaceholderComponent : CoinLikeComponent {

    @Composable
    override fun Content(creditScore: CreditScore, modifier: Modifier) {
        Box(
            modifier = modifier.size(250.dp),
            contentAlignment = Alignment.Center
        ) {
            // Background circle
            Canvas(modifier = Modifier.size(250.dp)) {
                val strokeWidth = 12.dp.toPx()
                val diameter = size.minDimension - strokeWidth

                // Background circle (gray)
                drawCircle(
                    color = Color(0xFFE0E0E0),
                    radius = diameter / 2,
                    style = Stroke(width = strokeWidth)
                )

                // Progress arc (colored based on score)
                val sweepAngle = 360f * creditScore.percentage
                val startAngle = -90f // Start from top

                drawArc(
                    color = Color(0xFFFFB800), // Yellow/gold color
                    startAngle = startAngle,
                    sweepAngle = sweepAngle,
                    useCenter = false,
                    topLeft = Offset(strokeWidth / 2, strokeWidth / 2),
                    size = Size(diameter, diameter),
                    style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                )
            }

            // Center content
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = stringResource(R.string.credit_score_label),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = creditScore.score.toString(),
                    fontSize = 72.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFFFB800)
                )

                Text(
                    text = stringResource(id = R.string.out_of_max_score, creditScore.maxScore),
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
            }
        }
    }

    override fun getType(): ComponentType = ComponentType.PLACEHOLDER
}
