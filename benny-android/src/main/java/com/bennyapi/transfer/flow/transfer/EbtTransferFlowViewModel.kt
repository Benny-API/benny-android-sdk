package com.bennyapi.transfer.flow.transfer

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
import com.bennyapi.transfer.flow.transfer.Event.Exit
import com.bennyapi.transfer.flow.transfer.Event.PinChange
import com.bennyapi.transfer.flow.transfer.Event.Transfer
import com.bennyapi.transfer.networking.EbtTransferClient
import com.bennyapi.transfer.networking.models.EbtTransferRequest
import com.bennyapi.transfer.networking.result.ClientApiResult.Failure.Http
import com.bennyapi.transfer.networking.result.ClientApiResult.Failure.Network
import com.bennyapi.transfer.networking.result.ClientApiResult.Success
import kotlinx.coroutines.flow.Flow

internal class EbtTransferViewModel(
    private val client: EbtTransferClient,
    private val stringStore: StringStore,
    private val listener: EbtTransferFlowListener,
    private val transferToken: String,
    private val organizationId: String,
    private val idempotencyKey: String,
    private val amountCents: Int,
) : MoleculeViewModel<Event, Model>() {

    @Composable
    override fun models(events: Flow<Event>): Model {
        return ebtTransferFlowPresenter(
            events = events,
            client = client,
            stringStore = stringStore,
            listener = listener,
            transferToken = transferToken,
            organizationId = organizationId,
            idempotencyKey = idempotencyKey,
            amountCents = amountCents,
        )
    }

    class Factory(
        private val client: EbtTransferClient,
        private val stringStore: StringStore,
        private val listener: EbtTransferFlowListener,
        private val transferToken: String,
        private val organizationId: String,
        private val idempotencyKey: String,
        private val amountCents: Int,
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(EbtTransferViewModel::class.java)) {
                return EbtTransferViewModel(
                    client = client,
                    stringStore = stringStore,
                    listener = listener,
                    transferToken = transferToken,
                    organizationId = organizationId,
                    idempotencyKey = idempotencyKey,
                    amountCents = amountCents,
                ) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}

@Composable
internal fun ebtTransferFlowPresenter(
    events: Flow<Event>,
    client: EbtTransferClient,
    stringStore: StringStore,
    listener: EbtTransferFlowListener,
    transferToken: String,
    organizationId: String,
    idempotencyKey: String,
    amountCents: Int,
): Model {
    var pin by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        events.collect { event ->
            when (event) {
                Transfer -> {
                    isLoading = true
                    val transferResult = client.transfer(
                        organizationId = organizationId,
                        request = EbtTransferRequest(
                            idempotencyKey = idempotencyKey,
                            amount = amountCents,
                            transferToken = transferToken,
                            pin = pin,
                        ),
                    )
                    isLoading = false

                    when (transferResult) {
                        is Http -> errorMessage = transferResult.error.message
                        is Network -> errorMessage = stringStore[R.string.network_error]
                        is Success -> listener.onTransferResult()
                    }
                }

                Exit -> listener.onExit()
                is PinChange -> pin = event.value
            }
        }
    }

    return Model(
        title = stringStore[R.string.approve_transfer],
        description = stringStore[R.string.enter_card_pin],
        primaryButtonText = stringStore[R.string.transfer],
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
    data object Transfer : Event()
    data object Exit : Event()
    data class PinChange(val value: String) : Event()
}
