package com.bennyapi.apply

interface BennyApplyListener {

    fun onExit()

    fun onDataExchange(applicantDataId: String)
}
