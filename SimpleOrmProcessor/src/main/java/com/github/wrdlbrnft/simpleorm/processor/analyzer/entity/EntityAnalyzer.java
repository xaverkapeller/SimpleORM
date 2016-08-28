package com.github.wrdlbrnft.simpleorm.processor.analyzer.entity;

import com.github.wrdlbrnft.codebuilder.util.ProcessingHelper;
import com.github.wrdlbrnft.simpleorm.annotations.AutoIncrement;
import com.github.wrdlbrnft.simpleorm.annotations.Column;
import com.github.wrdlbrnft.simpleorm.annotations.Entity;
import com.github.wrdlbrnft.simpleorm.annotations.Id;
import com.github.wrdlbrnft.simpleorm.annotations.Unique;
import com.github.wrdlbrnft.simpleorm.processor.analyzer.entity.exceptions.GetterWithParametersException;
import com.github.wrdlbrnft.simpleorm.processor.analyzer.entity.exceptions.InconsistentColumnAnnotationException;
import com.github.wrdlbrnft.simpleorm.processor.analyzer.entity.exceptions.InconsistentGetterSetterTypeException;
import com.github.wrdlbrnft.simpleorm.processor.analyzer.entity.exceptions.InvalidEntityException;
import com.github.wrdlbrnft.simpleorm.processor.analyzer.entity.exceptions.InvalidIdColumnException;
import com.github.wrdlbrnft.simpleorm.processor.analyzer.entity.exceptions.InvalidMethodNameException;
import com.github.wrdlbrnft.simpleorm.processor.analyzer.entity.exceptions.MissingColumnAnnotationException;
import com.github.wrdlbrnft.simpleorm.processor.analyzer.entity.exceptions.MissingIdAnnotationException;
import com.github.wrdlbrnft.simpleorm.processor.analyzer.entity.exceptions.MultipleGetterException;
import com.github.wrdlbrnft.simpleorm.processor.analyzer.entity.exceptions.MultipleIdColumnsException;
import com.github.wrdlbrnft.simpleorm.processor.analyzer.entity.exceptions.SetterWithoutParametersException;
import com.github.wrdlbrnft.simpleorm.processor.analyzer.typeadapter.TypeAdapterManager;
import com.github.wrdlbrnft.simpleorm.processor.analyzer.typeadapter.TypeAdapterResult;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.NoType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;

/**
 * Created with Android Studio
 * User: Xaver
 * Date: 03/07/16
 */
public class EntityAnalyzer {

    private static final String DEFAULT_ID_COLUMN_NAME = "_id";
    private final Map<String, EntityInfo> mCache = new HashMap<>();

    private final ProcessingEnvironment mProcessingEnvironment;
    private final ProcessingHelper mProcessingHelper;
    private final NoType mVoidType;

    public EntityAnalyzer(ProcessingEnvironment processingEnvironment) {
        mProcessingEnvironment = processingEnvironment;
        mProcessingHelper = ProcessingHelper.from(processingEnvironment);
        mVoidType = processingEnvironment.getTypeUtils().getNoType(TypeKind.VOID);
    }

    public EntityInfo analyze(TypeElement entity, TypeAdapterManager adapterManager) throws InvalidEntityException {
        final String entityClassName = entity.getQualifiedName().toString();
        final EntityInfo cachedInfo = mCache.get(entityClassName);
        if (cachedInfo != null) {
            return cachedInfo;
        }
        final EntityInfoWrapper wrapper = new EntityInfoWrapper();
        mCache.put(entityClassName, wrapper);

        final Entity entityAnnotation = entity.getAnnotation(Entity.class);
        final String tableName = entityAnnotation.value();

        final List<? extends Element> members = getMembers(entity);
        final Map<String, GetterSetterPair> getterSetterMap = createGetterSetterMap(members, adapterManager);
        ColumnInfo idColumn = null;
        final List<ColumnInfo> columns = new ArrayList<>();
        for (GetterSetterPair pair : getterSetterMap.values()) {
            final ColumnInfo columnInfo = createColumnInfo(pair, adapterManager);
            if (pair.getIdAnnotation() != null) {
                if (idColumn != null) {
                    throw new MultipleIdColumnsException("The entity " + entity.getSimpleName() + " has more than one Id columns.", entity);
                }
                idColumn = columnInfo;
            }
            columns.add(columnInfo);
        }

        if (idColumn != null) {
            if (idColumn.getColumnType() != ColumnType.LONG) {
                throw new InvalidIdColumnException("The type of the getter and setter method for the Id column of " + entity.getSimpleName() + " is of the wrong type. The Id getter and setters need to be of type Long.", entity);
            }

            if (idColumn.getGetterElement() == null) {
                throw new MissingIdAnnotationException("The entity " + entity.getSimpleName() + " requires a getter and a setter for its Id column. The getter method is missing.", entity);
            }

            if (idColumn.getSetterElement() == null) {
                throw new MissingIdAnnotationException("The entity " + entity.getSimpleName() + " requires a getter and a setter for its Id column. The setter method is missing.", entity);
            }
        }

        final EntityInfo entityInfo = new EntityInfoImpl(tableName, entity, idColumn, Collections.unmodifiableList(columns));
        wrapper.setEntityInfo(entityInfo);
        return entityInfo;
    }

    private List<? extends Element> getMembers(TypeElement entity) {
        return entity.getEnclosedElements();
    }

    private Map<String, GetterSetterPair> createGetterSetterMap(List<? extends Element> members, TypeAdapterManager adapterManager) {
        final Map<String, GetterSetterPair> pairMap = new HashMap<>();
        for (Element member : members) {
            if (member.getKind() != ElementKind.METHOD) {
                continue;
            }

            final ExecutableElement method = (ExecutableElement) member;
            final Column columnAnnotation = method.getAnnotation(Column.class);
            final List<? extends VariableElement> parameters = method.getParameters();

            final String name = method.getSimpleName().toString();
            final TypeMirror returnType = method.getReturnType();
            if (name.startsWith("get") || name.startsWith("is")) {
                final String key = name.startsWith("is") ? name.substring(2) : name.substring(3);
                final GetterSetterPair pair = getGetterSetterPair(pairMap, key);

                if (pair.getGetterMethod() != null) {
                    throw new MultipleGetterException("There are multiple getters defined for the same thing. One with the get prefix, another with the is prefix. You can only have one getter", method);
                }
                pair.setGetterMethod(method);

                if (!parameters.isEmpty()) {
                    throw new GetterWithParametersException("The getter " + name + " has one or more parameters. Getters are not allowed to have any parameters.", method);
                }

                final TypeAdapterResult result = adapterManager.resolve(returnType);
                final ColumnType type = result.getColumnType();
                if (pair.getColumnType() == null) {
                    pair.setColumnType(type);
                    pair.setTypeMirror(returnType);
                } else if (pair.getColumnType() != type || !mProcessingHelper.isSameType(returnType, pair.getTypeMirror())) {
                    throw new InconsistentGetterSetterTypeException("The type of the getter " + name + " is inconsistent with its setter.", method);
                }

                pair.setTypeAdapters(result.getAdapters());

                if (columnAnnotation != null) {
                    final Column existingAnnotation = pair.getColumnAnnotation();
                    if (existingAnnotation != null && !equals(existingAnnotation.value(), columnAnnotation.value())) {
                        throw new InconsistentColumnAnnotationException("The getter " + name + " and its setter have inconsistent column names.", method);
                    }
                    pair.setColumnAnnotation(columnAnnotation);
                }

                analyzeAnnotations(pair, method);
            } else if (name.startsWith("set")) {
                final String key = name.substring(3);
                final GetterSetterPair pair = getGetterSetterPair(pairMap, key);
                pair.setSetterMethod(method);

                if (parameters.isEmpty()) {
                    throw new SetterWithoutParametersException("The setter " + name + " has no parameters. A setter needs to have one parameter which machtes the type of its getter.", method);
                }

                if (!mProcessingHelper.isSameType(mVoidType, returnType)) {
                    throw new GetterWithParametersException("The setter " + name + " has a return type. Setter are not allowed to have any return type other than void.", method);
                }

                final TypeMirror parameterType = parameters.get(0).asType();
                final TypeAdapterResult result = adapterManager.resolve(parameterType);
                final ColumnType type = result.getColumnType();
                if (pair.getColumnType() == null) {
                    pair.setColumnType(type);
                    pair.setTypeMirror(parameterType);
                } else if (pair.getColumnType() != type || !mProcessingHelper.isSameType(parameterType, pair.getTypeMirror())) {
                    throw new InconsistentGetterSetterTypeException("The type of the setter " + name + " is inconsistent with its getter.", method);
                }

                pair.setTypeAdapters(result.getAdapters());

                if (columnAnnotation != null) {
                    final Column existingAnnotation = pair.getColumnAnnotation();
                    if (existingAnnotation != null && !equals(existingAnnotation.value(), columnAnnotation.value())) {
                        throw new InconsistentColumnAnnotationException("The setter " + name + " and its getter have inconsistent column names. You only need to annotate one of them with @Column.", method);
                    }
                    pair.setColumnAnnotation(columnAnnotation);
                }

                analyzeAnnotations(pair, method);
            } else {
                throw new InvalidMethodNameException(name + " is not a valid method name. It has to start with set for a setter or get/is for a getter", method);
            }
        }
        return pairMap;
    }

    private void analyzeAnnotations(GetterSetterPair pair, ExecutableElement method) {
        final Id idAnnotation = method.getAnnotation(Id.class);
        final AutoIncrement autoIncrementAnnotation = method.getAnnotation(AutoIncrement.class);
        final Unique uniqueAnnotation = method.getAnnotation(Unique.class);
        if (idAnnotation != null) {
            pair.setIdAnnotation(idAnnotation);
        }

        if (autoIncrementAnnotation != null) {
            pair.setAutoIncrementAnnotation(autoIncrementAnnotation);
        }

        if (uniqueAnnotation != null) {
            pair.setUniqueAnnotation(uniqueAnnotation);
        }
    }

    private GetterSetterPair getGetterSetterPair(Map<String, GetterSetterPair> pairMap, String key) {
        if (pairMap.containsKey(key)) {
            return pairMap.get(key);
        }

        final GetterSetterPair pair = new GetterSetterPair(key);
        pairMap.put(key, pair);
        return pair;
    }

    private ColumnInfo createColumnInfo(GetterSetterPair pair, TypeAdapterManager adapterManager) {
        final Column columnAnnotation = pair.getColumnAnnotation();
        final Id idAnnotation = pair.getIdAnnotation();
        final ExecutableElement setter = pair.getSetterMethod();
        final ExecutableElement getter = pair.getGetterMethod();
        final String columnName;
        if (columnAnnotation != null) {
            columnName = columnAnnotation.value();
        } else if(idAnnotation != null) {
            columnName = DEFAULT_ID_COLUMN_NAME;
        } else {
            final ExecutableElement method = getter != null ? getter : setter;
            throw new MissingColumnAnnotationException("The @Column annotation is missing from the method " + method.getSimpleName() + ". You need to set the column name with @Column.", method);
        }

        final Set<Constraint> constraints = new HashSet<>();
        if (pair.getIdAnnotation() != null) {
            constraints.add(Constraint.PRIMARY_KEY);
        }
        if (pair.getAutoIncrementAnnotation() != null) {
            constraints.add(Constraint.AUTO_INCREMENT);
        }
        if (pair.getUniqueAnnotation() != null) {
            constraints.add(Constraint.UNIQUE);
        }

        final EntityInfo childEntityInfo = pair.getColumnType() == ColumnType.ENTITY
                ? analyze((TypeElement) mProcessingEnvironment.getTypeUtils().asElement(pair.getTypeMirror()), adapterManager)
                : null;

        return new ColumnInfoImpl(pair.getColumnType(), pair.getTypeMirror(), constraints, columnName, pair.getTypeAdapters(), childEntityInfo, pair.getIdentifier(), getter, setter);
    }

    private static boolean equals(Object a, Object b) {
        return (a == b) || (a != null && a.equals(b));
    }

    private static class EntityInfoWrapper implements EntityInfo {

        private EntityInfo mEntityInfo;

        @Override
        public String getTableName() {
            return mEntityInfo.getTableName();
        }

        @Override
        public TypeElement getEntityElement() {
            return mEntityInfo.getEntityElement();
        }

        @Override
        public ColumnInfo getIdColumn() {
            return mEntityInfo.getIdColumn();
        }

        @Override
        public List<ColumnInfo> getColumns() {
            return mEntityInfo.getColumns();
        }

        public void setEntityInfo(EntityInfo entityInfo) {
            mEntityInfo = entityInfo;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o instanceof EntityInfo) {
                final EntityInfo other = (EntityInfo) o;
                return other.equals(mEntityInfo);
            }
            return false;
        }

        @Override
        public int hashCode() {
            return mEntityInfo != null ? mEntityInfo.hashCode() : 0;
        }
    }
}
