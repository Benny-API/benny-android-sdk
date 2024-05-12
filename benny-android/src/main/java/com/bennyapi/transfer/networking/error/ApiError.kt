package com.bennyapi.transfer.networking.error

import kotlinx.serialization.Serializable

interface ApiErrorCode

@Serializable
internal data class EbtTransferApiError(
    val code: EbtTransferApiErrorCode,
    val message: String,
)

internal enum class EbtTransferApiErrorCode : ApiErrorCode {
    ACCESS_NOT_GRANTED,
    EXISTING_IDEMPOTENCY_KEY,
    EXPIRED_LINK_TOKEN,
    EXPIRED_TRANSFER_TOKEN,
    INSUFFICIENT_FUNDS,
    INVALID_AMOUNT,
    INVALID_CARD_INFO,
    INVALID_LINK_TOKEN,
    INVALID_ORGANIZATION,
    INVALID_TRANSFER_TOKEN,
    INVALID_USER_ID,
    MAX_ATTEMPTS_EXCEEDED,
    PIN_TIMEOUT,
    TRANSFER_FAILED,
}
