package com.s4win.whatwelove.spike.network

import android.util.Log
import com.android.volley.*
import com.s4win.whatwelove.spike.response.SpikeError
import com.s4win.whatwelove.spike.response.SpikeErrorResponse
import com.s4win.whatwelove.spike.response.SpikeResponse

/**
 * Created by dariopellegrini on 19/05/2017.
 */
class SpikeNetwork(val requestQueue: RequestQueue) {
    init {
        Log.i("SpikeNetwork", "Init Spike network")
    }

    fun jsonRequest(url: String, method: Int, headers: Map<String, String>?, parameters: Map<String, Any>?, completion: (response: SpikeResponse?, error: SpikeErrorResponse?) -> Unit) {
        var currentURL = url
        if (parameters != null && method == Request.Method.GET) {
            currentURL = currentURL + "?"
            for (entry in parameters.entries) {
                currentURL = currentURL + entry.key + "=" + entry.value.toString() + "&"
            }
            currentURL = currentURL.removeSuffix("&")
        }

        val request = SpikeRequest(method, currentURL, headers, parameters,
                Response.Listener<SpikeResponse> {
                    response -> completion(response, null)
                },
                Response.ErrorListener { error ->
                    val networkResponse = error.networkResponse
                    if (networkResponse != null) {
                        val statusCode = error.networkResponse.statusCode
                        val headers = error.networkResponse.headers
                        val parameters = String(error.networkResponse.data)
                        val errorResponse = SpikeErrorResponse(statusCode, SpikeError.notFound, headers, parameters)
                        completion(null, errorResponse)
                    } else if (error is NoConnectionError) {
                        val statusCode = -1001
                        val headers = null
                        val parameters = null
                        val errorResponse = SpikeErrorResponse(statusCode, SpikeError.notFound, headers, parameters)
                        completion(null, errorResponse)
                    } else {
                        val statusCode = 0
                        val headers = null
                        val parameters = null
                        val errorResponse = SpikeErrorResponse(statusCode, SpikeError.notFound, headers, parameters)
                        completion(null, errorResponse)
                    }
                })
        requestQueue.add(request)
    }
}