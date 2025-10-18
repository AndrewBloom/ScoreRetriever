package com.scoreretriever.presentation.component

import androidx.annotation.StringRes
import com.scoreretriever.R

/**
 * Enum representing different types of CoinLikeComponent implementations.
 *
 * This enum supports the Strategy pattern and Open/Closed Principle:
 * - New component types can be added without modifying existing code
 * - ComponentFactory uses this to instantiate the correct implementation
 * - UI can display selector based on these types
 *
 * Implementation phases:
 * - Phase 1: PLACEHOLDER (simple text/basic display)
 * - Phase 3: BASIC_2D (2D donut chart from PDF spec)
 * - Phase 4: ENHANCED_2D (2.5D glassmorphic coin)
 * - Phase 5: OPENGL_3D (OpenGL-based 3D coin with rotation)
 *
 * @property displayNameRes String resource ID for the display name
 */
enum class ComponentType(@StringRes val displayNameRes: Int) {
    /**
     * Simple placeholder component for Phase 1.
     * Displays credit score as text with basic styling.
     */
    PLACEHOLDER(R.string.component_placeholder),

    /**
     * Basic 2D donut chart (to be implemented in Phase 3).
     * Matches the wireframe specification from PDF.
     */
    BASIC_2D(R.string.component_2d_donut),

    /**
     * Enhanced 2.5D glassmorphic coin (to be implemented in Phase 4).
     * Semi-transparent with blur, rotation gestures, light reflections.
     */
    ENHANCED_2D(R.string.component_2d5_coin),

    /**
     * OpenGL-based 3D coin (to be implemented in Phase 5).
     * Full 3D rendering with advanced effects.
     */
    OPENGL_3D(R.string.component_3d_opengl)
}
