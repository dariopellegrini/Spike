package com.s4win.whatwelove.spike

/**
 * Created by dariopellegrini on 25/07/17.
 */
interface TargetType {
    val baseURL: String

    val path: String

    val method: Int

    val headers: Map<String, String>?

    val parameters: Map<String, Any>?

    // Task

    // Sample data
}