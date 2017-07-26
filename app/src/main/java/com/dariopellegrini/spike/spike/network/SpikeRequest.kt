package com.s4win.whatwelove.spike.network

import com.android.volley.AuthFailureError
import com.android.volley.NetworkResponse
import com.android.volley.ParseError
import com.android.volley.Response
import com.android.volley.toolbox.HttpHeaderParser
import com.android.volley.toolbox.JsonRequest
import com.s4win.whatwelove.spike.response.SpikeSuccess
import com.s4win.whatwelove.spike.response.SpikeResponse
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import org.json.JSONTokener
import java.io.UnsupportedEncodingException
import java.util.HashMap

/**
 * Created by dariopellegrini on 25/07/17.
 */
class SpikeRequest : JsonRequest<SpikeResponse> {
    private var headers: Map<String, String>? = null

    constructor(method: Int, url: String, headers: Map<String, String>?, parameters: Map<String, Any>?, responseListener: Response.Listener<SpikeResponse>, errorListener: Response.ErrorListener):
            super(method, url, (if (parameters == null) null else JSONObject(parameters).toString()), responseListener, errorListener) {
        this.headers = headers as MutableMap<String, String>?
    }

    @Throws(AuthFailureError::class)
    override fun getHeaders(): Map<String, String> {
        if (headers != null) {
            return headers!!
        } else {
            return HashMap<String, String>()
        }
    }

    override fun parseNetworkResponse(response: NetworkResponse?): Response<SpikeResponse> {
        val data = response!!.data
        val charset = HttpHeaderParser.parseCharset(response!!.headers, PROTOCOL_CHARSET)
        val resultString = String(data)
        if (resultString.length == 0) { // Accepting empty results
            val jsonResponse = SpikeResponse(response.statusCode, SpikeSuccess.accepted, response.headers, null)
            return Response.success<SpikeResponse>(jsonResponse,
                    HttpHeaderParser.parseCacheHeaders(response))
        }
        val jsonResponse = SpikeResponse(response.statusCode, SpikeSuccess.accepted, response.headers, resultString)
        return Response.success(jsonResponse,
                HttpHeaderParser.parseCacheHeaders(response))
    }
}