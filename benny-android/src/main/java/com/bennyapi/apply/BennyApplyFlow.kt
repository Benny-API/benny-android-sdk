package com.bennyapi.apply

import android.content.Context
import android.widget.FrameLayout
import android.widget.FrameLayout.LayoutParams.MATCH_PARENT
import com.bennyapi.apply.webview.BennyApplyWebView

class BennyApplyFlow(
    context: Context,
    listener: BennyApplyListener?,
    parameters: BennyApplyParameters?,
) : FrameLayout(context) {
    private val webView = BennyApplyWebView(context, listener!!, parameters!!)

    /**
     * Default constructor to enable view development or snapshot testing
     */
    constructor(context: Context) : this(context, null, null)

    init {
        layoutParams = LayoutParams(MATCH_PARENT, MATCH_PARENT)
        addView(webView)
    }

    /**
     * Starts the Benny Apply flow
     * with the unique [externalId].
     */
    fun start(externalId: String) {
        webView.start(externalId = externalId)
    }

    /**
     * Go's back in the Benny Apply flow,
     * returning true if the flow went
     * back, false if it could not.
     */
    fun goBack(): Boolean {
        return if (webView.canGoBack()) {
            webView.goBack()
            true
        } else {
            false
        }
    }
}
