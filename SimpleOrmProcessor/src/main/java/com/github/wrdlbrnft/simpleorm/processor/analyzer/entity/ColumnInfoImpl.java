package com.github.wrdlbrnft.simpleorm.processor.analyzer.entity;

import com.github.wrdlbrnft.simpleorm.processor.analyzer.typeadapter.TypeAdapterInfo;

import java.util.Collections;
import java.util.List;
import java.util.Set;

import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;

/**
 * Created with Android Studio
 * User: Xaver
 * Date: 04/07/16
 */
class ColumnInfoImpl implements ColumnInfo {

    private final ColumnType mColumnType;
    private final TypeMirror mTypeMirror;
    private final Set<Constraint> mConstraints;
    private final String mColumnName;
    private final List<TypeAdapterInfo> mTypeAdapters;
    private final EntityInfo mChildEntityInfo;
    private final String mIdentifier;
    private final ExecutableElement mGetter;
    private final ExecutableElement mSetter;

    ColumnInfoImpl(ColumnType columnType, TypeMirror typeMirror, Set<Constraint> constraints, String columnName, List<TypeAdapterInfo> typeAdapters, EntityInfo childEntityInfo, String identifier, ExecutableElement getter, ExecutableElement setter) {
        mColumnType = columnType;
        mTypeMirror = typeMirror;
        mConstraints = Collections.unmodifiableSet(constraints);
        mColumnName = columnName;
        mTypeAdapters = typeAdapters;
        mChildEntityInfo = childEntityInfo;
        mIdentifier = identifier;
        mGetter = getter;
        mSetter = setter;
    }

    @Override
    public ColumnType getColumnType() {
        return mColumnType;
    }

    @Override
    public TypeMirror getTypeMirror() {
        return mTypeMirror;
    }

    @Override
    public Set<Constraint> getConstraints() {
        return mConstraints;
    }

    @Override
    public List<TypeAdapterInfo> getTypeAdapters() {
        return mTypeAdapters;
    }

    @Override
    public String getColumnName() {
        return mColumnName;
    }

    @Override
    public String getIdentifier() {
        return mIdentifier;
    }

    @Override
    public ExecutableElement getGetterElement() {
        return mGetter;
    }

    @Override
    public ExecutableElement getSetterElement() {
        return mSetter;
    }

    @Override
    public EntityInfo getChildEntityInfo() {
        return mChildEntityInfo;
    }
}
