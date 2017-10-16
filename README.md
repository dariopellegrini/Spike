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
    compile 'com.github.dariopellegrini:Spike:v0.9'
}
```
This library uses apache http libraries that need the following code at the end of the android section in app/build.gradle. Hopefully in the future this won't be needed.
``` groovy
android {
    ...
    packagingOptions {
            exclude 'META-INF/DEPENDENCIES'
            exclude 'META-INF/NOTICE'
            exclude 'META-INF/LICENSE'
     }
}
```
    
## Usage

This library lets you to split API request's details inside kotlin files, in order to have more control on what each API does and needs.
Each file is a sealed class and must implement the interface TargetType. Every detail of each call is selected using a when statement.
See this example (TVMazeAPI.kt):

``` kotlin

// Each of these data class represents a call
data class GetShows(val query: String): TVMazeTarget()
data class GetShowInformation(val showID: String, val embed: String): TVMazeTarget()

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
            return when(this) {
                is GetShows             -> "search/shows"
                is GetShowInformation   -> "shows/" + showID
                is AddShow              -> "shows/"
                is UpdateShow           -> "shows/" + showID
                is DeleteShow           -> "shows/" + showID
            }
        }

// Method of each call
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
        
// Headers of each call
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

// Multipart entries to load multipart form data: this is optional
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

// Call's parameters with the labels wanted by backend services
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
}

// Optional response closures
```

## Provider
After this the only thing to do is init a SpikeProvider and make a request using the desired instance:
``` kotlin
val provider = SpikeProvider<TVMazeTarget>(context)
        val request = provider.request(GetShowInformation("1", embed = "cast"), {
            response ->
            println(response.results.toString())
        }, {
            error ->
            println(error.results.toString())
        })
```

Here response object contains status code, an enum value describing status code, headers in map, result in String and a computed result (see later).
Then error contains the same values plus a VolleyError object.

The request is a Volley request and can be canceled as you wish.

There are different constructors for providers:
1. Context constructor: init a volley queue using the passed context. By this each provider has its queue.
``` kotlin
val provider = SpikeProvider<TVMazeTarget>(context)
```

2. Queue constructor: init a volley queue using a queue passed to it.
``` kotlin
val provider = SpikeProvider<TVMazeTarget>(queue)
```

3. Empty constructor: implementing this requires to configure a Spike singleton instance, which contains a queue that is global and shared between each provider.
``` kotlin
Spike.instance.configure(context) // called typically in Application file
val provider = SpikeProvider<TVMazeTarget>()
```

## Closure responses
It's possible to deal with network responses in the API file, implementing 2 optional closure variables.

```kotlin

...

override val successClosure: ((String, Map<String, String>?) -> Any?)?
        get() = {
            result, headers ->
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

    override val errorClosure: ((String, Map<String, String>?) -> Any?)?
        get() = { errorResult, _ ->
            val errorType = object : TypeToken<TVMazeError>() {}.type
            Gson().fromJson<TVMazeError>(errorResult, errorType)
        }
```

Here you can compute the result string from network (making for example a Gson mapping).
Result of those closures will be in computedResult inside povider request's closures as a parameter of type Any?.

```kotlin
val provider = SpikeProvider<TVMazeTarget>()
        provider.request(GetShowInformation("1", "cast"), {
            response ->
            // Printing success computed result
            println(response.computedResult)
        }, {
            error ->
            // Printing error computed result
            println(error.computedResult)
        })
```

Because computedResult is an Any? type, provider can perform a type safety call so that computed results for success and error have specific types.

```kotlin
// Movie and TVMazeError are data classes for TVMaze APIs
val provider = SpikeProvider<TVMazeTarget>()
        provider.requestTypesafe<Movie, TVMazeError>(GetShowInformation("1", "cast"), {
            response ->
            // Printing success computed result Movie? type
            println(response.computedResult)
        }, {
            error ->
            // Printing error computed result TVMazeError? type
            println(error.computedResult)
        })
```

## TODO
- File upload.
- Alternative way to deal with multipart form data.
- Testing.

## Author

Dario Pellegrini, pellegrini.dario.1303@gmail.com

## Credits
- [Volley](https://github.com/google/volley)
- [Moya for Swift](https://github.com/Moya/Moya)
