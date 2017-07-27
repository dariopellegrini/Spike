package com.dariopellegrini.spike

import com.google.gson.annotations.SerializedName

/**
 * Created by dariopellegrini on 27/07/17.
 */
data class Movie(@SerializedName("id") val id: String,
                 @SerializedName("name") val name: String,
                 @SerializedName("genres") val genres: List<String>)