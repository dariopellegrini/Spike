package com.dariopellegrini.spike.mapping

import com.dariopellegrini.spike.response.SpikeErrorResponse
import com.dariopellegrini.spike.response.SpikeSuccessResponse
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.IOException

class SuccessWrapper<T>(val base: T)
class ErrorWrapper<T>(val base: T)

val <T>SpikeSuccessResponse<T>.suspend: SuccessWrapper<SpikeSuccessResponse<T>>
    get() = SuccessWrapper(this)
val <T>SpikeErrorResponse<T>.suspend: ErrorWrapper<SpikeErrorResponse<T>>
    get() = ErrorWrapper(this)

suspend inline fun <reified T> SuccessWrapper<SpikeSuccessResponse<T>>.mapping(): T? = withContext(Dispatchers.IO) {
    this@mapping.base.results?.let {
        Gson().fromJsonOrNull<T>(it)
    } ?: run {
        null
    }
}

suspend inline fun <reified T> SuccessWrapper<SpikeSuccessResponse<T>>.mappingThrowable(): T = withContext(Dispatchers.IO) {
    this@mappingThrowable.base.results?.let {
        Gson().fromJson<T>(it)
    } ?: run {
        throw IOException("SpikeSuccessResponse result is null after call is null")
    }
}

suspend inline fun <reified T> ErrorWrapper<SpikeErrorResponse<T>>.mapping(): T? = withContext(Dispatchers.IO) {
    this@mapping.base.results?.let {
        Gson().fromJsonOrNull<T>(it)
    } ?: run {
        null
    }
}

suspend inline fun <reified T> ErrorWrapper<SpikeErrorResponse<T>>.mappingThrowable(): T = withContext(Dispatchers.IO) {
    this@mappingThrowable.base.results?.let {
        Gson().fromJson<T>(it)
    } ?: run {
        throw IOException("SpikeErrorResponse result is null after call is null")
    }
}