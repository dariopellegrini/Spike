package com.dariopellegrini.spike

import com.android.volley.Request
import com.dariopellegrini.spike.multipart.SpikeMultipartEntity
import com.dariopellegrini.spike.network.SpikeMethod
import com.dariopellegrini.spike.response.SpikeSuccessResponse

/**
 * Created by dariopellegrini on 26/07/17.
 */

data class AddDress(val name: String, val category: String, val dressImage: ByteArray, val detailImage: ByteArray, val token: String): DressesTarget()
data class UpdateDress(val dressID: String, val name: String, val category: String, val dressImage: ByteArray, val detailImage: ByteArray, val token: String): DressesTarget()

sealed class DressesTarget: TargetType {
    override val baseURL: String
        get() {
            return "https://wardrobe.com/"
        }

    override val path: String
        get() {
            when(this) {
                is AddDress -> return "dress"
                is UpdateDress -> return "dress/" + dressID
            }
        }

    override val method: SpikeMethod
        get() {
            when(this) {
                is AddDress -> return SpikeMethod.POST
                is UpdateDress -> return SpikeMethod.PUT
            }
        }
    override val headers: Map<String, String>?
        get() {
            when(this) {
                is AddDress -> return mapOf("user_token" to token)
                is UpdateDress -> return mapOf("user_token" to token)
            }
        }

    override val multipartEntities: List<SpikeMultipartEntity>?
        get() {
            when(this) {
                is AddDress -> return listOf(SpikeMultipartEntity("image/jpeg", dressImage, "dressImage", "dressImage.jpg"),
                        SpikeMultipartEntity("image/jpeg", detailImage, "detailImage", "detailImage.jpg"))
                is UpdateDress -> return listOf(SpikeMultipartEntity("image/jpeg", dressImage, "dressImage", "dressImage.jpg"),
                        SpikeMultipartEntity("image/jpeg", detailImage, "detailImage", "detailImage.jpg"))
            }
        }

    override val parameters: Map<String, Any>?
        get() {
            when(this) {
                is AddDress -> return mapOf("name" to name, "category" to category)
                is UpdateDress -> return mapOf("name" to name, "category" to category)
            }
        }

    override val successClosure: ((String) -> Any?)?
        get() = null
}