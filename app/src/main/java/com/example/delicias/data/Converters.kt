package com.example.delicias.data

import androidx.room.TypeConverter
import com.example.delicias.domain.Menu
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.lang.reflect.Type

class Converters {
    @TypeConverter
    fun fromString(value: String?): List<Menu>? {
        val listType: Type = object : TypeToken<List<Menu>?>() {}.getType()
        return Gson().fromJson(value, listType)
    }

    @TypeConverter
    fun fromArrayList(list: List<Menu>?): String? {
        val gson = Gson()
        return gson.toJson(list)
    }
}