package com.dariopellegrini.spike.network

import android.util.Log
import com.android.volley.*
import com.dariopellegrini.spike.response.SpikeSuccessResponse
import com.dariopellegrini.spike.multipart.SpikeMultipartEntity
import com.dariopellegrini.spike.response.SpikeErrorResponse

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
                    completion: (response: SpikeNetworkResponse?, error: VolleyError?) -> Unit) {
        var currentURL = url
        if (parameters != null && method == SpikeMethod.GET) {
            currentURL = currentURL + "?"
            for (entry in parameters.entries) {
                currentURL = currentURL + entry.key + "=" + entry.value.toString() + "&"
            }
            currentURL = currentURL.removeSuffix("&")
        }

        val request = SpikeRequest(getVolleyMethod(method), currentURL, headers, parameters, multipartEntities,
                Response.Listener<SpikeNetworkResponse> {
                    response -> completion(response, null)
                },
                Response.ErrorListener { error ->
                    completion(null, error)
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