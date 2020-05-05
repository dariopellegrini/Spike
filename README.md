# Spike

![](https://img.shields.io/static/v1.svg?url=<google.com>&logo=android&label=SDK&color=green&style=popout&message=29)
![](https://jitpack.io/v/dariopellegrini/Spike.svg)

A network abstraction layer over Volley, written in Kotlin and inspired by [Moya for Swift](https://github.com/Moya/Moya)

## Example
Download repository and try the app.

## Installation
Add in your build.gradle file
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
    implementation 'com.github.dariopellegrini:Spike:v0.16'
}
```
Versions below 0.10 of this library use apache http libraries, that need the following code at the end of the android section in app/build.gradle. From version 0.10 this is not necessary.
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

## Coroutines support
Starting by version 0.12 it's possible to use suspending functions to perform provider requests.
```kotlin
CoroutineScope(Dispatchers.Main).launch {
            try {
                val response = provider.suspendingRequest<Movie>(GetShows("gomorra"))
                println("${response.results}")
            } catch (e: SpikeProviderException) {
                // Exception in case of error, like server error or connection error

                // Status code
                val statusCode = e.statusCode
                
                // Generics used to have a typesafe computed result call
                val errorResponse = e.errorResponse<TVMazeError>()
                val computedError = errorResponse?.computedResult // TVMazeError
                println("""
                    $statusCode
                    $errorResponse
                    $computedError
                """.trimIndent())
            }
        }
```

## Builder
Version 0.14 supports builders for creating targets and requests.

### Target
```kotlin
val target = buildTarget {
            baseURL = "http://localhost"
            path = "endpoint"
            method = SpikeMethod.GET
            headers = mapOf("Content-Type" to "application/json")
            successClosure = {
                result, headers ->
                JSONObject(result)
            }
            errorClosure = {
                errorResult, headers ->
                JSONObject(errorResult)
            }
        }
```

### Request
Here is returned a suspending function to use within a coroutine.
```kotlin
CoroutineScope(Dispatchers.Main).launch {
            try {
                val response = SpikeProvider<TargetType>()
                        .buildRequest<JSONObject> {
                            baseURL = "http://localhost/"
                            path = "tales"
                            method = SpikeMethod.POST
                            headers = mapOf("Content-Type" to "application/json")
                            parameters = mapOf(
                                    "title" to " My tale",
                                    "content" to "This is the tale content",
                                    "author" to "John")
                        }
                Log.i("Success", "${response.computedResult}")
            } catch(e: Exception) {
                Log.e("Error", "$e")
            }
        }
```

### Other requests
In order to make requests shorter starting from version 0.16 other types of requests which use global providers are available, with and without coroutines.
```kotlin
CoroutineScope(Dispatchers.Main).launch {

            // Function with global provider and typesafe computed result
            try {
                val response = request<JSONObject> {
                    baseURL = "https://api.tvmaze.com/"
                    path = "shows"
                    method = SpikeMethod.GET
                    headers = mapOf("Content-Type" to "application/json")
                    successClosure = { response, header ->
                        JSONObject()
                    }
                }
                Log.i("Spike", "${response.computedResult}")
            } catch(e: SpikeProviderException) {
                Log.e("Spike", "$e")
            }

            // Function with global request
            try {
                val response = requestAny {
                    baseURL = "https://api.tvmaze.com/"
                    path = "shows"
                    method = SpikeMethod.GET
                    headers = mapOf("Content-Type" to "application/json")
                }
                Log.i("Spike", "${response}")
            } catch(e: SpikeProviderException) {
                Log.e("Spike", "$e")
            }
        }

        // Callbacks

        // Function with global provider and typesafe computed result
        request<JSONObject, JSONObject>({
            baseURL = "https://api.tvmaze.com/"
            path = "shows"
            method = SpikeMethod.GET
            headers = mapOf("Content-Type" to "application/json")
            successClosure = { response, header ->
                JSONObject()
            }
            errorClosure = { response, header ->
                JSONObject()
            }
        }, onSuccess =  { response ->
            Log.i("Spike", "${response.computedResult}")

        }, onError =  { error ->
            Log.e("Spike", "${error.computedResult}")
        })

        // Function with global provider
        requestAny({
            baseURL = "https://api.tvmaze.com/"
            path = "shows"
            method = SpikeMethod.GET
            headers = mapOf("Content-Type" to "application/json")
        }, onSuccess =  { response ->
            Log.i("Spike", "${response}")
        }, onError =  { error ->
            Log.e("Spike", "$error")
        })
```

## Mapping
Starting from version 0.22, mapping methods based on Gson has been introduced, for success and error responses.
```kotlin
try {
    val movies = request<List<Movie>> {
        baseURL = "https://api.tvmaze.com/"
        path = "shows"
        method = SpikeMethod.GET
        headers = mapOf("Content-Type" to "application/json")
    }.mapping()
    Log.i("Spike", "${movies}") // List<Movie>
} catch(e: SpikeProviderException) {
    val error = e.errorResponse<TVMazeError>().mapping() // TVMazeError
    Log.e("Spike", "$e")
} catch (e: Exception) {
    Log.e("Spike", "$e")
}
```

By default `mapping` function returns `null` if a mapping error is thrown.  
In order to throw mapping error `mappingThrowable` function is available.

### Coroutine
Mapping process can be expensive for large body sizes and can block main thread, freezing UI.  
To avoid that suspend mapping function are supported.
```kotlin
try {
    val movies = request<List<Movie>> {
        baseURL = "https://api.tvmaze.com/"
        path = "shows"
        method = SpikeMethod.GET
        headers = mapOf("Content-Type" to "application/json")
    }.suspend.mapping()
    Log.i("Spike", "${movies}") // List<Movie>
} catch(e: SpikeProviderException) {
    val error = e.errorResponse<TVMazeError>().suspend.mapping() // TVMazeError
    Log.e("Spike", "$e")
} catch (e: Exception) {
    Log.e("Spike", "$e")
}
```

Suspend mapping function are executed on `Dispatchers.Default`.

## TODO
- Testing.

## Author

Dario Pellegrini, pellegrini.dario.1303@gmail.com

## Credits
- [Volley](https://github.com/google/volley)
- [Moya for Swift](https://github.com/Moya/Moya)
- [Gson](https://github.com/google/gson)
