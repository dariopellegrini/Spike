package com.dariopellegrini.spike.response

import org.json.JSONObject

/**
 * Created by dariopellegrini on 25/07/17.
 */
open class SpikeResponse(statusCode: Int, headers: Map<String, String>?, results: String?) {
    val statusCode = statusCode
    val headers = headers
    val results = results
}