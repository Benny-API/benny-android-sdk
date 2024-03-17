package com.bennyapi.ebtbalancelink

interface EbtBalanceLinkFlowListener {

    fun onExit()

    fun onLinkSuccess(linkToken: String)
}
