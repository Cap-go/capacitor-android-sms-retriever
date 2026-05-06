import type { PluginListenerHandle } from '@capacitor/core';

/**
 * Result returned when SMS Retriever watching starts.
 *
 * @since 8.0.0
 */
export interface StartWatchResult {
  /**
   * Native status returned by Android.
   *
   * @since 8.0.0
   */
  status: SmsRetrieverStartStatus;
}

/**
 * Result returned when SMS Retriever watching stops.
 *
 * @since 8.0.0
 */
export interface StopWatchResult {
  /**
   * Native status returned by Android.
   *
   * @since 8.0.0
   */
  status: 'SMS_RETRIEVER_DONE';
}

/**
 * Result returned by {@link AndroidSmsRetrieverPlugin.getHashString}.
 *
 * @since 8.0.0
 */
export interface GetHashStringResult {
  /**
   * The 11-character app hash that must be appended to verification SMS messages.
   *
   * @since 8.0.0
   */
  hash: string;
}

/**
 * Result returned by {@link AndroidSmsRetrieverPlugin.getPhoneNumber}.
 *
 * @since 8.0.0
 */
export interface GetPhoneNumberResult {
  /**
   * Phone number selected by the user from Android's Phone Number Hint UI.
   *
   * @since 8.0.0
   */
  phoneNumber: string;
}

/**
 * SMS Retriever message event payload.
 *
 * @since 8.0.0
 */
export interface SmsReceivedEvent {
  /**
   * Full verification SMS message received by Android's SMS Retriever API.
   *
   * @since 8.0.0
   */
  message: string;
}

/**
 * SMS Retriever error event payload.
 *
 * @since 8.0.0
 */
export interface SmsRetrieverErrorEvent {
  /**
   * Error message returned by Android or Google Play services.
   *
   * @since 8.0.0
   */
  message: string;
}

/**
 * Plugin version payload.
 *
 * @since 8.0.0
 */
export interface PluginVersionResult {
  /**
   * Version identifier returned by the platform implementation.
   *
   * @since 8.0.0
   */
  version: string;
}

/**
 * Status values returned by {@link AndroidSmsRetrieverPlugin.startWatch}.
 *
 * @since 8.0.0
 */
export type SmsRetrieverStartStatus = 'SMS_RETRIEVER_STARTED' | 'SMS_RETRIEVER_ALREADY_STARTED';

/**
 * Android-only Capacitor plugin for Google Play services SMS Retriever and Phone Number Hint APIs.
 *
 * @since 8.0.0
 */
export interface AndroidSmsRetrieverPlugin {
  /**
   * Start listening for one verification SMS through Android's SMS Retriever API.
   *
   * Android stops listening automatically when a matching message arrives or after the
   * five-minute SMS Retriever timeout. No SMS permissions are required.
   *
   * @since 8.0.0
   */
  startWatch(): Promise<StartWatchResult>;

  /**
   * Stop the active SMS Retriever watch, if any.
   *
   * @since 8.0.0
   */
  stopWatch(): Promise<StopWatchResult>;

  /**
   * Get the 11-character app hash for the installed app signature.
   *
   * Generate this once for the signing key used to distribute the app and append it
   * to verification SMS messages.
   *
   * @since 8.0.0
   */
  getHashString(): Promise<GetHashStringResult>;

  /**
   * Show Android's Phone Number Hint UI and return the user-selected SIM-based phone number.
   *
   * @since 8.0.0
   */
  getPhoneNumber(): Promise<GetPhoneNumberResult>;

  /**
   * Listen for a verification SMS received after {@link startWatch}.
   *
   * @since 8.0.0
   */
  addListener(eventName: 'smsReceived', listenerFunc: (event: SmsReceivedEvent) => void): Promise<PluginListenerHandle>;

  /**
   * Listen for the five-minute SMS Retriever timeout.
   *
   * @since 8.0.0
   */
  addListener(eventName: 'smsRetrieverTimeout', listenerFunc: () => void): Promise<PluginListenerHandle>;

  /**
   * Listen for SMS Retriever runtime errors after {@link startWatch}.
   *
   * @since 8.0.0
   */
  addListener(
    eventName: 'smsRetrieverError',
    listenerFunc: (event: SmsRetrieverErrorEvent) => void,
  ): Promise<PluginListenerHandle>;

  /**
   * Remove all registered listeners for this plugin.
   *
   * @since 8.0.0
   */
  removeAllListeners(): Promise<void>;

  /**
   * Get the native Capacitor plugin version.
   *
   * @since 8.0.0
   */
  getPluginVersion(): Promise<PluginVersionResult>;
}
