package com.bennyapi.transfer.flow.link

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
import com.bennyapi.transfer.common.CARD_MIN_LENGTH
import com.bennyapi.transfer.common.PIN_LENGTH
import com.bennyapi.transfer.core.MoleculeViewModel
import com.bennyapi.transfer.core.StringStore
import com.bennyapi.transfer.flow.link.Event.AddCard
import com.bennyapi.transfer.flow.link.Event.CardNumberChange
import com.bennyapi.transfer.flow.link.Event.ConfirmPin
import com.bennyapi.transfer.flow.link.Event.EditCard
import com.bennyapi.transfer.flow.link.Event.Exit
import com.bennyapi.transfer.flow.link.Event.PinChange
import com.bennyapi.transfer.flow.link.Model.CardEntry
import com.bennyapi.transfer.flow.link.Model.PinEntry
import com.bennyapi.transfer.networking.EbtTransferClient
import com.bennyapi.transfer.networking.models.ExchangeLinkTokenRequest
import com.bennyapi.transfer.networking.result.ClientApiResult.Failure.Http
import com.bennyapi.transfer.networking.result.ClientApiResult.Failure.Network
import com.bennyapi.transfer.networking.result.ClientApiResult.Success
import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.toJavaInstant

internal class EbtTransferLinkCardFlowViewModel(
    private val client: EbtTransferClient,
    private val stringStore: StringStore,
    private val listener: EbtTransferFlowListener,
    private val temporaryLink: String,
    private val organizationId: String,
) : MoleculeViewModel<Event, Model>() {

    @Composable
    override fun models(events: Flow<Event>): Model {
        return ebtTransferLinkCardFlowPresenter(
            events = events,
            client = client,
            stringStore = stringStore,
            listener = listener,
            temporaryLink = temporaryLink,
            organizationId = organizationId,
        )
    }

    class Factory(
        private val client: EbtTransferClient,
        private val stringStore: StringStore,
        private val listener: EbtTransferFlowListener,
        private val temporaryLink: String,
        private val organizationId: String,
    ) : ViewModelProvider.Factory {

        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(EbtTransferLinkCardFlowViewModel::class.java)) {
                return EbtTransferLinkCardFlowViewModel(
                    client = client,
                    stringStore = stringStore,
                    listener = listener,
                    temporaryLink = temporaryLink,
                    organizationId = organizationId,
                ) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}

@Composable
internal fun ebtTransferLinkCardFlowPresenter(
    events: Flow<Event>,
    client: EbtTransferClient,
    stringStore: StringStore,
    listener: EbtTransferFlowListener,
    temporaryLink: String,
    organizationId: String,
): Model {
    var cardNumber by remember { mutableStateOf("") }
    var isPinEntryStep by remember { mutableStateOf(false) }
    var pin by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        events.collect { event ->
            when (event) {
                AddCard -> {
                    isPinEntryStep = true
                }

                ConfirmPin -> {
                    isLoading = true
                    val exchangeResult = client.exchangeLinkToken(
                        organizationId = organizationId,
                        request = ExchangeLinkTokenRequest(
                            temporaryLink = temporaryLink,
                            accountNumber = cardNumber,
                            pin = pin,
                        ),
                    )
                    isLoading = false

                    when (exchangeResult) {
                        is Http -> errorMessage = exchangeResult.error.message
                        is Network -> errorMessage = stringStore[R.string.network_error]
                        is Success -> {
                            listener.onLinkResult(
                                transferToken = exchangeResult.response.transferToken,
                                expiration = exchangeResult.response.expiration.toJavaInstant(),
                            )
                        }
                    }
                }

                EditCard -> {
                    isPinEntryStep = false
                }

                is CardNumberChange -> {
                    cardNumber = event.value
                }

                Exit -> listener.onExit()
                is PinChange -> {
                    pin = event.value
                }
            }
        }
    }

    if (isPinEntryStep) {
        return PinEntry(
            title = stringStore[R.string.confirm_pin],
            description = stringStore[R.string.enter_card_pin],
            primaryButtonText = stringStore[R.string.confirm],
            isPrimaryButtonEnabled = pin.length == PIN_LENGTH,
            secondaryButtonText = stringStore[R.string.edit_card],
            isSecondaryButtonEnabled = true,
            isLoading = isLoading,
            errorMessage = errorMessage,
        )
    }

    return CardEntry(
        title = stringStore[R.string.enter_card_number],
        description = stringStore[R.string.enter_ebt_card_number],
        primaryButtonText = stringStore[R.string.confirm],
        isPrimaryButtonEnabled = cardNumber.length >= CARD_MIN_LENGTH && !isLoading,
    )
}

internal sealed class Model {
    internal data class CardEntry(
        val title: String,
        val description: String,
        val primaryButtonText: String,
        val isPrimaryButtonEnabled: Boolean,
    ) : Model()

    internal data class PinEntry(
        val title: String,
        val description: String,
        val primaryButtonText: String,
        val isPrimaryButtonEnabled: Boolean,
        val secondaryButtonText: String,
        val isSecondaryButtonEnabled: Boolean,
        val isLoading: Boolean = false,
        val errorMessage: String? = null,
    ) : Model()
}

internal sealed class Event {
    data object AddCard : Event()
    data object ConfirmPin : Event()
    data class CardNumberChange(val value: String) : Event()
    data object EditCard : Event()
    data object Exit : Event()
    data class PinChange(val value: String) : Event()
}
