package com.dariopellegrini.spike.network

/**
 * Created by dariopellegrini on 30/10/17.
 */
class RequestToken(val request: SpikeRequest) {
    fun cancel() {
        if (!request.isCanceled) {
            request.cancel()
        }
    }
    val isCanceled: Boolean
    get() = request.isCanceled
}