package com.github.wrdlbrnft.simpleorm.processor.builder.entitymanager;

import com.github.wrdlbrnft.codebuilder.annotations.Annotations;
import com.github.wrdlbrnft.codebuilder.arrays.ArrayUtils;
import com.github.wrdlbrnft.codebuilder.code.Block;
import com.github.wrdlbrnft.codebuilder.code.BlockWriter;
import com.github.wrdlbrnft.codebuilder.code.CodeElement;
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
import com.github.wrdlbrnft.simpleorm.processor.builder.entitymanager.relationships.RelationshipInfo;
import com.github.wrdlbrnft.simpleorm.processor.builder.entitymanager.relationships.RelationshipTree;
import com.github.wrdlbrnft.simpleorm.processor.utils.MappingTables;

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
import static com.github.wrdlbrnft.simpleorm.processor.builder.entitymanager.EntityManagerBuilder.METHOD_GET_LIMIT;
import static com.github.wrdlbrnft.simpleorm.processor.builder.entitymanager.EntityManagerBuilder.METHOD_GET_ORDER_BY;
import static com.github.wrdlbrnft.simpleorm.processor.builder.entitymanager.EntityManagerBuilder.METHOD_GET_SELECTION;
import static com.github.wrdlbrnft.simpleorm.processor.builder.entitymanager.EntityManagerBuilder.METHOD_GET_SELECTION_ARGS;
import static com.github.wrdlbrnft.simpleorm.processor.builder.entitymanager.EntityManagerBuilder.METHOD_MOVE_TO_POSITION;
import static com.github.wrdlbrnft.simpleorm.processor.builder.entitymanager.EntityManagerBuilder.METHOD_QUERY;

/**
 * Created with Android Studio
 * User: Xaver
 * Date: 02/09/16
 */

class EntityIteratorBuilder {

    private static final Method METHOD_APPEND = Methods.stub("append");

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

    public EntityIteratorInfo build(final EntityInfo info, final List<RelationshipInfo> relationshipInfos, final DatabaseImplementationBuilder.EntityImplementationCache cache, final Map<TypeAdapterInfo, Field> adapterFieldMap) {
        final Type entityType = Types.of(info.getEntityElement());

        final Implementation.Builder builder = new Implementation.Builder();
        builder.setModifiers(EnumSet.of(Modifier.PRIVATE, Modifier.STATIC));
        builder.addImplementedType(Types.generic(SimpleOrmTypes.ENTITY_ITERATOR, entityType));

        final Field wrapperField = new Field.Builder()
                .setType(SimpleOrmTypes.CURSOR_WRAPPER)
                .setModifiers(EnumSet.of(Modifier.PRIVATE, Modifier.FINAL))
                .build();
        builder.addField(wrapperField);

        final Field indexField = new Field.Builder()
                .setType(Types.Primitives.INTEGER)
                .setModifiers(EnumSet.of(Modifier.PRIVATE))
                .setInitialValue(Values.of(0))
                .build();
        builder.addField(indexField);

        final Field sizeField = new Field.Builder()
                .setType(Types.Primitives.INTEGER)
                .setModifiers(EnumSet.of(Modifier.PRIVATE, Modifier.FINAL))
                .build();
        builder.addField(sizeField);

        final Field cacheField = new Field.Builder()
                .setType(Types.arrayOf(entityType))
                .setModifiers(EnumSet.of(Modifier.PRIVATE, Modifier.FINAL))
                .build();
        builder.addField(cacheField);

        final Map<RelationshipInfo, Field> wrapperMap = new HashMap<>();
        final Map<IndexIdentifier, Field> indexFieldMap = new HashMap<>();
        RelationshipTree.iterate(relationshipInfos, new RelationshipTree.Iterator() {
            @Override
            public void onPathFound(List<RelationshipInfo> path) {
                final int startIndex = path.size() - 1;
                final RelationshipInfo key = path.get(startIndex);
                final Field wrapperField = new Field.Builder()
                        .setType(SimpleOrmTypes.CURSOR_WRAPPER)
                        .setModifiers(EnumSet.of(Modifier.PRIVATE, Modifier.FINAL))
                        .build();
                builder.addField(wrapperField);
                wrapperMap.put(key, wrapperField);

                for (ColumnInfo columnInfo : key.getChildEntityInfo().getColumns()) {
                    if (columnInfo.getColumnType() == ColumnType.ENTITY) {
                        continue;
                    }
                    final Field field = new Field.Builder()
                            .setType(Types.Primitives.INTEGER)
                            .setModifiers(EnumSet.of(Modifier.PRIVATE, Modifier.FINAL))
                            .build();
                    builder.addField(field);
                    indexFieldMap.put(new IndexIdentifierImpl(key, columnInfo), field);
                }
            }
        });

        for (ColumnInfo columnInfo : info.getColumns()) {
            if (columnInfo.getColumnType() == ColumnType.ENTITY) {
                continue;
            }
            final Field field = new Field.Builder()
                    .setType(Types.Primitives.INTEGER)
                    .setModifiers(EnumSet.of(Modifier.PRIVATE, Modifier.FINAL))
                    .build();
            builder.addField(field);
            indexFieldMap.put(new IndexIdentifierImpl(null, columnInfo), field);
        }

        final Method performQuery = new Method.Builder()
                .setReturnType(Types.STRING)
                .setModifiers(EnumSet.of(Modifier.PRIVATE))
                .setCode(new ExecutableBuilder() {

                    private Variable mQuery;
                    private Variable mSelectionString;
                    private Variable mOrderByString;
                    private Variable mLimitString;

                    @Override
                    protected List<Variable> createParameters() {
                        final List<Variable> parameters = new ArrayList<>();
                        parameters.add(mQuery = Variables.of(Types.STRING));
                        parameters.add(mSelectionString = Variables.of(Types.STRING));
                        parameters.add(mOrderByString = Variables.of(Types.STRING));
                        parameters.add(mLimitString = Variables.of(Types.STRING));
                        return parameters;
                    }

                    @Override
                    protected void write(Block block) {
                        final Variable stringBuilder = Variables.of(SimpleOrmTypes.STRING_BUILDER, Modifier.FINAL);
                        block.set(stringBuilder, SimpleOrmTypes.STRING_BUILDER.newInstance(mQuery)).append(";").newLine();

                        block.append(new If.Builder()
                                .add(Operators.operate(mOrderByString, "!=", Values.ofNull()), new BlockWriter() {
                                    @Override
                                    protected void write(Block block) {
                                        block.append(METHOD_APPEND.callOnTarget(METHOD_APPEND.callOnTarget(stringBuilder, Values.of(" ORDER BY ")), mOrderByString)).append(";");
                                    }
                                })
                                .build());
                        block.newLine();

                        block.append(new If.Builder()
                                .add(Operators.operate(mLimitString, "!=", Values.ofNull()), new BlockWriter() {
                                    @Override
                                    protected void write(Block block) {
                                        block.append(METHOD_APPEND.callOnTarget(METHOD_APPEND.callOnTarget(stringBuilder, Values.of(" LIMIT ")), mLimitString)).append(";");
                                    }
                                })
                                .build());
                        block.newLine();

                        block.append(new If.Builder()
                                .add(Operators.operate(mSelectionString, "!=", Values.ofNull()), new BlockWriter() {
                                    @Override
                                    protected void write(Block block) {
                                        block.append(METHOD_APPEND.callOnTarget(METHOD_APPEND.callOnTarget(stringBuilder, Values.of(" WHERE ")), mSelectionString)).append(";");
                                    }
                                })
                                .build());
                        block.newLine();

                        block.append("return ").append(Methods.TO_STRING.callOnTarget(stringBuilder)).append(";");
                    }
                })
                .build();
        builder.addMethod(performQuery);

        builder.addConstructor(new Constructor.Builder()
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
                    protected void write(final Block block) {

                        final Variable selection = Variables.of(SimpleOrmTypes.SELECTION, Modifier.FINAL);
                        block.set(selection, METHOD_GET_SELECTION.callOnTarget(mQueryParameters)).append(";").newLine();

                        final List<CodeElement> columns = new ArrayList<>();
                        for (ColumnInfo columnInfo : info.getColumns()) {
                            if (columnInfo.getColumnType() == ColumnType.ENTITY) {
                                continue;
                            }
                            columns.add(Values.of(columnInfo.getColumnName()));
                        }

                        final Variable selectionString = Variables.of(Types.STRING, Modifier.FINAL);
                        block.set(selectionString, METHOD_GET_SELECTION.callOnTarget(selection, Values.of(info.getTableName()))).append(";").newLine();

                        final Variable selectionArgs = Variables.of(Types.arrayOf(Types.STRING), Modifier.FINAL);
                        block.set(selectionArgs, METHOD_GET_SELECTION_ARGS.callOnTarget(selection)).append(";").newLine();

                        final Variable orderByString = Variables.of(Types.STRING, Modifier.FINAL);
                        block.set(orderByString, METHOD_GET_ORDER_BY.callOnTarget(mQueryParameters)).append(";").newLine();

                        final Variable limitString = Variables.of(Types.STRING, Modifier.FINAL);
                        block.set(limitString, METHOD_GET_LIMIT.callOnTarget(mQueryParameters)).append(";").newLine();

                        block.set(wrapperField, METHOD_QUERY.callOnTarget(mReadableSQLiteWrapper,
                                Values.of(info.getTableName()),
                                ArrayUtils.of(Types.STRING, columns),
                                selectionString,
                                selectionArgs,
                                Values.ofNull(),
                                Values.ofNull(),
                                orderByString,
                                limitString
                        )).append(";");
                        for (ColumnInfo columnInfo : info.getColumns()) {
                            if (columnInfo.getColumnType() == ColumnType.ENTITY) {
                                continue;
                            }
                            final Field field = indexFieldMap.get(new IndexIdentifierImpl(null, columnInfo));
                            block.newLine().set(field, METHOD_GET_COLUMN_INDEX.callOnTarget(wrapperField, Values.of(columnInfo.getColumnName()))).append(";");
                        }

                        RelationshipTree.iterate(relationshipInfos, new RelationshipTree.Iterator() {
                            @Override
                            public void onPathFound(List<RelationshipInfo> path) {
                                final String query = createSelectionQuery(path);
                                final RelationshipInfo key = path.get(path.size() - 1);
                                final Field wrapperField = wrapperMap.get(key);
                                block.newLine().set(wrapperField, METHOD_QUERY.callOnTarget(mReadableSQLiteWrapper,
                                        performQuery.call(Values.of(query), selectionString, orderByString, limitString),
                                        selectionArgs
                                )).append(";");
                                for (ColumnInfo columnInfo : key.getChildEntityInfo().getColumns()) {
                                    if (columnInfo.getColumnType() == ColumnType.ENTITY) {
                                        continue;
                                    }
                                    final Field field = indexFieldMap.get(new IndexIdentifierImpl(key, columnInfo));
                                    block.newLine().set(field, METHOD_GET_COLUMN_INDEX.callOnTarget(wrapperField, Values.of(columnInfo.getColumnName()))).append(";");
                                }
                            }

                            private String createSelectionQuery(List<RelationshipInfo> path) {
                                final int startIndex = path.size() - 1;
                                final StringBuilder queryBuilder = new StringBuilder();
                                for (int i = startIndex; i >= 0; i--) {
                                    final RelationshipInfo relationshipInfo = path.get(i);
                                    final EntityInfo parentEntityInfo = relationshipInfo.getParentEntityInfo();
                                    final String parentTableName = parentEntityInfo.getTableName();
                                    final String parentIdColumn = parentEntityInfo.getIdColumn().getColumnName();
                                    final ColumnInfo relationshipColumnInfo = relationshipInfo.getColumnInfo();
                                    final EntityInfo childEntityInfo = relationshipInfo.getChildEntityInfo();
                                    final String childTableName = childEntityInfo.getTableName();
                                    final String childIdColumn = childEntityInfo.getIdColumn().getColumnName();
                                    final String mappingTableName = MappingTables.getTableName(parentEntityInfo, relationshipColumnInfo);

                                    if (i == startIndex) {
                                        queryBuilder.append("SELECT ");
                                        for (ColumnInfo columnInfo : childEntityInfo.getColumns()) {
                                            if (columnInfo.getColumnType() == ColumnType.ENTITY) {
                                                continue;
                                            }
                                            final String columnName = columnInfo.getColumnName();
                                            queryBuilder.append(childTableName).append(".").append(columnName).append(" AS ").append(columnName).append(", ");
                                        }
                                        queryBuilder.append(mappingTableName).append(".").append(MappingTables.COLUMN_PARENT_ID).append(" AS ").append(MappingTables.COLUMN_PARENT_ID);
                                        queryBuilder.append(" FROM ").append(childTableName);
                                    }

                                    queryBuilder.append(" JOIN ").append(mappingTableName).append(" ON ").append(childTableName).append(".").append(childIdColumn).append("=").append(mappingTableName).append(".").append(MappingTables.COLUMN_CHILD_ID)
                                            .append(" JOIN ").append(parentTableName).append(" ON ").append(mappingTableName).append(".").append(MappingTables.COLUMN_PARENT_ID).append("=").append(parentTableName).append(".").append(parentIdColumn);
                                }
                                return queryBuilder.toString();
                            }
                        });

                        block.newLine().set(sizeField, METHOD_GET_COUNT.callOnTarget(wrapperField)).append(";");
                        block.newLine().set(cacheField, Types.arrayOf(entityType).newInstance(sizeField)).append(";");
                    }
                })
                .build());

        final Method readFromPosition = createReadFromPositionMethod(info, cache, adapterFieldMap, entityType, wrapperField, indexFieldMap);
        builder.addMethod(readFromPosition);

        final Method read = new Method.Builder()
                .setModifiers(EnumSet.of(Modifier.PRIVATE))
                .setReturnType(entityType)
                .setCode(new ExecutableBuilder() {

                    private Variable mPosition;

                    @Override
                    protected List<Variable> createParameters() {
                        final List<Variable> parameters = new ArrayList<>();
                        parameters.add(mPosition = Variables.of(Types.Primitives.INTEGER));
                        return parameters;
                    }

                    @Override
                    protected void write(Block block) {
                        final Variable arrayItem = ArrayUtils.access(cacheField, mPosition);
                        block.append(new If.Builder()
                                .add(Operators.operate(arrayItem, "==", Values.ofNull()), new BlockWriter() {
                                    @Override
                                    protected void write(Block block) {
                                        block.set(arrayItem, readFromPosition.call(mPosition)).append(";");
                                    }
                                })
                                .build()).newLine();
                        block.append("return ").append(arrayItem).append(";");
                    }
                })
                .build();
        builder.addMethod(read);

        builder.addMethod(new Method.Builder()
                .setReturnType(Types.Primitives.BOOLEAN)
                .setName("hasNext")
                .addAnnotation(Annotations.forType(Override.class))
                .setModifiers(EnumSet.of(Modifier.PUBLIC))
                .setCode(new ArrayList<Variable>(), new BlockWriter() {
                    @Override
                    protected void write(Block block) {
                        block.append("return ").append(Operators.operate(indexField, "<", sizeField)).append(";");
                    }
                })
                .build());

        builder.addMethod(new Method.Builder()
                .setReturnType(entityType)
                .setName("next")
                .addAnnotation(Annotations.forType(Override.class))
                .setModifiers(EnumSet.of(Modifier.PUBLIC))
                .setCode(new ArrayList<Variable>(), new BlockWriter() {
                    @Override
                    protected void write(Block block) {
                        block.append(new If.Builder()
                                .add(Operators.operate(indexField, "<", sizeField), new BlockWriter() {
                                    @Override
                                    protected void write(Block block) {
                                        block.append("return ").append(read.call(Variables.postIncrement(indexField))).append(";");
                                    }
                                })
                                .build()).newLine();
                        block.append("throw ").append(Types.Exceptions.NO_SUCH_ELEMENT_EXCEPTION.newInstance()).append(";");
                    }
                })
                .build());

        builder.addMethod(new Method.Builder()
                .setReturnType(Types.Primitives.INTEGER)
                .setName("size")
                .addAnnotation(Annotations.forType(Override.class))
                .setModifiers(EnumSet.of(Modifier.PUBLIC))
                .setCode(new ArrayList<Variable>(), new BlockWriter() {
                    @Override
                    protected void write(Block block) {
                        block.append("return ").append(sizeField).append(";");
                    }
                })
                .build());

        final Implementation lazyListImplementation = new Implementation.Builder()
                .setModifiers(EnumSet.of(Modifier.PRIVATE))
                .setExtendedType(Types.generic(SimpleOrmTypes.ABSTRACT_LIST, entityType))
                .addMethod(new Method.Builder()
                        .setModifiers(EnumSet.of(Modifier.PUBLIC))
                        .setName("get")
                        .addAnnotation(Annotations.forType(Override.class))
                        .setReturnType(entityType)
                        .setCode(new ExecutableBuilder() {

                            private Variable mPosition;

                            @Override
                            protected List<Variable> createParameters() {
                                final List<Variable> parameters = new ArrayList<>();
                                parameters.add(mPosition = Variables.of(Types.Primitives.INTEGER));
                                return parameters;
                            }

                            @Override
                            protected void write(Block block) {
                                block.append(new If.Builder()
                                        .add(Operators.operate(Operators.operate(mPosition, "<", Values.of(0)), "||", Operators.operate(mPosition, ">=", sizeField)), new BlockWriter() {
                                            @Override
                                            protected void write(Block block) {
                                                block.append("throw ").append(Types.Exceptions.INDEX_OUT_OF_BOUNDS_EXCEPTION.newInstance(new Block()
                                                        .append(Values.of("Index: "))
                                                        .append(" + ").append(mPosition)
                                                        .append(" + ").append(Values.of(", Size: "))
                                                        .append(" + ").append(sizeField)
                                                )).append(";");
                                            }
                                        })
                                        .build()).newLine();
                                block.append("return ").append(read.call(mPosition)).append(";");
                            }
                        })
                        .build())
                .addMethod(new Method.Builder()
                        .setModifiers(EnumSet.of(Modifier.PUBLIC))
                        .setName("size")
                        .addAnnotation(Annotations.forType(Override.class))
                        .setReturnType(Types.Primitives.INTEGER)
                        .setCode(new ArrayList<Variable>(), new BlockWriter() {
                            @Override
                            protected void write(Block block) {
                                block.append("return ").append(sizeField).append(";");
                            }
                        })
                        .build())
                .build();
        builder.addNestedImplementation(lazyListImplementation);

        builder.addMethod(new Method.Builder()
                .setReturnType(Types.generic(Types.LIST, entityType))
                .setName("asList")
                .addAnnotation(Annotations.forType(Override.class))
                .setModifiers(EnumSet.of(Modifier.PUBLIC))
                .setCode(new ArrayList<Variable>(), new BlockWriter() {
                    @Override
                    protected void write(Block block) {
                        block.append("return ").append(lazyListImplementation.newInstance()).append(";");
                    }
                })
                .build());

        final Implementation implementation = builder.build();
        return new EntityIteratorInfoImpl(implementation);
    }

    private Method createReadFromPositionMethod(final EntityInfo info, final DatabaseImplementationBuilder.EntityImplementationCache cache, final Map<TypeAdapterInfo, Field> adapterFieldMap, Type entityType, final Field wrapperField, final Map<IndexIdentifier, Field> indexFieldMap) {
        return new Method.Builder()
                .setModifiers(EnumSet.of(Modifier.PRIVATE))
                .setReturnType(entityType)
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
                            final Field indexField = indexFieldMap.get(new IndexIdentifierImpl(null, columnInfo));
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
                .build();
    }

    private interface IndexIdentifier {

    }

    private static class IndexIdentifierImpl implements IndexIdentifier {

        private final RelationshipInfo mRelationshipInfo;
        private final ColumnInfo mColumnInfo;

        private IndexIdentifierImpl(RelationshipInfo relationshipInfo, ColumnInfo columnInfo) {
            mRelationshipInfo = relationshipInfo;
            mColumnInfo = columnInfo;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            IndexIdentifierImpl that = (IndexIdentifierImpl) o;

            if (mRelationshipInfo != null ? !mRelationshipInfo.equals(that.mRelationshipInfo) : that.mRelationshipInfo != null)
                return false;
            return mColumnInfo != null ? mColumnInfo.equals(that.mColumnInfo) : that.mColumnInfo == null;
        }

        @Override
        public int hashCode() {
            int result = mRelationshipInfo != null ? mRelationshipInfo.hashCode() : 0;
            result = 31 * result + (mColumnInfo != null ? mColumnInfo.hashCode() : 0);
            return result;
        }
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
