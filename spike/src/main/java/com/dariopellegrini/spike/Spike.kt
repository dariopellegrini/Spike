package com.dariopellegrini.spike.response

import android.content.Context
import android.util.Log
import com.android.volley.DefaultRetryPolicy
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

    var verboseExceptions: Boolean? = null

    init {
        Log.i("Spike", "Init Spike")
    }

    private object Holder { val INSTANCE = Spike() }

    companion object {
        val instance: Spike by lazy { Holder.INSTANCE }
    }

    fun configure(context: Context, retryPolicy: DefaultRetryPolicy? = null) {
        val cookieManager = CookieManager()
        CookieHandler.setDefault(cookieManager)
        queue = newRequest(context)
        this.network = SpikeNetwork(queue, retryPolicy)
    }

    fun newRequest(context: Context) = Volley.newRequestQueue(context, CustomHurlStack())
}