package com.scoreretriever

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.scoreretriever.presentation.screen.CreditScoreScreen
import com.scoreretriever.ui.theme.CreditScoreTheme
import dagger.hilt.android.AndroidEntryPoint

/**
 * Main activity for the Credit Score app.
 *
 * Responsibilities:
 * - Entry point for the app
 * - Sets up Compose UI with theme
 * - Hosts the CreditScoreScreen
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
            CreditScoreTheme {
                Surface(
                    modifier = Modifier.fillMaxSize()
                ) {
                    CreditScoreScreen()
                }
            }
        }
    }
}
