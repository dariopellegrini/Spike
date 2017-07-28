package com.dariopellegrini.spike.response

import com.android.volley.VolleyError

/**
 * Created by dariopellegrini on 25/07/17.
 */
open class SpikeErrorResponse<T>(statusCode: Int, headers: Map<String, String>?, results: String?, volleyError: VolleyError?): SpikeResponse(statusCode, headers, results) {
    val volleyError = volleyError
    var computedResult: T? = null

    val error: SpikeError get() {
        when(statusCode) {
            -1001 -> return SpikeError.noConnection
            400 -> return SpikeError.badRequest
            401 -> return SpikeError.unauthorized
            402 -> return SpikeError.paymentRequired
            403 -> return SpikeError.forbidden
            404 -> return SpikeError.notFound
            405 -> return SpikeError.methodNotAllowed
            406 -> return SpikeError.notAcceptable
            407 -> return SpikeError.proxyAuthenticationRequired
            408 -> return SpikeError.requestTimeout
            409 -> return SpikeError.conflict
            410 -> return SpikeError.gone
            411 -> return SpikeError.lengthRequired
            412 -> return SpikeError.preconditionFailed
            413 -> return SpikeError.requestEntityTooLarge
            414 -> return SpikeError.requestURITooLong
            415 -> return SpikeError.unsupportedMediaType
            416 -> return SpikeError.requestedRangeNotSatisfiable
            417 -> return SpikeError.requestedRangeNotSatisfiable
            418 -> return SpikeError.enhanceYourCalm
            421 -> return SpikeError.misdirectedRequest
            422 -> return SpikeError.unprocessableEntity
            423 -> return SpikeError.locked
            424 -> return SpikeError.failedDependency
            426 -> return SpikeError.upgradeRequired
            428 -> return SpikeError.preconditionRequired
            429 -> return SpikeError.tooManyRequests
            431 -> return SpikeError.requestHeaderFieldsTooLarge
            451 -> return SpikeError.unavailableForLegalReasons
            500 -> return SpikeError.internalServerError
            501 -> return SpikeError.notImplemented
            502 -> return SpikeError.badGateway
            503 -> return SpikeError.serviceUnavailable
            504 -> return SpikeError.gatewayTimeout
            505 -> return SpikeError.HTTPVersionNotSupported
            506 -> return SpikeError.bandwidthLimitExceeded
            else -> return SpikeError.unknownError
        }
    }
}