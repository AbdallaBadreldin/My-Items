package com.fstech.myItems.util

import com.google.gson.Gson
import com.google.gson.JsonObject

class ExtractJsonFromString(private val jsonString: String) {

    fun getJsonObject(): JsonObject? {
        var string = jsonString.trimIndent().trim()

        if (string.isEmpty()) {
            return null
        }
        string = string.removePrefix("```json").removeSuffix("```")
        val gson = Gson()
        val jsonObject = gson.fromJson(string, JsonObject::class.java)
        return jsonObject
    }

}