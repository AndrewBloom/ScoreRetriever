package com.scoreretriever

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.scoreretriever.presentation.screen.ScoreScreen
import com.scoreretriever.ui.theme.ScoreTheme
import dagger.hilt.android.AndroidEntryPoint

/**
 * Main activity for the Score Retriever app.
 *
 * Responsibilities:
 * - Entry point for the app
 * - Sets up Compose UI with theme
 * - Hosts the ScoreScreen
 * - Enables edge-to-edge display
 *
 * Annotated with @AndroidEntryPoint to enable Hilt dependency injection
 * in this activity and all Compose ViewModels within its scope.
 *
 * Following best practices:
 * - Single activity architecture with Jetpack Compose
 * - Minimal logic in Activity (UI setup only)
 * - All business logic in ViewModel
 * - Proper theming and Material 3 support
 */
@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Enable edge-to-edge display (draws behind system bars)
        enableEdgeToEdge()

        // Set up Compose UI
        setContent {
            ScoreTheme {
                Surface(
                    modifier = Modifier.fillMaxSize()
                ) {
                    ScoreScreen()
                }
            }
        }
    }
}
