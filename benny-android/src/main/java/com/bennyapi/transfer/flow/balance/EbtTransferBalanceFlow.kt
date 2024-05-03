package com.bennyapi.transfer.flow.balance

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import com.bennyapi.common.BennyFlowParameters
import com.bennyapi.common.BennyFlowParameters.Options.Environment.SANDBOX
import com.bennyapi.transfer.EbtTransferFlowListener
import com.bennyapi.transfer.core.RealStringStore
import com.bennyapi.transfer.flow.balance.EbtTransferBalanceFlowViewModel.Factory
import com.bennyapi.transfer.flow.balance.Event.CheckBalance
import com.bennyapi.transfer.flow.balance.Event.Exit
import com.bennyapi.transfer.flow.balance.Event.PinChange
import com.bennyapi.transfer.networking.HttpClientFactory.create
import com.bennyapi.transfer.networking.RealEbtTransferClient
import com.bennyapi.transfer.ui.EbtTransferFlowScaffold
import com.bennyapi.transfer.ui.PinEntry

@Composable
fun EbtTransferBalanceFlow(
    transferToken: String,
    parameters: BennyFlowParameters,
    listener: EbtTransferFlowListener,
) {
    EbtTransferCheckBalanceFlowInternal(transferToken, parameters, listener)
}

@Composable
internal fun EbtTransferCheckBalanceFlowInternal(
    transferToken: String,
    parameters: BennyFlowParameters,
    listener: EbtTransferFlowListener,
    viewModel: EbtTransferBalanceFlowViewModel = viewModel(
        factory = Factory(
            client = RealEbtTransferClient(
                httpClient = create(),
                isSandbox = parameters.options.environment === SANDBOX,
            ),
            stringStore = RealStringStore(LocalContext.current),
            listener = listener,
            transferToken = transferToken,
            organizationId = parameters.organizationId,
        ),
    ),
) {
    val model by viewModel.models.collectAsState()
    val onEvent = viewModel::take
    EbtTransferFlowScaffold(
        title = model.title,
        description = model.description,
        primaryButtonText = model.primaryButtonText,
        onPrimaryButtonPress = { onEvent(CheckBalance) },
        isPrimaryButtonEnabled = model.isPrimaryButtonEnabled,
        onExitPress = { onEvent(Exit) },
        entryComponent = {
            PinEntry(
                onPinChange = { onEvent(PinChange(it)) },
                isLoading = model.isLoading,
                error = model.errorMessage,
            )
        },
    )
}
