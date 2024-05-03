package com.bennyapi.common

import com.bennyapi.common.BennyFlowParameters.Options.Environment.PRODUCTION

data class BennyFlowParameters(
    /**
     * The organization ID. Prefixed with "org_".
     */
    val organizationId: String,
    val options: Options = Options(),
) {
    /**
     * Options to help developers with integrating
     *
     */
    data class Options(
        /**
         * If true Web View, request, etc. logging
         * wil be enabled to assist with debugging.
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
