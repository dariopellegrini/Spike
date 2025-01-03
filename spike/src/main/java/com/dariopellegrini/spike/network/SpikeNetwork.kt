package com.dariopellegrini.spike.network

import android.util.Log
import com.android.volley.*
import com.dariopellegrini.spike.multipart.SpikeMultipartEntity
import com.dariopellegrini.spike.multipart.SpikeMultipartRequest
import com.dariopellegrini.spike.utilities.applicationXWWWFormUrlEncoded
import com.dariopellegrini.spike.utilities.contentType

@Suppress("NAME_SHADOWING")
/**
 * Created by dariopellegrini on 19/05/2017.
 */
class SpikeNetwork(val requestQueue: RequestQueue, var retryPolicy: DefaultRetryPolicy? = null) {

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
        if (parameters != null && (method == SpikeMethod.GET || contentType(headers)?.equals(applicationXWWWFormUrlEncoded, true) == true)) {
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

            override fun getHeaders(): Map<String, String> {
                // Content-type is selected automatically. Removing every content type header
                val currentHeaders = (if (headers != null) headers else super.getHeaders()).toMutableMap()
                if (currentHeaders.containsKey("Content-type")) {
                    currentHeaders.remove("Content-type")
                }
                if (currentHeaders.containsKey("content-type")) {
                    currentHeaders.remove("content-type")
                }
                if (currentHeaders.containsKey("Content-Type")) {
                    currentHeaders.remove("Content-Type")
                }
                if (currentHeaders.containsKey("CONTENT-TYPE")) {
                    currentHeaders.remove("CONTENT-TYPE")
                }
                return currentHeaders
//                return mapOf("token" to "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJfaWQiOiI1YTk5MmRlMDQ0NDgxOTdkOGFhMTY3NmEiLCJ1c2VybmFtZSI6ImluZm9AZGFyaW9wZWxsZWdyaW5pLmNvbSIsInBhc3N3b3JkIjoiMTExRkNBMkQ1MkRFRjRDMzNGNEQ4RjFCRTdFNzREMTRCNjVEMzY1RTVEREI5MTYxMEMzQzBEQkVDQzE5MjA3M0IwQjBERjI4MjEzRTM4MjhDQzAzMjFGNjI4NkJBRjk0NDQ5QTRGODgwMzIwM0JFMzI5MzU5NUY0RDY3RkY3RTIiLCJpYXQiOjE1NDA4MDIzMDF9.7Z8Yrpq2J-NXYMl6-777LjQmn833cZAek8q9Ak0P-Hk")
            }

//            override fun getHeaders(): Map<String, String> {
//                return headers ?: mapOf()
//            }
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