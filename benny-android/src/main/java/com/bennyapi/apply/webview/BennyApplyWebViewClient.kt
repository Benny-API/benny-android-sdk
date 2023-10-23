package com.bennyapi.apply.webview

import android.content.Intent
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import com.bennyapi.benny.BuildConfig.VERSION

internal class BennyApplyWebViewClient(
    private val organizationId: String,
    private val clientSecret: String,
) : WebViewClient() {

    var externalId: String? = null

    override fun onPageFinished(view: WebView?, url: String?) {
        if (view == null || externalId == null) {
            return
        }
        val script =
            """
            try {
                window.addEventListener('message', console.log);
                window.postMessage({
                    type: 'BENNY_APPLY_INIT',
                    payload: {
                        organizationId: '$organizationId',
                        clientSecret: '$clientSecret',
                        externalId: '$externalId',
                        sdk: 'android',
                        platform: 'android',
                        version: '$VERSION'
                    }
                }, "*");
            } catch (err) {
                console.error(err);
            }
            """.trimIndent()

        view.loadUrl("javascript:(function f() {$script})()")
    }

    override fun shouldOverrideUrlLoading(view: WebView, request: WebResourceRequest): Boolean {
        view.context.startActivity(Intent(Intent.ACTION_VIEW, request.url))
        return true
    }
}
