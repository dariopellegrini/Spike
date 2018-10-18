package com.dariopellegrini.spike.network

import android.util.Log
import com.android.volley.*
import com.dariopellegrini.spike.multipart.SpikeMultipartEntity
import com.dariopellegrini.spike.multipart.SpikeMultipartRequest

@Suppress("NAME_SHADOWING")
/**
 * Created by dariopellegrini on 19/05/2017.
 */
class SpikeNetwork(val requestQueue: RequestQueue) {
    var retryPolicy: DefaultRetryPolicy? = null
    init {
        Log.i("SpikeNetwork", "Init Spike network")
    }

    fun networkRequest(url: String,
                    method: SpikeMethod,
                    headers: Map<String, String>?,
                    parameters: Map<String, Any>?,
                    multipartEntities: List<SpikeMultipartEntity>?,
                    completion: (response: SpikeNetworkResponse?, error: VolleyError?) -> Unit): Request<SpikeNetworkResponse> {
        val request = if (multipartEntities != null) {
            multipartRequest(url, method, headers, parameters, multipartEntities, completion)
        } else {
            jsonRequest(url, method, headers, parameters, multipartEntities, completion)
        }
        requestQueue.add(request)
        return request
    }

    fun jsonRequest(url: String,
                    method: SpikeMethod,
                    headers: Map<String, String>?,
                    parameters: Map<String, Any>?,
                    multipartEntities: List<SpikeMultipartEntity>?,
                    completion: (response: SpikeNetworkResponse?, error: VolleyError?) -> Unit): SpikeJsonRequest {
        var currentURL = url
        if (parameters != null && method == SpikeMethod.GET) {
            currentURL = currentURL + "?"
            for (entry in parameters.entries) {
                currentURL = currentURL + entry.key + "=" + entry.value.toString() + "&"
            }
            currentURL = currentURL.removeSuffix("&")
        }

        val request = SpikeJsonRequest(getVolleyMethod(method), currentURL, headers, parameters,
                Response.Listener<SpikeNetworkResponse> {
                    response -> completion(response, null)
                },
                Response.ErrorListener { error ->
                    completion(null, error)
                })
        if (retryPolicy != null) {
            request.retryPolicy = retryPolicy
        }
        return request
    }

    fun multipartRequest(url: String,
                    method: SpikeMethod,
                    headers: Map<String, String>?,
                    parameters: Map<String, Any>?,
                    multipartEntities: List<SpikeMultipartEntity>,
                    completion: (response: SpikeNetworkResponse?, error: VolleyError?) -> Unit): SpikeMultipartRequest {
        var currentURL = url
        if (parameters != null && method == SpikeMethod.GET) {
            currentURL = "$currentURL?"
            for (entry in parameters.entries) {
                currentURL = currentURL + entry.key + "=" + entry.value.toString() + "&"
            }
            currentURL = currentURL.removeSuffix("&")
        }

        val multipartRequest = object : SpikeMultipartRequest(getVolleyMethod(method), url, Response.Listener<SpikeNetworkResponse> { response ->
            completion(response, null)
            // parse success output
        }, Response.ErrorListener {
            error -> error.printStackTrace()
            completion(null, error)
        }) {

            override fun getByteData(): Map<String, DataPart> {
                val params = hashMapOf<String, DataPart>()
                multipartEntities.forEach {
                    params.put(it.label, DataPart(it.fileName, it.bytes, it.contentType))
                }

                return params
            }

            override fun getParams(): Map<String, String> {
                val params = hashMapOf<String, String>()
                parameters?.forEach {
                    val v = it.value
                    if (v is String) {
                        params.put(it.key, v)
                    }
                }
                return params
            }
        }


        if (retryPolicy != null) {
            multipartRequest.retryPolicy = retryPolicy
        }
        return multipartRequest
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