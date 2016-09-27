package com.github.wrdlbrnft.simpleorm.processor.analyzer.entity;

/**
 * Created with Android Studio
 * User: Xaver
 * Date: 28/09/2016
 */

class VersionInfoImpl implements VersionInfo {

    private final int mAddedInVersion;
    private final int mRemovedInVersion;

    VersionInfoImpl(int addedInVersion, int removedInVersion) {
        mAddedInVersion = addedInVersion;
        mRemovedInVersion = removedInVersion;
    }

    @Override
    public int getAddedInVersion() {
        return mAddedInVersion;
    }

    @Override
    public int getRemovedInVersion() {
        return mRemovedInVersion;
    }
}
