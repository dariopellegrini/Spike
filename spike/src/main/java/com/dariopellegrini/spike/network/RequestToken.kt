package com.dariopellegrini.spike.network

import com.dariopellegrini.spike.multipart.SpikeMultipartRequest

/**
 * Created by dariopellegrini on 30/10/17.
 */
interface RequestToken {
    fun cancel()
    val isCanceled: Boolean
}

class RequestJsonToken(val jsonRequest: SpikeJsonRequest): RequestToken {
    override fun cancel() {
        if (!jsonRequest.isCanceled) {
            jsonRequest.cancel()
        }
    }
    override val isCanceled: Boolean
    get() = jsonRequest.isCanceled
}

class RequestMultipartToken(val multipartRequest: SpikeMultipartRequest): RequestToken {
    override fun cancel() {
        if (!multipartRequest.isCanceled) {
            multipartRequest.cancel()
        }
    }
    override val isCanceled: Boolean
        get() = multipartRequest.isCanceled
}