package com.example.data.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class WeatherApiResponse(
    @Json(name = "latitude") val latitude: Double? = null,
    @Json(name = "longitude") val longitude: Double? = null,
    @Json(name = "timezone") val timezone: String? = null,
    @Json(name = "timezone_abbreviation") val timezoneAbbreviation: String? = null,
    @Json(name = "elevation") val elevation: Double? = null,
    @Json(name = "current") val current: CurrentWeatherDto? = null,
    @Json(name = "hourly") val hourly: HourlyWeatherDto? = null,
    @Json(name = "daily") val daily: DailyWeatherDto? = null
)

@JsonClass(generateAdapter = true)
data class CurrentWeatherDto(
    @Json(name = "time") val time: String? = null,
    @Json(name = "temperature_2m") val temperature: Double? = null,
    @Json(name = "relative_humidity_2m") val humidity: Double? = null,
    @Json(name = "apparent_temperature") val apparentTemperature: Double? = null,
    @Json(name = "is_day") val isDay: Int? = 1,
    @Json(name = "precipitation") val precipitation: Double? = 0.0,
    @Json(name = "weather_code") val weatherCode: Int? = 0,
    @Json(name = "wind_speed_10m") val windSpeed: Double? = null
)

@JsonClass(generateAdapter = true)
data class HourlyWeatherDto(
    @Json(name = "time") val time: List<String>? = emptyList(),
    @Json(name = "temperature_2m") val temperature: List<Double?>? = emptyList(),
    @Json(name = "precipitation_probability") val precipitationProbability: List<Int?>? = emptyList(),
    @Json(name = "weather_code") val weatherCode: List<Int?>? = emptyList()
)

@JsonClass(generateAdapter = true)
data class DailyWeatherDto(
    @Json(name = "time") val time: List<String>? = emptyList(),
    @Json(name = "weather_code") val weatherCode: List<Int?>? = emptyList(),
    @Json(name = "temperature_2m_max") val temperatureMax: List<Double?>? = emptyList(),
    @Json(name = "temperature_2m_min") val temperatureMin: List<Double?>? = emptyList(),
    @Json(name = "sunrise") val sunrise: List<String>? = emptyList(),
    @Json(name = "sunset") val sunset: List<String>? = emptyList(),
    @Json(name = "precipitation_probability_max") val precipitationProbabilityMax: List<Int?>? = emptyList()
)
