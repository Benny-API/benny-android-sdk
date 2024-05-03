package com.bennyapi.fakes

import com.bennyapi.transfer.networking.EbtTransferClient
import com.bennyapi.transfer.networking.error.EbtTransferApiError
import com.bennyapi.transfer.networking.models.CheckBalanceRequest
import com.bennyapi.transfer.networking.models.CheckBalanceResponse
import com.bennyapi.transfer.networking.models.EbtTransferRequest
import com.bennyapi.transfer.networking.models.ExchangeLinkTokenRequest
import com.bennyapi.transfer.networking.models.ExchangeLinkTokenResponse
import com.bennyapi.transfer.networking.result.ClientApiResult

internal class FakeEbtTransferClient : EbtTransferClient {
    internal var checkBalanceResult: ClientApiResult<CheckBalanceResponse, EbtTransferApiError>? =
        null
    internal var exchangeLinkTokenResult: ClientApiResult<ExchangeLinkTokenResponse, EbtTransferApiError>? =
        null
    internal var transferResult: ClientApiResult<Unit, EbtTransferApiError>? = null

    override suspend fun checkBalance(
        organizationId: String,
        request: CheckBalanceRequest,
    ) = checkBalanceResult!!

    override suspend fun exchangeLinkToken(
        organizationId: String,
        request: ExchangeLinkTokenRequest,
    ) = exchangeLinkTokenResult!!

    override suspend fun transfer(
        organizationId: String,
        request: EbtTransferRequest,
    ) = transferResult!!

    fun reset() {
        checkBalanceResult = null
        exchangeLinkTokenResult = null
        transferResult = null
    }
}
