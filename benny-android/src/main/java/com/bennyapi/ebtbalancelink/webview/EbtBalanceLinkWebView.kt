package com.bennyapi.ebtbalancelink.webview

import android.annotation.SuppressLint
import android.content.ClipboardManager
import android.content.Context
import android.content.Context.CLIPBOARD_SERVICE
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.webkit.WebView
import com.bennyapi.ebtbalancelink.EbtBalanceLinkFlowListener
import com.bennyapi.ebtbalancelink.EbtBalanceLinkFlowParameters
import com.bennyapi.ebtbalancelink.EbtBalanceLinkFlowParameters.Options.Environment.PRODUCTION
import com.bennyapi.ebtbalancelink.EbtBalanceLinkFlowParameters.Options.Environment.SANDBOX

@SuppressLint("SetJavaScriptEnabled", "ViewConstructor")
internal class EbtBalanceLinkWebView(
    activityContext: Context,
    listener: EbtBalanceLinkFlowListener,
    parameters: EbtBalanceLinkFlowParameters,
) : WebView(activityContext) {
    private val baseUrl: String
    private val clipboard = activityContext.getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
    private val organizationId: String
    private val ebtBalanceFlowWebViewClient: EbtBalanceLinkWebViewClient

    init {
        layoutParams = LayoutParams(MATCH_PARENT, MATCH_PARENT)
        ebtBalanceFlowWebViewClient = EbtBalanceLinkWebViewClient()
        webViewClient = ebtBalanceFlowWebViewClient
        setWebContentsDebuggingEnabled(parameters.options.isDebuggingEnabled)
        isVerticalScrollBarEnabled = false
        overScrollMode = OVER_SCROLL_NEVER

        settings.apply {
            javaScriptEnabled = true
            domStorageEnabled = true
        }

        addJavascriptInterface(
            EbtBalanceLinkJavascriptInterface(
                listener = listener,
                clipboardManager = clipboard,
                context = activityContext,
            ),
            "MobileSdkAndroid",
        )

        baseUrl = when (parameters.options.environment) {
            PRODUCTION -> "https://ebtbalance.bennyapi.com"
            SANDBOX -> "https://ebtbalance-sandbox.bennyapi.com"
        }
        organizationId = parameters.organizationId
    }

    internal fun start(temporaryLink: String) {
        loadUrl("$baseUrl?organizationId=$organizationId&temporaryLink=$temporaryLink")
    }
}
