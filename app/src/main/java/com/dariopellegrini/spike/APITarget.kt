package com.dariopellegrini.spike

import com.dariopellegrini.spike.multipart.SpikeMultipartEntity
import com.dariopellegrini.spike.network.SpikeMethod

sealed class APITarget : TargetType {
    object GetPresents: APITarget()
    object GetPhotos: APITarget()
    class AddPhoto(val title: String, val message: String, val image: ByteArray): APITarget()

    override val baseURL: String
        get() = "https://vertirest.api.dariopellegrini.com/"
//        get() = "http://192.168.0.5:23146/"

    override val path: String
        get() {
            return when(this) {
                is GetPresents      -> "presents"
                is GetPhotos        -> "photos"
                is AddPhoto        -> "photos"
            }
        }

    override val headers: Map<String, String>?
        get() {
            return when(this) {
                is GetPresents    -> mapOf("Content-Type" to "application/json;")
                is GetPhotos    -> mapOf("Content-Type" to "application/json;")
                is AddPhoto    -> mapOf("Content-Type" to "multipart/form-data;")
            }
        }

    override val method: SpikeMethod
        get() {
            return when(this) {
                is GetPresents  -> SpikeMethod.GET
                is GetPhotos    -> SpikeMethod.GET
                is AddPhoto     -> SpikeMethod.POST
            }
        }

    override val multipartEntities: List<SpikeMultipartEntity>?
        get() {
            return when(this) {
                is AddPhoto -> listOf(SpikeMultipartEntity("image/jpeg", image, "image", "$title.jpg"))
                else -> null
            }
        }

    override val parameters: Map<String, Any>?
        get() {
            return when(this) {
                is GetPresents  -> null
                is GetPhotos    -> null
                is AddPhoto     -> mapOf("title" to title, "message" to message)
            }
        }
}