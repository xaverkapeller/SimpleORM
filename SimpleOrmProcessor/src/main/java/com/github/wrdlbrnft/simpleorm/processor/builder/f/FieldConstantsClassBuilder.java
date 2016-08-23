package com.github.wrdlbrnft.simpleorm.processor.builder.f;

import com.github.wrdlbrnft.codebuilder.code.CodeElement;
import com.github.wrdlbrnft.codebuilder.code.SourceFile;
import com.github.wrdlbrnft.codebuilder.elements.values.Values;
import com.github.wrdlbrnft.codebuilder.implementations.Implementation;
import com.github.wrdlbrnft.codebuilder.types.Type;
import com.github.wrdlbrnft.codebuilder.types.Types;
import com.github.wrdlbrnft.codebuilder.util.MapBuilder;
import com.github.wrdlbrnft.codebuilder.util.Utils;
import com.github.wrdlbrnft.codebuilder.variables.Field;
import com.github.wrdlbrnft.simpleorm.processor.analyzer.entity.ColumnInfo;
import com.github.wrdlbrnft.simpleorm.processor.analyzer.entity.ColumnType;
import com.github.wrdlbrnft.simpleorm.processor.analyzer.entity.EntityInfo;
import com.github.wrdlbrnft.simpleorm.processor.utils.NameUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;

import static com.github.wrdlbrnft.simpleorm.processor.SimpleOrmTypes.BOOLEAN_FIELD;
import static com.github.wrdlbrnft.simpleorm.processor.SimpleOrmTypes.BOOLEAN_FIELD_IMPL;
import static com.github.wrdlbrnft.simpleorm.processor.SimpleOrmTypes.DATE_FIELD;
import static com.github.wrdlbrnft.simpleorm.processor.SimpleOrmTypes.DATE_FIELD_IMPL;
import static com.github.wrdlbrnft.simpleorm.processor.SimpleOrmTypes.DOUBLE_FIELD;
import static com.github.wrdlbrnft.simpleorm.processor.SimpleOrmTypes.DOUBLE_FIELD_IMPL;
import static com.github.wrdlbrnft.simpleorm.processor.SimpleOrmTypes.ENTITY_FIELD;
import static com.github.wrdlbrnft.simpleorm.processor.SimpleOrmTypes.ENTITY_FIELD_IMPL;
import static com.github.wrdlbrnft.simpleorm.processor.SimpleOrmTypes.FLOAT_FIELD;
import static com.github.wrdlbrnft.simpleorm.processor.SimpleOrmTypes.FLOAT_FIELD_IMPL;
import static com.github.wrdlbrnft.simpleorm.processor.SimpleOrmTypes.INT_FIELD;
import static com.github.wrdlbrnft.simpleorm.processor.SimpleOrmTypes.INT_FIELD_IMPL;
import static com.github.wrdlbrnft.simpleorm.processor.SimpleOrmTypes.LONG_FIELD;
import static com.github.wrdlbrnft.simpleorm.processor.SimpleOrmTypes.LONG_FIELD_IMPL;
import static com.github.wrdlbrnft.simpleorm.processor.SimpleOrmTypes.STRING_FIELD;
import static com.github.wrdlbrnft.simpleorm.processor.SimpleOrmTypes.STRING_FIELD_IMPL;

/**
 * Created with Android Studio
 * User: Xaver
 * Date: 04/07/16
 */

public class FieldConstantsClassBuilder {

    private final ProcessingEnvironment mProcessingEnvironment;
    private final Map<ColumnType, Type> mFieldInterfaceMap;
    private final Map<ColumnType, Type> mFieldImplementationMap;

    private final List<FieldInfo> mFieldInfos = new ArrayList<>();

    public FieldConstantsClassBuilder(ProcessingEnvironment processingEnvironment) {
        mProcessingEnvironment = processingEnvironment;
        mFieldInterfaceMap = new MapBuilder<ColumnType, Type>()
                .put(ColumnType.BOOLEAN, BOOLEAN_FIELD.asType())
                .put(ColumnType.DATE, DATE_FIELD.asType())
                .put(ColumnType.DOUBLE, DOUBLE_FIELD.asType())
                .put(ColumnType.ENTITY, ENTITY_FIELD.asType())
                .put(ColumnType.FLOAT, FLOAT_FIELD.asType())
                .put(ColumnType.INT, INT_FIELD.asType())
                .put(ColumnType.LONG, LONG_FIELD.asType())
                .put(ColumnType.STRING, STRING_FIELD.asType())
                .build();
        mFieldImplementationMap = new MapBuilder<ColumnType, Type>()
                .put(ColumnType.BOOLEAN, BOOLEAN_FIELD_IMPL.asType())
                .put(ColumnType.DATE, DATE_FIELD_IMPL.asType())
                .put(ColumnType.DOUBLE, DOUBLE_FIELD_IMPL.asType())
                .put(ColumnType.ENTITY, ENTITY_FIELD_IMPL.asType())
                .put(ColumnType.FLOAT, FLOAT_FIELD_IMPL.asType())
                .put(ColumnType.INT, INT_FIELD_IMPL.asType())
                .put(ColumnType.LONG, LONG_FIELD_IMPL.asType())
                .put(ColumnType.STRING, STRING_FIELD_IMPL.asType())
                .build();
    }

    public List<FieldInfo> build(Collection<EntityInfo> entityInfos) throws IOException {
        if (entityInfos.isEmpty()) {
            return new ArrayList<>();
        }

        mFieldInfos.clear();

        final Implementation.Builder fieldConstantClass = new Implementation.Builder();
        fieldConstantClass.setName("F");
        fieldConstantClass.setModifiers(EnumSet.of(Modifier.PUBLIC, Modifier.FINAL));

        String packageName = null;

        for (EntityInfo info : entityInfos) {
            if(packageName == null) {
                packageName = Utils.getPackageName(info.getEntityElement());
            }

            final Implementation entitySubclass = createFieldClassForEntity(info);
            fieldConstantClass.addNestedImplementation(entitySubclass);
        }

        final SourceFile sourceFile = SourceFile.create(mProcessingEnvironment, packageName);
        sourceFile.write(fieldConstantClass.build());
        sourceFile.flushAndClose();

        return new ArrayList<>(mFieldInfos);
    }

    private Implementation createFieldClassForEntity(EntityInfo info) {
        final Implementation.Builder builder = new Implementation.Builder();

        final TypeElement entityElement = info.getEntityElement();
        builder.setName(entityElement.getSimpleName().toString().toLowerCase());
        builder.setModifiers(EnumSet.of(Modifier.PUBLIC, Modifier.STATIC, Modifier.FINAL));

        for (ColumnInfo contentColumn : info.getColumns()) {
            final FieldInfo fieldInfo = createFieldInfo(contentColumn, info);
            addColumnField(builder, fieldInfo);
            mFieldInfos.add(fieldInfo);
        }

        return builder.build();
    }

    private void addColumnField(Implementation.Builder builder, FieldInfo fieldInfo) {
        final ColumnInfo column = fieldInfo.getColumnInfo();

        builder.addField(new Field.Builder()
                .setModifiers(EnumSet.of(Modifier.PUBLIC, Modifier.STATIC, Modifier.FINAL))
                .setName(NameUtils.toSnakeCase(column.getIdentifier()))
                .setType(getFieldType(fieldInfo))
                .setInitialValue(getNewInstanceOfField(fieldInfo))
                .build());
    }

    private FieldInfo createFieldInfo(ColumnInfo info, EntityInfo entityInfo) {
        return new FieldInfoImpl(entityInfo, info);
    }

    private Type getFieldType(FieldInfo fieldInfo) {
        final EntityInfo entity = fieldInfo.getEntityInfo();
        final ColumnInfo column = fieldInfo.getColumnInfo();
        final ColumnType columnType = column.getColumnType();

        final Type baseFieldType = mFieldInterfaceMap.get(columnType);
        if (columnType == ColumnType.ENTITY) {
            return Types.generic(baseFieldType, Types.of(entity.getEntityElement()), Types.of(column.getTypeMirror()));
        }
        return Types.generic(baseFieldType, Types.of(entity.getEntityElement()));
    }

    private CodeElement getNewInstanceOfField(FieldInfo fieldInfo) {
        final Type instanceType = getInstanceType(fieldInfo);
        final ColumnInfo columnInfo = fieldInfo.getColumnInfo();
        return instanceType.newInstance(Values.of(columnInfo.getColumnName()));
    }

    private Type getInstanceType(FieldInfo fieldInfo) {
        final EntityInfo entity = fieldInfo.getEntityInfo();
        final ColumnInfo column = fieldInfo.getColumnInfo();
        final ColumnType columnType = column.getColumnType();

        final Type baseFieldType = mFieldImplementationMap.get(columnType);
        if (columnType == ColumnType.ENTITY) {
            return Types.generic(baseFieldType, Types.of(entity.getEntityElement()), Types.of(column.getTypeMirror()));
        }
        return Types.generic(baseFieldType, Types.of(entity.getEntityElement()));
    }

    private static class FieldInfoImpl implements FieldInfo {

        private final EntityInfo mEntityInfo;
        private final ColumnInfo mColumnInfo;

        private FieldInfoImpl(EntityInfo entityInfo, ColumnInfo columnInfo) {
            mEntityInfo = entityInfo;
            mColumnInfo = columnInfo;
        }

        @Override
        public EntityInfo getEntityInfo() {
            return mEntityInfo;
        }

        @Override
        public ColumnInfo getColumnInfo() {
            return mColumnInfo;
        }
    }
}
