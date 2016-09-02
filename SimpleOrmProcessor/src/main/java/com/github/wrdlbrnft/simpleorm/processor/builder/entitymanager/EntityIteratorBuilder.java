package com.github.wrdlbrnft.simpleorm.processor.builder.entitymanager;

import com.github.wrdlbrnft.codebuilder.annotations.Annotations;
import com.github.wrdlbrnft.codebuilder.code.Block;
import com.github.wrdlbrnft.codebuilder.code.CodeElement;
import com.github.wrdlbrnft.codebuilder.elements.values.Values;
import com.github.wrdlbrnft.codebuilder.executables.Constructor;
import com.github.wrdlbrnft.codebuilder.executables.ExecutableBuilder;
import com.github.wrdlbrnft.codebuilder.executables.Method;
import com.github.wrdlbrnft.codebuilder.executables.Methods;
import com.github.wrdlbrnft.codebuilder.implementations.Implementation;
import com.github.wrdlbrnft.codebuilder.types.Type;
import com.github.wrdlbrnft.codebuilder.types.Types;
import com.github.wrdlbrnft.codebuilder.util.MapBuilder;
import com.github.wrdlbrnft.codebuilder.variables.Field;
import com.github.wrdlbrnft.codebuilder.variables.Variable;
import com.github.wrdlbrnft.codebuilder.variables.Variables;
import com.github.wrdlbrnft.simpleorm.processor.SimpleOrmTypes;
import com.github.wrdlbrnft.simpleorm.processor.analyzer.entity.ColumnInfo;
import com.github.wrdlbrnft.simpleorm.processor.analyzer.entity.ColumnType;
import com.github.wrdlbrnft.simpleorm.processor.analyzer.entity.EntityInfo;
import com.github.wrdlbrnft.simpleorm.processor.analyzer.typeadapter.TypeAdapterInfo;
import com.github.wrdlbrnft.simpleorm.processor.builder.databases.implementation.DatabaseImplementationBuilder;
import com.github.wrdlbrnft.simpleorm.processor.builder.entity.EntityImplementationInfo;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Modifier;

import static com.github.wrdlbrnft.simpleorm.processor.builder.entitymanager.EntityManagerBuilder.METHOD_CONVERT_TO;
import static com.github.wrdlbrnft.simpleorm.processor.builder.entitymanager.EntityManagerBuilder.METHOD_GET_COLUMN_INDEX;
import static com.github.wrdlbrnft.simpleorm.processor.builder.entitymanager.EntityManagerBuilder.METHOD_GET_COUNT;
import static com.github.wrdlbrnft.simpleorm.processor.builder.entitymanager.EntityManagerBuilder.METHOD_MOVE_TO_POSITION;

/**
 * Created with Android Studio
 * User: Xaver
 * Date: 02/09/16
 */

class EntityIteratorBuilder {

    private static final Map<ColumnType, Type> COLUMN_TYPE_MAP = new MapBuilder<ColumnType, Type>()
            .put(ColumnType.PRIMITIVE_INT, Types.Primitives.INTEGER)
            .put(ColumnType.INT, Types.Boxed.INTEGER)
            .put(ColumnType.PRIMITIVE_LONG, Types.Primitives.LONG)
            .put(ColumnType.LONG, Types.Boxed.LONG)
            .put(ColumnType.PRIMITIVE_BOOLEAN, Types.Primitives.BOOLEAN)
            .put(ColumnType.BOOLEAN, Types.Boxed.BOOLEAN)
            .put(ColumnType.PRIMITIVE_FLOAT, Types.Primitives.FLOAT)
            .put(ColumnType.FLOAT, Types.Boxed.FLOAT)
            .put(ColumnType.PRIMITIVE_DOUBLE, Types.Primitives.DOUBLE)
            .put(ColumnType.DOUBLE, Types.Boxed.DOUBLE)
            .put(ColumnType.STRING, Types.STRING)
            .put(ColumnType.DATE, Types.Boxed.LONG)
            .build();

    private static final Map<ColumnType, Method> COLUMN_METHOD_MAP = new MapBuilder<ColumnType, Method>()
            .put(ColumnType.PRIMITIVE_INT, Methods.stub("getInt"))
            .put(ColumnType.INT, Methods.stub("getIntOrNull"))
            .put(ColumnType.PRIMITIVE_LONG, Methods.stub("getLong"))
            .put(ColumnType.LONG, Methods.stub("getLongOrNull"))
            .put(ColumnType.PRIMITIVE_BOOLEAN, Methods.stub("getBoolean"))
            .put(ColumnType.BOOLEAN, Methods.stub("getBooleanOrNull"))
            .put(ColumnType.PRIMITIVE_FLOAT, Methods.stub("getFloat"))
            .put(ColumnType.FLOAT, Methods.stub("getFloatOrNull"))
            .put(ColumnType.PRIMITIVE_DOUBLE, Methods.stub("getDouble"))
            .put(ColumnType.DOUBLE, Methods.stub("getDoubleOrNull"))
            .put(ColumnType.STRING, Methods.stub("getString"))
            .put(ColumnType.DATE, Methods.stub("getLongOrNull"))
            .build();

    private final ProcessingEnvironment mProcessingEnvironment;

    EntityIteratorBuilder(ProcessingEnvironment processingEnvironment) {
        mProcessingEnvironment = processingEnvironment;
    }

    public EntityIteratorInfo build(final EntityInfo info, final DatabaseImplementationBuilder.EntityImplementationCache cache, final Map<TypeAdapterInfo, Field> adapterFieldMap) {
        final Type entityType = Types.of(info.getEntityElement());

        final Implementation.Builder builder = new Implementation.Builder();
        builder.setModifiers(EnumSet.of(Modifier.PRIVATE, Modifier.STATIC));
        builder.setExtendedType(Types.generic(SimpleOrmTypes.BASE_ENTITY_ITERATOR, entityType));

        final Field wrapperField = new Field.Builder()
                .setType(SimpleOrmTypes.CURSOR_WRAPPER)
                .setModifiers(EnumSet.of(Modifier.PRIVATE, Modifier.FINAL))
                .build();
        builder.addField(wrapperField);

        final Map<ColumnInfo, Field> indexFieldMap = new HashMap<>();

        for (ColumnInfo columnInfo : info.getColumns()) {
            if (columnInfo.getColumnType() == ColumnType.ENTITY) {
                continue;
            }
            final Field indexField = new Field.Builder()
                    .setType(Types.Primitives.INTEGER)
                    .setModifiers(EnumSet.of(Modifier.PRIVATE, Modifier.FINAL))
                    .build();
            builder.addField(indexField);
            indexFieldMap.put(columnInfo, indexField);
        }

        builder.addConstructor(new Constructor.Builder()
                .setCode(new ExecutableBuilder() {

                    private Variable mWrapper;

                    @Override
                    protected List<Variable> createParameters() {
                        final ArrayList<Variable> parameters = new ArrayList<>();
                        parameters.add(mWrapper = Variables.of(SimpleOrmTypes.CURSOR_WRAPPER));
                        return parameters;
                    }

                    @Override
                    protected void write(Block block) {
                        block.append(Methods.SUPER.call(entityType.classObject(), METHOD_GET_COUNT.callOnTarget(mWrapper))).append(";").newLine();
                        block.set(wrapperField, mWrapper).append(";");

                        for (ColumnInfo columnInfo : info.getColumns()) {
                            if (columnInfo.getColumnType() == ColumnType.ENTITY) {
                                continue;
                            }
                            final Field indexField = indexFieldMap.get(columnInfo);
                            block.newLine().set(indexField, METHOD_GET_COLUMN_INDEX.callOnTarget(mWrapper, Values.of(columnInfo.getColumnName()))).append(";");
                        }
                    }
                })
                .build());

        builder.addMethod(new Method.Builder()
                .setModifiers(EnumSet.of(Modifier.PROTECTED))
                .setName("readFromPosition")
                .setReturnType(entityType)
                .addAnnotation(Annotations.forType(Override.class))
                .setCode(new ExecutableBuilder() {

                    private Variable mPosition;

                    @Override
                    protected List<Variable> createParameters() {
                        final ArrayList<Variable> parameters = new ArrayList<>();
                        parameters.add(mPosition = Variables.of(Types.Primitives.INTEGER));
                        return parameters;
                    }

                    @Override
                    protected void write(Block block) {
                        block.append(METHOD_MOVE_TO_POSITION.callOnTarget(wrapperField, mPosition)).append(";").newLine();
                        final EntityImplementationInfo implementationInfo = cache.get(info);
                        final List<ColumnInfo> constructorParameters = implementationInfo.getConstructorParameters();
                        final CodeElement[] parameters = new CodeElement[constructorParameters.size()];
                        for (int i = 0, size = constructorParameters.size(); i < size; i++) {
                            final ColumnInfo columnInfo = constructorParameters.get(i);
                            final ColumnType columnType = columnInfo.getColumnType();
                            if (columnType == ColumnType.ENTITY) {
                                parameters[i] = Values.ofNull();
                                continue;
                            }
                            final Field indexField = indexFieldMap.get(columnInfo);
                            final Type type = COLUMN_TYPE_MAP.get(columnType);
                            final Variable parameter = Variables.of(type, Modifier.FINAL);
                            final Method method = COLUMN_METHOD_MAP.get(columnType);
                            block.set(parameter, method.callOnTarget(wrapperField, indexField)).append(";").newLine();
                            parameters[i] = applyAdaptersConvertTo(columnInfo.getTypeAdapters(), parameter);
                        }
                        block.append("return ").append(implementationInfo.getImplementation().newInstance(parameters)).append(";");
                    }

                    private CodeElement applyAdaptersConvertTo(List<TypeAdapterInfo> typeAdapters, CodeElement codeElement) {
                        if (typeAdapters.isEmpty()) {
                            return codeElement;
                        }

                        final TypeAdapterInfo info = typeAdapters.get(0);
                        final Field field = adapterFieldMap.get(info);
                        return METHOD_CONVERT_TO.callOnTarget(field, applyAdaptersConvertTo(typeAdapters.subList(1, typeAdapters.size()), codeElement));
                    }
                })
                .build());

        final Implementation implementation = builder.build();
        return new EntityIteratorInfoImpl(implementation);
    }

    private static class EntityIteratorInfoImpl implements EntityIteratorInfo {

        private final Implementation mImplementation;

        private EntityIteratorInfoImpl(Implementation implementation) {
            mImplementation = implementation;
        }

        @Override
        public Implementation getImplementation() {
            return mImplementation;
        }
    }
}
