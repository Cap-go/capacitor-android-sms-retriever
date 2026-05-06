package app.capgo.androidsmsretriever;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import com.google.android.gms.auth.api.phone.SmsRetriever;
import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.common.api.Status;

public class SmsBroadcastReceiver extends BroadcastReceiver {

    public interface Listener {
        void onSmsReceived(String message);
        void onTimeout();
        void onError(String message);
    }

    private Listener listener;

    public void setListener(Listener listener) {
        this.listener = listener;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (!SmsRetriever.SMS_RETRIEVED_ACTION.equals(intent.getAction())) {
            return;
        }

        Bundle extras = intent.getExtras();
        if (extras == null) {
            emitError("SMS Retriever returned no extras.");
            return;
        }

        Status status = (Status) extras.get(SmsRetriever.EXTRA_STATUS);
        if (status == null) {
            emitError("SMS Retriever returned no status.");
            return;
        }

        switch (status.getStatusCode()) {
            case CommonStatusCodes.SUCCESS:
                String message = (String) extras.get(SmsRetriever.EXTRA_SMS_MESSAGE);
                if (message == null) {
                    emitError("SMS Retriever returned no message.");
                    return;
                }
                if (listener != null) {
                    listener.onSmsReceived(message);
                }
                break;
            case CommonStatusCodes.TIMEOUT:
                if (listener != null) {
                    listener.onTimeout();
                }
                break;
            case CommonStatusCodes.API_NOT_CONNECTED:
                emitError("API_NOT_CONNECTED");
                break;
            case CommonStatusCodes.NETWORK_ERROR:
                emitError("NETWORK_ERROR");
                break;
            case CommonStatusCodes.ERROR:
                emitError("SMS Retriever failed.");
                break;
            default:
                emitError("SMS Retriever failed with status code " + status.getStatusCode() + ".");
                break;
        }
    }

    private void emitError(String message) {
        if (listener != null) {
            listener.onError(message);
        }
    }
}
