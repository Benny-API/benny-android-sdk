package com.bennyapi.apply.webview

import android.annotation.SuppressLint
import android.content.Context
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.webkit.WebView
import com.bennyapi.apply.BennyApplyListener
import com.bennyapi.apply.BennyApplyParameters
import com.bennyapi.apply.BennyApplyParameters.Options.Environment.PRODUCTION
import com.bennyapi.apply.BennyApplyParameters.Options.Environment.STAGING

private const val BENNY_APPLY_PRODUCTION_URL = "https://apply.bennyapi.com"
private const val BENNY_APPLY_STAGING_URL = "https://apply-dev.bennyapi.com"

@SuppressLint("SetJavaScriptEnabled", "ViewConstructor")
internal class BennyApplyWebView(
    context: Context,
    listener: BennyApplyListener,
    parameters: BennyApplyParameters,
) : WebView(context) {
    private val baseUrl: String
    private val bennyApplyWebViewClient: BennyApplyWebViewClient

    init {
        layoutParams = LayoutParams(MATCH_PARENT, MATCH_PARENT)
        bennyApplyWebViewClient = BennyApplyWebViewClient(
            organizationId = parameters.credentials.organizationId,
        )
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
            PRODUCTION -> BENNY_APPLY_PRODUCTION_URL
            STAGING -> BENNY_APPLY_STAGING_URL
        }
    }

    internal fun start(externalId: String) {
        bennyApplyWebViewClient.externalId = externalId
        loadUrl(baseUrl)
    }
}
