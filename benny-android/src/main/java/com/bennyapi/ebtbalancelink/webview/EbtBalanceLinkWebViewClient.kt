package com.bennyapi.ebtbalancelink.webview

import android.content.Intent
import android.content.Intent.ACTION_VIEW
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient

internal class EbtBalanceLinkWebViewClient : WebViewClient() {
    override fun shouldOverrideUrlLoading(view: WebView, request: WebResourceRequest): Boolean {
        view.context.startActivity(Intent(ACTION_VIEW, request.url))
        return true
    }
}
