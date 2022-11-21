package com.dariopellegrini.spike.utilities

import com.google.gson.Gson
import org.json.JSONException

@Throws(JSONException::class)
fun getJsonStringFromMap(map: Map<String, Any>): String {
    val gson = Gson()
    val json = gson.toJson(map)
    return json
}
val contentType = "Content-Type"
val applicationJson = "application/json"
val multipartFormData = "multipart/form-data"
val applicationXWWWFormUrlEncoded = "application/x-www-form-urlencoded"
