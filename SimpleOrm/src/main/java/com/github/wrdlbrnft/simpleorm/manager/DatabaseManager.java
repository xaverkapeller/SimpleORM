package com.github.wrdlbrnft.simpleorm.manager;

import android.content.Context;
import android.os.Build;
import android.support.annotation.IntDef;
import android.util.Log;

import com.github.wrdlbrnft.simpleorm.exceptions.SimpleOrmPasswordException;

import static com.github.wrdlbrnft.simpleorm.utils.Utils.requireNonNull;

/**
 * Created with Android Studio<br>
 * User: Xaver<br>
 * Date: 22/12/2016
 */
public class DatabaseManager<T> {

    private static final String TAG = "DatabaseManager";

    public static final int MESSAGE_SUCCESS = 0x01;
    public static final int MESSAGE_SENSOR_DIRTY = 0x02;
    public static final int MESSAGE_INSUFFICIENT = 0x04;
    public static final int MESSAGE_PARTIAL = 0x08;
    public static final int MESSAGE_MOVED_TOO_FAST = 0x10;
    public static final int MESSAGE_MOVED_TOO_SLOW = 0x20;

    @IntDef({MESSAGE_SUCCESS, MESSAGE_SENSOR_DIRTY, MESSAGE_INSUFFICIENT, MESSAGE_PARTIAL, MESSAGE_MOVED_TOO_FAST, MESSAGE_MOVED_TOO_SLOW})
    public @interface Message {
    }

    public static final int ERROR_CANCELED = 0x01;
    public static final int ERROR_HARDWARE_UNAVAILABLE = 0x02;
    public static final int ERROR_LOCKOUT = 0x04;
    public static final int ERROR_NO_SPACE = 0x08;
    public static final int ERROR_TIMEOUT = 0x10;
    public static final int ERROR_UNABLE_TO_PROCESS = 0x20;

    @IntDef({ERROR_CANCELED, ERROR_HARDWARE_UNAVAILABLE, ERROR_LOCKOUT, ERROR_NO_SPACE, ERROR_TIMEOUT, ERROR_UNABLE_TO_PROCESS})
    public @interface Error {
    }

    public interface DatabaseCreator<T> {
        T open(Context context, char[] password);
    }

    public interface FingerprintUnlockCallback {
        void onUnlockSuccessful();
        void onUnlockFailed();
        void onFallbackPasswordRequired(PasswordRequest request);
        void onFingerprintRequired(FingerprintRequest request);
        void onFingerprintError(@Error int message);
        void onFingerprintMessage(@Message int message);
    }

    public interface PasswordUnlockCallback {
        void onUnlockSuccessful();
        void onPasswordRequired(PasswordRequest request);
    }

    public interface PasswordRequest {
        boolean tryPassword(char[] password);
    }

    public interface FingerprintRequest {
        void startAuthenticating();
        void stopAuthenticating();
    }

    public interface UnlockRequest {
        boolean isFingerprintAvailable();
        void unlockWithFingerprint(FingerprintUnlockCallback callback);
        void unlockWithPassword(PasswordUnlockCallback callback);
    }

    public static class Builder<T> {

        private DatabaseCreator<T> mOpener;
        private String mKeyName;

        public Builder<T> setOpener(DatabaseCreator<T> opener) {
            mOpener = opener;
            return this;
        }

        public Builder<T> setKeyName(String keyName) {
            mKeyName = keyName;
            return this;
        }

        public DatabaseManager<T> build() {
            return new DatabaseManager<>(
                    requireNonNull(mKeyName, "You must supply a non-null key name to the DatabaseFingerprintManager.Builder."),
                    requireNonNull(mOpener, "You must supply a non-null DatabaseCreator instance to the DatabaseFingerprintManager.Builder.")
            );
        }
    }

    private final BaseUnlockRequest.Callback mUnlockRequestCallback = new BaseUnlockRequest.Callback() {
        @Override
        public boolean onUnlocked(Context context, char[] password) {
            synchronized (mLock) {
                if (mDatabase != null) {
                    return true;
                }

                try {
                    mDatabase = mCreator.open(context, password);
                    return true;
                } catch (SimpleOrmPasswordException e) {
                    Log.v(TAG, "Wrong password for database", e);
                }
                return false;
            }
        }
    };

    private final Object mLock = new Object();

    private final String mKeyName;
    private final DatabaseCreator<T> mCreator;
    private volatile T mDatabase;

    private DatabaseManager(String keyName, DatabaseCreator<T> creator) {
        mKeyName = keyName;
        mCreator = creator;
    }

    public T getUnlockedDatabase() {
        synchronized (mLock) {
            if (mDatabase == null) {
                throw new DatabaseLockedException("You have to unlock the database before you can use it.");
            }
            return mDatabase;
        }
    }

    public UnlockRequest unlock(Context context) {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                ? new MarshmallowUnlockRequestImpl(context, isUnlocked(), mUnlockRequestCallback, mKeyName)
                : new FallbackUnlockRequestImpl(context, isUnlocked(), mUnlockRequestCallback);
    }

    public boolean isUnlocked() {
        synchronized (mLock) {
            return mDatabase != null;
        }
    }
}
