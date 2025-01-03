package com.dariopellegrini.spikeapp

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.dariopellegrini.spike.response.Spike
import com.dariopellegrini.spike.network.SpikeMethod
import com.dariopellegrini.spike.SpikeProvider.*
import com.dariopellegrini.spike.request
import com.dariopellegrini.spike.utilities.applicationXWWWFormUrlEncoded
import com.dariopellegrini.spike.utilities.contentType
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONObject


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        Spike.instance.configure(this)
        Spike.instance.verboseExceptions = true
    }

    fun doSomething(view: View) {

        // Requests with coroutine
        CoroutineScope(Dispatchers.Main).launch {

            // Provider extension
//            try {
//                val response = SpikeProvider<TargetType>().buildRequest<JSONObject> {
//                    baseURL = "https://api.tvmaze.com/"
//                    path = "showsss"
//                    method = SpikeMethod.GET
//                    headers = mapOf("Content-Type" to "application/json")
//                    successClosure = { response, header ->
//                        JSONObject()
//                    }
//                    errorClosure = {response, header ->
//                        JSONObject()
//                    }
//                }
//                Log.i("Spike", "${response.computedResult}")
//            } catch(e: SpikeProviderException) {
//                Log.e("Spike", "$e")
//
//                // Exception in case of error, like server error or connection error
//
//                // Status code
//                val statusCode = e.statusCode
//
//                // Generics used to have a typesafe computed result call
//                val errorResponse = e.errorResponse<JSONObject>()
//                val computedError = errorResponse.computedResult // TVMazeError
//                println("""
//                    $statusCode
//                    $errorResponse
//                    $computedError
//                """.trimIndent())
//            }

            // Function with global provider and typesafe computed result
            try {
                val response = request<JSONObject> {
                    baseURL = "https://hello.it"
                    path = "/hello"
                    method = SpikeMethod.POST
                    headers = mapOf(contentType to applicationXWWWFormUrlEncoded)
                    parameters = mapOf(
                        "hello" to "Hello"
                    )
                }
                Log.i("Spike", "${response.computedResult}")
            } catch(e: SpikeProviderException) {
                val s = e.statusCode
                Log.e("Spike", "$e $s")
            }

            // Function with global request
//            try {
//                val movies = request<List<Movie>> {
//                    baseURL = "https://api.tvmaze.com/"
//                    path = "shows"
//                    method = SpikeMethod.GET
//                    headers = mapOf("Content-Type" to "application/json")
//                }.suspend.mapping()
//                Log.i("Spike", "${movies}")
//            } catch(e: SpikeProviderException) {
//                val error = e.errorResponse<TVMazeError>().mapping()
//                Log.e("Spike", "$e")
//            } catch (e: Exception) {
//                Log.e("Spike", "$e")
//            }
        }

        // Callbacks

        // Function with global provider and typesafe computed result
//        request<JSONObject, JSONObject>({
//            baseURL = "https://api.tvmaze.com/"
//            path = "shows"
//            method = SpikeMethod.GET
//            headers = mapOf("Content-Type" to "application/json")
//            successClosure = { response, header ->
//                JSONObject()
//            }
//            errorClosure = { response, header ->
//                JSONObject()
//            }
//        }, onSuccess = { response ->
//            Log.i("Spike", "${response.computedResult}")
//
//        }, onError = { error ->
//            Log.e("Spike", "${error.computedResult}")
//        })
//
//        // Function with global provider
//        requestAny({
//            baseURL = "https://api.tvmaze.com/"
//            path = "shows"
//            method = SpikeMethod.GET
//            headers = mapOf("Content-Type" to "application/json")
//        }, onSuccess = { response ->
//            Log.i("Spike", "${response}")
//        }, onError = { error ->
//            Log.e("Spike", "$error")
//        })
    }
}
