package app.capgo.androidsmsretriever;

import android.app.Activity;
import android.content.Intent;
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.IntentSenderRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import com.getcapacitor.JSObject;
import com.getcapacitor.Logger;
import com.getcapacitor.Plugin;
import com.getcapacitor.PluginCall;
import com.getcapacitor.PluginMethod;
import com.getcapacitor.annotation.CapacitorPlugin;
import com.google.android.gms.auth.api.identity.GetPhoneNumberHintIntentRequest;
import com.google.android.gms.auth.api.identity.Identity;
import com.google.android.gms.common.api.ApiException;

@CapacitorPlugin(name = "AndroidSmsRetriever")
public class AndroidSmsRetrieverPlugin extends Plugin {

    private static final String TAG = "AndroidSmsRetriever";
    private static final String EVENT_SMS_RECEIVED = "smsReceived";
    private static final String EVENT_SMS_TIMEOUT = "smsRetrieverTimeout";
    private static final String EVENT_SMS_ERROR = "smsRetrieverError";
    private static final String STATUS_DONE = "SMS_RETRIEVER_DONE";

    private final String pluginVersion = "8.0.7";
    private AndroidSmsRetriever implementation;
    private ActivityResultLauncher<IntentSenderRequest> phoneNumberHintLauncher;
    private String pendingPhoneNumberCallId;

    @Override
    public void load() {
        super.load();
        implementation = new AndroidSmsRetriever(getContext());
        implementation.setListener(
            new AndroidSmsRetriever.SmsRetrieverListener() {
                @Override
                public void onSmsReceived(String message) {
                    JSObject data = new JSObject();
                    data.put("message", message);
                    notifyListeners(EVENT_SMS_RECEIVED, data, true);
                }

                @Override
                public void onTimeout() {
                    notifyListeners(EVENT_SMS_TIMEOUT, new JSObject(), true);
                }

                @Override
                public void onError(String message) {
                    JSObject data = new JSObject();
                    data.put("message", message);
                    notifyListeners(EVENT_SMS_ERROR, data, true);
                }
            }
        );
        phoneNumberHintLauncher = bridge.registerForActivityResult(
            new ActivityResultContracts.StartIntentSenderForResult(),
            this::handlePhoneNumberHintResult
        );
    }

    @PluginMethod
    public void startWatch(PluginCall call) {
        if (implementation == null) {
            call.reject("Plugin is not initialized.");
            return;
        }

        implementation.startWatch(
            new AndroidSmsRetriever.StartWatchCallback() {
                @Override
                public void success(String status) {
                    JSObject ret = new JSObject();
                    ret.put("status", status);
                    call.resolve(ret);
                }

                @Override
                public void error(String message, Exception exception) {
                    Logger.error(TAG, message, exception);
                    call.reject(resolveMessage(message, exception), exception);
                }
            }
        );
    }

    @PluginMethod
    public void stopWatch(PluginCall call) {
        if (implementation != null) {
            implementation.stopWatch();
        }

        JSObject ret = new JSObject();
        ret.put("status", STATUS_DONE);
        call.resolve(ret);
    }

    @PluginMethod
    public void getHashString(PluginCall call) {
        if (implementation == null) {
            call.reject("Plugin is not initialized.");
            return;
        }

        try {
            JSObject ret = new JSObject();
            ret.put("hash", implementation.getHashString());
            call.resolve(ret);
        } catch (Exception exception) {
            call.reject(resolveMessage("Could not get app signature hash.", exception), exception);
        }
    }

    @PluginMethod
    public void getPhoneNumber(PluginCall call) {
        if (phoneNumberHintLauncher == null) {
            call.reject("Phone Number Hint is not ready.");
            return;
        }

        if (pendingPhoneNumberCallId != null) {
            call.reject("Another Phone Number Hint request is already in progress.");
            return;
        }

        Activity activity = getActivity();
        if (activity == null) {
            call.reject("Activity reference is unavailable.");
            return;
        }

        GetPhoneNumberHintIntentRequest request = GetPhoneNumberHintIntentRequest.builder().build();
        Identity.getSignInClient(activity)
            .getPhoneNumberHintIntent(request)
            .addOnSuccessListener((pendingIntent) -> {
                try {
                    bridge.saveCall(call);
                    pendingPhoneNumberCallId = call.getCallbackId();
                    IntentSenderRequest intentSenderRequest = new IntentSenderRequest.Builder(pendingIntent.getIntentSender()).build();
                    phoneNumberHintLauncher.launch(intentSenderRequest);
                } catch (Exception exception) {
                    pendingPhoneNumberCallId = null;
                    bridge.releaseCall(call);
                    call.reject(resolveMessage("Unable to launch Phone Number Hint.", exception), exception);
                }
            })
            .addOnFailureListener((exception) -> call.reject(resolveMessage("Unable to start Phone Number Hint.", exception), exception));
    }

    @PluginMethod
    public void getPluginVersion(PluginCall call) {
        JSObject ret = new JSObject();
        ret.put("version", this.pluginVersion);
        call.resolve(ret);
    }

    @Override
    protected void handleOnDestroy() {
        if (implementation != null) {
            implementation.stopWatch();
        }
        super.handleOnDestroy();
    }

    private void handlePhoneNumberHintResult(ActivityResult result) {
        PluginCall call = getPendingPhoneNumberCall();
        if (call == null) {
            pendingPhoneNumberCallId = null;
            return;
        }

        try {
            if (result.getResultCode() != Activity.RESULT_OK) {
                call.reject("Phone number was not selected.");
                return;
            }

            Intent data = result.getData();
            if (data == null) {
                call.reject("Phone Number Hint returned no data.");
                return;
            }

            Activity activity = getActivity();
            if (activity == null) {
                call.reject("Activity reference is unavailable.");
                return;
            }

            String phoneNumber = Identity.getSignInClient(activity).getPhoneNumberFromIntent(data);
            JSObject ret = new JSObject();
            ret.put("phoneNumber", phoneNumber);
            call.resolve(ret);
        } catch (ApiException exception) {
            call.reject(resolveMessage("Could not read selected phone number.", exception), exception);
        } finally {
            releasePendingPhoneNumberCall(call);
        }
    }

    private PluginCall getPendingPhoneNumberCall() {
        if (pendingPhoneNumberCallId == null) {
            return null;
        }
        return bridge.getSavedCall(pendingPhoneNumberCallId);
    }

    private void releasePendingPhoneNumberCall(PluginCall call) {
        if (pendingPhoneNumberCallId != null) {
            bridge.releaseCall(call);
            pendingPhoneNumberCallId = null;
        }
    }

    private String resolveMessage(String fallback, Exception exception) {
        if (exception != null && exception.getMessage() != null && !exception.getMessage().isEmpty()) {
            return exception.getMessage();
        }
        return fallback;
    }
}
