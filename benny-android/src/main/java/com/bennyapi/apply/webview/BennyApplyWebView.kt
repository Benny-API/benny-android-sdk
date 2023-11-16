package com.bennyapi.apply.webview

import android.annotation.SuppressLint
import android.content.Context
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.webkit.WebView
import com.bennyapi.apply.BennyApplyListener
import com.bennyapi.apply.BennyApplyParameters
import com.bennyapi.apply.BennyApplyParameters.Options.Environment.PRODUCTION
import com.bennyapi.apply.BennyApplyParameters.Options.Environment.STAGING

@SuppressLint("SetJavaScriptEnabled", "ViewConstructor")
internal class BennyApplyWebView(
    context: Context,
    listener: BennyApplyListener,
    parameters: BennyApplyParameters,
) : WebView(context) {
    private val baseUrl: String
    private val organizationId: String
    private val bennyApplyWebViewClient: BennyApplyWebViewClient

    init {
        layoutParams = LayoutParams(MATCH_PARENT, MATCH_PARENT)
        bennyApplyWebViewClient = BennyApplyWebViewClient()
        webViewClient = bennyApplyWebViewClient
        setWebContentsDebuggingEnabled(parameters.options.isDebuggingEnabled)
        isVerticalScrollBarEnabled = false
        overScrollMode = OVER_SCROLL_NEVER

        settings.apply {
            javaScriptEnabled = true
            domStorageEnabled = true
        }

        addJavascriptInterface(BennyApplyJavascriptInterface(listener), "MobileSdk")

        baseUrl = when (parameters.options.environment) {
            PRODUCTION -> "https://apply.bennyapi.com"
            STAGING -> "https://apply-staging.bennyapi.com"
        }
        organizationId = parameters.organizationId
    }

    internal fun start(externalId: String) {
        loadUrl("$baseUrl$organizationId&externalId=$externalId&isWebView=true")
    }
}
