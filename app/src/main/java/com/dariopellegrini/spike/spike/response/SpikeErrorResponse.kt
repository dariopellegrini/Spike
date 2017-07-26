package com.s4win.whatwelove.spike.response

import org.json.JSONObject

/**
 * Created by dariopellegrini on 25/07/17.
 */
open class SpikeErrorResponse(statusCode: Int, error: SpikeError, headers: Map<String, String>?, results: String?) {
    val statusCode = statusCode
    val error = error
    val headers = headers
    val results = results
}