package com.bennyapi.transfer

import java.time.Instant

interface EbtTransferFlowListener {

    fun onExit()

    fun onLinkResult(transferToken: String, expiration: Instant)

    fun onBalanceResult(balance: Int?, error: String?)

    fun onTransferResult()
}
