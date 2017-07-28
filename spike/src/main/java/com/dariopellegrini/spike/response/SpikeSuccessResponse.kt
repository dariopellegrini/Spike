package com.dariopellegrini.spike.response

/**
 * Created by dariopellegrini on 27/07/17.
 */
class SpikeSuccessResponse<T>(statusCode: Int, headers: Map<String, String>?, results: String?): SpikeResponse(statusCode, headers, results) {
    var computedResult: T? = null

    val success: SpikeSuccess get() {
        when(statusCode) {
            200 -> return SpikeSuccess.ok
            201 -> return SpikeSuccess.created
            202 -> return SpikeSuccess.accepted
            203 -> return SpikeSuccess.nonAuthoritativeInformation
            204 -> return SpikeSuccess.noContent
            205 -> return SpikeSuccess.resetContent
            206 -> return SpikeSuccess.partialContent
            207 -> return SpikeSuccess.multiStatus
            else -> return SpikeSuccess.unknown
        }
    }
}