package com.bennyapi

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.viewinterop.AndroidView
import com.bennyapi.apply.BennyApplyFlow
import com.bennyapi.apply.BennyApplyListener
import com.bennyapi.apply.BennyApplyParameters
import com.bennyapi.apply.BennyApplyParameters.Credentials

private const val LOG_TAG = "Benny"

class MainActivity : ComponentActivity(), BennyApplyListener {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { BennyApplyFlowScreen(listener = this) }
    }

    override fun onExit() {
        Log.d(LOG_TAG, "On exit called")
    }

    override fun onDataExchange(applicantDataId: String) {
        Log.d(LOG_TAG, "On data exchange called: $applicantDataId")
    }
}

@Composable
private fun BennyApplyFlowScreen(listener: BennyApplyListener) {
    Surface {
        BennyApplyApplyFlow(listener = listener)
    }
}

@Composable
private fun BennyApplyApplyFlow(listener: BennyApplyListener) {
    AndroidView(
        factory = {
            BennyApplyFlow(
                context = it,
                listener = listener,
                parameters = BennyApplyParameters(
                    credentials = Credentials(
                        organizationId = "",
                        clientSecret = "",
                    ),
                ),
            )
        },
        update = { it.start(externalId = "") },
    )
}
