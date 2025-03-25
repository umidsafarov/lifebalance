package com.gmail.umidsafarov.lifebalance.data.local.helpers

import androidx.room.TypeConverter
import com.google.gson.Gson

class Converter {
    @TypeConverter
    fun listToJson(value: List<String>?): String = Gson().toJson(value)

    @TypeConverter
    fun jsonToList(value: String?): List<String>? =
        if (value == null || value == "null") {
            null
        } else {
            Gson().fromJson(value, Array<String>::class.java).toList()
        }
}
