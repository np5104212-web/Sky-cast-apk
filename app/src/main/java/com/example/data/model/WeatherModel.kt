package com.example.data.model

data class LocationInfo(
    val name: String,
    val stateOrRegion: String? = null,
    val country: String = "India",
    val latitude: Double,
    val longitude: Double,
    val timezone: String? = null,
    val isFavorite: Boolean = false
) {
    val displayName: String
        get() = if (!stateOrRegion.isNullOrBlank()) "$name, $stateOrRegion" else name

    val fullDisplayName: String
        get() = buildString {
            append(name)
            if (!stateOrRegion.isNullOrBlank()) {
                append(", ").append(stateOrRegion)
            }
            if (country.isNotBlank() && country != stateOrRegion) {
                append(", ").append(country)
            }
        }

    val subtitle: String
        get() = buildString {
            if (!stateOrRegion.isNullOrBlank()) append(stateOrRegion)
            if (!stateOrRegion.isNullOrBlank() && country.isNotBlank()) append(", ")
            if (country.isNotBlank()) append(country)
        }

    companion object {
        val INDORE_DEFAULT = LocationInfo(
            name = "Indore",
            stateOrRegion = "Madhya Pradesh",
            country = "India",
            latitude = 22.7196,
            longitude = 75.8577,
            timezone = "Asia/Kolkata",
            isFavorite = true
        )
    }
}

enum class AlertSeverity {
    WARNING,
    WATCH,
    ADVISORY,
    INFO
}

data class WeatherAlert(
    val title: String,
    val description: String,
    val severity: AlertSeverity,
    val instruction: String? = null,
    val source: String = "Meteorological Advisory"
)

data class CurrentWeather(
    val temperatureC: Double,
    val apparentTemperatureC: Double,
    val humidity: Int,
    val windSpeedKmh: Double,
    val precipitationMm: Double,
    val isDay: Boolean,
    val weatherCode: Int,
    val conditionTitle: String,
    val conditionDescription: String,
    val localTime: String,
    val lastUpdated: String
)

data class HourlyForecastItem(
    val rawIsoTime: String,
    val timeLabel: String,
    val temperatureC: Double,
    val precipitationProbability: Int,
    val weatherCode: Int,
    val isDay: Boolean
)

data class DailyForecastItem(
    val rawDate: String,
    val dayOfWeek: String,
    val formattedDate: String,
    val maxTempC: Double,
    val minTempC: Double,
    val precipitationProbabilityMax: Int,
    val weatherCode: Int,
    val sunrise: String,
    val sunset: String
)

data class WeatherData(
    val location: LocationInfo,
    val current: CurrentWeather,
    val hourly: List<HourlyForecastItem>,
    val daily: List<DailyForecastItem>,
    val timezone: String,
    val alerts: List<WeatherAlert> = emptyList(),
    val fetchedAtEpochMs: Long = System.currentTimeMillis()
)

enum class TemperatureUnit(val symbol: String, val label: String) {
    CELSIUS("°C", "Celsius"),
    FAHRENHEIT("°F", "Fahrenheit")
}

enum class WindSpeedUnit(val unitSymbol: String, val label: String) {
    KM_H("km/h", "Kilometers per hour"),
    M_S("m/s", "Meters per second"),
    MPH("mph", "Miles per hour")
}

enum class AppThemeMode(val label: String) {
    SYSTEM("System Default"),
    LIGHT("Light Mode"),
    DARK("Dark Mode")
}
