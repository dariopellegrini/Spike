package com.dariopellegrini.spike

import com.google.gson.annotations.SerializedName

/**
 * Created by dariopellegrini on 27/07/17.
 */
data class MovieContainer(@SerializedName("score") val score: Double,
                          @SerializedName("show") val movie: Movie)