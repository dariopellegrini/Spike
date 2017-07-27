package com.dariopellegrini.spike.response

import android.content.Context
import android.util.Log
import com.android.volley.toolbox.Volley
import com.dariopellegrini.spike.network.SpikeNetwork

/**
 * Created by Dario on 25/07/17.
 */
class Spike private constructor() {
    var network: SpikeNetwork? = null

    init {
        Log.i("Spike", "Init Spike")
    }

    private object Holder { val INSTANCE = Spike() }

    companion object {
        val instance: Spike by lazy { Holder.INSTANCE }
    }

    fun configure(context: Context) {
        this.network = SpikeNetwork(Volley.newRequestQueue(context))
    }

    var b:String? = null
}