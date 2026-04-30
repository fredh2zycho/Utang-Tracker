package com.redlab.utang.utils;

import android.content.Context;
import android.util.Log;

import androidx.biometric.BiometricManager;
import androidx.biometric.BiometricPrompt;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import java.util.concurrent.Executor;

public class BiometricHelper {

    private static final String TAG = "BiometricHelper";

    public interface BiometricCallback {
        void onSuccess();
        void onFailure(String errorMessage);
        void onError(String errorMessage);
    }

    public static boolean isBiometricAvailable(Context context) {
        BiometricManager biometricManager = BiometricManager.from(context);
        int result = biometricManager.canAuthenticate(
                BiometricManager.Authenticators.BIOMETRIC_STRONG |
                BiometricManager.Authenticators.BIOMETRIC_WEAK
        );
        return result == BiometricManager.BIOMETRIC_SUCCESS;
    }

    public static void showBiometricPrompt(
            FragmentActivity activity,
            String title,
            String subtitle,
            String description,
            BiometricCallback callback
    ) {
        Executor executor = ContextCompat.getMainExecutor(activity);

        BiometricPrompt.AuthenticationCallback authCallback =
                new BiometricPrompt.AuthenticationCallback() {
                    @Override
                    public void onAuthenticationSucceeded(BiometricPrompt.AuthenticationResult result) {
                        super.onAuthenticationSucceeded(result);
                        callback.onSuccess();
                    }

                    @Override
                    public void onAuthenticationFailed() {
                        super.onAuthenticationFailed();
                        callback.onFailure("Hindi nakilala ang fingerprint. Subukan ulit.");
                    }

                    @Override
                    public void onAuthenticationError(int errorCode, CharSequence errString) {
                        super.onAuthenticationError(errorCode, errString);
                        if (errorCode == BiometricPrompt.ERROR_USER_CANCELED ||
                            errorCode == BiometricPrompt.ERROR_NEGATIVE_BUTTON) {
                            callback.onError("Kinansela ng user.");
                        } else {
                            callback.onError("Error: " + errString);
                        }
                        Log.e(TAG, "Auth error " + errorCode + ": " + errString);
                    }
                };

        BiometricPrompt biometricPrompt = new BiometricPrompt(activity, executor, authCallback);

        BiometricPrompt.PromptInfo promptInfo = new BiometricPrompt.PromptInfo.Builder()
                .setTitle(title)
                .setSubtitle(subtitle)
                .setDescription(description)
                .setNegativeButtonText("Kanselahin")
                .setAllowedAuthenticators(
                        BiometricManager.Authenticators.BIOMETRIC_STRONG |
                        BiometricManager.Authenticators.BIOMETRIC_WEAK
                )
                .build();

        biometricPrompt.authenticate(promptInfo);
    }
}
