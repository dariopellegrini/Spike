package com.dariopellegrini.spike

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import com.android.volley.*
import com.dariopellegrini.spike.model.Movie
import com.dariopellegrini.spike.response.Spike
import org.json.JSONException
import com.android.volley.toolbox.HttpHeaderParser
import com.android.volley.toolbox.StringRequest
import org.json.JSONObject
import com.android.volley.toolbox.Volley
import com.dariopellegrini.spike.model.TVMazeError
import java.io.UnsupportedEncodingException


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

//        Spike.instance.configure(this)
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
        Spike.instance.configure(this)

        val provider = SpikeProvider<TVMazeTarget>()
        provider.maxRetries = 5
        provider.requestTypesafe<Movie, TVMazeError>(GetShows("gomorra"), onSuccess = { response ->
            println(response.results.toString())
        }, onError = { error ->
            println(error.results.toString())
        })
    }

    fun sendRequest() {
        try {
            val requestQueue = Volley.newRequestQueue(this)
            val URL = "http://www.comixtime.it/api/web/api/logins"
            val jsonBody = JSONObject()
            jsonBody.put("email", "dario@comixtime.com")
            jsonBody.put("password", "Comix123")
            val requestBody = jsonBody.toString()

            val stringRequest = object : StringRequest(Request.Method.POST, URL, object : Response.Listener<String> {
                override fun onResponse(response: String) {
                    Log.i("VOLLEY", response)
                }
            }, object : Response.ErrorListener {
                override fun onErrorResponse(error: VolleyError) {
                    Log.e("VOLLEY", error.toString())
                }
            }) {
                override fun getBodyContentType(): String {
                    return "application/json; charset=utf-8"
                }

                @Throws(AuthFailureError::class)
                override fun getBody(): ByteArray? {
                    try {
                        return requestBody?.toByteArray(charset("utf-8"))
                    } catch (uee: UnsupportedEncodingException) {
                        VolleyLog.wtf("Unsupported Encoding while trying to get the bytes of %s using %s", requestBody, "utf-8")
                        return null
                    }

                }

                override fun parseNetworkResponse(response: NetworkResponse): Response<String> {
                    var responseString = ""
                    if (response != null) {
                        responseString = response.statusCode.toString()
                        // can get more details such as response.headers
                    }
                    return Response.success(responseString, HttpHeaderParser.parseCacheHeaders(response))
                }
            }

            requestQueue.add(stringRequest)
        } catch (e: JSONException) {
            e.printStackTrace()
        }

    }
}
