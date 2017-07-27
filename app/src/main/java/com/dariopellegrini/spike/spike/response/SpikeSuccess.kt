package com.s4win.whatwelove.spike.response

/**
 * Created by dariopellegrini on 25/07/17.
 */
enum class SpikeSuccess {
    ok, // 200
    created, // 201
    accepted, // 202
    nonAuthoritativeInformation, // 203
    noContent, // 204
    resetContent, // 205
    partialContent, // 206
    multiStatus, // 207
    unknown
}