![Maven Central](https://img.shields.io/maven-central/v/com.bennyapi/android)

# Benny Android SDK

The Benny Android SDK allows your Android app to use Benny client libraries.

> **Note**
> See our complete documentation at [docs.bennyapi.com](https://docs.bennyapi.com).

### Installation

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
        implementation("com.bennyapi:benny-android:1.1.0")
    }
    ```

## Usage

### EBT Balance Link Flow

The Ebt Balance Link Flow allows users to link their EBT account, verifying the account, and
returning a tokenized representation of the credentials for fetching balance and transaction
information.

#### Required IDs

You'll need an `organizationId`, the ID representing your organization, along with
a `temporarylink` that is generated serverside via a call to the Benny API.

> **Note**
> Reach out to [help@bennyapi.com](help@bennyapi.com) to set up your organization.

#### Integration

The Ebt Balance Link Flow is contained in a simple view, `EbtBalanceLinkFlow`, that
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

#### Starting the Flow

To start the flow, invoke:

```Kotlin
flow.start(temporaryLink = "temp_clr0vujq9000108l66odc7fxv")
```

See [Sample App](sample-app) as an example integration.

#### Listening for Flow Events

The `EbtBalanceLinkFlowListener` is responsible for communicating to your Android app when the
user wants to exit the flow and when a link is successful.

```Kotlin
interface EbtBalanceLinkFlowListener {
    fun onExit()
    fun onLinkSuccess(linkToken: String)
}
```

An implementation of this listener is a required constructor argument for `EbtBalanceLinkFlow`.

##### Environments
Set the environment to `SANDBOX` to integrate with the Benny sandbox environment,
or omit to default to the production environment.

#### Handling Browser Navigation

The `EbtBalanceLinkFlow` exposes a `goBack()` method that will have the flow navigate back when
invoked
on a hardware back press or gesture.
If the flow cannot go back further, this method will return false.

### Author

[Benny API Inc.](https://bennyapi.com)

### License

The Benny SDK is available under the MIT license.
