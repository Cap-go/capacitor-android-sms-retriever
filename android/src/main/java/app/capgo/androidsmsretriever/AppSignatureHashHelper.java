package app.capgo.androidsmsretriever;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.content.pm.SigningInfo;
import android.os.Build;
import android.util.Base64;
import android.util.Log;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;

public class AppSignatureHashHelper extends ContextWrapper {

    private static final String LOG_TAG = AppSignatureHashHelper.class.getSimpleName();
    private static final String HASH_TYPE = "SHA-256";
    private static final int NUM_HASHED_BYTES = 9;
    private static final int NUM_BASE64_CHAR = 11;

    public AppSignatureHashHelper(Context context) {
        super(context);
    }

    public ArrayList<String> getAppSignatures() {
        ArrayList<String> appCodes = new ArrayList<>();
        try {
            String packageName = getPackageName();
            Signature[] signatures = getSignatures(packageName);
            for (Signature signature : signatures) {
                String hash = hash(packageName, signature.toCharsString());
                if (hash != null) {
                    appCodes.add(hash);
                }
            }
        } catch (PackageManager.NameNotFoundException exception) {
            Log.e(LOG_TAG, "Unable to find package for SMS Retriever hash.", exception);
        }
        return appCodes;
    }

    private Signature[] getSignatures(String packageName) throws PackageManager.NameNotFoundException {
        PackageManager packageManager = getPackageManager();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            PackageInfo packageInfo = packageManager.getPackageInfo(packageName, PackageManager.GET_SIGNING_CERTIFICATES);
            SigningInfo signingInfo = packageInfo.signingInfo;
            if (signingInfo == null) {
                return new Signature[0];
            }
            if (signingInfo.hasMultipleSigners()) {
                return signingInfo.getApkContentsSigners();
            }
            return signingInfo.getSigningCertificateHistory();
        }

        PackageInfo packageInfo = packageManager.getPackageInfo(packageName, PackageManager.GET_SIGNATURES);
        return packageInfo.signatures;
    }

    private static String hash(String packageName, String signature) {
        String appInfo = packageName + " " + signature;
        try {
            MessageDigest messageDigest = MessageDigest.getInstance(HASH_TYPE);
            messageDigest.update(appInfo.getBytes(StandardCharsets.UTF_8));
            byte[] hashSignature = messageDigest.digest();
            hashSignature = Arrays.copyOfRange(hashSignature, 0, NUM_HASHED_BYTES);
            String base64Hash = Base64.encodeToString(hashSignature, Base64.NO_PADDING | Base64.NO_WRAP);
            return base64Hash.substring(0, NUM_BASE64_CHAR);
        } catch (NoSuchAlgorithmException exception) {
            Log.e(LOG_TAG, "Unable to calculate SMS Retriever hash.", exception);
            return null;
        }
    }
}
