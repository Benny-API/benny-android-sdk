package com.bennyapi

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement.Center
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.bennyapi.EbtTransferFlow.BALANCE
import com.bennyapi.EbtTransferFlow.LINK
import com.bennyapi.EbtTransferFlow.TRANSFER
import com.bennyapi.common.BennyFlowParameters
import com.bennyapi.common.BennyFlowParameters.Options
import com.bennyapi.common.BennyFlowParameters.Options.Environment.SANDBOX
import com.bennyapi.transfer.EbtTransferFlowListener
import com.bennyapi.transfer.flow.balance.EbtTransferBalanceFlow
import com.bennyapi.transfer.flow.link.EbtTransferLinkCardFlow
import com.bennyapi.transfer.flow.transfer.EbtTransferFlow
import com.bennyapi.ui.theme.BennyApplicationTheme
import com.bennyapi.ui.theme.White
import kotlinx.coroutines.launch
import java.time.Instant
import java.util.UUID

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            BennyApplicationTheme {
                MainScreen()
            }
        }
    }
}

private enum class EbtTransferFlow { BALANCE, LINK, TRANSFER }

@Composable
fun MainScreen() {
    val transferFlow = remember { mutableStateOf(LINK) }
    val isSheetVisible = remember { mutableStateOf(false) }
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    val listener = object : EbtTransferFlowListener {
        override fun onExit() {
            isSheetVisible.value = false
        }

        override fun onLinkResult(transferToken: String, expiration: Instant) {
            isSheetVisible.value = false
        }

        override fun onBalanceResult(balance: Int?, error: String?) {
            if (balance != null) {
                isSheetVisible.value = false
            }
        }

        override fun onTransferResult() {
            isSheetVisible.value = false
        }
    }

    val flowParameters = BennyFlowParameters(
        organizationId = "org_wup29bz683g8habsxvazvyz1",
        options = Options(
            isDebuggingEnabled = true,
            environment = SANDBOX,
        ),
    )

    Scaffold(modifier = Modifier.fillMaxSize()) {
        Column(
            verticalArrangement = Center,
            modifier = Modifier
                .padding(it)
                .fillMaxSize(),
        ) {
            LaunchButton(
                text = "Launch EBT Balance Link",
                onLaunch = {
                    context.startActivity(Intent(context, EbtBalanceLinkActivity::class.java))
                },
            )
            LaunchButton(
                text = "Launch EBT Transfer Link",
                onLaunch = {
                    scope.launch {
                        transferFlow.value = LINK
                        isSheetVisible.value = true
                    }
                },
            )
            LaunchButton(
                text = "Launch EBT Transfer Balance Check",
                onLaunch = {
                    scope.launch {
                        transferFlow.value = BALANCE
                        isSheetVisible.value = true
                    }
                },
            )
            LaunchButton(
                text = "Launch EBT Transfer",
                onLaunch = {
                    scope.launch {
                        transferFlow.value = TRANSFER
                        isSheetVisible.value = true
                    }
                },
            )
        }
    }

    if (isSheetVisible.value) {
        BottomSheet(isSheetVisible = isSheetVisible) {
            when (transferFlow.value) {
                LINK -> EbtTransferLinkCardFlow(
                    temporaryLink = "temp_123",
                    parameters = flowParameters,
                    listener = listener,
                )

                BALANCE -> EbtTransferBalanceFlow(
                    transferToken = "tkn_123",
                    parameters = flowParameters,
                    listener = listener,
                )

                TRANSFER -> EbtTransferFlow(
                    transferToken = "tkn_123",
                    idempotencyKey = UUID.randomUUID().toString(),
                    amountCents = 500,
                    parameters = flowParameters,
                    listener = listener,
                )
            }
        }
    }
}

@Composable
fun LaunchButton(
    text: String,
    modifier: Modifier = Modifier,
    onLaunch: () -> Unit,
) {
    Button(
        shape = RoundedCornerShape(4.dp),
        modifier = modifier
            .padding(16.dp)
            .height(52.dp)
            .fillMaxWidth(),
        onClick = { onLaunch() },
    ) {
        Text(text)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BottomSheet(
    isSheetVisible: MutableState<Boolean>,
    content: @Composable ColumnScope.() -> Unit,
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val scope = rememberCoroutineScope()
    ModalBottomSheet(
        sheetState = sheetState,
        onDismissRequest = {
            scope.launch { sheetState.hide() }
            isSheetVisible.value = false
        },
        containerColor = White,
    ) {
        content()
    }
}
