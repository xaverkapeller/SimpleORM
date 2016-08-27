package com.github.wrdlbrnft.simpleorm.processor.analyzer.entity;

import com.github.wrdlbrnft.simpleorm.annotations.AutoIncrement;
import com.github.wrdlbrnft.simpleorm.annotations.Column;
import com.github.wrdlbrnft.simpleorm.annotations.Id;
import com.github.wrdlbrnft.simpleorm.annotations.Unique;
import com.github.wrdlbrnft.simpleorm.processor.analyzer.typeadapter.TypeAdapterInfo;

import java.util.List;

import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;

/**
 * Created with Android Studio
 * User: Xaver
 * Date: 04/07/16
 */
class GetterSetterPair {

    private final String mIdentifier;

    private ColumnType mColumnType;
    private TypeMirror mTypeMirror;
    private Id mIdAnnotation;
    private AutoIncrement mAutoIncrementAnnotation;
    private Unique mUniqueAnnotation;
    private Column mColumnAnnotation;
    private ExecutableElement mSetterMethod;
    private ExecutableElement mGetterMethod;
    private List<TypeAdapterInfo> mTypeAdapters;

    GetterSetterPair(String identifier) {
        mIdentifier = identifier;
    }

    public String getIdentifier() {
        return mIdentifier;
    }

    public ColumnType getColumnType() {
        return mColumnType;
    }

    public void setColumnType(ColumnType columnType) {
        mColumnType = columnType;
    }

    public TypeMirror getTypeMirror() {
        return mTypeMirror;
    }

    public void setTypeMirror(TypeMirror typeMirror) {
        mTypeMirror = typeMirror;
    }

    public Column getColumnAnnotation() {
        return mColumnAnnotation;
    }

    public void setColumnAnnotation(Column columnAnnotation) {
        mColumnAnnotation = columnAnnotation;
    }

    public Id getIdAnnotation() {
        return mIdAnnotation;
    }

    public void setIdAnnotation(Id idAnnotation) {
        mIdAnnotation = idAnnotation;
    }

    public AutoIncrement getAutoIncrementAnnotation() {
        return mAutoIncrementAnnotation;
    }

    public void setAutoIncrementAnnotation(AutoIncrement autoIncrementAnnotation) {
        mAutoIncrementAnnotation = autoIncrementAnnotation;
    }

    public Unique getUniqueAnnotation() {
        return mUniqueAnnotation;
    }

    public void setUniqueAnnotation(Unique uniqueAnnotation) {
        mUniqueAnnotation = uniqueAnnotation;
    }

    public ExecutableElement getSetterMethod() {
        return mSetterMethod;
    }

    public void setSetterMethod(ExecutableElement setterMethod) {
        mSetterMethod = setterMethod;
    }

    public ExecutableElement getGetterMethod() {
        return mGetterMethod;
    }

    public void setGetterMethod(ExecutableElement getterMethod) {
        mGetterMethod = getterMethod;
    }

    public void setTypeAdapters(List<TypeAdapterInfo> typeAdapters) {
        mTypeAdapters = typeAdapters;
    }

    public List<TypeAdapterInfo> getTypeAdapters() {
        return mTypeAdapters;
    }
}
