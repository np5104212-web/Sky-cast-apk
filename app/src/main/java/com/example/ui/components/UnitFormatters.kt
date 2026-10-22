package com.example.ui.components

import com.example.data.model.TemperatureUnit
import com.example.data.model.WindSpeedUnit
import java.util.Locale
import kotlin.math.roundToInt

object UnitFormatters {

    fun formatTemperature(tempC: Double, unit: TemperatureUnit): String {
        return when (unit) {
            TemperatureUnit.CELSIUS -> "${tempC.roundToInt()}°"
            TemperatureUnit.FAHRENHEIT -> {
                val fahrenheit = (tempC * 9.0 / 5.0) + 32.0
                "${fahrenheit.roundToInt()}°"
            }
        }
    }

    fun formatTemperatureWithUnit(tempC: Double, unit: TemperatureUnit): String {
        return when (unit) {
            TemperatureUnit.CELSIUS -> "${tempC.roundToInt()}${unit.symbol}"
            TemperatureUnit.FAHRENHEIT -> {
                val fahrenheit = (tempC * 9.0 / 5.0) + 32.0
                "${fahrenheit.roundToInt()}${unit.symbol}"
            }
        }
    }

    fun formatWindSpeed(speedKmh: Double, unit: WindSpeedUnit): String {
        return when (unit) {
            WindSpeedUnit.KM_H -> String.format(Locale.US, "%.1f %s", speedKmh, unit.unitSymbol)
            WindSpeedUnit.M_S -> {
                val ms = speedKmh / 3.6
                String.format(Locale.US, "%.1f %s", ms, unit.unitSymbol)
            }
            WindSpeedUnit.MPH -> {
                val mph = speedKmh * 0.621371
                String.format(Locale.US, "%.1f %s", mph, unit.unitSymbol)
            }
        }
    }
}
