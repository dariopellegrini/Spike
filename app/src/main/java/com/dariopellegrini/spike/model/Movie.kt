package com.dariopellegrini.spike.model

import com.google.gson.annotations.SerializedName

/**
 * Created by dariopellegrini on 27/07/17.
 */
data class Movie(@SerializedName("id") val id: String,
                 @SerializedName("name") var name: String,
                 @SerializedName("genres") val genres: List<String>)