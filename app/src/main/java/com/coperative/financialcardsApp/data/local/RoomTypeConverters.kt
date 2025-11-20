package com.coperative.financialcardsApp.data.local

import androidx.room.TypeConverter
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types

object RoomTypeConverters {
    private val moshi = Moshi.Builder().build()
    private val mapAdapter = moshi.adapter<Map<String, Double>>(
        Types.newParameterizedType(Map::class.java, String::class.java, Double::class.javaObjectType)
    )

    @TypeConverter
    @JvmStatic
    fun mapToJson(map: Map<String, Double>?): String? = map?.let { mapAdapter.toJson(it) }

    @TypeConverter
    @JvmStatic
    fun jsonToMap(json: String?): Map<String, Double>? = json?.let { mapAdapter.fromJson(it) }
}
