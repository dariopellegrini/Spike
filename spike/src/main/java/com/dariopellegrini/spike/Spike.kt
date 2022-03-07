package com.dariopellegrini.spike.response

import android.content.Context
import android.util.Log
import com.android.volley.RequestQueue
import com.android.volley.toolbox.Volley
import com.dariopellegrini.spike.java.CustomHurlStack
import com.dariopellegrini.spike.network.SpikeNetwork
import java.net.CookieHandler
import java.net.CookieManager
import java.net.CookiePolicy
import java.net.CookiePolicy.ACCEPT_ALL



/**
 * Created by Dario on 25/07/17.
 */
class Spike private constructor() {
    var network: SpikeNetwork? = null
    private lateinit var queue: RequestQueue

    init {
        Log.i("Spike", "Init Spike")
    }

    private object Holder { val INSTANCE = Spike() }

    companion object {
        val instance: Spike by lazy { Holder.INSTANCE }
    }

    fun configure(context: Context) {
        val cookieManager = CookieManager()
        CookieHandler.setDefault(cookieManager)
        queue = newRequest(context)
        this.network = SpikeNetwork(queue)
    }

    fun newRequest(context: Context) = Volley.newRequestQueue(context, CustomHurlStack())
}