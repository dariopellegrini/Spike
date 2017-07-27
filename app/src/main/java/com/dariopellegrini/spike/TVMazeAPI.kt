package com.dariopellegrini.spike

import com.dariopellegrini.spike.multipart.SpikeMultipartEntity
import com.dariopellegrini.spike.network.SpikeMethod


/**
 * Created by dariopellegrini on 26/07/17.
 */

data class GetShows(val query: String): TVMazeTarget()
data class GetSingleShow(val query: String): TVMazeTarget()
data class GetPeople(val query: String): TVMazeTarget()
data class GetShowInformation(val showID: String, val embed: String): TVMazeTarget()
data class GetEdisodesByNumber(val showID: String, val season: Int, val number: Int): TVMazeTarget()

// Following actually don't exists
data class AddShow(val name: String, val token: String): TVMazeTarget()
data class UpdateShow(val showID: String, val name: String, val token: String): TVMazeTarget()
data class DeleteShow(val showID: String, val token: String): TVMazeTarget()

sealed class TVMazeTarget: TargetType {

    override val baseURL: String
        get() {
            return "https://api.tvmaze.com/"
        }

    override val path: String
        get() {
            when(this) {
                is GetShows -> return "search/shows"
                is GetSingleShow -> return "singlesearch/shows"
                is GetPeople -> return "search/people"
                is GetShowInformation -> return "shows/" + showID
                is GetEdisodesByNumber -> return "shows/" + showID
                is AddShow -> return "shows/"
                is UpdateShow -> return "shows/" + showID
                is DeleteShow -> return "shows/" + showID
            }
        }

    override val method: SpikeMethod
        get() {
            when(this) {
                is GetShows -> return SpikeMethod.GET
                is GetSingleShow -> return SpikeMethod.GET
                is GetPeople -> return SpikeMethod.GET
                is GetShowInformation -> return SpikeMethod.GET
                is GetEdisodesByNumber -> return SpikeMethod.GET
                is AddShow -> return SpikeMethod.POST
                is UpdateShow -> return SpikeMethod.PATCH
                is DeleteShow -> return SpikeMethod.DELETE
            }
        }
    override val headers: Map<String, String>?
        get() {
            when(this) {
                is GetShows -> return mapOf("Content-Type" to "application/json")
                is GetSingleShow -> return mapOf("Content-Type" to "application/json")
                is GetPeople -> return mapOf("Content-Type" to "application/json")
                is GetShowInformation -> return mapOf("Content-Type" to "application/json")
                is GetEdisodesByNumber -> return mapOf("Content-Type" to "application/json")
                is AddShow -> return mapOf("Content-Type" to "application/json", "user_token" to token)
                is UpdateShow -> return mapOf("Content-Type" to "application/json", "user_token" to token)
                is DeleteShow -> return mapOf("Content-Type" to "application/json", "user_token" to token)
            }
        }

    override val multipartEntities: List<SpikeMultipartEntity>?
        get() = null

    override val parameters: Map<String, Any>?
        get() {
            when(this) {
                is GetShows -> return mapOf("q" to query)
                is GetSingleShow -> return mapOf("q" to query)
                is GetPeople -> return mapOf("q" to query)
                is GetShowInformation -> return mapOf("embed" to embed)
                is GetEdisodesByNumber -> return mapOf("season" to season, "number" to number)
                is AddShow -> return mapOf("name" to name)
                is UpdateShow -> return mapOf("name" to name)
                is DeleteShow -> return null
            }
        }
}