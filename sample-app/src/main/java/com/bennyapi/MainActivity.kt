package com.bennyapi

import android.os.Bundle
import android.util.Log
import android.widget.LinearLayout
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import com.bennyapi.apply.BennyApplyFlow
import com.bennyapi.apply.BennyApplyListener
import com.bennyapi.apply.BennyApplyParameters

private const val LOG_TAG = "Benny"

class MainActivity : AppCompatActivity(), BennyApplyListener {

    private lateinit var flow: BennyApplyFlow

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main)
        flow = BennyApplyFlow(
            activity = this,
            listener = this,
            parameters = BennyApplyParameters(
                organizationId = "",
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
        flow.start(externalId = "")
    }

    override fun onExit() {
        Log.d(LOG_TAG, "On exit called")
    }

    override fun onDataExchange(applicantDataId: String) {
        Log.d(LOG_TAG, "On data exchange called: $applicantDataId")
    }
}
