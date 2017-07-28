package com.dariopellegrini.spike

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.dariopellegrini.spike.model.Movie
import com.dariopellegrini.spike.response.Spike

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        Spike.instance.configure(this)
    }

    fun doSomething(view: View) {
/*
        val bm1 = BitmapFactory.decodeResource(resources, R.drawable.ball)
        val stream1 = ByteArrayOutputStream()
        bm1.compress(Bitmap.CompressFormat.PNG, 100, stream1)
        val bytes1 = stream1.toByteArray()

        val bm2 = BitmapFactory.decodeResource(resources, R.drawable.spike)
        val stream2 = ByteArrayOutputStream()
        bm2.compress(Bitmap.CompressFormat.PNG, 100, stream2)
        val bytes2 = stream2.toByteArray()



        val provider = SpikeProvider<DressesTarget>()
        provider.request(AddDress("Super awesome dress 1", "Super awesome category 1", bytes1, bytes2, "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ0eXBlIjoidXNlciIsImFwcERvbWFpbiI6Imd1YXJkcm9iZS5zdGFtcGxheWFwcC5jb20iLCJhcHBJZCI6Imd1YXJkcm9iZSIsInVzZXIiOiI1OTY0N2U4YmY2Yzc0MTcxM2JhM2U2YmEiLCJpYXQiOjE1MDExNjYyNjYsImV4cCI6MTAxNTAxMTY2MjY1LCJqdGkiOiJCMVhhOXV3OGIifQ.8EfZqQCq95yHSEh2SGpcH2zH_MhurpiXkJzuSlsJ6gc"), {
            response ->
            println(response.results.toString())
        }, {
            error ->
            println(error.results.toString())
        })
*/

        val provider = SpikeProvider<TVMazeTarget>()
        provider.requestTypesafe<Movie, TVMazeTarget>(GetShowInformation("1", "cast"), {
            response ->
            println(response.results.toString())
        }, {
            error ->
            println(error.results.toString())
        })

    }
}
