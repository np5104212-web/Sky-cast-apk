package com.example.data.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class GeocodingResponse(
    @Json(name = "results") val results: List<GeocodingItemDto>? = null,
    @Json(name = "generationtime_ms") val generationTimeMs: Double? = null
)

@JsonClass(generateAdapter = true)
data class GeocodingItemDto(
    @Json(name = "id") val id: Long? = null,
    @Json(name = "name") val name: String,
    @Json(name = "latitude") val latitude: Double,
    @Json(name = "longitude") val longitude: Double,
    @Json(name = "elevation") val elevation: Double? = null,
    @Json(name = "feature_code") val featureCode: String? = null,
    @Json(name = "country_code") val countryCode: String? = null,
    @Json(name = "country") val country: String? = null,
    @Json(name = "admin1") val admin1: String? = null,
    @Json(name = "admin2") val admin2: String? = null,
    @Json(name = "timezone") val timezone: String? = null
)
