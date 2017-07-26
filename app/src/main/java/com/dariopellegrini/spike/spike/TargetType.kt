package com.s4win.whatwelove.spike

import com.dariopellegrini.spike.spike.upload.SpikeMultipartEntity

/**
 * Created by dariopellegrini on 25/07/17.
 */
interface TargetType {
    val baseURL: String

    val path: String

    val method: Int

    val headers: Map<String, String>?

    val multipartEntities: List<SpikeMultipartEntity>?

    val parameters: Map<String, Any>?

    // Task

    // Sample data
}