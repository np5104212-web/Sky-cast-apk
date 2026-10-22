package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.model.IndiaLocationsData
import com.example.data.model.LocationInfo
import com.example.data.model.TemperatureUnit
import com.example.data.model.WindSpeedUnit
import com.example.ui.components.UnitFormatters
import com.example.ui.model.WeatherCategory
import com.example.ui.model.WeatherConditionMapper
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class ExampleRobolectricTest {

    @Test
    fun `read string from context verifies SkyCast app name`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val appName = context.getString(R.string.app_name)
        assertEquals("SkyCast", appName)
    }

    @Test
    fun `weather condition mapper correctly classifies WMO codes`() {
        val clearDay = WeatherConditionMapper.getCondition(0, isDay = true)
        assertEquals(WeatherCategory.CLEAR_DAY, clearDay.category)
        assertEquals("Clear Sky", clearDay.title)

        val clearNight = WeatherConditionMapper.getCondition(0, isDay = false)
        assertEquals(WeatherCategory.CLEAR_NIGHT, clearNight.category)

        val rain = WeatherConditionMapper.getCondition(63, isDay = true)
        assertEquals(WeatherCategory.RAIN, rain.category)
        assertEquals("Moderate Rain", rain.title)

        val thunder = WeatherConditionMapper.getCondition(95, isDay = true)
        assertEquals(WeatherCategory.THUNDERSTORM, thunder.category)
        assertEquals("Thunderstorm", thunder.title)
    }

    @Test
    fun `unit formatters correctly converts temperature and wind speed`() {
        val celsiusStr = UnitFormatters.formatTemperature(25.0, TemperatureUnit.CELSIUS)
        assertEquals("25°", celsiusStr)

        val fahrenheitStr = UnitFormatters.formatTemperature(25.0, TemperatureUnit.FAHRENHEIT)
        assertEquals("77°", fahrenheitStr)

        val windKmh = UnitFormatters.formatWindSpeed(10.0, WindSpeedUnit.KM_H)
        assertTrue(windKmh.contains("km/h"))

        val windMph = UnitFormatters.formatWindSpeed(10.0, WindSpeedUnit.MPH)
        assertTrue(windMph.contains("mph"))
    }

    @Test
    fun `default location is Indore Madhya Pradesh`() {
        val indore = LocationInfo.INDORE_DEFAULT
        assertEquals("Indore", indore.name)
        assertEquals("Madhya Pradesh", indore.stateOrRegion)
        assertEquals("India", indore.country)
        assertEquals(22.7196, indore.latitude, 0.001)
        assertEquals(75.8577, indore.longitude, 0.001)
    }

    @Test
    fun `india locations data includes major states and cities`() {
        val locations = IndiaLocationsData.ALL_INDIA_LOCATIONS
        assertTrue(locations.isNotEmpty())

        val cityNames = locations.map { it.cityName }
        assertTrue(cityNames.contains("Indore"))
        assertTrue(cityNames.contains("Bhopal"))
        assertTrue(cityNames.contains("Gwalior"))
        assertTrue(cityNames.contains("New Delhi"))
        assertTrue(cityNames.contains("Mumbai"))
        assertTrue(cityNames.contains("Bengaluru"))
        assertTrue(cityNames.contains("Kolkata"))
        assertTrue(cityNames.contains("Chennai"))
        assertTrue(cityNames.contains("Jaipur"))
    }

    @Test
    fun `weather alert model properly supports severity levels`() {
        val alert = com.example.data.model.WeatherAlert(
            title = "Severe Thunderstorm Warning",
            description = "Active lightning detected.",
            severity = com.example.data.model.AlertSeverity.WARNING,
            instruction = "Stay indoors"
        )
        assertEquals(com.example.data.model.AlertSeverity.WARNING, alert.severity)
        assertEquals("Severe Thunderstorm Warning", alert.title)
        assertEquals("Stay indoors", alert.instruction)
    }
}
