package com.bennyapi.transfer.flow.link

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import com.bennyapi.common.BennyFlowParameters
import com.bennyapi.common.BennyFlowParameters.Options.Environment.SANDBOX
import com.bennyapi.transfer.EbtTransferFlowListener
import com.bennyapi.transfer.core.RealStringStore
import com.bennyapi.transfer.flow.link.EbtTransferLinkCardFlowViewModel.Factory
import com.bennyapi.transfer.flow.link.Event.AddCard
import com.bennyapi.transfer.flow.link.Event.CardNumberChange
import com.bennyapi.transfer.flow.link.Event.ConfirmPin
import com.bennyapi.transfer.flow.link.Event.EditCard
import com.bennyapi.transfer.flow.link.Event.Exit
import com.bennyapi.transfer.flow.link.Event.PinChange
import com.bennyapi.transfer.flow.link.Model.CardEntry
import com.bennyapi.transfer.flow.link.Model.PinEntry
import com.bennyapi.transfer.networking.HttpClientFactory.create
import com.bennyapi.transfer.networking.RealEbtTransferClient
import com.bennyapi.transfer.ui.CardNumberEntry
import com.bennyapi.transfer.ui.EbtTransferFlowScaffold
import com.bennyapi.transfer.ui.PinEntry

@Composable
fun EbtTransferLinkCardFlow(
    temporaryLink: String,
    parameters: BennyFlowParameters,
    listener: EbtTransferFlowListener,
) {
    EbtTransferLinkCardFlowInternal(temporaryLink, parameters, listener)
}

@Composable
internal fun EbtTransferLinkCardFlowInternal(
    temporaryLink: String,
    parameters: BennyFlowParameters,
    listener: EbtTransferFlowListener,
    viewModel: EbtTransferLinkCardFlowViewModel = viewModel(
        factory = Factory(
            client = RealEbtTransferClient(
                httpClient = create(),
                isSandbox = parameters.options.environment === SANDBOX,
            ),
            stringStore = RealStringStore(LocalContext.current),
            listener = listener,
            temporaryLink = temporaryLink,
            organizationId = parameters.organizationId,
        ),
    ),
) {
    val modelState by viewModel.models.collectAsState()
    val onEvent = viewModel::take
    when (val model = modelState) {
        is CardEntry -> EbtTransferFlowScaffold(
            title = model.title,
            description = model.description,
            primaryButtonText = model.primaryButtonText,
            onPrimaryButtonPress = { onEvent(AddCard) },
            isPrimaryButtonEnabled = model.isPrimaryButtonEnabled,
            onExitPress = { onEvent(Exit) },
            entryComponent = {
                CardNumberEntry(onCardNumberChange = { onEvent(CardNumberChange(it)) })
            },
        )

        is PinEntry -> EbtTransferFlowScaffold(
            title = model.title,
            description = model.description,
            primaryButtonText = model.primaryButtonText,
            onPrimaryButtonPress = { onEvent(ConfirmPin) },
            isPrimaryButtonEnabled = model.isPrimaryButtonEnabled,
            secondaryButtonText = model.secondaryButtonText,
            isSecondaryButtonEnabled = model.isSecondaryButtonEnabled,
            onSecondaryButtonPress = { onEvent(EditCard) },
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
}
