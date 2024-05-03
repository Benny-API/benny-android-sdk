package com.bennyapi.transfer.flow.transfer

import app.cash.molecule.RecompositionMode
import app.cash.molecule.moleculeFlow
import app.cash.turbine.test
import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isFalse
import assertk.assertions.isTrue
import com.bennyapi.android.R
import com.bennyapi.fakes.FakeEbtTransferClient
import com.bennyapi.fakes.FakeEbtTransferFlowListener
import com.bennyapi.fakes.FakeStringStore
import com.bennyapi.transfer.flow.transfer.Event.Exit
import com.bennyapi.transfer.flow.transfer.Event.PinChange
import com.bennyapi.transfer.flow.transfer.Event.Transfer
import com.bennyapi.transfer.networking.result.ClientApiResult.Failure.Network
import com.bennyapi.transfer.networking.result.ClientApiResult.Success
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test

internal class EbtTransferViewModelTest {
    private val fakeClient = FakeEbtTransferClient()
    private val fakeEbtTransferFlowListener = FakeEbtTransferFlowListener()
    private val fakeStringStore = FakeStringStore(
        R.string.approve_transfer to "Approve transfer",
        R.string.check_balance to "Check balance",
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
    fun `on initial load should return transfer model`() = runBlocking {
        val events = MutableSharedFlow<Event>()
        moleculeFlow(mode = RecompositionMode.Immediate) {
            ebtTransferFlowPresenter(
                events = events,
                client = fakeClient,
                stringStore = fakeStringStore,
                listener = fakeEbtTransferFlowListener,
                transferToken = "tkn_123",
                organizationId = "org_123",
                idempotencyKey = "ik_123",
                amountCents = 525,
            )
        }.test {
            with(awaitItem()) {
                assertThat(title).isEqualTo("Approve transfer")
                assertThat(description).isEqualTo("Enter your EBT card PIN")
                assertThat(primaryButtonText).assertThat("Transfer")
                assertThat(isPrimaryButtonEnabled).isFalse()
            }
            ensureAllEventsConsumed()
            cancel()
        }
    }

    @Test
    fun `when exit click should invoke listener`() = runBlocking {
        val events = MutableSharedFlow<Event>()
        moleculeFlow(mode = RecompositionMode.Immediate) {
            ebtTransferFlowPresenter(
                events = events,
                client = fakeClient,
                stringStore = fakeStringStore,
                listener = fakeEbtTransferFlowListener,
                transferToken = "tkn_123",
                organizationId = "org_123",
                idempotencyKey = "ik_123",
                amountCents = 525,
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
    fun `when transfer and error shows error`() = runBlocking {
        val events = MutableSharedFlow<Event>()
        moleculeFlow(mode = RecompositionMode.Immediate) {
            ebtTransferFlowPresenter(
                events = events,
                client = fakeClient,
                stringStore = fakeStringStore,
                listener = fakeEbtTransferFlowListener,
                transferToken = "tkn_123",
                organizationId = "org_123",
                idempotencyKey = "ik_123",
                amountCents = 525,
            )
        }.test {
            fakeClient.transferResult = Network(IllegalArgumentException("Something went wrong"))

            awaitItem()
            events.emit(PinChange("1234"))
            awaitItem()
            events.emit(Transfer)

            with(awaitItem()) {
                assertThat(title).isEqualTo("Approve transfer")
                assertThat(description).isEqualTo("Enter your EBT card PIN")
                assertThat(primaryButtonText).assertThat("Transfer")
                assertThat(errorMessage).isEqualTo("An error occurred. Try again.")
                assertThat(isPrimaryButtonEnabled).isTrue()
            }
            ensureAllEventsConsumed()
            cancel()
        }
    }

    @Test
    fun `when transfer successful should invoke listener`() = runBlocking {
        val events = MutableSharedFlow<Event>()
        moleculeFlow(mode = RecompositionMode.Immediate) {
            ebtTransferFlowPresenter(
                events = events,
                client = fakeClient,
                stringStore = fakeStringStore,
                listener = fakeEbtTransferFlowListener,
                transferToken = "tkn_123",
                organizationId = "org_123",
                idempotencyKey = "ik_123",
                amountCents = 525,
            )
        }.test {
            fakeClient.transferResult = Success(Unit)

            awaitItem()
            events.emit(PinChange("1234"))
            awaitItem()
            events.emit(Transfer)

            with(awaitItem()) {
                assertThat(title).isEqualTo("Approve transfer")
                assertThat(description).isEqualTo("Enter your EBT card PIN")
                assertThat(primaryButtonText).assertThat("Transfer")
                assertThat(isPrimaryButtonEnabled).isTrue()
            }

            assertThat(fakeEbtTransferFlowListener.onTransferResultCall).isTrue()
            ensureAllEventsConsumed()
            cancel()
        }
    }
}
