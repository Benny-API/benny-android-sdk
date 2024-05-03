package com.bennyapi.transfer.networking.result

import com.bennyapi.transfer.networking.globalJson
import com.bennyapi.transfer.networking.result.ClientApiResult.Failure.Http
import com.bennyapi.transfer.networking.result.ClientApiResult.Failure.Network
import com.bennyapi.transfer.networking.result.ClientApiResult.Success
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.request
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.http.isSuccess
import io.ktor.http.takeFrom

sealed class ClientApiResult<T, E> {
    data class Success<T, E>(val response: T) : ClientApiResult<T, E>()

    sealed class Failure<T, E> : ClientApiResult<T, E>() {
        data class Http<T, E>(val statusCode: HttpStatusCode, val error: E) : Failure<T, E>()

        data class Network<T, E>(val error: Throwable) : Failure<T, E>()
    }
}

@Suppress("TooGenericExceptionCaught")
suspend inline fun <reified T, reified E> HttpClient.safeRequestV2(
    method: HttpMethod,
    urlString: String?,
    block: HttpRequestBuilder.() -> Unit,
): ClientApiResult<T, E> {
    return try {
        val response =
            request {
                this.method = method
                urlString?.let { url.takeFrom(it) }
                block()
            }
        if (response.status.isSuccess()) {
            Success(response.body())
        } else {
            Http(
                statusCode = response.status,
                error = globalJson.decodeFromString(string = response.bodyAsText()),
            )
        }
    } catch (e: Throwable) {
        Network(e)
    }
}

fun <T, E> ClientApiResult<T, E>.successOrThrow(
    onServerError: ((e: E) -> Nothing),
    onNetworkError: ((e: Throwable) -> Nothing),
): T {
    return when (this) {
        is Success -> response
        is Http -> onServerError(error)
        is Network -> onNetworkError(error)
    }
}

suspend inline fun <reified T, reified E> HttpClient.safeGet(
    urlString: String? = null,
    block: HttpRequestBuilder.() -> Unit = {},
): ClientApiResult<T, E> = safeRequestV2(HttpMethod.Get, urlString, block)

suspend inline fun <reified T, reified E> HttpClient.safePost(
    urlString: String? = null,
    block: HttpRequestBuilder.() -> Unit,
): ClientApiResult<T, E> = safeRequestV2(HttpMethod.Post, urlString, block)
