package com.github.wrdlbrnft.simpleorm.processor.builder.entitymanager;

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
import com.github.wrdlbrnft.simpleorm.processor.analyzer.entity.EntityInfo;
import com.github.wrdlbrnft.simpleorm.processor.analyzer.relationships.RelationshipInfo;
import com.github.wrdlbrnft.simpleorm.processor.analyzer.relationships.RelationshipTree;
import com.github.wrdlbrnft.simpleorm.processor.analyzer.typeadapter.TypeAdapterInfo;
import com.github.wrdlbrnft.simpleorm.processor.utils.MappingTables;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.lang.model.element.Modifier;

import static com.github.wrdlbrnft.simpleorm.processor.builder.entitymanager.EntityManagerBuilder.METHOD_BUILD;
import static com.github.wrdlbrnft.simpleorm.processor.builder.entitymanager.EntityManagerBuilder.METHOD_CONVERT_FROM;
import static com.github.wrdlbrnft.simpleorm.processor.builder.entitymanager.EntityManagerBuilder.METHOD_DELETE;
import static com.github.wrdlbrnft.simpleorm.processor.builder.entitymanager.EntityManagerBuilder.METHOD_ENTITIES_TO_REMOVE;
import static com.github.wrdlbrnft.simpleorm.processor.builder.entitymanager.EntityManagerBuilder.METHOD_EXEC_SQL;
import static com.github.wrdlbrnft.simpleorm.processor.builder.entitymanager.EntityManagerBuilder.METHOD_GET_SELECTION;
import static com.github.wrdlbrnft.simpleorm.processor.builder.entitymanager.EntityManagerBuilder.METHOD_GET_SELECTION_ARGS;
import static com.github.wrdlbrnft.simpleorm.processor.builder.entitymanager.EntityManagerBuilder.METHOD_IS_EMPTY;
import static com.github.wrdlbrnft.simpleorm.processor.builder.entitymanager.EntityManagerBuilder.METHOD_OR;
import static com.github.wrdlbrnft.simpleorm.processor.builder.entitymanager.EntityManagerBuilder.METHOD_STATEMENT;
import static com.github.wrdlbrnft.simpleorm.processor.builder.entitymanager.EntityManagerBuilder.METHOD_VALUE_OF;

/**
 * Created with Android Studio
 * User: Xaver
 * Date: 02/09/16
 */

class PerformRemoveExecutableBuilder extends ExecutableBuilder {

    private final EntityInfo mEntityInfo;
    private final List<RelationshipInfo> mRelationshipInfos;
    private final Map<TypeAdapterInfo, Field> mAdapterFieldMap;
    private final Method mCreateRemoveQuery;

    private Variable mWritableSQLiteWrapper;
    private Variable mRemoveParameters;

    PerformRemoveExecutableBuilder(EntityInfo entityInfo, List<RelationshipInfo> relationshipInfos, Map<TypeAdapterInfo, Field> adapterFieldMap, Method createRemoveQuery) {
        mEntityInfo = entityInfo;
        mRelationshipInfos = relationshipInfos;
        mAdapterFieldMap = adapterFieldMap;
        mCreateRemoveQuery = createRemoveQuery;
    }

    @Override
    protected List<Variable> createParameters() {
        final List<Variable> parameters = new ArrayList<>();
        parameters.add(mWritableSQLiteWrapper = Variables.of(SimpleOrmTypes.WRITABLE_SQLITE_WRAPPER));
        parameters.add(mRemoveParameters = Variables.of(Types.generic(SimpleOrmTypes.REMOVE_PARAMETERS, Types.of(mEntityInfo.getEntityElement()))));
        return parameters;
    }

    @Override
    protected void write(Block block) {
        final Variable selection = Variables.of(SimpleOrmTypes.SELECTION, Modifier.FINAL);
        block.set(selection, METHOD_GET_SELECTION.callOnTarget(mRemoveParameters)).append(";").newLine();

        block.append(new If.Builder()
                .add(Values.invert(METHOD_IS_EMPTY.callOnTarget(selection)), new BlockWriter() {
                    @Override
                    protected void write(Block block) {
                        final Variable resolvedSelection = Variables.of(Types.STRING, Modifier.FINAL);
                        block.set(resolvedSelection, METHOD_GET_SELECTION.callOnTarget(selection, Values.of(mEntityInfo.getTableName()))).append(";").newLine();

                        final Variable resolvedSelectionArgs = Variables.of(Types.arrayOf(Types.STRING), Modifier.FINAL);
                        block.set(resolvedSelectionArgs, METHOD_GET_SELECTION_ARGS.callOnTarget(selection)).append(";").newLine();

                        appendChildRemoval(block, resolvedSelection, resolvedSelectionArgs);

                        block.append(METHOD_DELETE.callOnTarget(mWritableSQLiteWrapper,
                                Values.of(mEntityInfo.getTableName()),
                                resolvedSelection,
                                resolvedSelectionArgs
                        )).append(";");
                    }
                })
                .build());
        block.newLine();

        final Type entityType = Types.of(mEntityInfo.getEntityElement());
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

        final ColumnInfo idColumn = mEntityInfo.getIdColumn();
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
                        final Field field = mAdapterFieldMap.get(info);
                        return METHOD_CONVERT_FROM.callOnTarget(field, applyAdaptersConvertFrom(typeAdapters.subList(0, lastIndex), codeElement));
                    }
                })
                .build());
        block.newLine();

        final Variable entitySelection = Variables.of(SimpleOrmTypes.SELECTION, Modifier.FINAL);
        block.set(entitySelection, METHOD_BUILD.callOnTarget(builder)).append(";").newLine();

        final Variable resolvedEntitySelection = Variables.of(Types.STRING, Modifier.FINAL);
        block.set(resolvedEntitySelection, METHOD_GET_SELECTION.callOnTarget(entitySelection, Values.of(mEntityInfo.getTableName()))).append(";").newLine();

        final Variable resolvedEntitySelectionArgs = Variables.of(Types.arrayOf(Types.STRING), Modifier.FINAL);
        block.set(resolvedEntitySelectionArgs, METHOD_GET_SELECTION_ARGS.callOnTarget(entitySelection)).append(";").newLine();

        appendChildRemoval(block, resolvedEntitySelection, resolvedEntitySelectionArgs);

        block.append(METHOD_DELETE.callOnTarget(mWritableSQLiteWrapper,
                Values.of(mEntityInfo.getTableName()),
                resolvedEntitySelection,
                resolvedEntitySelectionArgs
        )).append(";");
    }

    private void appendChildRemoval(Block block, Variable selection, Variable selectionArgs) {
        for (CodeElement removeQuery : appendChildRemoval(selection)) {
            block.append(METHOD_EXEC_SQL.callOnTarget(mWritableSQLiteWrapper, removeQuery, selectionArgs)).append(";").newLine();
        }
    }

    private List<CodeElement> appendChildRemoval(final Variable selection) {
        final List<CodeElement> queryList = new ArrayList<>();

        RelationshipTree.iterate(mRelationshipInfos, new RelationshipTree.Iterator() {
            @Override
            public void onPathFound(List<RelationshipInfo> path) {
                queryList.addAll(createRemoveQueries(path, selection));
            }
        });

        return queryList;
    }

    private List<CodeElement> createRemoveQueries(final List<RelationshipInfo> relationInfos, final Variable selection) {
        final List<CodeElement> queryList = new ArrayList<>();

        final StringBuilder removeEntityQueryBuilder = new StringBuilder();

        final int startIndex = relationInfos.size() - 1;
        for (int i = startIndex; i >= 0; i--) {
            final RelationshipInfo relationInfo = relationInfos.get(i);
            final EntityInfo parent = relationInfo.getParentEntityInfo();
            final ColumnInfo parentColumnInfo = relationInfo.getColumnInfo();
            final EntityInfo child = relationInfo.getChildEntityInfo();
            final String mappingTableName = MappingTables.getTableName(parent, parentColumnInfo);
            final String parentTableName = parent.getTableName();
            final String parentIdColumn = parent.getIdColumn().getColumnName();
            final String childTableName = child.getTableName();
            final String childIdColumn = child.getIdColumn().getColumnName();

            if (i == startIndex) {
                removeEntityQueryBuilder.append("SELECT ").append(childTableName).append(".rowid FROM ").append(childTableName);
            }

            removeEntityQueryBuilder.append(" JOIN ").append(mappingTableName).append(" ON ").append(childTableName).append(".").append(childIdColumn).append("=").append(mappingTableName).append(".").append(MappingTables.COLUMN_CHILD_ID)
                    .append(" JOIN ").append(parentTableName).append(" ON ").append(mappingTableName).append(".").append(MappingTables.COLUMN_PARENT_ID).append("=").append(parentTableName).append(".").append(parentIdColumn);
        }

        queryList.add(new BlockWriter() {
            @Override
            protected void write(Block block) {
                final String childTableName = relationInfos.get(startIndex).getChildEntityInfo().getTableName();
                block.append(Values.of("DELETE FROM " + childTableName + " WHERE rowid in (")).append(" + ")
                        .append(mCreateRemoveQuery.call(Values.of(removeEntityQueryBuilder.toString()), selection))
                        .append(" + ").append(Values.of(")"));
            }
        });

        return queryList;
    }
}
