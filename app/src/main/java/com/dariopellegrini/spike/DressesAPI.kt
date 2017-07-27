package com.dariopellegrini.spike

import com.android.volley.Request
import com.dariopellegrini.spike.spike.network.SpikeMethod
import com.dariopellegrini.spike.spike.upload.SpikeMultipartEntity
import com.s4win.whatwelove.spike.TargetType

/**
 * Created by dariopellegrini on 26/07/17.
 */

data class AddDress(val name: String, val dressImage: ByteArray, val detailImage: ByteArray): DressesTarget()

sealed class DressesTarget: TargetType {

    override val baseURL: String
        get() {
            return "https://wardrobe.com/"
        }

    override val path: String
        get() {
            when(this) {
                is AddDress -> return "dresses"
            }
        }

    override val method: SpikeMethod
        get() {
            when(this) {
                is AddDress -> return SpikeMethod.POST
            }
        }
    override val headers: Map<String, String>?
        get() {
            when(this) {
                is AddDress -> return null
            }
        }

    override val multipartEntities: List<SpikeMultipartEntity>?
        get() {
            when(this) {
                is AddDress -> return listOf(SpikeMultipartEntity("image/jpeg", dressImage, "dress", "dress.jpg"),
                        SpikeMultipartEntity("image/jpeg", detailImage, "detail", "detail.jpg"))
            }
        }

    override val parameters: Map<String, Any>?
        get() {
            when(this) {
                is AddDress -> return mapOf("name" to name)
            }
        }
}