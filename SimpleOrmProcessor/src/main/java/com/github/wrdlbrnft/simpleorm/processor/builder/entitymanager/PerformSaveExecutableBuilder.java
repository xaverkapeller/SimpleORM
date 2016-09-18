package com.github.wrdlbrnft.simpleorm.processor.builder.entitymanager;

import com.github.wrdlbrnft.codebuilder.arrays.ArrayUtils;
import com.github.wrdlbrnft.codebuilder.code.Block;
import com.github.wrdlbrnft.codebuilder.code.BlockWriter;
import com.github.wrdlbrnft.codebuilder.code.CodeElement;
import com.github.wrdlbrnft.codebuilder.elements.forloop.item.Foreach;
import com.github.wrdlbrnft.codebuilder.elements.ifs.If;
import com.github.wrdlbrnft.codebuilder.elements.values.Values;
import com.github.wrdlbrnft.codebuilder.executables.ExecutableBuilder;
import com.github.wrdlbrnft.codebuilder.executables.Method;
import com.github.wrdlbrnft.codebuilder.executables.Methods;
import com.github.wrdlbrnft.codebuilder.types.Type;
import com.github.wrdlbrnft.codebuilder.types.Types;
import com.github.wrdlbrnft.codebuilder.util.Operators;
import com.github.wrdlbrnft.codebuilder.variables.Field;
import com.github.wrdlbrnft.codebuilder.variables.Variable;
import com.github.wrdlbrnft.codebuilder.variables.Variables;
import com.github.wrdlbrnft.simpleorm.processor.SimpleOrmTypes;
import com.github.wrdlbrnft.simpleorm.processor.analyzer.entity.ColumnInfo;
import com.github.wrdlbrnft.simpleorm.processor.analyzer.entity.ColumnType;
import com.github.wrdlbrnft.simpleorm.processor.analyzer.entity.EntityInfo;
import com.github.wrdlbrnft.simpleorm.processor.analyzer.typeadapter.TypeAdapterInfo;
import com.github.wrdlbrnft.simpleorm.processor.utils.MappingTables;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.lang.model.element.Modifier;

import static com.github.wrdlbrnft.simpleorm.processor.builder.entitymanager.EntityManagerBuilder.METHOD_BUILD;
import static com.github.wrdlbrnft.simpleorm.processor.builder.entitymanager.EntityManagerBuilder.METHOD_CONVERT_FROM;
import static com.github.wrdlbrnft.simpleorm.processor.builder.entitymanager.EntityManagerBuilder.METHOD_ENTITIES_TO_SAVE;
import static com.github.wrdlbrnft.simpleorm.processor.builder.entitymanager.EntityManagerBuilder.METHOD_EXEC_SQL;
import static com.github.wrdlbrnft.simpleorm.processor.builder.entitymanager.EntityManagerBuilder.METHOD_GET_SELECTION;
import static com.github.wrdlbrnft.simpleorm.processor.builder.entitymanager.EntityManagerBuilder.METHOD_GET_SELECTION_ARGS;
import static com.github.wrdlbrnft.simpleorm.processor.builder.entitymanager.EntityManagerBuilder.METHOD_INSERT;
import static com.github.wrdlbrnft.simpleorm.processor.builder.entitymanager.EntityManagerBuilder.METHOD_PUT;
import static com.github.wrdlbrnft.simpleorm.processor.builder.entitymanager.EntityManagerBuilder.METHOD_STATEMENT;
import static com.github.wrdlbrnft.simpleorm.processor.builder.entitymanager.EntityManagerBuilder.METHOD_VALUE_OF;
import static com.github.wrdlbrnft.simpleorm.processor.builder.entitymanager.EntityManagerBuilder.METHOD_VERIFY_ID;

/**
 * Created with Android Studio
 * User: Xaver
 * Date: 02/09/16
 */

class PerformSaveExecutableBuilder extends ExecutableBuilder {

    private final EntityInfo mEntityInfo;
    private final Map<TypeAdapterInfo, Field> mAdapterFieldMap;
    private final Method mCreateRemoveQuery;

    private Variable mWritableSQLiteWrapper;
    private Variable mSaveParameters;

    PerformSaveExecutableBuilder(EntityInfo entityInfo, Map<TypeAdapterInfo, Field> adapterFieldMap, Method createRemoveQuery) {
        mEntityInfo = entityInfo;
        mAdapterFieldMap = adapterFieldMap;
        mCreateRemoveQuery = createRemoveQuery;
    }

    @Override
    protected List<Variable> createParameters() {
        final List<Variable> parameters = new ArrayList<>();
        parameters.add(mWritableSQLiteWrapper = Variables.of(SimpleOrmTypes.WRITABLE_SQLITE_WRAPPER));
        parameters.add(mSaveParameters = Variables.of(Types.generic(SimpleOrmTypes.SAVE_PARAMETERS, Types.of(mEntityInfo.getEntityElement()))));
        return parameters;
    }

    @Override
    protected void write(Block block) {
        final Type entityType = Types.of(mEntityInfo.getEntityElement());
        final Variable entities = Variables.of(Types.generic(Types.LIST, entityType), Modifier.FINAL);
        block.set(entities, METHOD_ENTITIES_TO_SAVE.callOnTarget(mSaveParameters)).append(";").newLine();

        block.append(new Foreach.Builder()
                .setCollection(entities)
                .setItemType(entityType)
                .setIteration(new Foreach.Iteration() {
                    @Override
                    public void onIteration(Block block, Variable entity) {
                        appendSave(block, null, null, null, null, null, mEntityInfo, entity);
                    }

                    private void appendSave(Block block, EntityInfo parent, ColumnInfo parentColumn, Variable parentId, final Variable removeMappingSelection, Variable removeMappingparentId, final EntityInfo child, Variable entity) {
                        block.append(new If.Builder()
                                .add(Operators.operate(entity, "==", Values.ofNull()), new BlockWriter() {
                                    @Override
                                    protected void write(Block block) {
                                        block.append("continue;");
                                    }
                                })
                                .build()).newLine();

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

                        if (removeMappingparentId != null) {
                            block.newLine().append(METHOD_EXEC_SQL.callOnTarget(mWritableSQLiteWrapper,
                                    Values.of("DELETE FROM " + MappingTables.getTableName(parent, parentColumn) + " WHERE " + MappingTables.COLUMN_PARENT_ID + "=? AND " + MappingTables.COLUMN_CHILD_ID + "=?"),
                                    ArrayUtils.of(Types.STRING,
                                            METHOD_VALUE_OF.callOnTarget(Types.STRING, parentId),
                                            METHOD_VALUE_OF.callOnTarget(Types.STRING, id)
                                    )
                            )).append(";");
                        }

                        if (removeMappingSelection != null) {
                            block.newLine().append(METHOD_STATEMENT.callOnTarget(removeMappingSelection, Values.of(MappingTables.COLUMN_CHILD_ID), Values.of("<>"), METHOD_VALUE_OF.callOnTarget(Types.STRING, id))).append(";");
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
                                block.append(new If.Builder()
                                        .add(Operators.operate(childEntity, "!=", Values.ofNull()), new BlockWriter() {
                                            @Override
                                            protected void write(Block block) {
                                                appendSave(block, child, entityColumn, id, null, id, childEntityInfo, childEntity);
                                            }
                                        })
                                        .build());
                            } else if (collectionType == ColumnInfo.CollectionType.LIST) {
                                final Variable childEntityList = Variables.of(Types.generic(Types.LIST, childEntityType), Modifier.FINAL);
                                block.set(childEntityList, Methods.from(entityColumn.getGetterElement()).callOnTarget(entity)).append(";").newLine();
                                block.append(new If.Builder()
                                        .add(Operators.operate(childEntityList, "==", Values.ofNull()), new BlockWriter() {
                                            @Override
                                            protected void write(Block block) {
                                                block.append("continue;");
                                            }
                                        })
                                        .build()).newLine();
                                final Variable selectionBuilder = Variables.of(SimpleOrmTypes.SELECTION_BUILDER, Modifier.FINAL);
                                block.set(selectionBuilder, SimpleOrmTypes.SELECTION_BUILDER.newInstance()).append(";").newLine();
                                block.append(METHOD_STATEMENT.callOnTarget(selectionBuilder, Values.of(MappingTables.COLUMN_PARENT_ID), Values.of("="), METHOD_VALUE_OF.callOnTarget(Types.STRING, id))).append(";").newLine();
                                block.append(new Foreach.Builder()
                                        .setItemType(childEntityType)
                                        .setCollection(childEntityList)
                                        .setIteration(new Foreach.Iteration() {
                                            @Override
                                            public void onIteration(Block block, Variable childEntity) {
                                                appendSave(block, child, entityColumn, id, selectionBuilder, null, childEntityInfo, childEntity);
                                            }
                                        })
                                        .build());
                                block.newLine();
                                final Variable selection = Variables.of(SimpleOrmTypes.SELECTION, Modifier.FINAL);
                                block.set(selection, METHOD_BUILD.callOnTarget(selectionBuilder)).append(";").newLine();
                                block.append(METHOD_EXEC_SQL.callOnTarget(mWritableSQLiteWrapper,
                                        mCreateRemoveQuery.call(
                                                Values.of("DELETE FROM " + MappingTables.getTableName(child, entityColumn)),
                                                METHOD_GET_SELECTION.callOnTarget(selection, Values.ofNull())
                                        ),
                                        METHOD_GET_SELECTION_ARGS.callOnTarget(selection)
                                )).append(";");
                            } else {
                                throw new IllegalStateException("Encountered unknown collection type: " + collectionType);
                            }
                        }
                    }

                    private CodeElement applyAdaptersConvertFrom(List<TypeAdapterInfo> typeAdapters, CodeElement codeElement) {
                        if (typeAdapters.isEmpty()) {
                            return codeElement;
                        }

                        final int lastIndex = typeAdapters.size() - 1;
                        final TypeAdapterInfo info = typeAdapters.get(lastIndex);
                        final Field field = mAdapterFieldMap.get(info);
                        return METHOD_CONVERT_FROM.callOnTarget(field, applyAdaptersConvertFrom(typeAdapters.subList(0, lastIndex), codeElement));
                    }
                })
                .build());
    }
}
