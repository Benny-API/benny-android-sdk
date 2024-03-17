package com.bennyapi.ebtbalancelink

import com.bennyapi.ebtbalancelink.EbtBalanceLinkFlowParameters.Options.Environment.PRODUCTION

data class EbtBalanceLinkFlowParameters(
    /**
     * The organization ID. Prefixed with "org_".
     */
    val organizationId: String,
    val options: Options = Options(),
) {
    /**
     * Options to help developers with integrating
     * the EBT Balance Flow into their Android app.
     */
    data class Options(
        /**
         * If true, "setWebContentsDebuggingEnabled" will be enabled
         * to allow debugging of the WebView in Chrome Developer Tools.
         */
        val isDebuggingEnabled: Boolean = false,

        /**
         * Which Benny API environment to use. By
         * default this is [PRODUCTION].
         */
        val environment: Environment = PRODUCTION,
    ) {
        enum class Environment {
            PRODUCTION,
            SANDBOX,
        }
    }
}
