package com.bennyapi.ebtbalancelink.result

import kotlinx.serialization.Serializable

@Serializable
data class LinkResult(
    val type: String = "LinkResultSuccess",
    val linkToken: String,
    val accountId: String,
    val accountHolder: AccountHolder,
)

@Serializable
data class AccountHolder(
    val name: String?,
    val address: String?,
    val balances: Balances,
    val lastTransactionDate: String?,
)

@Serializable
data class Balances(
    val snap: Int?,
    val cash: Int?,
)
