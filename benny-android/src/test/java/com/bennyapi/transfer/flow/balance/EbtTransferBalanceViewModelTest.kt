package com.bennyapi.transfer.flow.balance

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
import com.bennyapi.transfer.flow.balance.Event.CheckBalance
import com.bennyapi.transfer.flow.balance.Event.PinChange
import com.bennyapi.transfer.networking.models.CheckBalanceResponse
import com.bennyapi.transfer.networking.result.ClientApiResult.Failure.Network
import com.bennyapi.transfer.networking.result.ClientApiResult.Success
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test

class EbtTransferBalanceViewModelTest {
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
    fun `on initial load should return check balance model`() = runBlocking {
        val events = MutableSharedFlow<Event>()
        moleculeFlow(mode = Immediate) {
            ebtTransferBalanceFlowPresenter(
                events = events,
                client = fakeClient,
                stringStore = fakeStringStore,
                listener = fakeEbtTransferFlowListener,
                transferToken = "tkn_123",
                organizationId = "org_123",
            )
        }.test {
            with(awaitItem()) {
                assertThat(title).isEqualTo("Check balance")
                assertThat(description).isEqualTo("Enter your EBT card PIN")
                assertThat(primaryButtonText).assertThat("Check balance")
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
            ebtTransferBalanceFlowPresenter(
                events = events,
                client = fakeClient,
                stringStore = fakeStringStore,
                listener = fakeEbtTransferFlowListener,
                transferToken = "tkn_123",
                organizationId = "org_123",
            )
        }.test {
            awaitItem()
            events.emit(Event.Exit)
            assertThat(fakeEbtTransferFlowListener.onExitCall).isTrue()
            ensureAllEventsConsumed()
            cancel()
        }
    }

    @Test
    fun `when check balance and error shows error`() = runBlocking {
        val events = MutableSharedFlow<Event>()
        moleculeFlow(mode = Immediate) {
            ebtTransferBalanceFlowPresenter(
                events = events,
                client = fakeClient,
                stringStore = fakeStringStore,
                listener = fakeEbtTransferFlowListener,
                transferToken = "tkn_123",
                organizationId = "org_123",
            )
        }.test {
            fakeClient.checkBalanceResult =
                Network(IllegalArgumentException("Something went wrong"))

            awaitItem()
            events.emit(PinChange("1234"))
            awaitItem()
            events.emit(CheckBalance)

            with(awaitItem()) {
                assertThat(title).isEqualTo("Check balance")
                assertThat(description).isEqualTo("Enter your EBT card PIN")
                assertThat(primaryButtonText).isEqualTo("Check balance")
                assertThat(isPrimaryButtonEnabled).isTrue()
                assertThat(errorMessage).isEqualTo("An error occurred. Try again.")
            }
            ensureAllEventsConsumed()
            cancel()
        }
    }

    @Test
    fun `when check balance successful should invoke listener`() = runBlocking {
        val events = MutableSharedFlow<Event>()
        moleculeFlow(mode = Immediate) {
            ebtTransferBalanceFlowPresenter(
                events = events,
                client = fakeClient,
                stringStore = fakeStringStore,
                listener = fakeEbtTransferFlowListener,
                transferToken = "tkn_123",
                organizationId = "org_123",
            )
        }.test {
            fakeClient.checkBalanceResult = Success(
                CheckBalanceResponse(
                    balance = 525,
                    numAttemptsLeft = 5,
                ),
            )

            awaitItem()
            events.emit(PinChange("1234"))
            awaitItem()
            events.emit(CheckBalance)

            with(awaitItem()) {
                assertThat(isLoading).isFalse()
                assertThat(title).isEqualTo("Check balance")
                assertThat(description).isEqualTo("Enter your EBT card PIN")
                assertThat(primaryButtonText).isEqualTo("Check balance")
                assertThat(isPrimaryButtonEnabled).isTrue()
                assertThat(errorMessage).isNull()
            }

            with(fakeEbtTransferFlowListener.onBalanceResultCall!!) {
                assertThat(balance).isEqualTo(525)
                assertThat(error).isNull()
            }
            ensureAllEventsConsumed()
            cancel()
        }
    }
}
