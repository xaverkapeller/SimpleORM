package com.github.wrdlbrnft.simpleorm.processor.builder.entitymanager;

import com.github.wrdlbrnft.codebuilder.annotations.Annotations;
import com.github.wrdlbrnft.codebuilder.arrays.ArrayUtils;
import com.github.wrdlbrnft.codebuilder.code.Block;
import com.github.wrdlbrnft.codebuilder.code.BlockWriter;
import com.github.wrdlbrnft.codebuilder.code.CodeElement;
import com.github.wrdlbrnft.codebuilder.elements.forloop.counting.CountingFor;
import com.github.wrdlbrnft.codebuilder.elements.ifs.If;
import com.github.wrdlbrnft.codebuilder.elements.ifs.TernaryIf;
import com.github.wrdlbrnft.codebuilder.elements.values.Values;
import com.github.wrdlbrnft.codebuilder.executables.Constructor;
import com.github.wrdlbrnft.codebuilder.executables.ExecutableBuilder;
import com.github.wrdlbrnft.codebuilder.executables.Method;
import com.github.wrdlbrnft.codebuilder.executables.Methods;
import com.github.wrdlbrnft.codebuilder.implementations.Implementation;
import com.github.wrdlbrnft.codebuilder.types.DefinedType;
import com.github.wrdlbrnft.codebuilder.types.GenericType;
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
import static com.github.wrdlbrnft.simpleorm.processor.builder.entitymanager.EntityManagerBuilder.METHOD_IS_EMPTY;
import static com.github.wrdlbrnft.simpleorm.processor.builder.entitymanager.EntityManagerBuilder.METHOD_MOVE_TO_POSITION;
import static com.github.wrdlbrnft.simpleorm.processor.builder.entitymanager.EntityManagerBuilder.METHOD_PUT;
import static com.github.wrdlbrnft.simpleorm.processor.builder.entitymanager.EntityManagerBuilder.METHOD_QUERY;

/**
 * Created with Android Studio
 * User: Xaver
 * Date: 02/09/16
 */

class EntityIteratorBuilder {

    private static final Method METHOD_APPEND = Methods.stub("append");
    private static final Method METHOD_ADD = Methods.stub("add");
    private static final Method METHOD_GET = Methods.stub("get");
    private static final Method METHOD_TRIM = Methods.stub("trim");
    private static final Method METHOD_FINALIZE = Methods.stub("finalize");
    private static final Method METHOD_CLOSE = Methods.stub("close");

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
        final Map<Identifier, Field> indexFieldMap = new HashMap<>();
        final Map<Identifier, MethodWrapper> childMethodWrapperMap = new HashMap<>();
        final Map<RelationshipInfo, Field> mappingIdIndexFieldMap = new HashMap<>();
        RelationshipTree.iterate(relationshipInfos, new RelationshipTree.Iterator() {
            @Override
            public void onPathFound(List<RelationshipInfo> path) {
                final int startIndex = path.size() - 1;
                final RelationshipInfo relationshipInfo = path.get(startIndex);
                final EntityInfo childEntityInfo = relationshipInfo.getChildEntityInfo();
                final DefinedType childEntityType = Types.of(childEntityInfo.getEntityElement());

                final Field wrapperField = new Field.Builder()
                        .setType(SimpleOrmTypes.CURSOR_WRAPPER)
                        .setModifiers(EnumSet.of(Modifier.PRIVATE, Modifier.FINAL))
                        .build();
                builder.addField(wrapperField);
                wrapperMap.put(relationshipInfo, wrapperField);

                final Field mappingIdIndexField = new Field.Builder()
                        .setType(Types.Primitives.INTEGER)
                        .setModifiers(EnumSet.of(Modifier.PRIVATE, Modifier.FINAL))
                        .build();
                builder.addField(mappingIdIndexField);
                mappingIdIndexFieldMap.put(relationshipInfo, mappingIdIndexField);

                for (ColumnInfo columnInfo : childEntityInfo.getColumns()) {
                    if (columnInfo.getColumnType() == ColumnType.ENTITY) {
                        continue;
                    }
                    final Field field = new Field.Builder()
                            .setType(Types.Primitives.INTEGER)
                            .setModifiers(EnumSet.of(Modifier.PRIVATE, Modifier.FINAL))
                            .build();
                    builder.addField(field);
                    indexFieldMap.put(new RelationshipColumnIdentifier(relationshipInfo, columnInfo), field);
                }

                final GenericType cacheType = Types.generic(SimpleOrmTypes.LONG_SPARSE_ARRAY_COMPAT, Types.generic(Types.LIST, childEntityType));
                final Field cacheField = new Field.Builder()
                        .setModifiers(EnumSet.of(Modifier.PRIVATE, Modifier.FINAL))
                        .setType(cacheType)
                        .setInitialValue(cacheType.newInstance())
                        .build();
                builder.addField(cacheField);

                final Field childrenLoadedFlag = new Field.Builder()
                        .setType(Types.Primitives.BOOLEAN)
                        .setModifiers(EnumSet.of(Modifier.PRIVATE))
                        .setInitialValue(Values.of(false))
                        .build();
                builder.addField(childrenLoadedFlag);

                final Method childMethod = new Method.Builder()
                        .setReturnType(Types.generic(Types.LIST, childEntityType))
                        .setModifiers(EnumSet.of(Modifier.PRIVATE))
                        .setCode(new ExecutableBuilder() {

                            private Variable mParentId;

                            @Override
                            protected List<Variable> createParameters() {
                                final List<Variable> parameters = new ArrayList<>();
                                parameters.add(mParentId = Variables.of(Types.Primitives.LONG));
                                return parameters;
                            }

                            @Override
                            protected void write(Block block) {
                                block.append(new If.Builder().add(Values.invert(childrenLoadedFlag), new BlockWriter() {
                                    @Override
                                    protected void write(Block block) {
                                        block.set(childrenLoadedFlag, Values.of(true)).append(";").newLine();

                                        block.append(new CountingFor.Builder()
                                                .setCounterType(Types.Primitives.INTEGER)
                                                .setValues(Values.of(0), METHOD_GET_COUNT.callOnTarget(wrapperField))
                                                .setMode(CountingFor.Mode.INCREMENTING)
                                                .setIteration(new CountingFor.Iteration() {
                                                    @Override
                                                    public void onIteration(Block block, Variable index, CodeElement endValue) {
                                                        final CodeElement instance = appendReadInstance(block, index, relationshipInfo, wrapperField, cache, childEntityInfo, indexFieldMap, childMethodWrapperMap, adapterFieldMap);
                                                        final Variable mappingId = Variables.of(Types.Primitives.LONG, Modifier.FINAL);
                                                        block.set(mappingId, COLUMN_METHOD_MAP.get(ColumnType.PRIMITIVE_LONG).callOnTarget(wrapperField, mappingIdIndexField)).append(";").newLine();

                                                        final Variable list = Variables.of(Types.generic(Types.LIST, childEntityType));
                                                        block.set(list, METHOD_GET.callOnTarget(cacheField, mappingId, Types.generic(Types.ARRAY_LIST, childEntityType).newInstance())).append(";").newLine();
                                                        block.append(METHOD_ADD.callOnTarget(list, instance)).append(";").newLine();
                                                        block.append(METHOD_PUT.callOnTarget(cacheField, mappingId, list)).append(";");
                                                    }

                                                    @Override
                                                    public void onCompare(Block block, Variable index, CodeElement endValue) {
                                                        block.append(Operators.operate(index, "<", endValue));
                                                    }
                                                })
                                                .build());
                                    }
                                })
                                        .build());
                                block.newLine();
                                block.append("return ").append(METHOD_GET.callOnTarget(cacheField, mParentId, Types.generic(Types.ARRAY_LIST, childEntityType).newInstance())).append(";");
                            }
                        })
                        .build();
                builder.addMethod(childMethod);
                getMethodWrapper(childMethodWrapperMap, childEntityInfo, relationshipInfo.getColumnInfo()).setMethod(childMethod);
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
            indexFieldMap.put(new RelationshipColumnIdentifier(null, columnInfo), field);
        }

        final Method notNullOrEmpty = new Method.Builder()
                .setReturnType(Types.Primitives.BOOLEAN)
                .setModifiers(EnumSet.of(Modifier.PRIVATE))
                .setCode(new ExecutableBuilder() {

                    private Variable mString;

                    @Override
                    protected List<Variable> createParameters() {
                        final List<Variable> parameters = new ArrayList<>();
                        parameters.add(mString = Variables.of(Types.STRING));
                        return parameters;
                    }

                    @Override
                    protected void write(Block block) {
                        block.append("return ")
                                .append(Operators.operate(mString, "!=", Values.ofNull()))
                                .append(" && ")
                                .append(Values.invert(METHOD_IS_EMPTY.callOnTarget(METHOD_TRIM.callOnTarget(mString))))
                                .append(";");
                    }
                })
                .build();
        builder.addMethod(notNullOrEmpty);

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
                                .add(notNullOrEmpty.call(mSelectionString), new BlockWriter() {
                                    @Override
                                    protected void write(Block block) {
                                        block.append(METHOD_APPEND.callOnTarget(METHOD_APPEND.callOnTarget(stringBuilder, Values.of(" WHERE ")), mSelectionString)).append(";");
                                    }
                                })
                                .build());
                        block.newLine();

                        block.append(new If.Builder()
                                .add(notNullOrEmpty.call(mOrderByString), new BlockWriter() {
                                    @Override
                                    protected void write(Block block) {
                                        block.append(METHOD_APPEND.callOnTarget(METHOD_APPEND.callOnTarget(stringBuilder, Values.of(" ORDER BY ")), mOrderByString)).append(";");
                                    }
                                })
                                .build());
                        block.newLine();

                        block.append(new If.Builder()
                                .add(notNullOrEmpty.call(mLimitString), new BlockWriter() {
                                    @Override
                                    protected void write(Block block) {
                                        block.append(METHOD_APPEND.callOnTarget(METHOD_APPEND.callOnTarget(stringBuilder, Values.of(" LIMIT ")), mLimitString)).append(";");
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
                            final Field field = indexFieldMap.get(new RelationshipColumnIdentifier(null, columnInfo));
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
                                    final Field field = indexFieldMap.get(new RelationshipColumnIdentifier(key, columnInfo));
                                    block.newLine().set(field, METHOD_GET_COLUMN_INDEX.callOnTarget(wrapperField, Values.of(columnInfo.getColumnName()))).append(";");
                                }
                                final Field mappingIdIndexField = mappingIdIndexFieldMap.get(key);
                                block.newLine().set(mappingIdIndexField, METHOD_GET_COLUMN_INDEX.callOnTarget(wrapperField, Values.of(MappingTables.COLUMN_PARENT_ID))).append(";");
                            }
                        });

                        block.newLine().set(sizeField, METHOD_GET_COUNT.callOnTarget(wrapperField)).append(";");
                        block.newLine().set(cacheField, Types.arrayOf(entityType).newInstance(sizeField)).append(";");
                    }
                })
                .build());

        final Method readFromPosition = new Method.Builder()
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
                        final CodeElement instance = appendReadInstance(block, mPosition, null, wrapperField, cache, info, indexFieldMap, childMethodWrapperMap, adapterFieldMap);
                        block.append("return ").append(instance).append(";");
                    }
                })
                .build();
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

        builder.addMethod(new Method.Builder()
                .setName("finalize")
                .addThrownException(SimpleOrmTypes.THROWABLE)
                .setModifiers(EnumSet.of(Modifier.PROTECTED))
                .addAnnotation(Annotations.forType(Override.class))
                .setCode(new ArrayList<Variable>(), new BlockWriter() {
                    @Override
                    protected void write(Block block) {
                        block.append(METHOD_FINALIZE.callOnTarget(Methods.SUPER)).append(";").newLine();
                        block.append(METHOD_CLOSE.callOnTarget(wrapperField)).append(";");

                        for (Field field : wrapperMap.values()) {
                            block.newLine().append(METHOD_CLOSE.callOnTarget(field)).append(";");
                        }
                    }
                })
                .build());

        final Implementation implementation = builder.build();
        return new EntityIteratorInfoImpl(implementation);
    }

    private CodeElement appendReadInstance(Block block, Variable index, RelationshipInfo relationshipInfo, Field wrapperField, DatabaseImplementationBuilder.EntityImplementationCache cache, EntityInfo info, Map<Identifier, Field> indexFieldMap, Map<Identifier, MethodWrapper> childMethodWrapperMap, Map<TypeAdapterInfo, Field> adapterFieldMap) {
        block.append(METHOD_MOVE_TO_POSITION.callOnTarget(wrapperField, index)).append(";").newLine();
        final EntityImplementationInfo implementationInfo = cache.get(info);
        final List<ColumnInfo> constructorParameters = implementationInfo.getConstructorParameters();
        final CodeElement[] parameters = new CodeElement[constructorParameters.size()];
        final List<ChildResolveInfo> resolveInfos = new ArrayList<>();
        Variable idVariable = null;
        for (int i = 0, size = constructorParameters.size(); i < size; i++) {
            final ColumnInfo columnInfo = constructorParameters.get(i);
            final ColumnType columnType = columnInfo.getColumnType();
            if (columnType == ColumnType.ENTITY) {
                resolveInfos.add(new ChildResolveInfo(i, columnInfo.getChildEntityInfo(), columnInfo));
                continue;
            }

            final Field indexField = indexFieldMap.get(new RelationshipColumnIdentifier(relationshipInfo, columnInfo));
            final Type type = COLUMN_TYPE_MAP.get(columnType);
            final Variable parameter = Variables.of(type, Modifier.FINAL);
            final Method method = COLUMN_METHOD_MAP.get(columnType);
            block.set(parameter, method.callOnTarget(wrapperField, indexField)).append(";").newLine();
            parameters[i] = applyAdaptersConvertTo(columnInfo.getTypeAdapters(), adapterFieldMap, parameter);

            if (columnInfo == info.getIdColumn()) {
                idVariable = parameter;
            }
        }
        for (ChildResolveInfo resolveInfo : resolveInfos) {
            final EntityInfo childInfo = resolveInfo.mChildInfo;
            final ColumnInfo columnInfo = resolveInfo.mColumnInfo;
            final MethodWrapper wrapper = getMethodWrapper(childMethodWrapperMap, childInfo, columnInfo);
            final ColumnInfo.CollectionType collectionType = columnInfo.getCollectionType();
            final Variable listVariable = Variables.of(Types.generic(Types.LIST, Types.of(childInfo.getEntityElement())), Modifier.FINAL);
            block.set(listVariable, wrapper.call(idVariable)).append(";").newLine();
            if (collectionType == ColumnInfo.CollectionType.LIST) {
                parameters[resolveInfo.mIndex] = listVariable;
            } else if (collectionType == ColumnInfo.CollectionType.NONE) {
                parameters[resolveInfo.mIndex] = new TernaryIf.Builder()
                        .setComparison(METHOD_IS_EMPTY.callOnTarget(listVariable))
                        .setTrueBlock(Values.ofNull())
                        .setFalseBlock(METHOD_GET.callOnTarget(listVariable, Values.of(0)))
                        .build();
            }
        }
        return implementationInfo.getImplementation().newInstance(parameters);
    }

    private MethodWrapper getMethodWrapper(Map<Identifier, MethodWrapper> childMethodWrapperMap, EntityInfo info, ColumnInfo column) {
        final Identifier key = new EntityInfoColumnIdentifier(info, column);
        final MethodWrapper wrapper = childMethodWrapperMap.get(key);
        if (wrapper != null) {
            return wrapper;
        }
        final MethodWrapper newWrapper = new MethodWrapper();
        childMethodWrapperMap.put(key, newWrapper);
        return newWrapper;
    }

    private class ChildResolveInfo {

        private final int mIndex;
        private final EntityInfo mChildInfo;
        private final ColumnInfo mColumnInfo;

        private ChildResolveInfo(int index, EntityInfo childInfo, ColumnInfo columnInfo) {
            mIndex = index;
            mChildInfo = childInfo;
            mColumnInfo = columnInfo;
        }

        public int getIndex() {
            return mIndex;
        }

        public EntityInfo getChildInfo() {
            return mChildInfo;
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

    private CodeElement applyAdaptersConvertTo(List<TypeAdapterInfo> typeAdapters, Map<TypeAdapterInfo, Field> adapterFieldMap, CodeElement codeElement) {
        if (typeAdapters.isEmpty()) {
            return codeElement;
        }

        final TypeAdapterInfo info = typeAdapters.get(0);
        final Field field = adapterFieldMap.get(info);
        return METHOD_CONVERT_TO.callOnTarget(field, applyAdaptersConvertTo(typeAdapters.subList(1, typeAdapters.size()), adapterFieldMap, codeElement));
    }

    private interface Identifier {

    }

    private static class RelationshipColumnIdentifier implements Identifier {

        private final RelationshipInfo mRelationshipInfo;
        private final ColumnInfo mColumnInfo;

        private RelationshipColumnIdentifier(RelationshipInfo relationshipInfo, ColumnInfo columnInfo) {
            mRelationshipInfo = relationshipInfo;
            mColumnInfo = columnInfo;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            RelationshipColumnIdentifier that = (RelationshipColumnIdentifier) o;

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

    private static class EntityInfoColumnIdentifier implements Identifier {

        private final EntityInfo mEntityInfo;
        private final ColumnInfo mColumnInfo;

        private EntityInfoColumnIdentifier(EntityInfo entityInfo, ColumnInfo columnInfo) {
            mEntityInfo = entityInfo;
            mColumnInfo = columnInfo;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            EntityInfoColumnIdentifier that = (EntityInfoColumnIdentifier) o;

            if (mEntityInfo != null ? !mEntityInfo.equals(that.mEntityInfo) : that.mEntityInfo != null)
                return false;
            return mColumnInfo != null ? mColumnInfo.equals(that.mColumnInfo) : that.mColumnInfo == null;

        }

        @Override
        public int hashCode() {
            int result = mEntityInfo != null ? mEntityInfo.hashCode() : 0;
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

    private static class MethodWrapper extends BlockWriter implements Method {

        private Method mMethod;

        @Override
        protected void write(Block block) {
            block.append(mMethod);
        }

        @Override
        public CodeElement callOnTarget(final CodeElement codeElement, final CodeElement... codeElements) {
            return new BlockWriter() {
                @Override
                protected void write(Block block) {
                    block.append(mMethod.callOnTarget(codeElement, codeElements));
                }
            };
        }

        @Override
        public CodeElement call(final CodeElement... codeElements) {
            return new BlockWriter() {
                @Override
                protected void write(Block block) {
                    block.append(mMethod.call(codeElements));
                }
            };
        }

        @Override
        public CodeElement getDeclaration() {
            return new BlockWriter() {
                @Override
                protected void write(Block block) {
                    block.append(mMethod.getDeclaration());
                }
            };
        }

        public void setMethod(Method method) {
            mMethod = method;
        }
    }
}
