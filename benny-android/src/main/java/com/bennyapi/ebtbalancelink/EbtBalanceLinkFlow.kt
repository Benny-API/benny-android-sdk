package com.bennyapi.ebtbalancelink

import android.annotation.SuppressLint
import android.app.Activity
import android.widget.FrameLayout
import android.widget.FrameLayout.LayoutParams.MATCH_PARENT
import com.bennyapi.common.BennyFlowParameters
import com.bennyapi.ebtbalancelink.webview.EbtBalanceLinkWebView

@SuppressLint("ViewConstructor")
class EbtBalanceLinkFlow(
    activity: Activity,
    listener: EbtBalanceLinkFlowListener,
    parameters: BennyFlowParameters,
) : FrameLayout(activity) {
    private val webView = EbtBalanceLinkWebView(activity, listener, parameters)

    init {
        layoutParams = LayoutParams(MATCH_PARENT, MATCH_PARENT)
        addView(webView)
    }

    /**
     * Starts the EBT Balance Link flow
     * with the unique [temporaryLink].
     */
    fun start(temporaryLink: String) {
        webView.start(temporaryLink = temporaryLink)
    }

    /**
     * Go's back in the EBT Balance Link flow,
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
