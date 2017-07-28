package com.dariopellegrini.spike.model

import com.google.gson.annotations.SerializedName

/**
 * Created by dariopellegrini on 28/07/17.
 */
data class TVMazeError(@SerializedName("name") val name: String,
                       @SerializedName("message") val message: String,
                       @SerializedName("code") val code: Int,
                       @SerializedName("sattus") val status: Int)