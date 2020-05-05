package com.dariopellegrini.spike.mapping

import com.dariopellegrini.spike.response.SpikeErrorResponse
import com.dariopellegrini.spike.response.SpikeSuccessResponse
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.IOException

suspend inline fun <reified T> SpikeSuccessResponse<T>.mapping(): T? = withContext(Dispatchers.IO) {
    this@mapping.results?.let {
        Gson().fromJsonOrNull<T>(it)
    } ?: run {
        null
    }
}

suspend inline fun <reified T> SpikeSuccessResponse<T>.mappingThrowable(): T = withContext(Dispatchers.IO) {
    this@mappingThrowable.results?.let {
        Gson().fromJson<T>(it)
    } ?: run {
        throw IOException("SpikeSuccessResponse result is null after call is null")
    }
}

suspend inline fun <reified T> SpikeErrorResponse<T>.mapping(): T? = withContext(Dispatchers.IO) {
    this@mapping.results?.let {
        Gson().fromJsonOrNull<T>(it)
    } ?: run {
        null
    }
}

suspend inline fun <reified T> SpikeErrorResponse<T>.mappingThrowable(): T = withContext(Dispatchers.IO) {
    this@mappingThrowable.results?.let {
        Gson().fromJson<T>(it)
    } ?: run {
        throw IOException("SpikeErrorResponse result is null after call is null")
    }
}