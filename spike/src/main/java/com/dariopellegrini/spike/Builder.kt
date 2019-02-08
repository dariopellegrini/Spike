package com.dariopellegrini.spike

import com.dariopellegrini.spike.multipart.SpikeMultipartEntity
import com.dariopellegrini.spike.network.SpikeMethod
import com.dariopellegrini.spike.response.SpikeErrorResponse
import com.dariopellegrini.spike.response.SpikeSuccessResponse

/**
 * Created by Dario on 10/12/2018.
 * Dario Pellegrini Brescia
 */

class TargetBuilder() {
    var baseURL: String = ""
    var path: String = ""
    var method: SpikeMethod = SpikeMethod.GET
    var headers: Map<String, String>? = null
    var parameters: Map<String, Any>? = null
    var multipartEntities: List<SpikeMultipartEntity>? = null
    var successClosure: ((String, Map<String, String>?) -> Any?)? = null
    var errorClosure: ((String, Map<String, String>?) -> Any?)? = null
    var sampleHeaders: Map<String, String>? = null
    var sampleResult: String? = null
    
    val target: TargetType
        get() = object : TargetType {
            override val baseURL = this@TargetBuilder.baseURL
            override val path = this@TargetBuilder.path
            override val method: SpikeMethod = this@TargetBuilder.method
            override val headers: Map<String, String>? = this@TargetBuilder.headers
            override val parameters: Map<String, Any>? = this@TargetBuilder.parameters
            override val multipartEntities: List<SpikeMultipartEntity>? = this@TargetBuilder.multipartEntities
            override val successClosure: ((String, Map<String, String>?) -> Any?)? = this@TargetBuilder.successClosure
            override val errorClosure: ((String, Map<String, String>?) -> Any?)? = this@TargetBuilder.errorClosure
            override val sampleHeaders: Map<String, String>? = this@TargetBuilder.sampleHeaders
            override val sampleResult: String? = this@TargetBuilder.sampleResult
        }
}

// Builder functions
fun buildTarget(closure: TargetBuilder.() -> Unit): TargetType {
    val targetBuilder = TargetBuilder()
    targetBuilder.closure()
    return targetBuilder.target
}

// SpikeProvider extensions
suspend fun <T>SpikeProvider<TargetType>.buildRequest(closure: TargetBuilder.() -> Unit): SpikeSuccessResponse<T> {
    val targetBuilder = TargetBuilder()
    targetBuilder.closure()
    return this.suspendingRequest<T>(targetBuilder.target)
}

suspend fun SpikeProvider<TargetType>.buildAnyRequest(closure: TargetBuilder.() -> Unit): SpikeSuccessResponse<Any> {
    val targetBuilder = TargetBuilder()
    targetBuilder.closure()
    return this.suspendingRequest(targetBuilder.target)
}

// Suspendable functions with global SpikeProvider
suspend fun requestAny(closure: TargetBuilder.() -> Unit): SpikeSuccessResponse<Any> {
    val provider = SpikeProvider<TargetType>()
    return provider.buildAnyRequest(closure)
}

suspend fun <T>request(closure: TargetBuilder.() -> Unit): SpikeSuccessResponse<T> {
    val provider = SpikeProvider<TargetType>()
    return provider.buildRequest(closure)
}

// Functions with callback with global SpikeProvider
fun requestAny(closure: TargetBuilder.() -> Unit, onSuccess: (SpikeSuccessResponse<Any>) -> Unit, onError: (SpikeErrorResponse<Any>) -> Unit) {
    val targetBuilder = TargetBuilder()
    targetBuilder.closure()
    val provider = SpikeProvider<TargetType>()
    provider.request(targetBuilder.target, onSuccess, onError)
}

fun <S, E>request(closure: TargetBuilder.() -> Unit, onSuccess: (SpikeSuccessResponse<S>) -> Unit, onError: (SpikeErrorResponse<E>) -> Unit) {
    val targetBuilder = TargetBuilder()
    targetBuilder.closure()
    val provider = SpikeProvider<TargetType>()
    provider.requestTypesafe<S, E>(targetBuilder.target, onSuccess, onError)
}
