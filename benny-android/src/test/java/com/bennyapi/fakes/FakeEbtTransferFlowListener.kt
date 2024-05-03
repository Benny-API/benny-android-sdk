package com.bennyapi.fakes

import com.bennyapi.transfer.EbtTransferFlowListener
import java.time.Instant

internal class FakeEbtTransferFlowListener : EbtTransferFlowListener {
    var onExitCall = false
    var onBalanceResultCall: OnBalanceResultCall? = null
    var onLinkResultCall: OnLinkResultCall? = null
    var onTransferResultCall = false

    override fun onExit() {
        onExitCall = true
    }

    override fun onLinkResult(transferToken: String, expiration: Instant) {
        onLinkResultCall = OnLinkResultCall(transferToken, expiration)
    }

    internal data class OnLinkResultCall(val transferToken: String, val expiration: Instant)

    override fun onBalanceResult(balance: Int?, error: String?) {
        onBalanceResultCall = OnBalanceResultCall(balance, error)
    }

    internal data class OnBalanceResultCall(val balance: Int?, val error: String?)

    override fun onTransferResult() {
        onTransferResultCall = true
    }

    internal fun reset() {
        onExitCall = false
        onBalanceResultCall = null
        onTransferResultCall = false
        onLinkResultCall = null
    }
}
