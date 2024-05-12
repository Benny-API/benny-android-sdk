package com.bennyapi.transfer.flow.balance

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.bennyapi.android.R
import com.bennyapi.transfer.EbtTransferFlowListener
import com.bennyapi.transfer.common.PIN_LENGTH
import com.bennyapi.transfer.core.MoleculeViewModel
import com.bennyapi.transfer.core.StringStore
import com.bennyapi.transfer.flow.balance.Event.CheckBalance
import com.bennyapi.transfer.flow.balance.Event.Exit
import com.bennyapi.transfer.flow.balance.Event.PinChange
import com.bennyapi.transfer.networking.EbtTransferClient
import com.bennyapi.transfer.networking.models.CheckBalanceRequest
import com.bennyapi.transfer.networking.result.ClientApiResult.Failure.Http
import com.bennyapi.transfer.networking.result.ClientApiResult.Failure.Network
import com.bennyapi.transfer.networking.result.ClientApiResult.Success
import kotlinx.coroutines.flow.Flow

internal class EbtTransferBalanceFlowViewModel(
    private val client: EbtTransferClient,
    private val stringStore: StringStore,
    private val listener: EbtTransferFlowListener,
    private val transferToken: String,
    private val organizationId: String,
) : MoleculeViewModel<Event, Model>() {

    @Composable
    override fun models(events: Flow<Event>): Model {
        return ebtTransferBalanceFlowPresenter(
            events = events,
            client = client,
            stringStore = stringStore,
            listener = listener,
            transferToken = transferToken,
            organizationId = organizationId,
        )
    }

    class Factory(
        private val client: EbtTransferClient,
        private val stringStore: StringStore,
        private val listener: EbtTransferFlowListener,
        private val transferToken: String,
        private val organizationId: String,
    ) : ViewModelProvider.Factory {

        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(EbtTransferBalanceFlowViewModel::class.java)) {
                return EbtTransferBalanceFlowViewModel(
                    client = client,
                    stringStore = stringStore,
                    listener = listener,
                    transferToken = transferToken,
                    organizationId = organizationId,
                ) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}

@Composable
internal fun ebtTransferBalanceFlowPresenter(
    events: Flow<Event>,
    client: EbtTransferClient,
    stringStore: StringStore,
    listener: EbtTransferFlowListener,
    transferToken: String,
    organizationId: String,
): Model {
    var pin by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        events.collect { event ->
            when (event) {
                CheckBalance -> {
                    isLoading = true
                    val checkBalanceResult = client.checkBalance(
                        organizationId = organizationId,
                        request = CheckBalanceRequest(transferToken = transferToken, pin = pin),
                    )
                    isLoading = false

                    errorMessage = when (checkBalanceResult) {
                        is Http -> checkBalanceResult.error.message
                        is Network -> stringStore[R.string.network_error]
                        else -> null
                    }
                    val balance = (checkBalanceResult as? Success)?.response?.balance
                    listener.onBalanceResult(balance = balance, error = errorMessage)
                }

                Exit -> listener.onExit()
                is PinChange -> pin = event.value
            }
        }
    }

    return Model(
        title = stringStore[R.string.check_balance],
        description = stringStore[R.string.enter_card_pin],
        primaryButtonText = stringStore[R.string.check_balance],
        isPrimaryButtonEnabled = pin.length == PIN_LENGTH && !isLoading,
        isLoading = isLoading,
        errorMessage = errorMessage,
    )
}

internal data class Model(
    val title: String,
    val description: String,
    val primaryButtonText: String,
    val isPrimaryButtonEnabled: Boolean,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
)

internal sealed class Event {
    data object CheckBalance : Event()
    data object Exit : Event()
    data class PinChange(val value: String) : Event()
}
