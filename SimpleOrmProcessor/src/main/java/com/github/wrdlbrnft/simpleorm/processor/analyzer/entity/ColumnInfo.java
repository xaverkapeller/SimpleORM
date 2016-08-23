package com.github.wrdlbrnft.simpleorm.processor.analyzer.entity;

import java.util.Set;

import javax.lang.model.element.ExecutableElement;
import javax.lang.model.type.TypeMirror;

/**
 * Created with Android Studio
 * User: Xaver
 * Date: 03/07/16
 */

public interface ColumnInfo {
    ColumnType getColumnType();
    TypeMirror getTypeMirror();
    Set<Constraint> getConstraints();
    EntityInfo getChildEntityInfo();
    String getColumnName();
    String getIdentifier();
    ExecutableElement getGetterElement();
    ExecutableElement getSetterElement();
}
