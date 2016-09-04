package com.github.wrdlbrnft.simpleorm.processor.builder.entitymanager;

import com.github.wrdlbrnft.codebuilder.annotations.Annotations;
import com.github.wrdlbrnft.codebuilder.code.Block;
import com.github.wrdlbrnft.codebuilder.executables.Constructor;
import com.github.wrdlbrnft.codebuilder.executables.ExecutableBuilder;
import com.github.wrdlbrnft.codebuilder.executables.Method;
import com.github.wrdlbrnft.codebuilder.executables.Methods;
import com.github.wrdlbrnft.codebuilder.implementations.Implementation;
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
import com.github.wrdlbrnft.simpleorm.processor.builder.databases.implementation.DatabaseImplementationBuilder;
import com.github.wrdlbrnft.simpleorm.processor.builder.entitymanager.relationships.RelationshipAnalyzer;
import com.github.wrdlbrnft.simpleorm.processor.builder.entitymanager.relationships.RelationshipInfo;

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

    static final Method METHOD_ENTITIES_TO_SAVE = Methods.stub("getEntitiesToSave");
    static final Method METHOD_ENTITIES_TO_REMOVE = Methods.stub("getEntitiesToRemove");
    static final Method METHOD_DELETE = Methods.stub("delete");
    static final Method METHOD_OR = Methods.stub("or");
    static final Method METHOD_STATEMENT = Methods.stub("statement");
    static final Method METHOD_VALUE_OF = Methods.stub("valueOf");
    static final Method METHOD_BUILD = Methods.stub("build");
    static final Method METHOD_IS_EMPTY = Methods.stub("isEmpty");
    static final Method METHOD_EXEC_SQL = Methods.stub("execSql");
    static final Method METHOD_PUT = Methods.stub("put");
    static final Method METHOD_INSERT = Methods.stub("insert");
    static final Method METHOD_QUERY = Methods.stub("query");
    static final Method METHOD_GET_SELECTION = Methods.stub("getSelection");
    static final Method METHOD_GET_SELECTION_ARGS = Methods.stub("getSelectionArgs");
    static final Method METHOD_GET_ORDER_BY = Methods.stub("getOrderBy");
    static final Method METHOD_GET_LIMIT = Methods.stub("getLimit");
    static final Method METHOD_GET_COUNT = Methods.stub("getCount");
    static final Method METHOD_GET_COLUMN_INDEX = Methods.stub("getColumnIndex");
    static final Method METHOD_MOVE_TO_POSITION = Methods.stub("moveToPosition");
    static final Method METHOD_CONVERT_TO = Methods.stub("convertTo");
    static final Method METHOD_CONVERT_FROM = Methods.stub("convertFrom");
    static final Method METHOD_VERIFY_ID = Methods.stub("verifyIdOrThrow");

    private final ProcessingEnvironment mProcessingEnvironment;
    private final EntityIteratorBuilder mIteratorBuilder;
    private final RelationshipAnalyzer mRelationshipAnalyzer;

    public EntityManagerBuilder(ProcessingEnvironment processingEnv) {
        mProcessingEnvironment = processingEnv;
        mIteratorBuilder = new EntityIteratorBuilder(processingEnv);
        mRelationshipAnalyzer = new RelationshipAnalyzer(processingEnv);
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

        final Map<TypeAdapterInfo, Field> adapterFieldMap = new HashMap<>();
        for (TypeAdapterInfo typeAdapterInfo : getAllTypeAdapters(info)) {
            final Type type = Types.of(typeAdapterInfo.getAdapterElement());
            final Field field = new Field.Builder()
                    .setType(type)
                    .setModifiers(EnumSet.of(Modifier.PRIVATE, Modifier.STATIC, Modifier.FINAL))
                    .setInitialValue(type.newInstance())
                    .build();
            builder.addField(field);
            adapterFieldMap.put(typeAdapterInfo, field);
        }

        final List<RelationshipInfo> relationshipInfos = mRelationshipAnalyzer.analyze(info);
        final EntityIteratorInfo iteratorInfo = mIteratorBuilder.build(info, relationshipInfos, cache, adapterFieldMap);
        builder.addNestedImplementation(iteratorInfo.getImplementation());

        builder.addMethod(new Method.Builder()
                .setName("performSave")
                .setModifiers(EnumSet.of(Modifier.PROTECTED))
                .addAnnotation(Annotations.forType(Override.class))
                .setCode(new PerformSaveExecutableBuilder(info, adapterFieldMap))
                .build());

        builder.addMethod(new Method.Builder()
                .setName("performRemove")
                .setModifiers(EnumSet.of(Modifier.PROTECTED))
                .addAnnotation(Annotations.forType(Override.class))
                .setCode(new PerformRemoveExecutableBuilder(info, relationshipInfos, adapterFieldMap))
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
                        final Type type = iteratorInfo.getImplementation();
                        block.append("return ").append(type.newInstance(mReadableSQLiteWrapper, mQueryParameters)).append(";");
                    }
                })
                .build());

        return builder.build();
    }

    private Set<TypeAdapterInfo> getAllTypeAdapters(EntityInfo entityInfo) {
        final Set<TypeAdapterInfo> infos = new HashSet<>();
        iterateAllTypeAdapters(entityInfo, infos, new HashSet<ColumnInfo>());
        return infos;
    }

    private void iterateAllTypeAdapters(EntityInfo entityInfo, Set<TypeAdapterInfo> adapterInfos, Set<ColumnInfo> handledColumns) {
        for (ColumnInfo columnInfo : entityInfo.getColumns()) {
            if (!handledColumns.add(columnInfo)) {
                continue;
            }

            adapterInfos.addAll(columnInfo.getTypeAdapters());

            if (columnInfo.getColumnType() == ColumnType.ENTITY) {
                iterateAllTypeAdapters(columnInfo.getChildEntityInfo(), adapterInfos, handledColumns);
            }
        }
    }
}