package com.bennyapi.transfer.flow.link

import app.cash.molecule.RecompositionMode.Immediate
import app.cash.molecule.moleculeFlow
import app.cash.turbine.test
import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isFalse
import assertk.assertions.isNull
import assertk.assertions.isTrue
import com.bennyapi.android.R
import com.bennyapi.fakes.FakeEbtTransferClient
import com.bennyapi.fakes.FakeEbtTransferFlowListener
import com.bennyapi.fakes.FakeStringStore
import com.bennyapi.transfer.flow.link.Event.AddCard
import com.bennyapi.transfer.flow.link.Event.CardNumberChange
import com.bennyapi.transfer.flow.link.Event.ConfirmPin
import com.bennyapi.transfer.flow.link.Event.Exit
import com.bennyapi.transfer.flow.link.Event.PinChange
import com.bennyapi.transfer.flow.link.Model.CardEntry
import com.bennyapi.transfer.flow.link.Model.PinEntry
import com.bennyapi.transfer.networking.models.ExchangeLinkTokenResponse
import com.bennyapi.transfer.networking.result.ClientApiResult.Failure.Network
import com.bennyapi.transfer.networking.result.ClientApiResult.Success
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.runBlocking
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant
import kotlinx.datetime.toJavaInstant
import org.junit.Before
import org.junit.Test

class EbtTransferLinkCardViewModelTest {
    private val fakeClient = FakeEbtTransferClient()
    private val fakeEbtTransferFlowListener = FakeEbtTransferFlowListener()
    private val fakeStringStore = FakeStringStore(
        R.string.approve_transfer to "Approve transfer",
        R.string.confirm to "Confirm",
        R.string.confirm_pin to "Confirm EBT card PIN",
        R.string.edit_card to "Edit card",
        R.string.enter_card_number to "Enter card number",
        R.string.enter_card_pin to "Enter your EBT card PIN",
        R.string.enter_ebt_card_number to "Enter your EBT card number",
        R.string.network_error to "An error occurred. Try again.",
        R.string.next to "Next",
        R.string.transfer to "Transfer",
    )

    @Before
    fun setup() {
        fakeClient.reset()
        fakeEbtTransferFlowListener.reset()
    }

    @Test
    fun `on initial load should return card entry model`() = runBlocking {
        val events = MutableSharedFlow<Event>()
        moleculeFlow(mode = Immediate) {
            ebtTransferLinkCardFlowPresenter(
                events = events,
                client = fakeClient,
                stringStore = fakeStringStore,
                listener = fakeEbtTransferFlowListener,
                temporaryLink = "temp_123",
                organizationId = "org_123",
            )
        }.test {
            with(awaitItem() as CardEntry) {
                assertThat(title).isEqualTo("Enter card number")
                assertThat(description).isEqualTo("Enter your EBT card number")
                assertThat(primaryButtonText).assertThat("Next")
                assertThat(isPrimaryButtonEnabled).isFalse()
            }
            ensureAllEventsConsumed()
            cancel()
        }
    }

    @Test
    fun `when exit click should invoke listener`() = runBlocking {
        val events = MutableSharedFlow<Event>()
        moleculeFlow(mode = Immediate) {
            ebtTransferLinkCardFlowPresenter(
                events = events,
                client = fakeClient,
                stringStore = fakeStringStore,
                listener = fakeEbtTransferFlowListener,
                temporaryLink = "temp_123",
                organizationId = "org_123",
            )
        }.test {
            awaitItem()
            events.emit(Exit)
            assertThat(fakeEbtTransferFlowListener.onExitCall).isTrue()
            ensureAllEventsConsumed()
            cancel()
        }
    }

    @Test
    fun `when card number entered next should be enabled`() = runBlocking {
        val events = MutableSharedFlow<Event>()
        moleculeFlow(mode = Immediate) {
            ebtTransferLinkCardFlowPresenter(
                events = events,
                client = fakeClient,
                stringStore = fakeStringStore,
                listener = fakeEbtTransferFlowListener,
                temporaryLink = "temp_123",
                organizationId = "org_123",
            )
        }.test {
            awaitItem()
            events.emit(CardNumberChange(value = "1234567890123456"))
            with(awaitItem() as CardEntry) {
                assertThat(isPrimaryButtonEnabled).isTrue()
            }
            ensureAllEventsConsumed()
            cancel()
        }
    }

    @Test
    fun `when add card click transitions to pin entry`() = runBlocking {
        val events = MutableSharedFlow<Event>()
        moleculeFlow(mode = Immediate) {
            ebtTransferLinkCardFlowPresenter(
                events = events,
                client = fakeClient,
                stringStore = fakeStringStore,
                listener = fakeEbtTransferFlowListener,
                temporaryLink = "temp_123",
                organizationId = "org_123",
            )
        }.test {
            awaitItem()
            events.emit(CardNumberChange(value = "1234567890123456"))
            awaitItem()

            events.emit(AddCard)

            with(awaitItem() as PinEntry) {
                assertThat(title).isEqualTo("Confirm EBT card PIN")
                assertThat(description).isEqualTo("Enter your EBT card PIN")
                assertThat(secondaryButtonText).assertThat("Edit card")
                assertThat(primaryButtonText).assertThat("Confirm")
                assertThat(isPrimaryButtonEnabled).isFalse()
            }
            ensureAllEventsConsumed()
            cancel()
        }
    }

    @Test
    fun `when confirm PIN click and error shows error`() = runBlocking {
        val events = MutableSharedFlow<Event>()
        moleculeFlow(mode = Immediate) {
            ebtTransferLinkCardFlowPresenter(
                events = events,
                client = fakeClient,
                stringStore = fakeStringStore,
                listener = fakeEbtTransferFlowListener,
                temporaryLink = "temp_123",
                organizationId = "org_123",
            )
        }.test {
            fakeClient.exchangeLinkTokenResult =
                Network(IllegalArgumentException("Something went wrong"))

            awaitItem()
            events.emit(CardNumberChange(value = "1234567890123456"))
            awaitItem()
            events.emit(AddCard)
            awaitItem()
            events.emit(PinChange("1234"))
            awaitItem()

            events.emit(ConfirmPin)

            with(awaitItem() as PinEntry) {
                assertThat(title).isEqualTo("Confirm EBT card PIN")
                assertThat(description).isEqualTo("Enter your EBT card PIN")
                assertThat(secondaryButtonText).isEqualTo("Edit card")
                assertThat(primaryButtonText).isEqualTo("Confirm")
                assertThat(isPrimaryButtonEnabled).isTrue()
                assertThat(errorMessage).isEqualTo("An error occurred. Try again.")
            }
            ensureAllEventsConsumed()
            cancel()
        }
    }

    @Test
    fun `when confirm PIN click when successful should invoke listener`() = runBlocking {
        val events = MutableSharedFlow<Event>()
        moleculeFlow(mode = Immediate) {
            ebtTransferLinkCardFlowPresenter(
                events = events,
                client = fakeClient,
                stringStore = fakeStringStore,
                listener = fakeEbtTransferFlowListener,
                temporaryLink = "temp_123",
                organizationId = "org_123",
            )
        }.test {
            val expiration = LocalDateTime(
                year = 2024,
                monthNumber = 1,
                dayOfMonth = 1,
                hour = 0,
                minute = 0,
            ).toInstant(TimeZone.UTC)

            fakeClient.exchangeLinkTokenResult =
                Success(
                    ExchangeLinkTokenResponse(
                        transferToken = "token_123",
                        expiration = expiration,
                    ),
                )

            awaitItem()
            events.emit(CardNumberChange(value = "1234567890123456"))
            awaitItem()
            events.emit(AddCard)
            awaitItem()
            events.emit(PinChange("1234"))
            awaitItem()

            events.emit(ConfirmPin)

            with(awaitItem() as PinEntry) {
                assertThat(title).isEqualTo("Confirm EBT card PIN")
                assertThat(description).isEqualTo("Enter your EBT card PIN")
                assertThat(secondaryButtonText).isEqualTo("Edit card")
                assertThat(primaryButtonText).isEqualTo("Confirm")
                assertThat(isPrimaryButtonEnabled).isTrue()
                assertThat(errorMessage).isNull()
            }

            with(fakeEbtTransferFlowListener.onLinkResultCall!!) {
                assertThat(transferToken).isEqualTo("token_123")
                assertThat(this.expiration).isEqualTo(expiration.toJavaInstant())
            }
            ensureAllEventsConsumed()
            cancel()
        }
    }
}
