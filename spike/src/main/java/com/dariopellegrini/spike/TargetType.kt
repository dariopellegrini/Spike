package com.dariopellegrini.spike

import com.dariopellegrini.spike.multipart.SpikeMultipartEntity
import com.dariopellegrini.spike.network.SpikeMethod

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

    // Task

    // Sample data
}