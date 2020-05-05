package com.dariopellegrini.spike.mapping

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

@Throws(Exception::class)
// reified: pass to have that class type
inline fun <reified T> Gson.fromJson(jsonString: String): T {
    return this.fromJson<T>(jsonString, object: TypeToken<T>() {}.type)
}

@Throws(Exception::class)
fun Gson.toJSONMap(o: Any): Map<String, Any> {
    val jsonString = this.toJson(o)
    return Gson().fromJson<Map<String, Any>>(jsonString)
}

inline fun <reified T> Gson.fromJsonOrNull(jsonString: String): T? {
    return try {
        this.fromJson<T>(jsonString, object: TypeToken<T>() {}.type)
    } catch (e: Exception) {
        null
    }
}