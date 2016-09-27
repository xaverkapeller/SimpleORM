package com.github.wrdlbrnft.simpleorm.processor.analyzer.entity;

import com.github.wrdlbrnft.codebuilder.types.Type;
import com.github.wrdlbrnft.simpleorm.processor.analyzer.typeadapter.TypeAdapterInfo;

import java.util.List;
import java.util.Set;

import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;

/**
 * Created with Android Studio
 * User: Xaver
 * Date: 03/07/16
 */

public interface ColumnInfo {
    enum CollectionType {
        NONE,
        LIST
    }

    ColumnType getColumnType();
    TypeMirror getTypeMirror();
    Type getObjectType();
    Set<Constraint> getConstraints();
    List<TypeAdapterInfo> getTypeAdapters();
    EntityInfo getChildEntityInfo();
    CollectionType getCollectionType();
    String getColumnName();
    String getIdentifier();
    VersionInfo getVersionInfo();
    ExecutableElement getGetterElement();
    ExecutableElement getSetterElement();
}
