package com.github.wrdlbrnft.simpleorm.processor.builder.entity;

import com.github.wrdlbrnft.codebuilder.implementations.Implementation;
import com.github.wrdlbrnft.simpleorm.processor.analyzer.entity.ColumnInfo;

import java.util.List;

/**
 * Created with Android Studio
 * User: Xaver
 * Date: 11/07/16
 */

public interface EntityImplementationInfo {
    Implementation getImplementation();
    List<ColumnInfo> getConstructorParameters();
}
