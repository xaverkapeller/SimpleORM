package com.github.wrdlbrnft.simpleorm.processor.builder.entitymanager;

import com.github.wrdlbrnft.codebuilder.annotations.Annotations;
import com.github.wrdlbrnft.codebuilder.arrays.Arrays;
import com.github.wrdlbrnft.codebuilder.code.Block;
import com.github.wrdlbrnft.codebuilder.code.CodeElement;
import com.github.wrdlbrnft.codebuilder.elements.forloop.item.Foreach;
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
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Modifier;

/**
 * Created with Android Studio
 * User: Xaver
 * Date: 17/07/16
 */
public class EntityManagerBuilder {

    private static final Method METHOD_ENTITIES_TO_SAVE = Methods.stub("getEntitiesToSave");
    private static final Method METHOD_PUT = Methods.stub("put");
    private static final Method METHOD_GET_TIME = Methods.stub("getTime");
    private static final Method METHOD_INSERT = Methods.stub("insert");
    private static final Method METHOD_QUERY = Methods.stub("query");
    private static final Method METHOD_GET_SELECTION = Methods.stub("getSelection");
    private static final Method METHOD_GET_SELECTION_ARGS = Methods.stub("getSelectionArgs");
    private static final Method METHOD_GET_ORDER_BY = Methods.stub("getOrderBy");
    private static final Method METHOD_GET_LIMIT = Methods.stub("getLimit");
    private static final Method METHOD_GET_COUNT = Methods.stub("getCount");
    private static final Method METHOD_GET_COLUMN_INDEX = Methods.stub("getColumnIndex");
    private static final Method METHOD_MOVE_TO_POSITION = Methods.stub("moveToPosition");
    private static final Method METHOD_CONVERT_TO = Methods.stub("convertTo");
    private static final Method METHOD_CONVERT_FROM = Methods.stub("convertFrom");

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

    public EntityManagerBuilder(ProcessingEnvironment processingEnvironment) {
        mProcessingEnvironment = processingEnvironment;
    }

    public Implementation build(final EntityInfo info, DatabaseImplementationBuilder.EntityImplementationCache cache) {
        final Type entityType = Types.of(info.getEntityElement());

        final Implementation.Builder builder = new Implementation.Builder();
        builder.setExtendedType(Types.generic(SimpleOrmTypes.BASE_ENTITY_MANAGER.asType(), entityType));
        builder.setModifiers(EnumSet.of(Modifier.PRIVATE, Modifier.STATIC));

        builder.addConstructor(new Constructor.Builder()
                .setModifiers(EnumSet.of(Modifier.PUBLIC))
                .setCode(new ExecutableBuilder() {

                    private Variable mParamProvider;

                    @Override
                    protected List<Variable> createParameters() {
                        final List<Variable> parameters = new ArrayList<>();
                        parameters.add(mParamProvider = Variables.of(SimpleOrmTypes.SQLITE_PROVIDER.asType()));
                        return parameters;
                    }

                    @Override
                    protected void write(Block block) {
                        block.append(Methods.SUPER.call(mParamProvider)).append(";");
                    }
                })
                .build());

        final Set<TypeAdapterInfo> infos = new HashSet<>();
        for (ColumnInfo columnInfo : info.getColumns()) {
            infos.addAll(columnInfo.getTypeAdapters());
        }

        final Map<TypeAdapterInfo, Field> adapterFieldMap = new HashMap<>();
        for (TypeAdapterInfo typeAdapterInfo : infos) {
            final Type type = Types.of(typeAdapterInfo.getAdapterElement());
            final Field field = new Field.Builder()
                    .setType(type)
                    .setModifiers(EnumSet.of(Modifier.PRIVATE, Modifier.STATIC, Modifier.FINAL))
                    .setInitialValue(type.newInstance())
                    .build();
            builder.addField(field);
            adapterFieldMap.put(typeAdapterInfo, field);
        }

        final Implementation iteratorImpl = buildIterator(info, cache, adapterFieldMap);
        builder.addNestedImplementation(iteratorImpl);

        builder.addMethod(new Method.Builder()
                .setName("performSave")
                .setModifiers(EnumSet.of(Modifier.PROTECTED))
                .addAnnotation(Annotations.forType(Override.class))
                .setCode(new ExecutableBuilder() {

                    private Variable mWritableSQLiteWrapper;
                    private Variable mSaveParameters;

                    @Override
                    protected List<Variable> createParameters() {
                        final List<Variable> parameters = new ArrayList<>();
                        parameters.add(mWritableSQLiteWrapper = Variables.of(SimpleOrmTypes.WRITABLE_SQLITE_WRAPPER.asType()));
                        parameters.add(mSaveParameters = Variables.of(Types.generic(SimpleOrmTypes.SAVE_PARAMETERS.asType(), entityType)));
                        return parameters;
                    }

                    @Override
                    protected void write(Block block) {
                        final Type entityType = Types.of(info.getEntityElement());
                        final Variable entities = Variables.of(Types.generic(Types.LIST, entityType), Modifier.FINAL);
                        block.set(entities, METHOD_ENTITIES_TO_SAVE.callOnTarget(mSaveParameters)).append(";").newLine();

                        block.append(new Foreach.Builder()
                                .setCollection(entities)
                                .setItemType(entityType)
                                .setIteration(new Foreach.Iteration() {
                                    @Override
                                    public void onIteration(Block block, Variable entity) {
                                        final Variable values = Variables.of(SimpleOrmTypes.CONTENT_VALUES.asType(), Modifier.FINAL);
                                        block.set(values, SimpleOrmTypes.CONTENT_VALUES.asType().newInstance()).append(";");
                                        for (ColumnInfo columnInfo : info.getColumns()) {
                                            final ColumnType columnType = columnInfo.getColumnType();
                                            if (columnType == ColumnType.ENTITY) {
                                                continue;
                                            }

                                            final Method getterMethod = Methods.from(columnInfo.getGetterElement());
                                            final List<TypeAdapterInfo> typeAdapters = columnInfo.getTypeAdapters();
                                            final CodeElement value = applyAdaptersConvertFrom(typeAdapters, getterMethod.callOnTarget(entity));
                                            block.newLine().append(METHOD_PUT.callOnTarget(values, Values.of(columnInfo.getColumnName()), value)).append(";");
                                        }

                                        final Variable id = Variables.of(Types.Primitives.LONG, Modifier.FINAL);
                                        block.newLine().set(id, METHOD_INSERT.callOnTarget(mWritableSQLiteWrapper, Values.of(info.getTableName()), values)).append(";");

                                        final ColumnInfo idColumn = info.getIdColumn();
                                        if (idColumn != null) {
                                            block.newLine().append(Methods.from(idColumn.getSetterElement()).callOnTarget(entity, id)).append(";");
                                        }
                                    }

                                    private CodeElement applyAdaptersConvertFrom(List<TypeAdapterInfo> typeAdapters, CodeElement codeElement) {
                                        if (typeAdapters.isEmpty()) {
                                            return codeElement;
                                        }

                                        final int lastIndex = typeAdapters.size() - 1;
                                        final TypeAdapterInfo info = typeAdapters.get(lastIndex);
                                        final Field field = adapterFieldMap.get(info);
                                        return METHOD_CONVERT_FROM.callOnTarget(field, applyAdaptersConvertFrom(typeAdapters.subList(0, lastIndex), codeElement));
                                    }
                                })
                                .build());
                    }
                })
                .build());

        builder.addMethod(new Method.Builder()
                .setName("performRemove")
                .setModifiers(EnumSet.of(Modifier.PROTECTED))
                .addAnnotation(Annotations.forType(Override.class))
                .setCode(new ExecutableBuilder() {

                    private Variable mWritableSQLiteWrapper;
                    private Variable mRemoveParameters;

                    @Override
                    protected List<Variable> createParameters() {
                        final List<Variable> parameters = new ArrayList<>();
                        parameters.add(mWritableSQLiteWrapper = Variables.of(SimpleOrmTypes.WRITABLE_SQLITE_WRAPPER.asType()));
                        parameters.add(mRemoveParameters = Variables.of(Types.generic(SimpleOrmTypes.REMOVE_PARAMETERS.asType(), entityType)));
                        return parameters;
                    }

                    @Override
                    protected void write(Block block) {

                    }
                })
                .build());

        builder.addMethod(new Method.Builder()
                .setName("performQuery")
                .setModifiers(EnumSet.of(Modifier.PROTECTED))
                .addAnnotation(Annotations.forType(Override.class))
                .setReturnType(Types.generic(SimpleOrmTypes.ENTITY_ITERATOR.asType(), entityType))
                .setCode(new ExecutableBuilder() {

                    private Variable mReadableSQLiteWrapper;
                    private Variable mQueryParameters;

                    @Override
                    protected List<Variable> createParameters() {
                        final List<Variable> parameters = new ArrayList<>();
                        parameters.add(mReadableSQLiteWrapper = Variables.of(SimpleOrmTypes.READABLE_SQLITE_WRAPPER.asType()));
                        parameters.add(mQueryParameters = Variables.of(SimpleOrmTypes.QUERY_PARAMETERS.asType()));
                        return parameters;
                    }

                    @Override
                    protected void write(Block block) {
                        final Variable selection = Variables.of(SimpleOrmTypes.SELECTION.asType(), Modifier.FINAL);
                        block.set(selection, METHOD_GET_SELECTION.callOnTarget(mQueryParameters)).append(";").newLine();

                        final List<CodeElement> columns = new ArrayList<>();
                        for (ColumnInfo columnInfo : info.getColumns()) {
                            columns.add(Values.of(columnInfo.getColumnName()));
                        }

                        final Variable wrapper = Variables.of(SimpleOrmTypes.CURSOR_WRAPPER.asType(), Modifier.FINAL);
                        block.set(wrapper, METHOD_QUERY.callOnTarget(mReadableSQLiteWrapper,
                                Values.of(info.getTableName()),
                                Arrays.of(Types.STRING, columns),
                                METHOD_GET_SELECTION.callOnTarget(selection),
                                METHOD_GET_SELECTION_ARGS.callOnTarget(selection),
                                Values.ofNull(),
                                Values.ofNull(),
                                METHOD_GET_ORDER_BY.callOnTarget(mQueryParameters),
                                METHOD_GET_LIMIT.callOnTarget(mQueryParameters)
                        )).append(";").newLine();

                        block.append("return ").append(iteratorImpl.newInstance(wrapper)).append(";");
                    }
                })
                .build());

        return builder.build();
    }

    private Implementation buildIterator(final EntityInfo info, final DatabaseImplementationBuilder.EntityImplementationCache cache, final Map<TypeAdapterInfo, Field> adapterFieldMap) {
        final Type entityType = Types.of(info.getEntityElement());

        final Implementation.Builder builder = new Implementation.Builder();
        builder.setModifiers(EnumSet.of(Modifier.PRIVATE, Modifier.STATIC));
        builder.setExtendedType(Types.generic(SimpleOrmTypes.BASE_ENTITY_ITERATOR.asType(), entityType));

        final Field wrapperField = new Field.Builder()
                .setType(SimpleOrmTypes.CURSOR_WRAPPER.asType())
                .setModifiers(EnumSet.of(Modifier.PRIVATE, Modifier.FINAL))
                .build();
        builder.addField(wrapperField);

        final Map<ColumnInfo, Field> indexFieldMap = new HashMap<>();

        for (ColumnInfo columnInfo : info.getColumns()) {
            final Field indexField = new Field.Builder()
                    .setType(Types.Primitives.INTEGER)
                    .setModifiers(EnumSet.of(Modifier.PRIVATE, Modifier.STATIC))
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
                        parameters.add(mWrapper = Variables.of(SimpleOrmTypes.CURSOR_WRAPPER.asType()));
                        return parameters;
                    }

                    @Override
                    protected void write(Block block) {
                        block.append(Methods.SUPER.call(entityType.classObject(), METHOD_GET_COUNT.callOnTarget(mWrapper))).append(";").newLine();
                        block.set(wrapperField, mWrapper).append(";");

                        for (ColumnInfo columnInfo : info.getColumns()) {
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
                            final Field indexField = indexFieldMap.get(columnInfo);
                            final Type type = COLUMN_TYPE_MAP.get(columnInfo.getColumnType());
                            final Variable parameter = Variables.of(type, Modifier.FINAL);
                            final Method method = COLUMN_METHOD_MAP.get(columnInfo.getColumnType());
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

        return builder.build();
    }
}