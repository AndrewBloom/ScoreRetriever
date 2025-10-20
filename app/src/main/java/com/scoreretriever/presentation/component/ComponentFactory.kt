package com.scoreretriever.presentation.component

import com.scoreretriever.presentation.component.impl.Enhanced2DComponent
import com.scoreretriever.presentation.component.impl.HazeCoinComponent
import com.scoreretriever.presentation.component.impl.PlaceholderComponent

/**
 * Factory for creating CoinLikeComponent instances based on ComponentType.
 *
 * This class implements the Factory pattern and follows SOLID principles:
 * - Single Responsibility: Only responsible for component instantiation
 * - Open/Closed: Can add new component types without modifying existing code
 * - Dependency Inversion: Returns interface, not concrete types
 *
 * Benefits:
 * - Centralizes component creation logic
 * - Makes it easy to add new implementations
 * - Supports runtime component switching
 * - Easier to test (can inject mock factory)
 *
 * Usage:
 * ```kotlin
 * val component = ComponentFactory.create(ComponentType.PLACEHOLDER)
 * component.Content(score)
 * ```
 */
object ComponentFactory {
    /**
     * Creates a CoinLikeComponent instance based on the specified type.
     *
     * Current implementations:
     * - PLACEHOLDER: PlaceholderComponent - Simple 2D with circular progress
     * - ENHANCED_2D: Enhanced2DComponent - Glassmorphic coin with Haze blur effects
     * - BASIC_2D: Not yet implemented - returns placeholder for now
     * - OPENGL_3D: Not yet implemented - returns placeholder for now
     *
     * As new implementations are added in future phases, this method will be updated
     * to return the appropriate component type.
     *
     * @param type The type of component to create
     * @return CoinLikeComponent instance
     */
    fun create(type: ComponentType): CoinLikeComponent {
        return when (type) {
            ComponentType.PLACEHOLDER -> PlaceholderComponent()

            ComponentType.ENHANCED_2D -> Enhanced2DComponent()

            // Future implementations (to be added in later phases)
            ComponentType.BASIC_2D -> {
                // TODO: Phase 3 - Implement Basic2DComponent
                // return Basic2DComponent()
                PlaceholderComponent() // Fallback for now
            }

            ComponentType.OPENGL_3D -> {
                // TODO: Phase 5 - Implement OpenGL3DComponent
                // return OpenGL3DComponent()
                HazeCoinComponent()
            }
        }
    }
}
