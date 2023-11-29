package com.bennyapi.apply.webview

import android.content.ClipData.newPlainText
import android.content.ClipboardManager
import android.webkit.JavascriptInterface
import com.bennyapi.apply.BennyApplyListener

internal class BennyApplyJavascriptInterface(
    private val listener: BennyApplyListener,
    private val clipboardManager: ClipboardManager,
) {
    @JavascriptInterface
    fun copyToClipboard(label: String, text: String) {
        clipboardManager.setPrimaryClip(newPlainText(label, text))
    }

    @JavascriptInterface
    fun onExit() = listener.onExit()

    @JavascriptInterface
    fun onDataExchange(applicantDataId: String) =
        listener.onDataExchange(applicantDataId = applicantDataId)
}
