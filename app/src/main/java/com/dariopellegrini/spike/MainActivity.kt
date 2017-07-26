package com.dariopellegrini.spike

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.s4win.whatwelove.GetShowInformation
import com.s4win.whatwelove.TVMazeTarget
import com.s4win.whatwelove.spike.SpikeProvider
import com.s4win.whatwelove.spike.response.Spike

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

         Spike.instance.configure(this)
    }

    fun doSomething(view: View) {
        val provider = SpikeProvider<TVMazeTarget>()
        provider.request(GetShowInformation("1", embed = "cast"), {
            response ->
            println(response.results.toString())
        }, {
            error ->
            println(error.results.toString())
        })
    }
}
