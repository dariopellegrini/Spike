package com.s4win.whatwelove.spike.response

import org.json.JSONObject

/**
 * Created by dariopellegrini on 25/07/17.
 */
open class SpikeResponse(statusCode: Int, success: SpikeSuccess, headers: Map<String, String>, results: String?) {
    val statusCode = statusCode
    val success = success
    val headers = headers
    val results = results
}