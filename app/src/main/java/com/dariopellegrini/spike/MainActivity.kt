package com.dariopellegrini.spike

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import com.dariopellegrini.spike.response.Spike
import com.dariopellegrini.spike.network.SpikeMethod
import com.dariopellegrini.spike.SpikeProvider.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONObject


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        Spike.instance.configure(this)
    }

    fun doSomething(view: View) {

        // Requests with coroutine
        CoroutineScope(Dispatchers.Main).launch {

            // Provider extension
            try {
                val response = SpikeProvider<TargetType>().buildRequest<JSONObject> {
                    baseURL = "https://api.tvmaze.com/"
                    path = "shows"
                    method = SpikeMethod.GET
                    headers = mapOf("Content-Type" to "application/json")
                    successClosure = { response, header ->
                        JSONObject()
                    }
                    errorClosure = {response, header ->
                        JSONObject()
                    }
                }
                Log.i("Spike", "${response.computedResult}")
            } catch(e: SpikeProviderException) {
                Log.e("Spike", "$e")

                // Exception in case of error, like server error or connection error

                // Status code
                val statusCode = e.statusCode

                // Generics used to have a typesafe computed result call
                val errorResponse = e.errorResponse<JSONObject>()
                val computedError = errorResponse.computedResult // TVMazeError
                println("""
                    $statusCode
                    $errorResponse
                    $computedError
                """.trimIndent())
            }

            // Function with global provider and typesafe computed result
            try {
                val response = request<JSONObject> {
                    baseURL = "https://api.tvmaze.com/"
                    path = "shows"
                    method = SpikeMethod.GET
                    headers = mapOf("Content-Type" to "application/json")
                    successClosure = { response, header ->
                        JSONObject()
                    }
                }
                Log.i("Spike", "${response.computedResult}")
            } catch(e: SpikeProviderException) {
                Log.e("Spike", "$e")
            }

            // Function with global request
            try {
                val response = requestAny {
                    baseURL = "https://api.tvmaze.com/"
                    path = "shows"
                    method = SpikeMethod.GET
                    headers = mapOf("Content-Type" to "application/json")
                }
                Log.i("Spike", "${response}")
            } catch(e: SpikeProviderException) {
                Log.e("Spike", "$e")
            }
        }

        // Callbacks

        // Function with global provider and typesafe computed result
        request<JSONObject, JSONObject>({
            baseURL = "https://api.tvmaze.com/"
            path = "shows"
            method = SpikeMethod.GET
            headers = mapOf("Content-Type" to "application/json")
            successClosure = { response, header ->
                JSONObject()
            }
            errorClosure = { response, header ->
                JSONObject()
            }
        }, onSuccess =  { response ->
            Log.i("Spike", "${response.computedResult}")

        }, onError =  { error ->
            Log.e("Spike", "${error.computedResult}")
        })

        // Function with global provider
        requestAny({
            baseURL = "https://api.tvmaze.com/"
            path = "shows"
            method = SpikeMethod.GET
            headers = mapOf("Content-Type" to "application/json")
        }, onSuccess =  { response ->
            Log.i("Spike", "${response}")
        }, onError =  { error ->
            Log.e("Spike", "$error")
        })
    }
}
