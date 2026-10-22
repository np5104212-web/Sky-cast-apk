package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.repository.PreferencesRepository
import com.example.data.repository.WeatherRepository
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.IndiaOverviewScreen
import com.example.ui.screens.SearchScreen
import com.example.ui.screens.SettingsScreen
import com.example.ui.screens.SevenDayForecastScreen
import com.example.ui.theme.SkyCastTheme
import com.example.ui.viewmodel.WeatherViewModel

enum class AppScreen {
    HOME,
    SEARCH,
    SEVEN_DAY_FORECAST,
    INDIA_OVERVIEW,
    SETTINGS
}

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            val preferencesRepo = remember { PreferencesRepository(applicationContext) }
            val weatherRepo = remember { WeatherRepository() }
            val viewModel = remember { WeatherViewModel(weatherRepo, preferencesRepo) }

            val uiState by viewModel.uiState.collectAsStateWithLifecycle()
            var currentScreen by remember { mutableStateOf(AppScreen.HOME) }

            SkyCastTheme(themeMode = uiState.userSettings.themeMode) {
                Crossfade(
                    targetState = currentScreen,
                    label = "screen_navigation",
                    modifier = Modifier.fillMaxSize()
                ) { screen ->
                    when (screen) {
                        AppScreen.HOME -> HomeScreen(
                            viewModel = viewModel,
                            uiState = uiState,
                            onNavigateToSearch = { currentScreen = AppScreen.SEARCH },
                            onNavigateToForecast = { currentScreen = AppScreen.SEVEN_DAY_FORECAST },
                            onNavigateToIndiaOverview = { currentScreen = AppScreen.INDIA_OVERVIEW },
                            onNavigateToSettings = { currentScreen = AppScreen.SETTINGS }
                        )

                        AppScreen.SEARCH -> SearchScreen(
                            viewModel = viewModel,
                            uiState = uiState,
                            onBack = { currentScreen = AppScreen.HOME }
                        )

                        AppScreen.SEVEN_DAY_FORECAST -> SevenDayForecastScreen(
                            uiState = uiState,
                            onBack = { currentScreen = AppScreen.HOME }
                        )

                        AppScreen.INDIA_OVERVIEW -> IndiaOverviewScreen(
                            viewModel = viewModel,
                            uiState = uiState,
                            onBack = { currentScreen = AppScreen.HOME }
                        )

                        AppScreen.SETTINGS -> SettingsScreen(
                            viewModel = viewModel,
                            uiState = uiState,
                            onBack = { currentScreen = AppScreen.HOME }
                        )
                    }
                }
            }
        }
    }
}
