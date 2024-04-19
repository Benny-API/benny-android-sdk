package com.bennyapi.ebtbalancelink

import com.bennyapi.ebtbalancelink.result.LinkResult

interface EbtBalanceLinkFlowListener {

    fun onExit()

    /**
     * @deprecated use [onLinkResult] as this method will
     * be removed in a future released.
     */
    fun onLinkSuccess(linkToken: String) {}

    fun onLinkResult(result: LinkResult)
}
