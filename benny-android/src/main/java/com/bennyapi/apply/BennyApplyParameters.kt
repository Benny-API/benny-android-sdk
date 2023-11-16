package com.bennyapi.apply

import com.bennyapi.apply.BennyApplyParameters.Options.Environment.PRODUCTION

data class BennyApplyParameters(
    /**
     * The organization ID. Prefixed with "org_".
     */
    val organizationId: String,
    val options: Options = Options(),
) {
    /**
     * Options to help developers with integrating
     * Benny Apply into their Android app.
     */
    data class Options(
        /**
         * If true, [setWebContentsDebuggingEnabled] will be enabled
         * to allow debugging of the WebView in Chrome Developer Tools.
         */
        val isDebuggingEnabled: Boolean = false,

        /**
         * Which Benny API environment to use. By
         * default this is [PRODUCTION]. Client
         * secrets and organization IDs are scoped by
         * API environment.
         */
        val environment: Environment = PRODUCTION,
    ) {
        enum class Environment {
            PRODUCTION,
            STAGING,
        }
    }
}
