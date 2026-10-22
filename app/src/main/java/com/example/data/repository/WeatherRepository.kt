package com.example.data.repository

import com.example.data.api.NetworkClient
import com.example.data.api.OpenMeteoApiService
import com.example.data.model.CurrentWeather
import com.example.data.model.DailyForecastItem
import com.example.data.model.HourlyForecastItem
import com.example.data.model.LocationInfo
import com.example.data.model.WeatherApiResponse
import com.example.data.model.WeatherData
import com.example.ui.model.WeatherConditionMapper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone
import java.util.concurrent.ConcurrentHashMap

class WeatherRepository(
    private val apiService: OpenMeteoApiService = NetworkClient.apiService
) {

    private val cache = ConcurrentHashMap<String, WeatherData>()
    private val cacheTtlMs = 10 * 60 * 1000L // 10 minutes

    suspend fun getWeatherData(
        location: LocationInfo,
        forceRefresh: Boolean = false
    ): Result<WeatherData> = withContext(Dispatchers.IO) {
        val cacheKey = "${String.format(Locale.US, "%.4f", location.latitude)},${String.format(Locale.US, "%.4f", location.longitude)}"
        val cached = cache[cacheKey]

        if (!forceRefresh && cached != null && (System.currentTimeMillis() - cached.fetchedAtEpochMs) < cacheTtlMs) {
            return@withContext Result.success(cached.copy(location = location))
        }

        try {
            val response = apiService.getForecast(
                latitude = location.latitude,
                longitude = location.longitude,
                current = "temperature_2m,relative_humidity_2m,apparent_temperature,is_day,precipitation,weather_code,wind_speed_10m",
                hourly = "temperature_2m,precipitation_probability,weather_code",
                daily = "weather_code,temperature_2m_max,temperature_2m_min,sunrise,sunset,precipitation_probability_max",
                timezone = "auto",
                forecastDays = 7
            )

            val parsedData = mapResponseToWeatherData(response, location)
            cache[cacheKey] = parsedData
            Result.success(parsedData)
        } catch (e: Exception) {
            // If network fails but we have cached data, return cached with error note
            if (cached != null) {
                Result.success(cached.copy(location = location))
            } else {
                Result.failure(e)
            }
        }
    }

    suspend fun searchLocations(query: String): Result<List<LocationInfo>> = withContext(Dispatchers.IO) {
        val trimmed = query.trim()
        if (trimmed.length < 2) {
            return@withContext Result.success(emptyList())
        }

        try {
            val response = apiService.searchLocation(name = trimmed, count = 15)
            val results = response.results.orEmpty().map { item ->
                LocationInfo(
                    name = item.name,
                    stateOrRegion = item.admin1 ?: item.admin2,
                    country = item.country ?: (if (item.countryCode == "IN") "India" else item.countryCode ?: ""),
                    latitude = item.latitude,
                    longitude = item.longitude,
                    timezone = item.timezone
                )
            }

            // Prioritize Indian results first, followed by others, maintaining relevance
            val sorted = results.sortedWith(
                compareByDescending<LocationInfo> { it.country.equals("India", ignoreCase = true) }
                    .thenBy { it.name.length }
            )

            Result.success(sorted)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun mapResponseToWeatherData(
        response: WeatherApiResponse,
        location: LocationInfo
    ): WeatherData {
        val timezoneId = response.timezone ?: location.timezone ?: "Asia/Kolkata"
        val tz = try {
            TimeZone.getTimeZone(timezoneId)
        } catch (e: Exception) {
            TimeZone.getDefault()
        }

        val currentDto = response.current
        val currentTemp = currentDto?.temperature ?: 0.0
        val apparentTemp = currentDto?.apparentTemperature ?: currentTemp
        val humidity = currentDto?.humidity?.toInt() ?: 0
        val windSpeed = currentDto?.windSpeed ?: 0.0
        val precipMm = currentDto?.precipitation ?: 0.0
        val weatherCode = currentDto?.weatherCode ?: 0
        val isDay = (currentDto?.isDay ?: 1) == 1

        val conditionInfo = WeatherConditionMapper.getCondition(weatherCode, isDay)

        val localTimeFormat = SimpleDateFormat("h:mm a, EEE d MMM", Locale.ENGLISH).apply {
            timeZone = tz
        }
        val lastUpdatedFormat = SimpleDateFormat("h:mm a", Locale.ENGLISH).apply {
            timeZone = tz
        }

        val currentTime = Date()
        val localTimeStr = localTimeFormat.format(currentTime)
        val lastUpdatedStr = "Updated at " + lastUpdatedFormat.format(currentTime)

        // Parse hourly items
        val hourlyItems = mutableListOf<HourlyForecastItem>()
        val hourlyDto = response.hourly
        if (hourlyDto != null && !hourlyDto.time.isNullOrEmpty()) {
            val times = hourlyDto.time
            val temps = hourlyDto.temperature.orEmpty()
            val precipProbs = hourlyDto.precipitationProbability.orEmpty()
            val codes = hourlyDto.weatherCode.orEmpty()

            val isoParser = SimpleDateFormat("yyyy-MM-dd'T'HH:mm", Locale.ENGLISH).apply {
                timeZone = tz
            }
            val hourLabelFormat = SimpleDateFormat("h a", Locale.ENGLISH).apply {
                timeZone = tz
            }

            val currentEpoch = System.currentTimeMillis()
            var startIndex = 0

            // Find index closest to now
            for (i in times.indices) {
                try {
                    val parsed = isoParser.parse(times[i])
                    if (parsed != null && parsed.time >= (currentEpoch - 3600000)) {
                        startIndex = i
                        break
                    }
                } catch (e: Exception) {
                    // Ignore parse error
                }
            }

            val count = minOf(24, times.size - startIndex)
            for (i in 0 until count) {
                val idx = startIndex + i
                val rawTime = times[idx]
                val temp = temps.getOrNull(idx) ?: 0.0
                val prob = precipProbs.getOrNull(idx) ?: 0
                val code = codes.getOrNull(idx) ?: 0

                val label = if (i == 0) {
                    "Now"
                } else {
                    try {
                        val parsed = isoParser.parse(rawTime)
                        if (parsed != null) hourLabelFormat.format(parsed) else rawTime
                    } catch (e: Exception) {
                        rawTime.substringAfter("T")
                    }
                }

                // Check day/night for hour (roughly 6am to 7pm)
                val hourInt = try {
                    val parsed = isoParser.parse(rawTime)
                    val cal = java.util.Calendar.getInstance(tz)
                    if (parsed != null) {
                        cal.time = parsed
                        cal.get(java.util.Calendar.HOUR_OF_DAY)
                    } else 12
                } catch (e: Exception) {
                    12
                }
                val hourlyIsDay = hourInt in 6..18

                hourlyItems.add(
                    HourlyForecastItem(
                        rawIsoTime = rawTime,
                        timeLabel = label,
                        temperatureC = temp,
                        precipitationProbability = prob,
                        weatherCode = code,
                        isDay = hourlyIsDay
                    )
                )
            }
        }

        // Parse daily items
        val dailyItems = mutableListOf<DailyForecastItem>()
        val dailyDto = response.daily
        if (dailyDto != null && !dailyDto.time.isNullOrEmpty()) {
            val dailyTimes = dailyDto.time
            val dailyCodes = dailyDto.weatherCode.orEmpty()
            val maxTemps = dailyDto.temperatureMax.orEmpty()
            val minTemps = dailyDto.temperatureMin.orEmpty()
            val sunrises = dailyDto.sunrise.orEmpty()
            val sunsets = dailyDto.sunset.orEmpty()
            val maxPrecipProbs = dailyDto.precipitationProbabilityMax.orEmpty()

            val dayParser = SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH).apply {
                timeZone = tz
            }
            val dayOfWeekFormat = SimpleDateFormat("EEE", Locale.ENGLISH).apply {
                timeZone = tz
            }
            val formattedDateFormat = SimpleDateFormat("MMM d", Locale.ENGLISH).apply {
                timeZone = tz
            }
            val sunParser = SimpleDateFormat("yyyy-MM-dd'T'HH:mm", Locale.ENGLISH).apply {
                timeZone = tz
            }
            val sunDisplayFormat = SimpleDateFormat("h:mm a", Locale.ENGLISH).apply {
                timeZone = tz
            }

            for (i in dailyTimes.indices) {
                val rawDate = dailyTimes[i]
                val code = dailyCodes.getOrNull(i) ?: 0
                val maxT = maxTemps.getOrNull(i) ?: currentTemp
                val minT = minTemps.getOrNull(i) ?: currentTemp
                val precipProbMax = maxPrecipProbs.getOrNull(i) ?: 0

                var dayName = rawDate
                var dateStr = rawDate
                try {
                    val parsed = dayParser.parse(rawDate)
                    if (parsed != null) {
                        dayName = when (i) {
                            0 -> "Today"
                            1 -> "Tomorrow"
                            else -> dayOfWeekFormat.format(parsed)
                        }
                        dateStr = formattedDateFormat.format(parsed)
                    } else {
                        dayName = rawDate
                        dateStr = rawDate
                    }
                } catch (e: Exception) {
                    dayName = rawDate
                    dateStr = rawDate
                }

                val sunriseRaw = sunrises.getOrNull(i)
                val sunsetRaw = sunsets.getOrNull(i)

                val sunriseStr = try {
                    if (sunriseRaw != null) {
                        val parsed = sunParser.parse(sunriseRaw)
                        if (parsed != null) sunDisplayFormat.format(parsed) else "--:--"
                    } else "--:--"
                } catch (e: Exception) {
                    "--:--"
                }

                val sunsetStr = try {
                    if (sunsetRaw != null) {
                        val parsed = sunParser.parse(sunsetRaw)
                        if (parsed != null) sunDisplayFormat.format(parsed) else "--:--"
                    } else "--:--"
                } catch (e: Exception) {
                    "--:--"
                }

                dailyItems.add(
                    DailyForecastItem(
                        rawDate = rawDate,
                        dayOfWeek = dayName,
                        formattedDate = dateStr,
                        maxTempC = maxT,
                        minTempC = minT,
                        precipitationProbabilityMax = precipProbMax,
                        weatherCode = code,
                        sunrise = sunriseStr,
                        sunset = sunsetStr
                    )
                )
            }
        }

        return WeatherData(
            location = location.copy(timezone = timezoneId),
            current = CurrentWeather(
                temperatureC = currentTemp,
                apparentTemperatureC = apparentTemp,
                humidity = humidity,
                windSpeedKmh = windSpeed,
                precipitationMm = precipMm,
                isDay = isDay,
                weatherCode = weatherCode,
                conditionTitle = conditionInfo.title,
                conditionDescription = conditionInfo.description,
                localTime = localTimeStr,
                lastUpdated = lastUpdatedStr
            ),
            hourly = hourlyItems,
            daily = dailyItems,
            timezone = timezoneId
        )
    }
}
