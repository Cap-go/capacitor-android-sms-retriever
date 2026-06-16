# @capgo/capacitor-android-sms-retriever

<a href="https://capgo.app/"><img src="https://capgo.app/readme-banner.svg?repo=Cap-go/capacitor-android-sms-retriever" alt="Capgo - Instant updates for Capacitor" /></a>

<div align="center">
  <h2><a href="https://capgo.app/?ref=plugin_android_sms_retriever">Get instant updates for your app with Capgo</a></h2>
  <h2><a href="https://capgo.app/consulting/?ref=plugin_android_sms_retriever">Missing a feature? We can build the plugin for you</a></h2>
</div>

Android-only Capacitor plugin for Google Play services SMS Retriever and Phone Number Hint APIs.

## Install

You can use our AI-Assisted Setup to install the plugin. Add the Capgo skills to your AI tool using the following command:

```bash
npx skills add https://github.com/cap-go/capacitor-skills --skill capacitor-plugins
```

Then use the following prompt:

```text
Use the `capacitor-plugins` skill from `cap-go/capacitor-skills` to install the `@capgo/capacitor-android-sms-retriever` plugin in my project.
```

If you prefer Manual Setup, install the plugin by running the following commands and follow the platform-specific instructions below:

```bash
bun add @capgo/capacitor-android-sms-retriever
bunx cap sync android
```

## Android

This plugin does not request SMS permissions. SMS retrieval uses Google Play services and waits for one matching verification SMS for up to five minutes.

The verification SMS must include the app hash returned by `getHashString()`.

## Usage

```typescript
import { AndroidSmsRetriever } from '@capgo/capacitor-android-sms-retriever';

const { hash } = await AndroidSmsRetriever.getHashString();
console.log('SMS hash:', hash);

const received = await AndroidSmsRetriever.addListener('smsReceived', ({ message }) => {
  console.log('Verification SMS:', message);
});

await AndroidSmsRetriever.addListener('smsRetrieverTimeout', () => {
  console.log('SMS Retriever timed out');
});

await AndroidSmsRetriever.startWatch();

// Later, if needed:
await AndroidSmsRetriever.stopWatch();
await received.remove();
```

```typescript
const { phoneNumber } = await AndroidSmsRetriever.getPhoneNumber();
console.log(phoneNumber);
```

## API

<docgen-index>

* [`startWatch()`](#startwatch)
* [`stopWatch()`](#stopwatch)
* [`getHashString()`](#gethashstring)
* [`getPhoneNumber()`](#getphonenumber)
* [`addListener('smsReceived', ...)`](#addlistenersmsreceived-)
* [`addListener('smsRetrieverTimeout', ...)`](#addlistenersmsretrievertimeout-)
* [`addListener('smsRetrieverError', ...)`](#addlistenersmsretrievererror-)
* [`removeAllListeners()`](#removealllisteners)
* [`getPluginVersion()`](#getpluginversion)
* [Interfaces](#interfaces)
* [Type Aliases](#type-aliases)

</docgen-index>

<docgen-api>
<!--Update the source file JSDoc comments and rerun docgen to update the docs below-->

Android-only Capacitor plugin for Google Play services SMS Retriever and Phone Number Hint APIs.

### startWatch()

```typescript
startWatch() => Promise<StartWatchResult>
```

Start listening for one verification SMS through Android's SMS Retriever API.

Android stops listening automatically when a matching message arrives or after the
five-minute SMS Retriever timeout. No SMS permissions are required.

**Returns:** <code>Promise&lt;<a href="#startwatchresult">StartWatchResult</a>&gt;</code>

**Since:** 8.0.0

--------------------


### stopWatch()

```typescript
stopWatch() => Promise<StopWatchResult>
```

Stop the active SMS Retriever watch, if any.

**Returns:** <code>Promise&lt;<a href="#stopwatchresult">StopWatchResult</a>&gt;</code>

**Since:** 8.0.0

--------------------


### getHashString()

```typescript
getHashString() => Promise<GetHashStringResult>
```

Get the 11-character app hash for the installed app signature.

Generate this once for the signing key used to distribute the app and append it
to verification SMS messages.

**Returns:** <code>Promise&lt;<a href="#gethashstringresult">GetHashStringResult</a>&gt;</code>

**Since:** 8.0.0

--------------------


### getPhoneNumber()

```typescript
getPhoneNumber() => Promise<GetPhoneNumberResult>
```

Show Android's Phone Number Hint UI and return the user-selected SIM-based phone number.

**Returns:** <code>Promise&lt;<a href="#getphonenumberresult">GetPhoneNumberResult</a>&gt;</code>

**Since:** 8.0.0

--------------------


### addListener('smsReceived', ...)

```typescript
addListener(eventName: 'smsReceived', listenerFunc: (event: SmsReceivedEvent) => void) => Promise<PluginListenerHandle>
```

Listen for a verification SMS received after {@link startWatch}.

| Param              | Type                                                                              |
| ------------------ | --------------------------------------------------------------------------------- |
| **`eventName`**    | <code>'smsReceived'</code>                                                        |
| **`listenerFunc`** | <code>(event: <a href="#smsreceivedevent">SmsReceivedEvent</a>) =&gt; void</code> |

**Returns:** <code>Promise&lt;<a href="#pluginlistenerhandle">PluginListenerHandle</a>&gt;</code>

**Since:** 8.0.0

--------------------


### addListener('smsRetrieverTimeout', ...)

```typescript
addListener(eventName: 'smsRetrieverTimeout', listenerFunc: () => void) => Promise<PluginListenerHandle>
```

Listen for the five-minute SMS Retriever timeout.

| Param              | Type                               |
| ------------------ | ---------------------------------- |
| **`eventName`**    | <code>'smsRetrieverTimeout'</code> |
| **`listenerFunc`** | <code>() =&gt; void</code>         |

**Returns:** <code>Promise&lt;<a href="#pluginlistenerhandle">PluginListenerHandle</a>&gt;</code>

**Since:** 8.0.0

--------------------


### addListener('smsRetrieverError', ...)

```typescript
addListener(eventName: 'smsRetrieverError', listenerFunc: (event: SmsRetrieverErrorEvent) => void) => Promise<PluginListenerHandle>
```

Listen for SMS Retriever runtime errors after {@link startWatch}.

| Param              | Type                                                                                          |
| ------------------ | --------------------------------------------------------------------------------------------- |
| **`eventName`**    | <code>'smsRetrieverError'</code>                                                              |
| **`listenerFunc`** | <code>(event: <a href="#smsretrievererrorevent">SmsRetrieverErrorEvent</a>) =&gt; void</code> |

**Returns:** <code>Promise&lt;<a href="#pluginlistenerhandle">PluginListenerHandle</a>&gt;</code>

**Since:** 8.0.0

--------------------


### removeAllListeners()

```typescript
removeAllListeners() => Promise<void>
```

Remove all registered listeners for this plugin.

**Since:** 8.0.0

--------------------


### getPluginVersion()

```typescript
getPluginVersion() => Promise<PluginVersionResult>
```

Get the native Capacitor plugin version.

**Returns:** <code>Promise&lt;<a href="#pluginversionresult">PluginVersionResult</a>&gt;</code>

**Since:** 8.0.0

--------------------


### Interfaces


#### StartWatchResult

Result returned when SMS Retriever watching starts.

| Prop         | Type                                                                        | Description                        | Since |
| ------------ | --------------------------------------------------------------------------- | ---------------------------------- | ----- |
| **`status`** | <code><a href="#smsretrieverstartstatus">SmsRetrieverStartStatus</a></code> | Native status returned by Android. | 8.0.0 |


#### StopWatchResult

Result returned when SMS Retriever watching stops.

| Prop         | Type                              | Description                        | Since |
| ------------ | --------------------------------- | ---------------------------------- | ----- |
| **`status`** | <code>'SMS_RETRIEVER_DONE'</code> | Native status returned by Android. | 8.0.0 |


#### GetHashStringResult

Result returned by {@link AndroidSmsRetrieverPlugin.getHashString}.

| Prop       | Type                | Description                                                                   | Since |
| ---------- | ------------------- | ----------------------------------------------------------------------------- | ----- |
| **`hash`** | <code>string</code> | The 11-character app hash that must be appended to verification SMS messages. | 8.0.0 |


#### GetPhoneNumberResult

Result returned by {@link AndroidSmsRetrieverPlugin.getPhoneNumber}.

| Prop              | Type                | Description                                                            | Since |
| ----------------- | ------------------- | ---------------------------------------------------------------------- | ----- |
| **`phoneNumber`** | <code>string</code> | Phone number selected by the user from Android's Phone Number Hint UI. | 8.0.0 |


#### PluginListenerHandle

| Prop         | Type                                      |
| ------------ | ----------------------------------------- |
| **`remove`** | <code>() =&gt; Promise&lt;void&gt;</code> |


#### SmsReceivedEvent

SMS Retriever message event payload.

| Prop          | Type                | Description                                                            | Since |
| ------------- | ------------------- | ---------------------------------------------------------------------- | ----- |
| **`message`** | <code>string</code> | Full verification SMS message received by Android's SMS Retriever API. | 8.0.0 |


#### SmsRetrieverErrorEvent

SMS Retriever error event payload.

| Prop          | Type                | Description                                                | Since |
| ------------- | ------------------- | ---------------------------------------------------------- | ----- |
| **`message`** | <code>string</code> | Error message returned by Android or Google Play services. | 8.0.0 |


#### PluginVersionResult

Plugin version payload.

| Prop          | Type                | Description                                                 | Since |
| ------------- | ------------------- | ----------------------------------------------------------- | ----- |
| **`version`** | <code>string</code> | Version identifier returned by the platform implementation. | 8.0.0 |


### Type Aliases


#### SmsRetrieverStartStatus

Status values returned by {@link AndroidSmsRetrieverPlugin.startWatch}.

<code>'SMS_RETRIEVER_STARTED' | 'SMS_RETRIEVER_ALREADY_STARTED'</code>

</docgen-api>
