package app.capgo.androidsmsretriever;

import android.content.Context;
import android.content.IntentFilter;
import android.os.Build;
import com.google.android.gms.auth.api.phone.SmsRetriever;
import com.google.android.gms.auth.api.phone.SmsRetrieverClient;
import com.google.android.gms.tasks.Task;
import java.util.ArrayList;

public class AndroidSmsRetriever {

    public interface StartWatchCallback {
        void success(String status);
        void error(String message, Exception exception);
    }

    public interface SmsRetrieverListener {
        void onSmsReceived(String message);
        void onTimeout();
        void onError(String message);
    }

    private static final String STATUS_STARTED = "SMS_RETRIEVER_STARTED";
    private static final String STATUS_ALREADY_STARTED = "SMS_RETRIEVER_ALREADY_STARTED";

    private final Context applicationContext;
    private SmsBroadcastReceiver smsBroadcastReceiver;
    private SmsRetrieverListener listener;

    public AndroidSmsRetriever(Context context) {
        this.applicationContext = context.getApplicationContext();
    }

    public void setListener(SmsRetrieverListener listener) {
        this.listener = listener;
    }

    public void startWatch(StartWatchCallback callback) {
        if (smsBroadcastReceiver != null) {
            callback.success(STATUS_ALREADY_STARTED);
            return;
        }

        smsBroadcastReceiver = new SmsBroadcastReceiver();
        smsBroadcastReceiver.setListener(
            new SmsBroadcastReceiver.Listener() {
                @Override
                public void onSmsReceived(String message) {
                    if (listener != null) {
                        listener.onSmsReceived(message);
                    }
                    unregisterReceiver();
                }

                @Override
                public void onTimeout() {
                    if (listener != null) {
                        listener.onTimeout();
                    }
                    unregisterReceiver();
                }

                @Override
                public void onError(String message) {
                    if (listener != null) {
                        listener.onError(message);
                    }
                    unregisterReceiver();
                }
            }
        );

        SmsRetrieverClient smsRetrieverClient = SmsRetriever.getClient(applicationContext);
        Task<Void> task = smsRetrieverClient.startSmsRetriever();

        task.addOnSuccessListener((aVoid) -> {
            try {
                registerReceiver();
                callback.success(STATUS_STARTED);
            } catch (Exception exception) {
                unregisterReceiver();
                callback.error("Unable to register SMS Retriever receiver.", exception);
            }
        });

        task.addOnFailureListener((exception) -> {
            unregisterReceiver();
            callback.error("Unable to start SMS Retriever.", exception);
        });
    }

    public void stopWatch() {
        unregisterReceiver();
    }

    public String getHashString() {
        ArrayList<String> appCodes = new AppSignatureHashHelper(applicationContext).getAppSignatures();
        if (appCodes.isEmpty() || appCodes.get(0) == null) {
            throw new IllegalStateException("Unable to find package signature hash.");
        }
        return appCodes.get(0);
    }

    private void registerReceiver() {
        if (smsBroadcastReceiver == null) {
            throw new IllegalStateException("SMS Retriever receiver is not initialized.");
        }

        IntentFilter intentFilter = new IntentFilter(SmsRetriever.SMS_RETRIEVED_ACTION);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            applicationContext.registerReceiver(
                smsBroadcastReceiver,
                intentFilter,
                SmsRetriever.SEND_PERMISSION,
                null,
                Context.RECEIVER_EXPORTED
            );
        } else {
            applicationContext.registerReceiver(smsBroadcastReceiver, intentFilter, SmsRetriever.SEND_PERMISSION, null);
        }
    }

    private void unregisterReceiver() {
        if (smsBroadcastReceiver == null) {
            return;
        }

        try {
            applicationContext.unregisterReceiver(smsBroadcastReceiver);
        } catch (IllegalArgumentException ignored) {
            // Receiver was already unregistered by Android.
        } finally {
            smsBroadcastReceiver = null;
        }
    }
}
