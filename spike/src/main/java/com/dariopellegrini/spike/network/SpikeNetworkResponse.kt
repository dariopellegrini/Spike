package com.dariopellegrini.spike.network

/**
 * Created by dariopellegrini on 28/07/17.
 */
class SpikeNetworkResponse(statusCode: Int, headers: Map<String, String>?, results: String?) {
    val statusCode = statusCode
    val headers = headers
    val results = results
}