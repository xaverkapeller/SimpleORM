package com.github.wrdlbrnft.simpleorm.manager;

import android.content.Context;

/**
 * Created with Android Studio<br>
 * User: Xaver<br>
 * Date: 23/12/2016
 */
abstract class BaseUnlockRequest implements DatabaseManager.UnlockRequest {

    public interface Callback {
        boolean onUnlocked(Context context, char[] password);
    }

    private final Context mContext;
    private final boolean mAlreadyUnlocked;
    private final Callback mCallback;

    protected BaseUnlockRequest(Context context, boolean alreadyUnlocked, Callback callback) {
        mContext = context;
        mAlreadyUnlocked = alreadyUnlocked;
        mCallback = callback;
    }

    @Override
    public final void unlockWithPassword(final DatabaseManager.PasswordUnlockCallback callback) {
        if (mAlreadyUnlocked) {
            callback.onUnlockSuccessful();
            return;
        }
        callback.onPasswordRequired(new DatabaseManager.PasswordRequest() {
            @Override
            public boolean tryPassword(char[] password) {
                if (unlockDatabase(password)) {
                    callback.onUnlockSuccessful();
                    return true;
                }
                return false;
            }
        });
    }

    @Override
    public final void unlockWithFingerprint(DatabaseManager.FingerprintUnlockCallback callback) {
        if (!isFingerprintAvailable()) {
            throw new FingerprintNotAvailableException("This device does not have a fingerprint sensor, or it has been disabled");
        }
        if (mAlreadyUnlocked) {
            callback.onUnlockSuccessful();
            return;
        }
        performFingerprintUnlock(callback);
    }

    protected abstract void performFingerprintUnlock(DatabaseManager.FingerprintUnlockCallback callback);

    protected boolean unlockDatabase(char[] password) {
        return mCallback.onUnlocked(mContext, password);
    }
}
