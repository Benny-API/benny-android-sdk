# Benny Android SDK
The Benny Android SDK allows your Android app to start and communicate with the Benny Apply flow.

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
        implementation("com.bennyapi:benny-android:1.0.0")
    }
    ```

### Usage
#### Required IDs
You'll need an `organizationId`, the ID representing your company or organization and
ensure correct attribution.

> **Note**
> Reach out to [help@bennyapi.com](help@bennyapi.com) to setup your organization.

#### Integration
The Benny Apply flow is contained in a simple view, `BennyApplyFlow`. This view can be programmatically added to any Android Activity or Fragment
or wrapped in AndroidView if leveraged in a Compose UI app. 

An `Activity` Context is required to ensure that the WebView date, dropdown, and other inputs function correctly.

```Kotlin
val flow = BennyApplyFlow(
    activity = activity,
    listener = listener,
    parameters = BennyApplyParameters(organizationId = "org_123")
)
```

#### Starting the Flow
To start the flow, invoke:

```Kotlin
flow.start(externalId = "cus_123")
```
Where `externalId` is a non-empty string without spaces that is your organization's unique representation of a user. 
This ID is important to ensure that flow state can be tracked and restored.

See [Sample App](sample-app) as an example integration.

#### Listening for Flow Events
The `BennyApplyListener` is responsible for communicating to your Android app when the user wants to exit the flow
and when a data exchange is requested.

```Kotlin
interface BennyApplyListener {
    fun onExit()
    fun onDataExchange(applicantDataId: String)
}
```
An implementation of this listener is a required constructor argument for `BennyApplyFlow`.

#### Handling Browser Navigation
The `BennyApplyFlow` exposes a `goBack()` method that will have the flow navigate back when invoked on a hardware back press or gesture. 
If the flow cannot go back further, this method will return false.

### Author
[Benny API Inc.](https://bennyapi.com)

### License
The Benny SDK is available under the MIT license.
