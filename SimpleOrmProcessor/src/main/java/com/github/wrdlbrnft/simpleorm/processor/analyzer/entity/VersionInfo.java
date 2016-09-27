package com.github.wrdlbrnft.simpleorm.processor.analyzer.entity;

/**
 * Created with Android Studio
 * User: Xaver
 * Date: 28/09/2016
 */

public interface VersionInfo {
    int NO_VERSION = 0;
    int getAddedInVersion();
    int getRemovedInVersion();
}
