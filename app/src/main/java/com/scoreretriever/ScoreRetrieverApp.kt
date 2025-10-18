package com.scoreretriever

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

/**
 * Application class for the Credit Score app.
 *
 * Annotated with @HiltAndroidApp to:
 * - Enable Hilt dependency injection throughout the app
 * - Trigger Hilt's code generation
 * - Create the application-level dependency container
 *
 * This class must be declared in AndroidManifest.xml as the application name.
 *
 * Following Clean Architecture principles:
 * - Minimal logic in Application class
 * - All dependencies injected via Hilt modules
 * - No direct instantiation of repositories or use cases
 */
@HiltAndroidApp
class ScoreRetrieverApp : Application()
