package com.github.wrdlbrnft.simpleorm.manager;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.fingerprint.FingerprintManager;
import android.os.Build;
import android.os.CancellationSignal;
import android.os.Handler;
import android.os.Looper;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyPermanentlyInvalidatedException;
import android.security.keystore.KeyProperties;
import android.support.annotation.IntDef;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;

/**
 * Created with Android Studio<br>
 * User: Xaver<br>
 * Date: 23/12/2016
 */
@TargetApi(Build.VERSION_CODES.M)
class MarshmallowUnlockRequestImpl extends BaseUnlockRequest {

    private static final String TAG = "MarshmallowUnlockReques";

    private static final int STATE_IDLE = 0x01;
    private static final int STATE_FALLBACK_PASSWORD_REQUIRED = 0x02;
    private static final int STATE_FINGERPRINT_REGISTRATION_REQUIRED = 0x04;
    private static final int STATE_FINGERPRINT_READY = 0x08;
    private static final int STATE_NO_FINGERPRINTS = 0x10;

    @IntDef({STATE_IDLE, STATE_FALLBACK_PASSWORD_REQUIRED, STATE_FINGERPRINT_REGISTRATION_REQUIRED, STATE_FINGERPRINT_READY, STATE_NO_FINGERPRINTS})
    private @interface State {
    }

    private static final Handler mHandler = new Handler(Looper.getMainLooper());
    private static final String KEY_FILE_PREFIX = "SimpleOrm_";

    private final Context mContext;
    private final FingerprintManager mFingerprintManager;
    private final String mKeyName;
    private final KeyStore mKeyStore;
    private final KeyGenerator mKeyGenerator;
    private final Cipher mCipher;

    @State
    private int mState = STATE_IDLE;
    private File mKeyFile;

    protected MarshmallowUnlockRequestImpl(Context context, boolean alreadyUnlocked, Callback callback, String keyName) {
        super(context, alreadyUnlocked, callback);
        mContext = context;
        mFingerprintManager = context.getSystemService(FingerprintManager.class);
        mKeyName = keyName;
        mKeyStore = createKeystore();
        mKeyGenerator = createKeyGenerator();
        mKeyFile = context.getFileStreamPath(KEY_FILE_PREFIX + mKeyName);
        mCipher = createCipher();
        performSetup();
    }

    @Override
    protected void performFingerprintUnlock(DatabaseManager.FingerprintUnlockCallback callback) {
        switch (mState) {

            case STATE_FALLBACK_PASSWORD_REQUIRED:
                callback.onFallbackPasswordRequired(new PasswordRequestImpl(callback));
                break;

            case STATE_FINGERPRINT_READY:
                callback.onFingerprintRequired(new DecryptFingerprintRequestImpl(callback));
                break;

            case STATE_NO_FINGERPRINTS:
            case STATE_FINGERPRINT_REGISTRATION_REQUIRED:
            case STATE_IDLE:
                throw new IllegalStateException("Encountered illegal state: " + mState);

            default:
                throw new IllegalStateException("Encountered unknown state: " + mState);
        }
    }

    @Override
    public boolean isFingerprintAvailable() {
        return mContext.checkSelfPermission(Manifest.permission.USE_FINGERPRINT) == PackageManager.PERMISSION_GRANTED
                && mFingerprintManager.isHardwareDetected()
                && mFingerprintManager.hasEnrolledFingerprints();
    }

    private void performSetup() {
        try {
            if (mKeyStore.containsAlias(mKeyName)) {
                if (mKeyFile.exists()) {
                    mState = STATE_FINGERPRINT_READY;
                } else {
                    mState = STATE_FALLBACK_PASSWORD_REQUIRED;
                }
            } else {
                if (mKeyFile.exists()) {
                    mKeyFile.delete();
                }

                if (isFingerprintAvailable()) {
                    mState = STATE_FALLBACK_PASSWORD_REQUIRED;
                    createKey(mKeyName, false);
                } else {
                    mState = STATE_NO_FINGERPRINTS;
                }
            }
        } catch (KeyStoreException e) {
            throw new IllegalStateException("Failed to access KeyStore", e);
        }
    }

    private KeyGenerator createKeyGenerator() {
        try {
            return KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, "AndroidKeyStore");
        } catch (NoSuchAlgorithmException | NoSuchProviderException e) {
            throw new IllegalStateException("Failed to setup KeyGenerator.", e);
        }
    }

    private Cipher createCipher() {
        try {
            return Cipher.getInstance(KeyProperties.KEY_ALGORITHM_AES + "/" + KeyProperties.BLOCK_MODE_CBC + "/" + KeyProperties.ENCRYPTION_PADDING_PKCS7);
        } catch (NoSuchAlgorithmException | NoSuchPaddingException e) {
            throw new IllegalStateException("Failed to create Cipher.", e);
        }
    }

    private KeyStore createKeystore() {
        try {
            final KeyStore keyStore = KeyStore.getInstance("AndroidKeyStore");
            keyStore.load(null);
            return keyStore;
        } catch (KeyStoreException | IOException | CertificateException | NoSuchAlgorithmException e) {
            throw new IllegalStateException("Failed to setup Keystore.", e);
        }
    }

    private boolean initCipher(Cipher cipher, String keyName, int mode) {
        try {
            final SecretKey key = (SecretKey) mKeyStore.getKey(keyName, null);
            cipher.init(mode, key);
            return true;
        } catch (KeyPermanentlyInvalidatedException e) {
            return false;
        } catch (KeyStoreException | UnrecoverableKeyException | NoSuchAlgorithmException | InvalidKeyException e) {
            throw new RuntimeException("Failed to init Cipher.", e);
        }
    }

    private void createKey(String keyName, boolean invalidatedByBiometricEnrollment) {
        try {
            final KeyGenParameterSpec.Builder builder = new KeyGenParameterSpec.Builder(keyName, KeyProperties.PURPOSE_ENCRYPT | KeyProperties.PURPOSE_DECRYPT)
                    .setBlockModes(KeyProperties.BLOCK_MODE_CBC)
                    .setUserAuthenticationRequired(true)
                    .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_PKCS7);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                builder.setInvalidatedByBiometricEnrollment(invalidatedByBiometricEnrollment);
            }
            mKeyGenerator.init(builder.build());
            mKeyGenerator.generateKey();
        } catch (InvalidAlgorithmParameterException e) {
            throw new RuntimeException(e);
        }
    }

    @DatabaseManager.Error
    private static int translateErrorCode(int errorCode) {
        switch (errorCode) {
            case FingerprintManager.FINGERPRINT_ERROR_CANCELED:
                return DatabaseManager.ERROR_CANCELED;

            case FingerprintManager.FINGERPRINT_ERROR_HW_UNAVAILABLE:
                return DatabaseManager.ERROR_HARDWARE_UNAVAILABLE;

            case FingerprintManager.FINGERPRINT_ERROR_LOCKOUT:
                return DatabaseManager.ERROR_LOCKOUT;

            case FingerprintManager.FINGERPRINT_ERROR_NO_SPACE:
                return DatabaseManager.ERROR_NO_SPACE;

            case FingerprintManager.FINGERPRINT_ERROR_TIMEOUT:
                return DatabaseManager.ERROR_TIMEOUT;

            case FingerprintManager.FINGERPRINT_ERROR_UNABLE_TO_PROCESS:
                return DatabaseManager.ERROR_UNABLE_TO_PROCESS;

            default:
                throw new IllegalStateException("Unknown error code: " + errorCode);
        }
    }

    @DatabaseManager.Message
    private static int translateHelpCode(int helpCode) {
        switch (helpCode) {
            case FingerprintManager.FINGERPRINT_ACQUIRED_GOOD:
                return DatabaseManager.MESSAGE_SUCCESS;

            case FingerprintManager.FINGERPRINT_ACQUIRED_IMAGER_DIRTY:
                return DatabaseManager.MESSAGE_SENSOR_DIRTY;

            case FingerprintManager.FINGERPRINT_ACQUIRED_INSUFFICIENT:
                return DatabaseManager.MESSAGE_INSUFFICIENT;

            case FingerprintManager.FINGERPRINT_ACQUIRED_PARTIAL:
                return DatabaseManager.MESSAGE_PARTIAL;

            case FingerprintManager.FINGERPRINT_ACQUIRED_TOO_FAST:
                return DatabaseManager.MESSAGE_MOVED_TOO_FAST;

            case FingerprintManager.FINGERPRINT_ACQUIRED_TOO_SLOW:
                return DatabaseManager.MESSAGE_MOVED_TOO_SLOW;

            default:
                throw new IllegalStateException("Unknown help code: " + helpCode);
        }
    }

    private class PasswordRequestImpl implements DatabaseManager.PasswordRequest {

        private final DatabaseManager.FingerprintUnlockCallback mCallback;

        private PasswordRequestImpl(DatabaseManager.FingerprintUnlockCallback callback) {
            mCallback = callback;
        }

        @Override
        public boolean tryPassword(char[] password) {
            if (password == null) {
                return false;
            }
            mState = STATE_FINGERPRINT_REGISTRATION_REQUIRED;
            mCallback.onFingerprintRequired(new EncryptFingerprintRequestImpl(password, mCallback));
            return true;
        }
    }

    private class EncryptFingerprintRequestImpl implements DatabaseManager.FingerprintRequest {

        private final CancellationSignal mCancellationSignal = new CancellationSignal();
        private final FingerprintManager.AuthenticationCallback mAuthenticationCallback = new FingerprintManager.AuthenticationCallback() {

            @Override
            public void onAuthenticationError(int errorCode, CharSequence errString) {
                mCallback.onFingerprintError(translateErrorCode(errorCode));
            }

            @Override
            public void onAuthenticationHelp(int helpCode, CharSequence helpString) {
                mCallback.onFingerprintMessage(translateHelpCode(helpCode));
            }

            @Override
            public void onAuthenticationSucceeded(FingerprintManager.AuthenticationResult result) {
                final Cipher cipher = result.getCryptoObject().getCipher();

                if (mState != MarshmallowUnlockRequestImpl.STATE_FINGERPRINT_REGISTRATION_REQUIRED) {
                    throw new IllegalStateException("Encountered illegal state: " + mState);
                }

                final byte[] passwordBytes = new String(mPassword).getBytes(StandardCharsets.UTF_8);
                final byte[] keyData;
                try {
                    keyData = cipher.doFinal(passwordBytes);
                } catch (IllegalBlockSizeException | BadPaddingException e) {
                    throw new IllegalStateException("Failed to encrypt key.", e);
                }

                try (final BufferedOutputStream stream = new BufferedOutputStream(new FileOutputStream(mKeyFile))) {
                    mKeyFile.createNewFile();
                    stream.write(keyData);
                } catch (IOException e) {
                    throw new IllegalStateException("Failed to write to key file.", e);
                }

                if (unlockDatabase(mPassword)) {
                    mCallback.onUnlockSuccessful();
                } else {
                    mCallback.onUnlockFailed();
                }
            }

            @Override
            public void onAuthenticationFailed() {
                mCallback.onUnlockFailed();
            }
        };

        private final char[] mPassword;
        private final DatabaseManager.FingerprintUnlockCallback mCallback;

        private EncryptFingerprintRequestImpl(char[] password, DatabaseManager.FingerprintUnlockCallback callback) {
            mPassword = password;
            mCallback = callback;
        }

        @Override
        @SuppressWarnings("MissingPermission")
        public void startAuthenticating() {
            initCipher(mCipher, mKeyName, Cipher.ENCRYPT_MODE);
            mFingerprintManager.authenticate(
                    new FingerprintManager.CryptoObject(mCipher),
                    mCancellationSignal,
                    0,
                    mAuthenticationCallback,
                    mHandler
            );
        }

        @Override
        public void stopAuthenticating() {
            mCancellationSignal.cancel();
        }
    }

    private class DecryptFingerprintRequestImpl implements DatabaseManager.FingerprintRequest {

        private final CancellationSignal mCancellationSignal = new CancellationSignal();
        private final FingerprintManager.AuthenticationCallback mAuthenticationCallback = new FingerprintManager.AuthenticationCallback() {

            @Override
            public void onAuthenticationError(int errorCode, CharSequence errString) {
                mCallback.onFingerprintError(translateErrorCode(errorCode));
            }

            @Override
            public void onAuthenticationHelp(int helpCode, CharSequence helpString) {
                mCallback.onFingerprintMessage(translateHelpCode(helpCode));
            }

            @Override
            public void onAuthenticationSucceeded(FingerprintManager.AuthenticationResult result) {
                final Cipher cipher = result.getCryptoObject().getCipher();

                if (mState != MarshmallowUnlockRequestImpl.STATE_FINGERPRINT_READY) {
                    throw new IllegalStateException("Encountered illegal state: " + mState);
                }

                final byte[] keyData = readKeyFile();

                try {
                    final byte[] passwordBytes = cipher.doFinal(keyData);
                    final char[] password = new String(passwordBytes, StandardCharsets.UTF_8).toCharArray();
                    if (unlockDatabase(password)) {
                        mCallback.onUnlockSuccessful();
                    } else {
                        mCallback.onUnlockFailed();
                    }
                } catch (IllegalBlockSizeException | BadPaddingException e) {
                    Log.e(TAG, "Failed to decrypt key", e);
                    mCallback.onUnlockFailed();
                }
            }

            @Override
            public void onAuthenticationFailed() {
                mCallback.onUnlockFailed();
            }
        };

        private byte[] readKeyFile() {
            final byte[] keyData = new byte[(int) mKeyFile.length()];
            try (final BufferedInputStream stream = new BufferedInputStream(new FileInputStream(mKeyFile))) {
                stream.read(keyData, 0, keyData.length);
            } catch (IOException e) {
                Log.e(TAG, "Failed to read key file.", e);
                mCallback.onUnlockFailed();
            }
            return keyData;
        }

        private final DatabaseManager.FingerprintUnlockCallback mCallback;

        private DecryptFingerprintRequestImpl(DatabaseManager.FingerprintUnlockCallback callback) {
            mCallback = callback;
        }

        @Override
        @SuppressWarnings("MissingPermission")
        public void startAuthenticating() {
            initCipher(mCipher, mKeyName, Cipher.DECRYPT_MODE);
            mFingerprintManager.authenticate(
                    new FingerprintManager.CryptoObject(mCipher),
                    mCancellationSignal,
                    0,
                    mAuthenticationCallback,
                    mHandler
            );
        }

        @Override
        public void stopAuthenticating() {
            mCancellationSignal.cancel();
        }
    }
}
