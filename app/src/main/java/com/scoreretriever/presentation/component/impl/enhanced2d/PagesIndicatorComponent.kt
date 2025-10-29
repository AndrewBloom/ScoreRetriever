package com.scoreretriever.presentation.component.impl.enhanced2d

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

class PagesIndicatorComponent(
    val totalPages: Int
)
{
    /**
     * The main composable for the page indicator.
     * It internally calculates which page to display based on the rotation.
     */
    @Composable
    fun Content(
        currentPage: Int,
    ) {
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
}
