package com.bennyapi

import android.app.Activity
import android.os.Bundle
import android.util.Log
import android.widget.LinearLayout
import com.bennyapi.apply.BennyApplyFlow
import com.bennyapi.apply.BennyApplyListener
import com.bennyapi.apply.BennyApplyParameters
import com.bennyapi.apply.BennyApplyParameters.Credentials

private const val LOG_TAG = "Benny"

class MainActivity : Activity(), BennyApplyListener {

    private lateinit var flow: BennyApplyFlow

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main)
        flow = BennyApplyFlow(
            context = applicationContext,
            listener = this,
            parameters = BennyApplyParameters(
                credentials = Credentials(
                    organizationId = "",
                    clientSecret = "",
                ),
            ),
        )
        findViewById<LinearLayout>(R.id.main_layout).addView(flow)
    }

    override fun onStart() {
        super.onStart()
        flow.start(externalId = "")
    }

    override fun onExit() {
        Log.d(LOG_TAG, "On exit called")
    }

    override fun onDataExchange(applicantDataId: String) {
        Log.d(LOG_TAG, "On data exchange called: $applicantDataId")
    }
}
