package com.s4win.whatwelove.spike.network

import com.android.volley.AuthFailureError
import com.android.volley.NetworkResponse
import com.android.volley.Response
import com.android.volley.toolbox.HttpHeaderParser
import com.android.volley.toolbox.JsonRequest
import com.dariopellegrini.spike.spike.upload.SpikeMultipartEntity
import com.s4win.whatwelove.spike.response.SpikeSuccess
import com.s4win.whatwelove.spike.response.SpikeResponse
import org.apache.http.HttpEntity
import org.apache.http.entity.ContentType
import org.apache.http.entity.mime.HttpMultipartMode
import org.json.JSONObject
import java.util.HashMap
import com.android.volley.VolleyLog
import org.apache.http.entity.mime.MultipartEntityBuilder
import java.io.ByteArrayOutputStream
import java.io.IOException


/**
 * Created by dariopellegrini on 25/07/17.
 */
class SpikeRequest : JsonRequest<SpikeResponse> {
    private var headers: Map<String, String>? = null
    private var multipartEntities: List<SpikeMultipartEntity>? = null
    var httpEntity: HttpEntity? = null

    constructor(method: Int,
                url: String,
                headers: Map<String, String>?,
                parameters: Map<String, Any>?,
                multipartEntities: List<SpikeMultipartEntity>?,
                responseListener: Response.Listener<SpikeResponse>,
                errorListener: Response.ErrorListener):
            super(method, url, (if (parameters == null) null else JSONObject(parameters).toString()), responseListener, errorListener) {
        this.headers = headers as MutableMap<String, String>?

        multipartEntities?.let { multipartEntities ->
            configureMultipartEntities(multipartEntities)
        }
    }

    private fun configureMultipartEntities(multipartEntities: List<SpikeMultipartEntity>) {
        this.multipartEntities = multipartEntities
        val builder = MultipartEntityBuilder.create()
        builder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE)

        multipartEntities.map {
            entity ->
            val contentType = ContentType.create(entity.contentType)
            builder.addBinaryBody(entity.label, entity.bytes, contentType, entity.fileName);
        }
        this.httpEntity = builder.build()
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


    override fun getBodyContentType(): String {
        if (httpEntity == null) {
            return super.getBodyContentType()
        } else {
            return httpEntity!!.getContentType().getValue();
        }
    }

    override fun getBody(): ByteArray {
        if (httpEntity == null) {
            return super.getBody()
        } else {
            val bos = ByteArrayOutputStream()
            try {
                httpEntity!!.writeTo(bos)
                return bos.toByteArray()
            } catch (e: IOException) {
                return super.getBody()
            }
        }
    }

}