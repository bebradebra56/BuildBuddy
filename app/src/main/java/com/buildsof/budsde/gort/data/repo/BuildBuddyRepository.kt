package com.buildsof.budsde.gort.data.repo

import android.util.Log
import com.buildsof.budsde.gort.domain.model.BuildBuddyEntity
import com.buildsof.budsde.gort.domain.model.BuildBuddyParam
import com.buildsof.budsde.gort.presentation.app.BuildBuddyApplication.Companion.BUILD_BUDDY_MAIN_TAG
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.HttpSend
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.plugin
import io.ktor.client.request.accept
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonNull
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.buildJsonArray
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.serializer



private const val BUILD_BUDDY_MAIN = "https://builldbuddy.com/config.php"

class BuildBuddyRepository {


    private val buildBuddyKtorClient = HttpClient(CIO) {
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
                encodeDefaults = true
            })
        }
        install(HttpTimeout) {
            connectTimeoutMillis = 30000
            socketTimeoutMillis = 30000
            requestTimeoutMillis = 30000
        }

    }

    suspend fun buildBuddyGetClient(
        buildBuddyParam: BuildBuddyParam,
        buildBuddyConversion: MutableMap<String, Any>?
    ): BuildBuddyEntity? =
        withContext(Dispatchers.IO) {
            buildBuddyKtorClient.plugin(HttpSend).intercept { request ->
                Log.d(BUILD_BUDDY_MAIN_TAG, "Ktor: Intercept body ${request.body}")
                execute(request)
            }
            val buildBuddyJson = Json {
                ignoreUnknownKeys = true
                encodeDefaults = true
            }
            Log.d(
                BUILD_BUDDY_MAIN_TAG,
                "Ktor: conversation json: ${buildBuddyConversion.toString()}"
            )
            val buildBuddyBody = buildBuddyMergeToFlatJson(
                json = buildBuddyJson,
                param = buildBuddyParam,
                conversation = buildBuddyConversion
            )
            Log.d(
                BUILD_BUDDY_MAIN_TAG,
                "Ktor: request json: $buildBuddyBody"
            )
            return@withContext try {
                val response = buildBuddyKtorClient.post(BUILD_BUDDY_MAIN) {
                    contentType(ContentType.Application.Json) // обязательно JSON
                    accept(ContentType.Application.Json)
                    setBody(buildBuddyBody) // JsonObject
                }
                val code = response.status.value
                Log.d(BUILD_BUDDY_MAIN_TAG, "Ktor: Request status code: $code")
                if (code == 200) {
                    val rawBody = response.bodyAsText() // читаем ответ как текст
                    val buildBuddyEntity = Json { ignoreUnknownKeys = true }
                        .decodeFromString(BuildBuddyEntity.serializer(), rawBody)
                    Log.d(BUILD_BUDDY_MAIN_TAG, "Ktor: Get request success")
                    Log.d(BUILD_BUDDY_MAIN_TAG, "Ktor: $buildBuddyEntity")
                    buildBuddyEntity
                } else {
                    Log.d(BUILD_BUDDY_MAIN_TAG, "Ktor: Status code invalid, return null")
                    Log.d(BUILD_BUDDY_MAIN_TAG, "Ktor: ${response.body<String>()}")
                    null
                }

            } catch (e: Exception) {
                Log.d(BUILD_BUDDY_MAIN_TAG, "Ktor: Get request failed")
                Log.d(BUILD_BUDDY_MAIN_TAG, "Ktor: ${e.message}")
                null
            }
        }

    private inline fun <reified T> Json.buildBuddyEncodeToJsonObject(value: T): JsonObject =
        encodeToJsonElement(serializer(), value).jsonObject

    private inline fun <reified T> buildBuddyMergeToFlatJson(
        json: Json,
        param: T,
        conversation: Map<String, Any>?
    ): JsonObject {

        val paramJson = json.buildBuddyEncodeToJsonObject(param)

        return buildJsonObject {
            // поля из param
            paramJson.forEach { (key, value) ->
                put(key, value)
            }

            // динамические поля
            conversation?.forEach { (key, value) ->
                put(key, buildBuddyAnyToJsonElement(value))
            }
        }
    }

    private fun buildBuddyAnyToJsonElement(value: Any?): JsonElement {
        return when (value) {
            null -> JsonNull
            is String -> JsonPrimitive(value)
            is Number -> JsonPrimitive(value)
            is Boolean -> JsonPrimitive(value)
            is Map<*, *> -> buildJsonObject {
                value.forEach { (k, v) ->
                    if (k is String) {
                        put(k, buildBuddyAnyToJsonElement(v))
                    }
                }
            }
            is List<*> -> buildJsonArray {
                value.forEach {
                    add(buildBuddyAnyToJsonElement(it))
                }
            }
            else -> JsonPrimitive(value.toString())
        }
    }


}
