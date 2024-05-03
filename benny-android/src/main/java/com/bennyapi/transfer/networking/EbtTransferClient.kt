package com.bennyapi.transfer.networking

import com.bennyapi.transfer.networking.error.EbtTransferApiError
import com.bennyapi.transfer.networking.models.CheckBalanceRequest
import com.bennyapi.transfer.networking.models.CheckBalanceResponse
import com.bennyapi.transfer.networking.models.EbtTransferRequest
import com.bennyapi.transfer.networking.models.ExchangeLinkTokenRequest
import com.bennyapi.transfer.networking.models.ExchangeLinkTokenResponse
import com.bennyapi.transfer.networking.result.ClientApiResult
import com.bennyapi.transfer.networking.result.safePost
import io.ktor.client.HttpClient
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.request.header
import io.ktor.client.request.setBody
import io.ktor.http.ContentType.Application.Json
import io.ktor.http.contentType

internal interface EbtTransferClient {
    suspend fun checkBalance(
        organizationId: String,
        request: CheckBalanceRequest,
    ): ClientApiResult<CheckBalanceResponse, EbtTransferApiError>

    suspend fun exchangeLinkToken(
        organizationId: String,
        request: ExchangeLinkTokenRequest,
    ): ClientApiResult<ExchangeLinkTokenResponse, EbtTransferApiError>

    suspend fun transfer(
        organizationId: String,
        request: EbtTransferRequest,
    ): ClientApiResult<Unit, EbtTransferApiError>
}

internal class RealEbtTransferClient(
    httpClient: HttpClient,
    private val isSandbox: Boolean,
) : EbtTransferClient {
    private val client = httpClient.config {
        defaultRequest {
            val baseUrl =
                if (isSandbox) "https://api-sandbox.bennyapi.com" else "https://api-production.bennyapi.com"
            url("$baseUrl/v1/ebt/")
            contentType(Json)
        }
    }

    override suspend fun checkBalance(
        organizationId: String,
        request: CheckBalanceRequest,
    ): ClientApiResult<CheckBalanceResponse, EbtTransferApiError> {
        return client.safePost<CheckBalanceResponse, EbtTransferApiError>("transfer/check-balance") {
            header("benny-organization", organizationId)
            setBody(request)
        }
    }

    override suspend fun exchangeLinkToken(
        organizationId: String,
        request: ExchangeLinkTokenRequest,
    ): ClientApiResult<ExchangeLinkTokenResponse, EbtTransferApiError> {
        return client.safePost<ExchangeLinkTokenResponse, EbtTransferApiError>("transfer/link/exchange") {
            header("benny-organization", organizationId)
            setBody(request)
        }
    }

    override suspend fun transfer(
        organizationId: String,
        request: EbtTransferRequest,
    ): ClientApiResult<Unit, EbtTransferApiError> {
        return client.safePost<Unit, EbtTransferApiError>("transfer") {
            header("benny-organization", organizationId)
            setBody(request)
        }
    }
}
