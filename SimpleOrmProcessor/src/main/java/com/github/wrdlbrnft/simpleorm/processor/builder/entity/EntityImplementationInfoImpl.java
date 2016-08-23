package com.github.wrdlbrnft.simpleorm.processor.builder.entity;

import com.github.wrdlbrnft.codebuilder.implementations.Implementation;
import com.github.wrdlbrnft.simpleorm.processor.analyzer.entity.ColumnInfo;

import java.util.List;

/**
 * Created with Android Studio
 * User: Xaver
 * Date: 11/07/16
 */
class EntityImplementationInfoImpl implements EntityImplementationInfo {

    private final Implementation mImplementation;
    private final List<ColumnInfo> mConstructorParameters;

    EntityImplementationInfoImpl(Implementation implementation, List<ColumnInfo> constructorParameters) {
        mImplementation = implementation;
        mConstructorParameters = constructorParameters;
    }

    @Override
    public Implementation getImplementation() {
        return mImplementation;
    }

    @Override
    public List<ColumnInfo> getConstructorParameters() {
        return mConstructorParameters;
    }
}
