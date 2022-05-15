package com.abhinavmarwaha.walletx.db.room

import androidx.room.TypeConverter
import com.google.gson.JsonElement
import com.google.gson.JsonParser

class Converters {
    @TypeConverter
    public fun toKeyValue(keyValuesJson: String): List<Pair<String, String>> {

        val res = mutableListOf<Pair<String, String>>()
        val jElement: JsonElement = JsonParser.parseString(keyValuesJson)
        var jArray = jElement.asJsonArray
        jArray.map {
            val jObj = it.asJsonObject
            val key = jObj.keySet().first() as String
            val value = jObj[key].asString
            res.add(Pair(key, value))
        }
        return res
    }

    @TypeConverter
    public fun fromKeyValue(keyValues: List<Pair<String,String>>): String {
        val len = keyValues.size
        var res = ""
        keyValues.forEachIndexed{ index,it ->
            var str:String = "{" + '"' + it.first + '"' + ":" + '"' + it.second + '"' + "}"
            if(len!=1 && index!=len-1) str += ","
            res += str
        }
        res = "[$res]"

        return res
    }
}
