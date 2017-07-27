package com.dariopellegrini.spike.multipart

/**
 * Created by dariopellegrini on 26/07/17.
 */
class SpikeMultipartEntity(contentType: String, bytes: ByteArray, label: String, fileName: String) {
    val contentType = contentType
    val bytes = bytes
    val label = label
    val fileName = fileName
}