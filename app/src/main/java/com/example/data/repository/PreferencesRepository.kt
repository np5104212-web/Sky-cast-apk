package com.example.data.repository

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.data.model.AppThemeMode
import com.example.data.model.LocationInfo
import com.example.data.model.TemperatureUnit
import com.example.data.model.WindSpeedUnit
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "skycast_settings")

data class UserSettings(
    val temperatureUnit: TemperatureUnit = TemperatureUnit.CELSIUS,
    val windSpeedUnit: WindSpeedUnit = WindSpeedUnit.KM_H,
    val themeMode: AppThemeMode = AppThemeMode.SYSTEM,
    val lastLocation: LocationInfo = LocationInfo.INDORE_DEFAULT,
    val recentLocations: List<LocationInfo> = emptyList(),
    val favoriteLocations: List<LocationInfo> = listOf(LocationInfo.INDORE_DEFAULT)
)

class PreferencesRepository(private val context: Context) {

    private val moshi = Moshi.Builder().addLast(KotlinJsonAdapterFactory()).build()
    private val locationListAdapter = moshi.adapter<List<LocationInfo>>(
        Types.newParameterizedType(List::class.java, LocationInfo::class.java)
    )
    private val locationAdapter = moshi.adapter(LocationInfo::class.java)

    companion object {
        private val KEY_TEMP_UNIT = stringPreferencesKey("temp_unit")
        private val KEY_WIND_UNIT = stringPreferencesKey("wind_unit")
        private val KEY_THEME_MODE = stringPreferencesKey("theme_mode")
        private val KEY_LAST_LOCATION = stringPreferencesKey("last_location")
        private val KEY_RECENT_LOCATIONS = stringPreferencesKey("recent_locations")
        private val KEY_FAVORITES = stringPreferencesKey("favorite_locations")
    }

    val userSettingsFlow: Flow<UserSettings> = context.dataStore.data.map { prefs ->
        val tempUnit = prefs[KEY_TEMP_UNIT]?.let {
            try { TemperatureUnit.valueOf(it) } catch (e: Exception) { TemperatureUnit.CELSIUS }
        } ?: TemperatureUnit.CELSIUS

        val windUnit = prefs[KEY_WIND_UNIT]?.let {
            try { WindSpeedUnit.valueOf(it) } catch (e: Exception) { WindSpeedUnit.KM_H }
        } ?: WindSpeedUnit.KM_H

        val themeMode = prefs[KEY_THEME_MODE]?.let {
            try { AppThemeMode.valueOf(it) } catch (e: Exception) { AppThemeMode.SYSTEM }
        } ?: AppThemeMode.SYSTEM

        val lastLoc = prefs[KEY_LAST_LOCATION]?.let { json ->
            try { locationAdapter.fromJson(json) } catch (e: Exception) { null }
        } ?: LocationInfo.INDORE_DEFAULT

        val recentList = prefs[KEY_RECENT_LOCATIONS]?.let { json ->
            try { locationListAdapter.fromJson(json) } catch (e: Exception) { null }
        } ?: emptyList()

        val favList = prefs[KEY_FAVORITES]?.let { json ->
            try { locationListAdapter.fromJson(json) } catch (e: Exception) { null }
        } ?: listOf(LocationInfo.INDORE_DEFAULT)

        UserSettings(
            temperatureUnit = tempUnit,
            windSpeedUnit = windUnit,
            themeMode = themeMode,
            lastLocation = lastLoc,
            recentLocations = recentList,
            favoriteLocations = favList
        )
    }

    suspend fun setTemperatureUnit(unit: TemperatureUnit) {
        context.dataStore.edit { it[KEY_TEMP_UNIT] = unit.name }
    }

    suspend fun setWindSpeedUnit(unit: WindSpeedUnit) {
        context.dataStore.edit { it[KEY_WIND_UNIT] = unit.name }
    }

    suspend fun setThemeMode(mode: AppThemeMode) {
        context.dataStore.edit { it[KEY_THEME_MODE] = mode.name }
    }

    suspend fun setLastLocation(location: LocationInfo) {
        context.dataStore.edit { prefs ->
            prefs[KEY_LAST_LOCATION] = locationAdapter.toJson(location)

            // Also update recents
            val currentRecents = prefs[KEY_RECENT_LOCATIONS]?.let { json ->
                try { locationListAdapter.fromJson(json) } catch (e: Exception) { null }
            }?.toMutableList() ?: mutableListOf()

            // Remove if already present
            currentRecents.removeAll { it.name.equals(location.name, ignoreCase = true) && it.country == location.country }
            currentRecents.add(0, location)

            // Keep max 10 recents
            val trimmed = currentRecents.take(10)
            prefs[KEY_RECENT_LOCATIONS] = locationListAdapter.toJson(trimmed)
        }
    }

    suspend fun toggleFavorite(location: LocationInfo): Boolean {
        var isNowFavorite = false
        context.dataStore.edit { prefs ->
            val favs = prefs[KEY_FAVORITES]?.let { json ->
                try { locationListAdapter.fromJson(json) } catch (e: Exception) { null }
            }?.toMutableList() ?: mutableListOf()

            val existingIndex = favs.indexOfFirst {
                it.name.equals(location.name, ignoreCase = true) && it.country == location.country
            }

            if (existingIndex >= 0) {
                favs.removeAt(existingIndex)
                isNowFavorite = false
            } else {
                favs.add(location.copy(isFavorite = true))
                isNowFavorite = true
            }
            prefs[KEY_FAVORITES] = locationListAdapter.toJson(favs)
        }
        return isNowFavorite
    }

    suspend fun clearRecentLocations() {
        context.dataStore.edit { prefs ->
            prefs.remove(KEY_RECENT_LOCATIONS)
        }
    }
}
