package com.bennyapi.ebtbalancelink.webview

import android.content.ClipData.newPlainText
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.content.Intent.ACTION_VIEW
import android.net.Uri.parse
import android.webkit.JavascriptInterface
import androidx.core.content.ContextCompat.startActivity
import com.bennyapi.ebtbalancelink.EbtBalanceLinkFlowListener
import kotlinx.serialization.json.Json

private val json = Json {
    ignoreUnknownKeys = true
    encodeDefaults = true
}

internal class EbtBalanceLinkJavascriptInterface(
    private val listener: EbtBalanceLinkFlowListener,
    private val clipboardManager: ClipboardManager,
    private val context: Context,
) {
    @JavascriptInterface
    fun onCopyToClipboard(label: String, text: String) {
        clipboardManager.setPrimaryClip(newPlainText(label, text))
    }

    @JavascriptInterface
    fun onExit() = listener.onExit()

    @JavascriptInterface
    fun onLinkResult(resultJsonString: String) = listener.onLinkResult(json.decodeFromString(resultJsonString))

    /**
     * @deprecated use [onLinkResult].
     */
    @JavascriptInterface
    fun onLinkSuccess(linkToken: String) = listener.onLinkSuccess(linkToken = linkToken)

    @JavascriptInterface
    fun onOpenUrl(url: String) {
        startActivity(context, Intent(ACTION_VIEW, parse(url)), null)
    }
}
