package com.bennyapi.apply

import android.annotation.SuppressLint
import android.app.Activity
import android.widget.FrameLayout
import android.widget.FrameLayout.LayoutParams.MATCH_PARENT
import com.bennyapi.apply.webview.BennyApplyWebView

@SuppressLint("ViewConstructor")
class BennyApplyFlow(
    activity: Activity,
    listener: BennyApplyListener,
    parameters: BennyApplyParameters,
) : FrameLayout(activity) {
    private val webView = BennyApplyWebView(activity, listener, parameters)

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
