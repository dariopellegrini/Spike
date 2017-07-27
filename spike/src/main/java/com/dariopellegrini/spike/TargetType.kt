package com.dariopellegrini.spike

import com.dariopellegrini.spike.multipart.SpikeMultipartEntity
import com.dariopellegrini.spike.network.SpikeMethod
import com.dariopellegrini.spike.response.SpikeErrorResponse
import com.dariopellegrini.spike.response.SpikeSuccessResponse

/**
 * Created by dariopellegrini on 25/07/17.
 */
interface TargetType {
    val baseURL: String

    val path: String

    val method: SpikeMethod

    val headers: Map<String, String>?

    val multipartEntities: List<SpikeMultipartEntity>?

    val parameters: Map<String, Any>?

    val successClosure: ((String) -> (Any?))?

    // Task

    // Sample data
}