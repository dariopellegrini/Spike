package com.dariopellegrini.spike

import android.content.Context
import android.util.Log
import com.android.volley.DefaultRetryPolicy
import com.android.volley.NoConnectionError
import com.android.volley.RequestQueue
import com.android.volley.VolleyError
import com.android.volley.toolbox.Volley
import com.dariopellegrini.spike.multipart.SpikeMultipartRequest
import com.dariopellegrini.spike.network.*
import com.dariopellegrini.spike.response.Spike
import com.dariopellegrini.spike.response.SpikeErrorResponse
import com.dariopellegrini.spike.response.SpikeSuccessResponse
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

/**
 * Created by dariopellegrini on 25/07/17.
 */
class SpikeProvider<in T : TargetType> {
    val queue: RequestQueue?
    val network: SpikeNetwork?

    var timeout = 10000 //DefaultRetryPolicy.DEFAULT_TIMEOUT_MS
    var maxRetries = DefaultRetryPolicy.DEFAULT_MAX_RETRIES
    var backoffMultiplier = DefaultRetryPolicy.DEFAULT_BACKOFF_MULT

    constructor(context: Context) {
        this.queue = Volley.newRequestQueue(context)
        this.network = SpikeNetwork(queue)
    }

    constructor(queue: RequestQueue) {
        this.queue = queue
        this.network = SpikeNetwork(queue)
    }

    constructor() {
        this.queue = null
        this.network = Spike.instance.network
    }

    fun request(target: T, onSuccess: (SpikeSuccessResponse<Any>)-> Unit, onError: (SpikeErrorResponse<Any>) -> Unit): RequestToken? {
        if (target.sampleResult != null) {
            val response = SpikeNetworkResponse(200, target.sampleHeaders, target.sampleResult)
            onSuccess(createSuccessResponse<Any>(response, target))
            return null
        }

        network?.let { network ->
            network.retryPolicy = retryPolicy
            val request = network.networkRequest(target.baseURL + target.path, target.method, target.headers, target.parameters, target.multipartEntities) {
                response, error ->

                // Creating success response
                if (response != null) {
                    onSuccess(createSuccessResponse<Any>(response, target))
                } else if (error != null) {
                    onError(createErrorResponse<Any>(error, target))
                }
            }
            return when(request) {
                is SpikeJsonRequest -> RequestJsonToken(request)
                is SpikeMultipartRequest -> RequestMultipartToken(request)
                else -> null
            }
        }

        // If here network has not been initialized
        Log.e("Spike", "Spike non initiated. Run: Spike.instance.configure(context)")
        return null
    }

    fun <S, E>requestTypesafe(target: T, onSuccess: (SpikeSuccessResponse<S>) -> Unit, onError: (SpikeErrorResponse<E>) -> Unit): RequestToken? {
        if (target.sampleResult != null) {
            val response = SpikeNetworkResponse(200, target.sampleHeaders, target.sampleResult)
            onSuccess(createSuccessResponse<S>(response, target))
            return null
        }

        network?.let { network ->
            network.retryPolicy = retryPolicy
            val request = network.networkRequest(target.baseURL + target.path, target.method, target.headers, target.parameters, target.multipartEntities) { response, error ->
                if (response != null) {
                    onSuccess(createSuccessResponse<S>(response, target))
                } else if (error != null) {
                    onError(createErrorResponse<E>(error, target))
                }

            }
            return when(request) {
                is SpikeJsonRequest -> RequestJsonToken(request)
                is SpikeMultipartRequest -> RequestMultipartToken(request)
                else -> null
            }
        }

        // If here network has not been initialized
        Log.e("Spike", "Spike non initiated. Run: Spike.instance.configure(context)")
        return null
    }

    suspend fun <S>suspendingRequest(target: T): SpikeSuccessResponse<S> {
        return suspendCancellableCoroutine { continuation ->
            if (target.sampleResult != null) {
                val response = SpikeNetworkResponse(200, target.sampleHeaders, target.sampleResult)
                continuation.resume(createSuccessResponse(response, target))
            }

            network?.let { network ->
                network.retryPolicy = retryPolicy
                network.networkRequest(target.baseURL + target.path, target.method, target.headers, target.parameters, target.multipartEntities) {
                    response, error ->
                    if (response != null) {
                        continuation.resume(createSuccessResponse(response, target))
                    } else if (error != null) {
                        continuation.resumeWithException(SpikeProviderException(this, target, error))
                    }
                }
            } ?: run {
                // If here network has not been initialized
                Log.e("Spike", "Spike non initiated. Run: Spike.instance.configure(context)")
            }
        }
    }

    private fun <S>createSuccessResponse(response: SpikeNetworkResponse, target: TargetType) : SpikeSuccessResponse<S> {
        // Creating success response
        val successResponse = SpikeSuccessResponse<S>(response.statusCode, response.headers, response.results)
        target.successClosure?.let {
            closure ->
            successResponse.results?.let {
                results ->
                successResponse.computedResult = closure(results, successResponse.headers) as? S
            }
        }
        return successResponse
    }

    private fun <E>createErrorResponse(error: VolleyError, target: TargetType) : SpikeErrorResponse<E> {
        // Creating error response
        val networkResponse = error.networkResponse
        if (networkResponse != null) {
            val statusCode = error.networkResponse.statusCode
            val headers = error.networkResponse.headers
            val results = String(error.networkResponse.data)
            val errorResponse = SpikeErrorResponse<E>(statusCode, headers, results, error)

            target.errorClosure?.let {
                closure ->
                results.let {
                    results ->
                    errorResponse.computedResult = closure(results, errorResponse.headers) as E
                }
            }

            return errorResponse
        } else if (error is NoConnectionError) {
            val statusCode = -1001
            val headers = null
            val parameters = null
            return SpikeErrorResponse(statusCode, headers, parameters, error)
        } else {
            val statusCode = 0
            val headers = null
            val parameters = null
            return SpikeErrorResponse(statusCode, headers, parameters, error)
        }
    }

    private val retryPolicy: DefaultRetryPolicy
        get() = DefaultRetryPolicy(timeout,
                maxRetries,
                backoffMultiplier)

    class SpikeProviderException(val provider: SpikeProvider<*>,
                                 val target: TargetType,
                                 private val volleyError: VolleyError): Exception() {
        fun <E>errorResponse(): SpikeErrorResponse<E> {
            return provider.createErrorResponse(volleyError, target)
        }

        val statusCode: Int
            get() {
                val s = if (volleyError.networkResponse == null) -1001 else volleyError.networkResponse.statusCode
                return s
            }

        val requestUrl get() = target.baseURL + target.path

        override fun toString(): String {
            if (Spike.instance.verboseExceptions == true) {
                return "${this::class.java} $requestUrl $statusCode ${errorResponse<Any>().results}"
            }
            return super.toString()
        }
    }
}