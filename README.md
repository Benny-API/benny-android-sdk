![Maven Central](https://img.shields.io/maven-central/v/com.bennyapi/android)

# Benny Android SDK

The Benny Android SDK allows your Android app to use Benny client libraries.

> **Note**
> See our complete documentation at [docs.bennyapi.com](https://docs.bennyapi.com).

## Installation

Install the SDK using the Maven Central Repository.

1. Update your app's `build.gradle.kts` repositories block:

    ```Gradle
    repositories {
        mavenCentral()
    }
    ```
2. Add the SDK dependency:

    ```Gradle
    dependencies {
        implementation("com.bennyapi:android:1.2.1")
    }
    ```
## EBT Balance Link Flow

The `EbtBalanceLinkFlow` allows users to link their EBT account, 
returning a tokenized representation of the credentials for fetching balance and transactioninformation.

### Required IDs

You'll need an `organizationId`, the ID representing your organization, along with
a `temporarylink` that is generated serverside via a call to the Benny API.

> **Note**
> Reach out to [help@bennyapi.com](help@bennyapi.com) to set up your organization.

### Integration

The flow is contained in a simple view, `EbtBalanceLinkFlow`, that
is initialized with your organization ID and the single-use temporary link.
This view can be programmatically added to any Android Activity or Fragment or
wrapped in AndroidView if leveraged in a Compose UI app.

An `Activity` Context is required to ensure that the WebView functions correctly.

```Kotlin
val flow = EbtBalanceLinkFlow(
    activity = activity,
    listener = listener,
    parameters = EbtBalanceLinkFlowParameters(
        organizationId = "org_wup29bz683g8habsxvazvyz1",
        options = Options(environment = SANDBOX),
    ),
)
```

### Starting the Flow

To start the flow, invoke:

```Kotlin
flow.start(temporaryLink = "temp_clr0vujq9000108l66odc7fxv")
```

See [Sample App](sample-app) as an example integration.

### Listening for Flow Events

The `EbtBalanceLinkFlowListener` is responsible for communicating to your Android app when the
user wants to exit the flow and on link result.

```Kotlin
interface EbtBalanceLinkFlowListener {
    fun onExit()
    fun onLinkResult(linkToken: String)
}
```

An implementation of this listener is a required constructor argument for `EbtBalanceLinkFlow`.

### Environments
Set the environment to `SANDBOX` to integrate with the Benny sandbox environment,
or omit to default to the production environment.

### Handling Browser Navigation

The `EbtBalanceLinkFlow` exposes a `goBack()` method that will have the flow navigate back when
invoked
on a hardware back press or gesture.
If the flow cannot go back further, this method will return false.

## EBT Transfer 

The EBT Transfer product consists of `EbtTransferLinkCardFlow`, `EbtTransferBalanceFlow`, and the `EbtTransferFlow`. Once a user successfully links their EBT card through the `EbtTransferLinkCardFlow`, a transfer token is created. The transfer token allows for EBT balance checks through the `EbtTransferBalanceFlow` and EBT cash transfers through the `EbtTransferBalanceFlow`.

### Commmon Parameters
All flows require a `EbtTransferFlowListener` and `BennyFlowParamters` for communication and configuration. As an example:

```Kotlin
val listener = object : EbtTransferFlowListener {
    override fun onExit() {
        // On Exit logic.
    }

    override fun onLinkResult(transferToken: String, expiration: Instant) {
        // On link result logic.
    }

    override fun onBalanceResult(balance: Int?, error: String?) {
        // On balance result logic.
    }

    override fun onTransferResult() {
        // On transfer result logic.
    }
}

val flowParameters = BennyFlowParameters(
    organizationId = "org_wup29bz683g8habsxvazvyz1",
    options = Options(
        isDebuggingEnabled = true,
        environment = SANDBOX,
    ),
)
```

### Link Card Flow 

The `EbtTransferLinkCardFlow` is initialized with an organization ID and a single-use temporary link generated serverside via a call to Benny's API.

Callbacks (i.e., `onExit` and `onLinkResult`) are responsible for communicating to your app when the user wants to exit the flow and when a link result is obtained. A
successful link result returns a transfer token along with its expiration date.

```Kotlin
EbtTransferLinkCardFlow(
    temporaryLink = "temp_clr0vujq9000108l66odc7fxv",
    parameters = flowParameters,
    listener = listener,
)
```

### Balance Flow 
The `EbtTransferBalanceFlow` is initialized with an organization ID and the transfer token obtained earlier.

Callbacks (i.e., `onExit` and `onBalanceResult`) are responsible for communicating to your app when the user wants to
exit the flow and when a result is obtained. A successful result returns the customer's cents-denominated EBT cash balance, while a failed link result returns an error message.

```Kotlin
EbtTransferBalanceFlow(
    transferToken = "transfer_sf3k3absxvazvjsd3lks",
    parameters = flowParameters,
    listener = listener,
)
```

### Transfer Flow
The `EbtTransferFlow` is initialized with an organization ID, the transfer token obtained earlier, a cents-denominated amount that the customer wishes to transfer, and an idempotency key.

Callbacks (i.e., `onExit` and `onTransferResult`) are responsible for communicating to your app when the user wants to exit the flow and when a transfer result is obtained. 
A successful result invokes the `onTransferResult`, while a failed result surfaces an error in the flow.

```Kotlin
EbtTransferFlow(
    transferToken = "transfer_sf3k3absxvazvjsd3lks",
    idempotencyKey = "b4fd2463-5dfd-4c89-8aa7-e4d45772f88c,
    amountCents = 500,
    parameters = flowParameters,
    listener = listener,
)
```

### Environments
Set the environment to `SANDBOX` to integrate with the Benny sandbox environment,
or omit to default to the production environment.

## Author

[Benny API Inc.](https://bennyapi.com)

## License

The Benny SDK is available under the MIT license.
