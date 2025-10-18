package com.scoreretriever.presentation.component

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.scoreretriever.domain.model.Score

/**
 * Interface for different implementations of the score display component.
 *
 * This interface follows SOLID principles:
 * - Interface Segregation: Minimal interface with only required methods
 * - Open/Closed: Open for extension (new implementations), closed for modification
 * - Liskov Substitution: All implementations can be used interchangeably
 * - Dependency Inversion: UI depends on abstraction, not concrete implementations
 *
 * This enables the Strategy pattern:
 * - Multiple algorithms/implementations for displaying score
 * - Implementations can be swapped at runtime
 * - Each implementation is isolated and testable
 *
 * Expected implementations:
 * - PlaceholderComponent: Simple text display (Phase 1)
 * - Basic2DComponent: 2D donut chart (Phase 3)
 * - Enhanced2DComponent: 2.5D glassmorphic coin (Phase 4)
 * - OpenGL3DComponent: OpenGL 3D coin (Phase 5)
 */
interface CoinLikeComponent {
    /**
     * Renders the score display component.
     *
     * This is a Composable function that implementations must provide.
     * Each implementation can render the score in its own unique way.
     *
     * @param score The score data to display
     * @param modifier Compose modifier for styling and layout (default = Modifier)
     */
    @Composable
    fun Content(
        score: Score,
        modifier: Modifier = Modifier
    )

    /**
     * Returns the component type identifier.
     *
     * Used by ComponentFactory and for debugging/logging.
     *
     * @return ComponentType enum value
     */
    fun getType(): ComponentType
}
