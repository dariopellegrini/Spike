# Spike
A network abstraction layer over Volley, written in Kotlin and inspired by [Moya for Swift](https://github.com/Moya/Moya)

## Example
Download repository and try the app.

## Installation
Add edit your build.gradle file
``` groovy
allprojects {
    repositories {
        ...
        maven { url 'https://jitpack.io' }
    }
}
```
Then add as dependency to yout app/build.gradle
``` groovy
dependencies {
    ...
    compile 'com.github.dariopellegrini:Spike:v0.5'
}
```
This library uses apache http libraries that need the following code at the end of the android section in app/build.gradle. Hopefully in the future this won't be needed.
``` groovy
packagingOptions {
        exclude 'META-INF/DEPENDENCIES'
        exclude 'META-INF/NOTICE'
        exclude 'META-INF/LICENSE'
    }
```
    
## Usage
Before start init Spike:
``` kotlin
Spike.instance.configure(context)
```

This library lets you to split API request's details inside kotlin files, in order to have more control on what each API does and needs.
Each file is a sealed class and must implement the interface TargetType. Every detail of each call is selected using a when statement.
See this example (TVMazeAPI.kt):

``` kotlin

// Each of this data class represent a call
data class GetShows(val query: String): TVMazeTarget()
data class GetSingleShow(val query: String): TVMazeTarget()
data class GetPeople(val query: String): TVMazeTarget()
data class GetShowInformation(val showID: String, val embed: String): TVMazeTarget()
data class GetEdisodesByNumber(val showID: String, val season: Int, val number: Int): TVMazeTarget()

// Following actually don't exists
data class AddShow(val name: String, val coverImage: ByteArray, val token: String): TVMazeTarget()
data class UpdateShow(val showID: String, val name: String, val token: String): TVMazeTarget()
data class DeleteShow(val showID: String, val token: String): TVMazeTarget()

// This is the target sealed class, from which every data class inherits.
sealed class TVMazeTarget: TargetType {

// BaseURL of each call
    override val baseURL: String
        get() {
            return "https://api.tvmaze.com/"
        }

// Path of each call
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

// Method of each call
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
        
// Headers of each call
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

// Multipart entries to load multipart form data
    override val multipartEntities: List<SpikeMultipartEntity>?
        get() = is GetShows -> return null
                is GetSingleShow -> return null
                is GetPeople -> return null
                is GetShowInformation -> null
                is GetEdisodesByNumber -> null
                is AddShow -> return listOf(SpikeMultipartEntity("image/jpeg", coverImage, "coverImage", "coverImage.jpg"))
                is UpdateShow -> return null
                is DeleteShow -> return null

// Call's parameters with the labels wanted by backend services
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
```

After this the only thing to do is init a SpikeProvider and make a request using the desired instance:
``` kotlin
val provider = SpikeProvider<TVMazeTarget>()
        provider.request(GetShowInformation("1", embed = "cast"), {
            response ->
            println(response.results.toString())
        }, {
            error ->
            println(error.results.toString())
        })
```

Here response object contains status code, an enum value describing status code, headers in map and body in String.
Then error contains the same values plus a VolleyError object.

## TODO
- File upload.
- Alternative way to deal with multipart form data.
- Testing.

## Author

Dario Pellegrini, pellegrini.dario.1303@gmail.com

## Credits
- [Volley](https://github.com/google/volley)
- [Moya for Swift](https://github.com/Moya/Moya)
