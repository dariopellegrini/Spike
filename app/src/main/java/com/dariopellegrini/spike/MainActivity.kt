package com.dariopellegrini.spike

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import com.dariopellegrini.spike.model.Movie
import com.dariopellegrini.spike.response.Spike
import com.dariopellegrini.spike.model.TVMazeError
import java.io.ByteArrayOutputStream
import com.dariopellegrini.spike.SpikeProvider.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        Spike.instance.configure(this)

        val provider = SpikeProvider<TVMazeTarget>()
        provider.maxRetries = 5
//        provider.requestTypesafe<Movie, TVMazeError>(GetShows("gomorra"), onSuccess = { response ->
//            Log.i("Get Shows", "Success")
//            println(response.results.toString())
//        }, onError = { error ->
//            Log.i("Get Shows", "Error")
//            println(error.results.toString())
//        })


        CoroutineScope(Dispatchers.Main).launch {
            try {
                val response = provider.suspendingRequest<Movie, TVMazeError>(GetShows("gomorra"))
                println(response.results.toString())
            } catch (e: SpikeProviderException) {
                val err = e.errorResponse<String>()
                println(e.statusCode)
            }
        }
    }

    fun doSomething(view: View) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(arrayOf(Manifest.permission.CAMERA),
                        1000)
            } else {
                dispatchTakePictureIntent()
            }
        } else {
            dispatchTakePictureIntent()
        }
    }

    val REQUEST_IMAGE_CAPTURE = 1

    private fun dispatchTakePictureIntent() {
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
            takePictureIntent.resolveActivity(packageManager)?.also {
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == Activity.RESULT_OK) {
            Log.i("Add photo", "Compressing")
            val imageBitmap = data?.extras?.get("data") as Bitmap
            val stream = ByteArrayOutputStream()
            imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream)
            val byteArray = stream.toByteArray()
            load(byteArray)
        }
    }

    private fun getPhotos() {
        val provider = SpikeProvider<APITarget>()
        provider.request(APITarget.GetPhotos("token"), {
            result ->
            print(result)
            Log.i("Get photo", "Success")
        }, {
            error ->
            print(error)
            Log.i("Get photo", "Error")
        })
    }

    private fun load(byteArray: ByteArray) {
        Log.i("Add photo", "Uploading")
        val provider = SpikeProvider<APITarget>()
        provider.request(APITarget.AddPhoto("Hello", "Hello", byteArray, "token"), {
            result ->
            print(result)
            Log.i("Add photo", "Success")
        }, {
            error ->
            print(error)
            Log.i("Add photo", "Error")
        })
    }
}
