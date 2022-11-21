package com.dariopellegrini.spike.network

import com.android.volley.AuthFailureError
import com.android.volley.NetworkResponse
import com.android.volley.Response
import com.android.volley.toolbox.HttpHeaderParser
import com.android.volley.toolbox.JsonRequest
import com.dariopellegrini.spike.utilities.contentType
import com.dariopellegrini.spike.utilities.getJsonStringFromMap
import java.util.*


/**
 * Created by dariopellegrini on 25/07/17.
 */
class SpikeJsonRequest : JsonRequest<SpikeNetworkResponse> {
    private var headers: Map<String, String>? = null

    constructor(method: Int,
                url: String,
                headers: Map<String, String>?,
                parameters: Map<String, Any>?,
                responseListener: Response.Listener<SpikeNetworkResponse>,
                errorListener: Response.ErrorListener):
            super(method, url, (if (parameters == null) null else getJsonStringFromMap(parameters)), responseListener, errorListener) {
        this.headers = headers as MutableMap<String, String>?
    }

    @Throws(AuthFailureError::class)
    override fun getHeaders(): Map<String, String> {
        if (headers != null) {
            return headers!!
        } else {
            return HashMap()
        }
    }

    override fun parseNetworkResponse(response: NetworkResponse?): Response<SpikeNetworkResponse> {
        val data = response!!.data
        // val charset = HttpHeaderParser.parseCharset(response!!.headers, PROTOCOL_CHARSET)
        val resultString = String(data)
        if (resultString.isEmpty()) { // Accepting empty results
            val jsonResponse = SpikeNetworkResponse(response.statusCode, response.headers, null)
            return Response.success<SpikeNetworkResponse>(jsonResponse,
                    HttpHeaderParser.parseCacheHeaders(response))
        }
        val successResponse = SpikeNetworkResponse(response.statusCode, response.headers, resultString)
        return Response.success(successResponse,
                HttpHeaderParser.parseCacheHeaders(response))
    }


    override fun getBodyContentType(): String {
        return headers?.get(contentType) ?: headers?.get(contentType.toUpperCase(Locale.ROOT)) ?: headers?.get(contentType.toLowerCase(Locale.ROOT))
        ?: super.getBodyContentType()
    }
}
