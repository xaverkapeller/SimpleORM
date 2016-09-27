package com.github.wrdlbrnft.simpleorm.processor.analyzer.entity;

import com.github.wrdlbrnft.simpleorm.annotations.AddedInVersion;
import com.github.wrdlbrnft.simpleorm.annotations.AutoIncrement;
import com.github.wrdlbrnft.simpleorm.annotations.Column;
import com.github.wrdlbrnft.simpleorm.annotations.Id;
import com.github.wrdlbrnft.simpleorm.annotations.RemovedInVersion;
import com.github.wrdlbrnft.simpleorm.annotations.Unique;
import com.github.wrdlbrnft.simpleorm.processor.analyzer.entity.exceptions.InconsistentColumnAnnotationException;
import com.github.wrdlbrnft.simpleorm.processor.analyzer.entity.exceptions.InconsistentRemovedAnnotationException;
import com.github.wrdlbrnft.simpleorm.processor.analyzer.entity.exceptions.MissingColumnAnnotationException;
import com.github.wrdlbrnft.simpleorm.processor.analyzer.typeadapter.TypeAdapterInfo;
import com.github.wrdlbrnft.simpleorm.processor.analyzer.typeadapter.TypeAdapterManager;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;

/**
 * Created with Android Studio
 * User: Xaver
 * Date: 04/07/16
 */
class GetterSetterPair {

    private static final String DEFAULT_ID_COLUMN_NAME = "_id";

    private final String mIdentifier;

    private ColumnType mColumnType;
    private TypeMirror mTypeMirror;
    private ColumnInfo.CollectionType mCollectionType;
    private Id mIdAnnotation;
    private AutoIncrement mAutoIncrementAnnotation;
    private Unique mUniqueAnnotation;
    private Column mColumnAnnotation;
    private AddedInVersion mAddedInVersion;
    private RemovedInVersion mRemovedInVersion;
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

    private VersionInfo createVersionInfo() {
        return new VersionInfoImpl(
                mAddedInVersion != null ? mAddedInVersion.value() : VersionInfo.NO_VERSION,
                mRemovedInVersion != null ? mRemovedInVersion.value() : VersionInfo.NO_VERSION
        );
    }

    public void updateAnnotations(ExecutableElement method) {
        final Id idAnnotation = method.getAnnotation(Id.class);
        if (idAnnotation != null) {
            mIdAnnotation = idAnnotation;
        }

        final AutoIncrement autoIncrementAnnotation = method.getAnnotation(AutoIncrement.class);
        if (autoIncrementAnnotation != null) {
            mAutoIncrementAnnotation = autoIncrementAnnotation;
        }

        final Unique uniqueAnnotation = method.getAnnotation(Unique.class);
        if (uniqueAnnotation != null) {
            mUniqueAnnotation = uniqueAnnotation;
        }

        updateAddedAnnotation(method);
        updateRemovedAnnotation(method);
        updateColumnAnnotation(method);
    }

    private void updateColumnAnnotation(ExecutableElement method) {
        final Column annotation = method.getAnnotation(Column.class);
        if (annotation == null) {
            return;
        }

        if (mColumnAnnotation != null && !equals(mColumnAnnotation.value(), annotation.value())) {
            throw new InconsistentColumnAnnotationException("The getter and setter for " + mIdentifier + " inconsistent column names. You only need to annotate one of them with @Column.", method);
        }
        mColumnAnnotation = annotation;
    }

    private void updateRemovedAnnotation(ExecutableElement method) {
        final RemovedInVersion annotation = method.getAnnotation(RemovedInVersion.class);
        if (annotation == null) {
            return;
        }

        if (mRemovedInVersion != null && annotation.value() != mRemovedInVersion.value()) {
            throw new InconsistentRemovedAnnotationException("The getter and setter for " + mIdentifier + " have inconsistent remove versions. You only need to annotate one of them with @RemovedInVersion.", method);
        }
        mRemovedInVersion = annotation;
    }

    private void updateAddedAnnotation(ExecutableElement method) {
        final AddedInVersion annotation = method.getAnnotation(AddedInVersion.class);
        if (annotation == null) {
            return;
        }

        if (mAddedInVersion != null && annotation.value() != mAddedInVersion.value()) {
            throw new InconsistentRemovedAnnotationException("The getter and setter for " + mIdentifier + " have inconsistent added versions. You only need to annotate one of them with @AddedInVersion.", method);
        }
        mAddedInVersion = annotation;
    }

    private static boolean equals(Object a, Object b) {
        return (a == b) || (a != null && a.equals(b));
    }

    public Id getIdAnnotation() {
        return mIdAnnotation;
    }

    public AutoIncrement getAutoIncrementAnnotation() {
        return mAutoIncrementAnnotation;
    }

    public Unique getUniqueAnnotation() {
        return mUniqueAnnotation;
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

    public ColumnInfo.CollectionType getCollectionType() {
        return mCollectionType;
    }

    public void setCollectionType(ColumnInfo.CollectionType collectionType) {
        mCollectionType = collectionType;
    }

    public ColumnInfo createColumnInfo(ProcessingEnvironment processingEnvironment, EntityAnalyzer analyzer, TypeAdapterManager adapterManager) {
        final String columnName;
        if (mColumnAnnotation != null) {
            columnName = mColumnAnnotation.value();
        } else if (mIdAnnotation != null) {
            columnName = DEFAULT_ID_COLUMN_NAME;
        } else {
            final ExecutableElement method = mGetterMethod != null ? mGetterMethod : mSetterMethod;
            throw new MissingColumnAnnotationException("The @Column annotation is missing from the method " + method.getSimpleName() + ". You need to set the column name with @Column.", method);
        }

        final Set<Constraint> constraints = new HashSet<>();
        if (mIdAnnotation != null) {
            constraints.add(Constraint.PRIMARY_KEY);
        }
        if (mAutoIncrementAnnotation != null) {
            constraints.add(Constraint.AUTO_INCREMENT);
        }
        if (mUniqueAnnotation != null) {
            constraints.add(Constraint.UNIQUE);
        }

        final EntityInfo childEntityInfo = mColumnType == ColumnType.ENTITY
                ? analyzer.analyze((TypeElement) processingEnvironment.getTypeUtils().asElement(mTypeMirror), adapterManager)
                : null;

        return new ColumnInfoImpl(
                mColumnType,
                mTypeMirror,
                constraints,
                columnName,
                mTypeAdapters,
                childEntityInfo,
                mCollectionType,
                mIdentifier,
                mGetterMethod,
                mSetterMethod,
                createVersionInfo()
        );
    }
}
