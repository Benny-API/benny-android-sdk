package com.bennyapi

import android.os.Bundle
import android.util.Log
import android.widget.LinearLayout
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import com.bennyapi.ebtbalancelink.EbtBalanceLinkFlow
import com.bennyapi.ebtbalancelink.EbtBalanceLinkFlowListener
import com.bennyapi.ebtbalancelink.EbtBalanceLinkFlowParameters
import com.bennyapi.ebtbalancelink.EbtBalanceLinkFlowParameters.Options
import com.bennyapi.ebtbalancelink.EbtBalanceLinkFlowParameters.Options.Environment.SANDBOX

private const val LOG_TAG = "Benny"

class MainActivity : AppCompatActivity(), EbtBalanceLinkFlowListener {

    private lateinit var flow: EbtBalanceLinkFlow

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main)
        flow = EbtBalanceLinkFlow(
            activity = this,
            listener = this,
            parameters = EbtBalanceLinkFlowParameters(
                organizationId = "org_wup29bz683g8habsxvazvyz1",
                options = Options(environment = SANDBOX),
            ),
        )
        findViewById<LinearLayout>(R.id.main_layout).addView(flow)

        onBackPressedDispatcher.addCallback(
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    flow.goBack()
                }
            },
        )
    }

    override fun onStart() {
        super.onStart()
        flow.start(temporaryLink = "temp_clr0vujq9000108l66odc7fxv")
    }

    override fun onExit() {
        Log.d(LOG_TAG, "onExit called.")
    }

    override fun onLinkSuccess(linkToken: String) {
        Log.d(LOG_TAG, "onLinkSuccess called: $linkToken.")
    }
}
