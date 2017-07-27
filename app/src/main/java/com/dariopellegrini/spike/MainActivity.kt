package com.dariopellegrini.spike

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.s4win.whatwelove.TVMazeTarget
import com.s4win.whatwelove.spike.SpikeProvider
import com.s4win.whatwelove.spike.response.Spike
import android.graphics.BitmapFactory
import android.graphics.Bitmap
import java.io.ByteArrayOutputStream


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

         Spike.instance.configure(this)
    }

    fun doSomething(view: View) {

        val bm1 = BitmapFactory.decodeResource(resources, R.drawable.ball)
        val stream1 = ByteArrayOutputStream()
        bm1.compress(Bitmap.CompressFormat.PNG, 100, stream1)
        val bytes1 = stream1.toByteArray()

        val bm2 = BitmapFactory.decodeResource(resources, R.drawable.spike)
        val stream2 = ByteArrayOutputStream()
        bm2.compress(Bitmap.CompressFormat.PNG, 100, stream2)
        val bytes2 = stream2.toByteArray()


        val provider = SpikeProvider<DressesTarget>()
        provider.request(AddDress("Super awesome dress", "Super awesome category", bytes1, bytes2, "userToken"), {
            response ->
            println(response.results.toString())
        }, {
            error ->
            println(error.results.toString())
        })

/*
        val provider = SpikeProvider<TVMazeTarget>()
        provider.request(GetShowInformation("1", embed = "cast"), {
            response ->
            println(response.results.toString())
        }, {
            error ->
            println(error.results.toString())
        })
        */
    }
}
