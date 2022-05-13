package com.abhinavmarwaha.walletx.db.room

import androidx.room.TypeConverter
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import java.lang.reflect.Type

class Converters {
    @TypeConverter
    public fun toKeyValue(keyValuesJson: String):  List<Pair<String,String>> {
        val gson = GsonBuilder()
        val collectionType: Type = object : TypeToken<Pair<String, String>>() {}.type

        return gson.create().fromJson(keyValuesJson, collectionType)
    }

    @TypeConverter
    public fun fromKeyValue(keyValues: List<Pair<String,String>>): String {
        val gson = GsonBuilder()
        val collectionType: Type = object : TypeToken<Pair<String, String>>() {}.type

        if(keyValues.isEmpty()) return "{}"

        return gson.create().toJson(keyValues, collectionType)
    }
}
