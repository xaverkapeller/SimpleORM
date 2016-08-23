package com.github.wrdlbrnft.simpleorm.processor.analyzer.databases;

import com.github.wrdlbrnft.simpleorm.processor.analyzer.entity.EntityInfo;

import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;

/**
 * Created with Android Studio
 * User: Xaver
 * Date: 10/07/16
 */
class RepositoryInfoImpl implements RepositoryInfo {

    private final TypeElement mEntityType;
    private final EntityInfo mEntityInfo;
    private final ExecutableElement mMethod;

    RepositoryInfoImpl(TypeElement entityType, EntityInfo entityInfo, ExecutableElement method) {
        mEntityType = entityType;
        mEntityInfo = entityInfo;
        mMethod = method;
    }

    @Override
    public TypeElement getEntityType() {
        return mEntityType;
    }

    @Override
    public EntityInfo getEntityInfo() {
        return mEntityInfo;
    }

    @Override
    public ExecutableElement getMethod() {
        return mMethod;
    }
}
