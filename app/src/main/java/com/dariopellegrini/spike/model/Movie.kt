package com.dariopellegrini.spike.model


/**
 * Created by dariopellegrini on 27/07/17.
 */
data class Movie(val id: String,
                 var name: String,
                 val genres: List<String>)