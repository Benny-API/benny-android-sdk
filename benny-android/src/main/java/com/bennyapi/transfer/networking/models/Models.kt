package com.bennyapi.transfer.networking.models

import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable

@Serializable
internal data class ExchangeLinkTokenRequest(
    val temporaryLink: String,
    val accountNumber: String,
    val pin: String,
)

@Serializable
internal data class ExchangeLinkTokenResponse(val transferToken: String, val expiration: Instant)

@Serializable
internal data class CheckBalanceRequest(
    val transferToken: String,
    val pin: String,
)

@Serializable
internal data class CheckBalanceResponse(
    val balance: Int? = null,
    val numAttemptsLeft: Int? = null,
)

@Serializable
internal data class EbtTransferRequest(
    val idempotencyKey: String,
    val amount: Int,
    val transferToken: String,
    val pin: String,
)
