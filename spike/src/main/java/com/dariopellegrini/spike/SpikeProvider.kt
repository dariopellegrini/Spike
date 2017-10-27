package com.dariopellegrini.spike

import android.content.Context
import android.util.Log
import com.android.volley.DefaultRetryPolicy
import com.android.volley.NoConnectionError
import com.android.volley.RequestQueue
import com.android.volley.VolleyError
import com.android.volley.toolbox.Volley
import com.dariopellegrini.spike.network.SpikeNetwork
import com.dariopellegrini.spike.network.SpikeNetworkResponse
import com.dariopellegrini.spike.network.SpikeRequest
import com.dariopellegrini.spike.response.Spike
import com.dariopellegrini.spike.response.SpikeErrorResponse
import com.dariopellegrini.spike.response.SpikeSuccessResponse

/**
 * Created by dariopellegrini on 25/07/17.
 */
class SpikeProvider<in T : TargetType> {
    val queue: RequestQueue?
    val network: SpikeNetwork?

    var timeout = DefaultRetryPolicy.DEFAULT_TIMEOUT_MS
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

    fun request(target: T, onSuccess: (SpikeSuccessResponse<Any>)-> Unit, onError: (SpikeErrorResponse<Any>) -> Unit): SpikeRequest? {
        if (target.sampleResult != null) {
            val response = SpikeNetworkResponse(200, target.sampleHeaders, target.sampleResult)
            onSuccess(createSuccessResponse<Any>(response, target))
            return null
        }

        network?.let { network ->
            network.retryPolicy = retryPolicy
            return network.jsonRequest(target.baseURL + target.path, target.method, target.headers, target.parameters, target.multipartEntities, {
                response, error ->

                // Creating success response
                if (response != null) {
                    onSuccess(createSuccessResponse<Any>(response, target))
                } else if (error != null) {
                    onError(createErrorResponse<Any>(error, target))
                }
            })
        }

        // If here network has not been initialized
        Log.e("Spike", "Spike non initiated. Run: Spike.instance.configure(context)")
        return null
    }

    fun <S, E>requestTypesafe(target: T, onSuccess: (SpikeSuccessResponse<S>) -> Unit, onError: (SpikeErrorResponse<E>) -> Unit): SpikeRequest? {
        if (target.sampleResult != null) {
            val response = SpikeNetworkResponse(200, target.sampleHeaders, target.sampleResult)
            onSuccess(createSuccessResponse<S>(response, target))
            return null
        }

        network?.let { network ->
            network.retryPolicy = retryPolicy
            return network.jsonRequest(target.baseURL + target.path, target.method, target.headers, target.parameters, target.multipartEntities, {
                response, error ->
                if (response != null) {
                    onSuccess(createSuccessResponse<S>(response, target))
                } else if (error != null) {
                    onError(createErrorResponse<E>(error, target))
                }

            })
        }

        // If here network has not been initialized
        Log.e("Spike", "Spike non initiated. Run: Spike.instance.configure(context)")
        return null
    }

    private fun <S>createSuccessResponse(response: SpikeNetworkResponse, target: TargetType) : SpikeSuccessResponse<S> {
        // Creating success response
        val successResponse = SpikeSuccessResponse<S>(response.statusCode, response.headers, response.results)
        target.successClosure?.let {
            closure ->
            successResponse.results?.let {
                results ->
                successResponse.computedResult = closure(successResponse.results, successResponse.headers) as? S
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
                    errorResponse.computedResult = closure(results, errorResponse.headers) as? E
                }
            }

            return errorResponse
        } else if (error is NoConnectionError) {
            val statusCode = -1001
            val headers = null
            val parameters = null
            val errorResponse = SpikeErrorResponse<E>(statusCode, headers, parameters, error)
            return errorResponse
        } else {
            val statusCode = 0
            val headers = null
            val parameters = null
            val errorResponse = SpikeErrorResponse<E>(statusCode, headers, parameters, error)
            return errorResponse
        }
    }

    val retryPolicy: DefaultRetryPolicy
    get() = DefaultRetryPolicy(timeout,
            maxRetries,
            backoffMultiplier)
}