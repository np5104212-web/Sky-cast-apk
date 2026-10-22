package com.example.data.api

import com.example.data.model.GeocodingResponse
import com.example.data.model.WeatherApiResponse
import retrofit2.http.GET
import retrofit2.http.Query
import retrofit2.http.Url

interface OpenMeteoApiService {

    @GET("v1/forecast")
    suspend fun getForecast(
        @Query("latitude") latitude: Double,
        @Query("longitude") longitude: Double,
        @Query("current") current: String = "temperature_2m,relative_humidity_2m,apparent_temperature,is_day,precipitation,weather_code,wind_speed_10m",
        @Query("hourly") hourly: String = "temperature_2m,precipitation_probability,weather_code",
        @Query("daily") daily: String = "weather_code,temperature_2m_max,temperature_2m_min,sunrise,sunset,precipitation_probability_max",
        @Query("timezone") timezone: String = "auto",
        @Query("forecast_days") forecastDays: Int = 7
    ): WeatherApiResponse

    @GET
    suspend fun searchLocation(
        @Url url: String = "https://geocoding-api.open-meteo.com/v1/search",
        @Query("name") name: String,
        @Query("count") count: Int = 15,
        @Query("language") language: String = "en",
        @Query("format") format: String = "json"
    ): GeocodingResponse
}
