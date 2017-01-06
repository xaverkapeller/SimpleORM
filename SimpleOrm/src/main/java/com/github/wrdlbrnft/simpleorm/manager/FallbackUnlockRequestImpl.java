package com.github.wrdlbrnft.simpleorm.manager;

import android.content.Context;

/**
 * Created with Android Studio<br>
 * User: Xaver<br>
 * Date: 23/12/2016
 */

class FallbackUnlockRequestImpl extends BaseUnlockRequest {

    protected FallbackUnlockRequestImpl(Context context, boolean alreadyUnlocked, Callback callback) {
        super(context, alreadyUnlocked, callback);
    }

    @Override
    public boolean isFingerprintAvailable() {
        return false;
    }

    @Override
    protected void performFingerprintUnlock(DatabaseManager.FingerprintUnlockCallback callback) {

    }
}
