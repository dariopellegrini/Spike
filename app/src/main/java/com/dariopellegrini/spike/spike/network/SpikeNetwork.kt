package com.s4win.whatwelove.spike.network

import android.util.Log
import com.android.volley.*
import com.dariopellegrini.spike.spike.network.SpikeMethod
import com.dariopellegrini.spike.spike.response.SpikeSuccessResponse
import com.dariopellegrini.spike.spike.upload.SpikeMultipartEntity
import com.s4win.whatwelove.spike.response.SpikeErrorResponse
import com.s4win.whatwelove.spike.response.SpikeResponse

@Suppress("NAME_SHADOWING")
/**
 * Created by dariopellegrini on 19/05/2017.
 */
class SpikeNetwork(val requestQueue: RequestQueue) {
    init {
        Log.i("SpikeNetwork", "Init Spike network")
    }

    fun jsonRequest(url: String,
                    method: SpikeMethod,
                    headers: Map<String, String>?,
                    parameters: Map<String, Any>?,
                    multipartEntities: List<SpikeMultipartEntity>?,
                    completion: (response: SpikeSuccessResponse?, error: SpikeErrorResponse?) -> Unit) {
        var currentURL = url
        if (parameters != null && method == SpikeMethod.GET) {
            currentURL = currentURL + "?"
            for (entry in parameters.entries) {
                currentURL = currentURL + entry.key + "=" + entry.value.toString() + "&"
            }
            currentURL = currentURL.removeSuffix("&")
        }

        val request = SpikeRequest(getVolleyMethod(method), currentURL, headers, parameters, multipartEntities,
                Response.Listener<SpikeSuccessResponse> {
                    response -> completion(response, null)
                },
                Response.ErrorListener { error ->
                    val networkResponse = error.networkResponse
                    if (networkResponse != null) {
                        val statusCode = error.networkResponse.statusCode
                        val headers = error.networkResponse.headers
                        val parameters = String(error.networkResponse.data)
                        val errorResponse = SpikeErrorResponse(statusCode, headers, parameters, error)
                        completion(null, errorResponse)
                    } else if (error is NoConnectionError) {
                        val statusCode = -1001
                        val headers = null
                        val parameters = null
                        val errorResponse = SpikeErrorResponse(statusCode, headers, parameters, error)
                        completion(null, errorResponse)
                    } else {
                        val statusCode = 0
                        val headers = null
                        val parameters = null
                        val errorResponse = SpikeErrorResponse(statusCode, headers, parameters, error)
                        completion(null, errorResponse)
                    }
                })
        requestQueue.add(request)
    }

    fun getVolleyMethod(method: SpikeMethod): Int {
        when(method) {
            SpikeMethod.GET -> return Request.Method.GET
            SpikeMethod.POST -> return Request.Method.POST
            SpikeMethod.PUT -> return Request.Method.PUT
            SpikeMethod.DELETE -> return Request.Method.DELETE
            SpikeMethod.HEAD -> return Request.Method.HEAD
            SpikeMethod.OPTIONS -> return Request.Method.OPTIONS
            SpikeMethod.TRACE -> return Request.Method.TRACE
            SpikeMethod.PATCH -> return Request.Method.PATCH
            SpikeMethod.DEPRECATED_GET_OR_POST -> return Request.Method.DEPRECATED_GET_OR_POST
        }
    }
}