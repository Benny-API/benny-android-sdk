package com.bennyapi.apply.webview

import android.webkit.JavascriptInterface
import com.bennyapi.apply.BennyApplyListener

internal class BennyApplyJavascriptInterface(private val listener: BennyApplyListener) {

    @JavascriptInterface
    fun onExit() = listener.onExit()

    @JavascriptInterface
    fun onDataExchange(applicantDataId: String) =
        listener.onDataExchange(applicantDataId = applicantDataId)
}
