package com.github.wrdlbrnft.simpleorm.processor.builder.entitymanager;

import com.github.wrdlbrnft.codebuilder.code.Block;
import com.github.wrdlbrnft.codebuilder.code.CodeElement;
import com.github.wrdlbrnft.codebuilder.elements.forloop.item.Foreach;
import com.github.wrdlbrnft.codebuilder.elements.values.Values;
import com.github.wrdlbrnft.codebuilder.executables.ExecutableBuilder;
import com.github.wrdlbrnft.codebuilder.executables.Method;
import com.github.wrdlbrnft.codebuilder.executables.Methods;
import com.github.wrdlbrnft.codebuilder.types.Type;
import com.github.wrdlbrnft.codebuilder.types.Types;
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

import static com.github.wrdlbrnft.simpleorm.processor.builder.entitymanager.EntityManagerBuilder.METHOD_CONVERT_FROM;
import static com.github.wrdlbrnft.simpleorm.processor.builder.entitymanager.EntityManagerBuilder.METHOD_ENTITIES_TO_SAVE;
import static com.github.wrdlbrnft.simpleorm.processor.builder.entitymanager.EntityManagerBuilder.METHOD_INSERT;
import static com.github.wrdlbrnft.simpleorm.processor.builder.entitymanager.EntityManagerBuilder.METHOD_PUT;
import static com.github.wrdlbrnft.simpleorm.processor.builder.entitymanager.EntityManagerBuilder.METHOD_VERIFY_ID;

/**
 * Created with Android Studio
 * User: Xaver
 * Date: 02/09/16
 */

class PerformSaveExecutableBuilder extends ExecutableBuilder {

    private final EntityInfo mEntityInfo;
    private final Map<TypeAdapterInfo, Field> mAdapterFieldMap;

    private Variable mWritableSQLiteWrapper;
    private Variable mSaveParameters;

    PerformSaveExecutableBuilder(EntityInfo entityInfo, Map<TypeAdapterInfo, Field> adapterFieldMap) {
        mEntityInfo = entityInfo;
        mAdapterFieldMap = adapterFieldMap;
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
                        appendSave(block, null, null, null, mEntityInfo, entity);
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
                        final Field field = mAdapterFieldMap.get(info);
                        return METHOD_CONVERT_FROM.callOnTarget(field, applyAdaptersConvertFrom(typeAdapters.subList(0, lastIndex), codeElement));
                    }
                })
                .build());
    }
}
