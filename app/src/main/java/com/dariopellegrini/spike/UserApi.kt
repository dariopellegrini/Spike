package it.comixtime.comixtime.api

import com.dariopellegrini.spike.TargetType
import com.dariopellegrini.spike.network.SpikeMethod

/**
 * Created by dariopellegrini on 28/07/17.
 */

data class SignUp(val username: String, val password: String, val nickname: String, val profileImage: String): UserTarget()
data class Login(val username: String, val password: String): UserTarget()

sealed class UserTarget: TargetType {
    override val baseURL: String
        get() = "http://www.comixtime.it/api/web/api/"

    override val path: String
        get() {
            return when(this) {
                is SignUp   -> "signUp"
                is Login    -> "logins"
            }
        }

    override val headers: Map<String, String>?
        get() = mapOf(
                "Content-Type" to "application/json; charset=utf-8")

    override val method: SpikeMethod
        get() {
            return when(this) {
                is SignUp   -> SpikeMethod.POST
                is Login    -> SpikeMethod.POST
            }
        }

    override val parameters: Map<String, Any>?
        get() {
            return when(this) {
                is SignUp   -> mapOf("username" to username, "password" to password, "nickname" to nickname, "profileImage" to profileImage)
                is Login    -> mapOf("email" to username, "password" to password)
            }
        }

/*  override val successClosure: ((String, Map<String, String>?) -> Any?)?
      get() = {
          result, headers ->
          when(this) {
              is SignUp -> null
              is Login -> {
                  var user = Gson().fromJson<User>(result)
                  if (headers != null && headers["authToken"] != null) {
                      user.token = headers["authToken"] as String
                  }
                  user
              }
          }
      }
   override val errorClose: ((String, Map<String, String>?) -> Any?)?
     get() = { errorResult, _ ->
         Gson().fromJson<BackendError>(errorResult)
     }

override val sampleResult: String?
     get() {
         return when(this) {
             is Login -> "{" +
                     "\"username\":\"info@dariopellegrini.com\"}"
             else -> null
         }
     }

 override val sampleHeaders: Map<String, String>?
     get() {
         return when(this) {
             is Login -> mapOf("token" to "aoisdusdaoidasum")
             else -> null
         }
     }
     */
}
