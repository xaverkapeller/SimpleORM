package com.github.wrdlbrnft.simpleorm.processor.builder.entitymanager;

import com.github.wrdlbrnft.codebuilder.annotations.Annotations;
import com.github.wrdlbrnft.codebuilder.arrays.Arrays;
import com.github.wrdlbrnft.codebuilder.code.Block;
import com.github.wrdlbrnft.codebuilder.code.BlockWriter;
import com.github.wrdlbrnft.codebuilder.code.CodeElement;
import com.github.wrdlbrnft.codebuilder.elements.forloop.item.Foreach;
import com.github.wrdlbrnft.codebuilder.elements.ifs.If;
import com.github.wrdlbrnft.codebuilder.elements.values.Values;
import com.github.wrdlbrnft.codebuilder.executables.Constructor;
import com.github.wrdlbrnft.codebuilder.executables.ExecutableBuilder;
import com.github.wrdlbrnft.codebuilder.executables.Method;
import com.github.wrdlbrnft.codebuilder.executables.Methods;
import com.github.wrdlbrnft.codebuilder.implementations.Implementation;
import com.github.wrdlbrnft.codebuilder.types.Type;
import com.github.wrdlbrnft.codebuilder.types.Types;
import com.github.wrdlbrnft.codebuilder.util.MapBuilder;
import com.github.wrdlbrnft.codebuilder.util.Operators;
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
import com.github.wrdlbrnft.simpleorm.processor.utils.MappingTables;

import java.util.ArrayList;
import java.util.Collections;
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
    private static final Method METHOD_ENTITIES_TO_REMOVE = Methods.stub("getEntitiesToRemove");
    private static final Method METHOD_PUT = Methods.stub("put");
    private static final Method METHOD_DELETE = Methods.stub("delete");
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
    private static final Method METHOD_OR = Methods.stub("or");
    private static final Method METHOD_STATEMENT = Methods.stub("statement");
    private static final Method METHOD_VALUE_OF = Methods.stub("valueOf");
    private static final Method METHOD_BUILD = Methods.stub("build");
    private static final Method METHOD_IS_EMPTY = Methods.stub("isEmpty");
    private static final Method METHOD_VERIFY_ID = Methods.stub("verifyIdOrThrow");
    private static final Method METHOD_EXEC_SQL = Methods.stub("execSql");

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
        builder.setExtendedType(Types.generic(SimpleOrmTypes.BASE_ENTITY_MANAGER, entityType));
        builder.setModifiers(EnumSet.of(Modifier.PRIVATE, Modifier.STATIC));

        builder.addConstructor(new Constructor.Builder()
                .setModifiers(EnumSet.of(Modifier.PUBLIC))
                .setCode(new ExecutableBuilder() {

                    private Variable mParamProvider;

                    @Override
                    protected List<Variable> createParameters() {
                        final List<Variable> parameters = new ArrayList<>();
                        parameters.add(mParamProvider = Variables.of(SimpleOrmTypes.SQLITE_PROVIDER));
                        return parameters;
                    }

                    @Override
                    protected void write(Block block) {
                        block.append(Methods.SUPER.call(mParamProvider)).append(";");
                    }
                })
                .build());

        final Set<TypeAdapterInfo> infos = getAllTypeAdapters(info);

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
                        parameters.add(mWritableSQLiteWrapper = Variables.of(SimpleOrmTypes.WRITABLE_SQLITE_WRAPPER));
                        parameters.add(mSaveParameters = Variables.of(Types.generic(SimpleOrmTypes.SAVE_PARAMETERS, entityType)));
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
                                        appendSave(block, null, null, null, info, entity);
                                    }

                                    private void appendSave(Block block, EntityInfo parent, ColumnInfo parentColumn, Variable parentId, final EntityInfo child, Variable entity) {
                                        final List<ColumnInfo> entityColumns = new ArrayList<>();

                                        final Variable values = Variables.of(SimpleOrmTypes.CONTENT_VALUES, Modifier.FINAL);
                                        block.set(values, SimpleOrmTypes.CONTENT_VALUES.newInstance()).append(";");
                                        for (ColumnInfo columnInfo : child.getColumns()) {
                                            final ColumnType columnType = columnInfo.getColumnType();
                                            if (columnType == ColumnType.ENTITY) {
                                                entityColumns.add(columnInfo);
                                                continue;
                                            }

                                            final Method getterMethod = Methods.from(columnInfo.getGetterElement());
                                            final List<TypeAdapterInfo> typeAdapters = columnInfo.getTypeAdapters();
                                            final CodeElement value = applyAdaptersConvertFrom(typeAdapters, getterMethod.callOnTarget(entity));
                                            block.newLine().append(METHOD_PUT.callOnTarget(values, Values.of(columnInfo.getColumnName()), value)).append(";");
                                        }

                                        final Variable id = Variables.of(Types.Primitives.LONG, Modifier.FINAL);
                                        block.newLine().set(id, METHOD_INSERT.callOnTarget(mWritableSQLiteWrapper, Values.of(child.getTableName()), values)).append(";");
                                        block.newLine().append(METHOD_VERIFY_ID.call(id, entity)).append(";");
                                        final ColumnInfo idColumn = child.getIdColumn();
                                        if (idColumn != null) {
                                            block.newLine().append(Methods.from(idColumn.getSetterElement()).callOnTarget(entity, id)).append(";");
                                        }

                                        if (parent != null) {
                                            block.newLine().newLine();
                                            final Variable mappingValues = MappingTables.appendContentValuesForMapping(block, parentId, id);
                                            final Variable mappingId = Variables.of(Types.Primitives.LONG, Modifier.FINAL);
                                            final String mappingTableName = MappingTables.getTableName(parent, parentColumn);
                                            block.set(mappingId, METHOD_INSERT.callOnTarget(mWritableSQLiteWrapper, Values.of(mappingTableName), mappingValues)).append(";").newLine();
                                            block.append(METHOD_VERIFY_ID.call(mappingId, entity)).append(";");
                                        }

                                        for (final ColumnInfo entityColumn : entityColumns) {
                                            block.newLine().newLine();

                                            final EntityInfo childEntityInfo = entityColumn.getChildEntityInfo();
                                            final Type childEntityType = Types.of(childEntityInfo.getEntityElement());

                                            final ColumnInfo.CollectionType collectionType = entityColumn.getCollectionType();
                                            if (collectionType == ColumnInfo.CollectionType.NONE) {
                                                final Variable childEntity = Variables.of(childEntityType, Modifier.FINAL);
                                                block.set(childEntity, Methods.from(entityColumn.getGetterElement()).callOnTarget(entity)).append(";").newLine();
                                                appendSave(block, child, entityColumn, id, childEntityInfo, childEntity);
                                            } else if (collectionType == ColumnInfo.CollectionType.LIST) {
                                                block.append(new Foreach.Builder()
                                                        .setItemType(childEntityType)
                                                        .setCollection(Methods.from(entityColumn.getGetterElement()).callOnTarget(entity))
                                                        .setIteration(new Foreach.Iteration() {
                                                            @Override
                                                            public void onIteration(Block block, Variable childEntity) {
                                                                appendSave(block, child, entityColumn, id, childEntityInfo, childEntity);
                                                            }
                                                        })
                                                        .build());
                                            } else {
                                                throw new IllegalStateException("Encountered unkown collection type: " + collectionType);
                                            }
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
                        parameters.add(mWritableSQLiteWrapper = Variables.of(SimpleOrmTypes.WRITABLE_SQLITE_WRAPPER));
                        parameters.add(mRemoveParameters = Variables.of(Types.generic(SimpleOrmTypes.REMOVE_PARAMETERS, entityType)));
                        return parameters;
                    }

                    @Override
                    protected void write(Block block) {
                        final Variable selection = Variables.of(SimpleOrmTypes.SELECTION, Modifier.FINAL);
                        block.set(selection, METHOD_GET_SELECTION.callOnTarget(mRemoveParameters)).append(";").newLine();

                        block.append(new If.Builder()
                                .add(new Block().append("!").append(METHOD_IS_EMPTY.callOnTarget(selection)), new BlockWriter() {
                                    @Override
                                    protected void write(Block block) {
                                        final Variable resolvedSelection = Variables.of(Types.STRING, Modifier.FINAL);
                                        block.set(resolvedSelection, METHOD_GET_SELECTION.callOnTarget(selection, Values.of(info.getTableName()))).append(";").newLine();

                                        final Variable resolvedSelectionArgs = Variables.of(Types.arrayOf(Types.STRING), Modifier.FINAL);
                                        block.set(resolvedSelectionArgs, METHOD_GET_SELECTION_ARGS.callOnTarget(selection)).append(";").newLine();

                                        appendChildRemoval(block, info, resolvedSelection, resolvedSelectionArgs);

                                        block.append(METHOD_DELETE.callOnTarget(mWritableSQLiteWrapper,
                                                Values.of(info.getTableName()),
                                                resolvedSelection,
                                                resolvedSelectionArgs
                                        )).append(";").newLine();
                                    }
                                })
                                .build());
                        block.newLine();

                        final Type entityType = Types.of(info.getEntityElement());
                        final Variable entities = Variables.of(Types.generic(Types.LIST, entityType), Modifier.FINAL);
                        block.set(entities, METHOD_ENTITIES_TO_REMOVE.callOnTarget(mRemoveParameters)).append(";").newLine();

                        block.append(new If.Builder()
                                .add(METHOD_IS_EMPTY.callOnTarget(entities), new BlockWriter() {
                                    @Override
                                    protected void write(Block block) {
                                        block.append("return;");
                                    }
                                })
                                .build());
                        block.newLine();

                        final Variable builder = Variables.of(SimpleOrmTypes.SELECTION_BUILDER, Modifier.FINAL);
                        block.set(builder, SimpleOrmTypes.SELECTION_BUILDER.newInstance()).append(";").newLine();

                        final ColumnInfo idColumn = info.getIdColumn();
                        final Method idGetterMethod = Methods.from(idColumn.getGetterElement());

                        block.append(new Foreach.Builder()
                                .setCollection(entities)
                                .setItemType(entityType)
                                .setIteration(new Foreach.Iteration() {
                                    @Override
                                    public void onIteration(Block block, Variable entity) {
                                        final Variable id = Variables.of(Types.Boxed.LONG, Modifier.FINAL);
                                        block.set(id, applyAdaptersConvertFrom(idColumn.getTypeAdapters(), idGetterMethod.callOnTarget(entity))).append(";").newLine();
                                        block.append(new If.Builder()
                                                .add(Operators.operate(id, "!=", Values.ofNull()), new BlockWriter() {
                                                    @Override
                                                    protected void write(Block block) {
                                                        block.append(METHOD_OR.callOnTarget(METHOD_STATEMENT.callOnTarget(builder,
                                                                Values.of(idColumn.getColumnName()),
                                                                Values.of("="),
                                                                METHOD_VALUE_OF.callOnTarget(Types.STRING, id)
                                                        ))).append(";");
                                                    }
                                                })
                                                .build());
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
                        block.newLine();

                        final Variable entitySelection = Variables.of(SimpleOrmTypes.SELECTION, Modifier.FINAL);
                        block.set(entitySelection, METHOD_BUILD.callOnTarget(builder)).append(";").newLine();

                        final Variable resolvedEntitySelection = Variables.of(Types.STRING, Modifier.FINAL);
                        block.set(resolvedEntitySelection, METHOD_GET_SELECTION.callOnTarget(entitySelection, Values.of(info.getTableName()))).append(";").newLine();

                        final Variable resolvedEntitySelectionArgs = Variables.of(Types.arrayOf(Types.STRING), Modifier.FINAL);
                        block.set(resolvedEntitySelectionArgs, METHOD_GET_SELECTION_ARGS.callOnTarget(entitySelection)).append(";").newLine();

                        appendChildRemoval(block, info, resolvedEntitySelection, resolvedEntitySelectionArgs);

                        block.append(METHOD_DELETE.callOnTarget(mWritableSQLiteWrapper,
                                Values.of(info.getTableName()),
                                resolvedEntitySelection,
                                resolvedEntitySelectionArgs
                        )).append(";");
                    }

                    private void appendChildRemoval(Block block, EntityInfo info, Variable selection, Variable selectionArgs) {
                        for (ColumnInfo columnInfo : info.getColumns()) {
                            if (columnInfo.getColumnType() != ColumnType.ENTITY) {
                                continue;
                            }

                            final EntityInfo childEntityInfo = columnInfo.getChildEntityInfo();


                            for (CodeElement removeQuery : createRemoveQueries(info, columnInfo, childEntityInfo, selection)) {
                                block.append(METHOD_EXEC_SQL.callOnTarget(mWritableSQLiteWrapper, removeQuery, selectionArgs)).append(";").newLine();
                            }
                        }
                    }

                    private List<CodeElement> createRemoveQueries(EntityInfo parent, ColumnInfo columnInfo, EntityInfo child, Variable selection) {
                        return createRemoveQueries(Collections.singletonList(new ChildInfo(parent, columnInfo, child)), new HashSet<ChildInfo>(), selection);
                    }

                    private List<CodeElement> createRemoveQueries(List<ChildInfo> childInfos, Set<ChildInfo> handledRelations, Variable selection) {
                        final List<CodeElement> queryList = new ArrayList<>();

                        final StringBuilder removeEntityQueryBuilder = new StringBuilder();
                        final StringBuilder removeMappingQueryBuilder = new StringBuilder();

                        final int startIndex = childInfos.size() - 1;
                        for (int i = startIndex; i >= 0; i--) {
                            final ChildInfo childInfo = childInfos.get(i);
                            final EntityInfo parent = childInfo.getParentInfo();
                            final ColumnInfo parentColumnInfo = childInfo.getColumnInfo();
                            final EntityInfo child = childInfo.getChildInfo();
                            final String mappingTableName = MappingTables.getTableName(parent, parentColumnInfo);
                            final String parentTableName = parent.getTableName();
                            final String parentIdColumn = parent.getIdColumn().getColumnName();
                            final String childTableName = child.getTableName();
                            final String childIdColumn = child.getIdColumn().getColumnName();

                            for (ColumnInfo columnInfo : child.getColumns()) {
                                if (columnInfo.getColumnType() != ColumnType.ENTITY) {
                                    continue;
                                }

                                final EntityInfo childEntityInfo = columnInfo.getChildEntityInfo();
                                final ChildInfo childChildInfo = new ChildInfo(child, columnInfo, childEntityInfo);
                                if (handledRelations.add(childChildInfo)) {
                                    final List<ChildInfo> childChildInfos = new ArrayList<>(childInfos);
                                    childChildInfos.add(childChildInfo);
                                    queryList.addAll(createRemoveQueries(childChildInfos, handledRelations, selection));
                                }
                            }

                            if (i == startIndex) {
                                removeEntityQueryBuilder.append("DELETE FROM ").append(childTableName).append(" WHERE rowid in (")
                                        .append("SELECT ").append(childTableName).append(".rowid FROM ").append(childTableName);

                                removeMappingQueryBuilder.append("DELETE FROM ").append(mappingTableName).append(" WHERE rowid in (")
                                        .append("SELECT ").append(mappingTableName).append(".rowid FROM ").append(mappingTableName);
                            }

                            removeEntityQueryBuilder.append(" JOIN ").append(mappingTableName).append(" ON ").append(childTableName).append(".").append(childIdColumn).append("=").append(mappingTableName).append(".").append(MappingTables.COLUMN_CHILD_ID)
                                    .append(" JOIN ").append(parentTableName).append(" ON ").append(mappingTableName).append(".").append(MappingTables.COLUMN_PARENT_ID).append("=").append(parentTableName).append(".").append(parentIdColumn);

                            removeMappingQueryBuilder.append(" JOIN ").append(parentTableName).append(" ON ").append(mappingTableName).append(".").append(MappingTables.COLUMN_PARENT_ID).append("=").append(parentTableName + ".").append(parentIdColumn);
                        }

                        removeEntityQueryBuilder.append(" WHERE ");
                        removeMappingQueryBuilder.append(" WHERE ");

                        queryList.add(new Block().append(Values.of(removeEntityQueryBuilder.toString())).append(" + ").append(selection).append(" + ").append(Values.of(")")));
                        queryList.add(new Block().append(Values.of(removeMappingQueryBuilder.toString())).append(" + ").append(selection).append(" + ").append(Values.of(")")));

                        return queryList;
                    }

                    class ChildInfo {

                        private final EntityInfo mParentInfo;
                        private final ColumnInfo mColumnInfo;
                        private final EntityInfo mChildInfo;

                        ChildInfo(EntityInfo parentInfo, ColumnInfo columnInfo, EntityInfo childInfo) {
                            mParentInfo = parentInfo;
                            mColumnInfo = columnInfo;
                            mChildInfo = childInfo;
                        }

                        public EntityInfo getParentInfo() {
                            return mParentInfo;
                        }

                        public ColumnInfo getColumnInfo() {
                            return mColumnInfo;
                        }

                        public EntityInfo getChildInfo() {
                            return mChildInfo;
                        }

                        @Override
                        public boolean equals(Object o) {
                            if (this == o) return true;
                            if (o == null || getClass() != o.getClass()) return false;

                            ChildInfo childInfo = (ChildInfo) o;

                            if (mParentInfo != null ? !mParentInfo.equals(childInfo.mParentInfo) : childInfo.mParentInfo != null)
                                return false;
                            if (mColumnInfo != null ? !mColumnInfo.equals(childInfo.mColumnInfo) : childInfo.mColumnInfo != null)
                                return false;
                            return mChildInfo != null ? mChildInfo.equals(childInfo.mChildInfo) : childInfo.mChildInfo == null;

                        }

                        @Override
                        public int hashCode() {
                            int result = mParentInfo != null ? mParentInfo.hashCode() : 0;
                            result = 31 * result + (mColumnInfo != null ? mColumnInfo.hashCode() : 0);
                            result = 31 * result + (mChildInfo != null ? mChildInfo.hashCode() : 0);
                            return result;
                        }
                    }

                    private CodeElement createRemoveMappingQuery(EntityInfo parent, ColumnInfo columnInfo, Variable selection) {

                        final String parentTableName = parent.getTableName();
                        final String parentIdColumn = parent.getIdColumn().getColumnName();
                        final String mappingTableName = MappingTables.getTableName(parent, columnInfo);

                        final Block block = new Block();
                        block.append(Values.of("DELETE FROM " + mappingTableName + " WHERE rowid in ("
                                + "SELECT " + mappingTableName + ".rowid FROM " + mappingTableName
                                + " JOIN " + parentTableName + " ON " + mappingTableName + "." + MappingTables.COLUMN_PARENT_ID + "=" + parentTableName + "." + parentIdColumn
                                + " WHERE ")).append(" + ").append(selection).append(" + ").append(Values.of(")"));
                        return block;
                    }
                })
                .build());

        builder.addMethod(new Method.Builder()
                .setName("performQuery")
                .setModifiers(EnumSet.of(Modifier.PROTECTED))
                .addAnnotation(Annotations.forType(Override.class))
                .setReturnType(Types.generic(SimpleOrmTypes.ENTITY_ITERATOR, entityType))
                .setCode(new ExecutableBuilder() {

                    private Variable mReadableSQLiteWrapper;
                    private Variable mQueryParameters;

                    @Override
                    protected List<Variable> createParameters() {
                        final List<Variable> parameters = new ArrayList<>();
                        parameters.add(mReadableSQLiteWrapper = Variables.of(SimpleOrmTypes.READABLE_SQLITE_WRAPPER));
                        parameters.add(mQueryParameters = Variables.of(SimpleOrmTypes.QUERY_PARAMETERS));
                        return parameters;
                    }

                    @Override
                    protected void write(Block block) {
                        final Variable selection = Variables.of(SimpleOrmTypes.SELECTION, Modifier.FINAL);
                        block.set(selection, METHOD_GET_SELECTION.callOnTarget(mQueryParameters)).append(";").newLine();

                        final List<CodeElement> columns = new ArrayList<>();
                        for (ColumnInfo columnInfo : info.getColumns()) {
                            if (columnInfo.getColumnType() == ColumnType.ENTITY) {
                                continue;
                            }
                            columns.add(Values.of(columnInfo.getColumnName()));
                        }

                        final Variable wrapper = Variables.of(SimpleOrmTypes.CURSOR_WRAPPER, Modifier.FINAL);
                        block.set(wrapper, METHOD_QUERY.callOnTarget(mReadableSQLiteWrapper,
                                Values.of(info.getTableName()),
                                Arrays.of(Types.STRING, columns),
                                METHOD_GET_SELECTION.callOnTarget(selection, Values.of(info.getTableName())),
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

    private Set<TypeAdapterInfo> getAllTypeAdapters(EntityInfo info) {
        final Set<TypeAdapterInfo> infos = new HashSet<>();
        for (ColumnInfo columnInfo : info.getColumns()) {
            if (columnInfo.getColumnType() == ColumnType.ENTITY) {
                infos.addAll(getAllTypeAdapters(columnInfo.getChildEntityInfo()));
                continue;
            }
            infos.addAll(columnInfo.getTypeAdapters());
        }
        return infos;
    }

    private Implementation buildIterator(final EntityInfo info, final DatabaseImplementationBuilder.EntityImplementationCache cache, final Map<TypeAdapterInfo, Field> adapterFieldMap) {
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

        return builder.build();
    }
}