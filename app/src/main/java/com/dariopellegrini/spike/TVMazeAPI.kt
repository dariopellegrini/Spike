package com.dariopellegrini.spike

import com.dariopellegrini.spike.model.Movie
import com.dariopellegrini.spike.model.MovieContainer
import com.dariopellegrini.spike.model.TVMazeError
import com.dariopellegrini.spike.multipart.SpikeMultipartEntity
import com.dariopellegrini.spike.network.SpikeMethod
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken


/**
 * Created by dariopellegrini on 26/07/17.
 */

data class GetShows(val query: String): TVMazeTarget()
data class GetShowInformation(val showID: String, val embed: String): TVMazeTarget()

// Following actually don't exists
data class AddShow(val name: String, val coverImage: ByteArray, val token: String): TVMazeTarget()
data class UpdateShow(val showID: String, val name: String, val token: String): TVMazeTarget()
data class DeleteShow(val showID: String, val token: String): TVMazeTarget()

sealed class TVMazeTarget: TargetType {

    override val baseURL: String
        get() {
            return "https://api.tvmaze.com/"
        }

    override val path: String
        get() {
            return when(this) {
                is GetShows             -> "search/shows"
                is GetShowInformation   -> "shows/" + showID
                is AddShow              -> "shows/"
                is UpdateShow           -> "shows/" + showID
                is DeleteShow           -> "shows/" + showID
            }
        }

    override val method: SpikeMethod
        get() {
            return when(this) {
                is GetShows             -> SpikeMethod.GET
                is GetShowInformation   -> SpikeMethod.GET
                is AddShow              -> SpikeMethod.POST
                is UpdateShow           -> SpikeMethod.PATCH
                is DeleteShow           -> SpikeMethod.DELETE
            }
        }
    override val headers: Map<String, String>?
        get() {
            return when(this) {
                is GetShows             -> mapOf("Content-Type" to "application/json")
                is GetShowInformation   -> mapOf("Content-Type" to "application/json")
                is AddShow              -> mapOf("Content-Type" to "application/json", "user_token" to token)
                is UpdateShow           -> mapOf("Content-Type" to "application/json", "user_token" to token)
                is DeleteShow           -> mapOf("Content-Type" to "application/json", "user_token" to token)
            }
        }

    override val multipartEntities: List<SpikeMultipartEntity>?
        get() {
            return when(this) {
                is GetShows             -> null
                is GetShowInformation   -> null
                is AddShow              -> listOf(SpikeMultipartEntity("image/jpeg", coverImage, "coverImage", "coverImage.jpg"))
                is UpdateShow           -> null
                is DeleteShow           -> null
            }
        }

    override val parameters: Map<String, Any>?
        get() {
            return when(this) {
                is GetShows             -> mapOf("q" to query)
                is GetShowInformation   -> mapOf("embed" to embed)
                is AddShow              -> mapOf("name" to name)
                is UpdateShow           -> mapOf("name" to name)
                is DeleteShow           -> null
            }
        }

    override val successClosure: ((String) -> Any?)?
        get() = {
            result ->
            when(this) {
                is GetShows -> {
                    val movieType = object : TypeToken<List<MovieContainer>>() {}.type
                    Gson().fromJson<List<MovieContainer>>(result, movieType)
                }

                is GetShowInformation -> {
                    val movieType = object : TypeToken<Movie>() {}.type
                    Gson().fromJson<Movie>(result, movieType)
                }

                is AddShow -> {
                    val movieType = object : TypeToken<Movie>() {}.type
                    Gson().fromJson<Movie>(result, movieType)
                }

                is UpdateShow -> {
                    val movieType = object : TypeToken<Movie>() {}.type
                    Gson().fromJson<Movie>(result, movieType)
                }

                is DeleteShow -> null
            }
        }

    override val errorClosure: ((String) -> Any?)?
        get() = { errorResult ->
            val errorType = object : TypeToken<TVMazeError>() {}.type
            Gson().fromJson<TVMazeError>(errorResult, errorType)
        }
}