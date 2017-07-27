package com.dariopellegrini.spike

import android.util.Log
import com.dariopellegrini.spike.response.Spike
import com.dariopellegrini.spike.response.SpikeErrorResponse
import com.dariopellegrini.spike.response.SpikeResponse
import com.dariopellegrini.spike.response.SpikeSuccessResponse

/**
 * Created by dariopellegrini on 25/07/17.
 */
class SpikeProvider<in T : TargetType> {
    fun request(target: T, onSuccess: (SpikeSuccessResponse)-> Unit, onError: (SpikeErrorResponse) -> Unit) {
        if (Spike.instance.network == null) {
            Log.i("Spike", "Spike non initiated. Run: Spike.instance.configure(context)")
            return
        }

        Spike.instance.network?.let { network ->
            network.jsonRequest(target.baseURL + target.path, target.method, target.headers, target.parameters, target.multipartEntities, {
                response, error ->
                if (response != null) {
                    target.successClosure?.let {
                        closure ->
                        response.results?.let {
                            results ->
                            response.computedResult = closure(response.results)
                        }
                    }
                    onSuccess(response)
                } else if (error != null) {
                    onError(error)
                }

            })
        }
    }
}