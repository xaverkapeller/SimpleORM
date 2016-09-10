package com.github.wrdlbrnft.simpleorm.processor.builder.openhelper;

import com.github.wrdlbrnft.codebuilder.code.Block;
import com.github.wrdlbrnft.codebuilder.elements.values.Values;
import com.github.wrdlbrnft.codebuilder.executables.Constructor;
import com.github.wrdlbrnft.codebuilder.executables.ExecutableBuilder;
import com.github.wrdlbrnft.codebuilder.executables.Method;
import com.github.wrdlbrnft.codebuilder.executables.Methods;
import com.github.wrdlbrnft.codebuilder.implementations.Implementation;
import com.github.wrdlbrnft.codebuilder.types.Type;
import com.github.wrdlbrnft.codebuilder.types.Types;
import com.github.wrdlbrnft.codebuilder.variables.Variable;
import com.github.wrdlbrnft.codebuilder.variables.Variables;
import com.github.wrdlbrnft.simpleorm.processor.SimpleOrmTypes;
import com.github.wrdlbrnft.simpleorm.processor.analyzer.databases.DatabaseInfo;
import com.github.wrdlbrnft.simpleorm.processor.analyzer.entity.EntityInfo;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Modifier;

/**
 * Created with Android Studio
 * User: Xaver
 * Date: 08/07/16
 */

public class SQLiteProviderBuilder {

    private final ProcessingEnvironment mProcessingEnvironment;

    public SQLiteProviderBuilder(ProcessingEnvironment processingEnvironment) {
        mProcessingEnvironment = processingEnvironment;
    }

    public Implementation build(final DatabaseInfo databaseInfo) {
        final Implementation.Builder builder = new Implementation.Builder();
        final Type providerType = databaseInfo.isEncrypted()
                ? SimpleOrmTypes.BASE_ENCRYPTED_SQLITE_PROVIDER
                : SimpleOrmTypes.BASE_PLAIN_SQLITE_PROVIDER;
        builder.setExtendedType(providerType);
        builder.setModifiers(EnumSet.of(Modifier.PRIVATE, Modifier.STATIC, Modifier.FINAL));

        builder.addConstructor(new Constructor.Builder()
                .setModifiers(EnumSet.of(Modifier.PRIVATE))
                .setCode(new ExecutableBuilder() {

                    private Variable mParamContext;
                    private Variable mParamPassword;

                    @Override
                    protected List<Variable> createParameters() {
                        final List<Variable> parameters = new ArrayList<>();
                        parameters.add(mParamContext = Variables.of(Types.Android.CONTEXT));

                        if (databaseInfo.isEncrypted()) {
                            parameters.add(mParamPassword = Variables.of(Types.of(char[].class)));
                        }

                        return parameters;
                    }

                    @Override
                    protected void write(Block block) {
                        if (databaseInfo.isEncrypted()) {
                            block.append(Methods.SUPER.call(
                                    mParamContext,
                                    Values.of(databaseInfo.getDatabaseName()),
                                    Values.of(databaseInfo.getDatabaseVersion()),
                                    mParamPassword
                            )).append(";");
                        } else {
                            block.append(Methods.SUPER.call(
                                    mParamContext,
                                    Values.of(databaseInfo.getDatabaseName()),
                                    Values.of(databaseInfo.getDatabaseVersion())
                            )).append(";");
                        }
                    }
                })
                .build());

        builder.addMethod(new Method.Builder()
                .setName("onCreate")
                .setModifiers(EnumSet.of(Modifier.PROTECTED))
                .setCode(new ExecutableBuilder() {

                    private Variable mManager;

                    @Override
                    protected List<Variable> createParameters() {
                        final List<Variable> parameters = new ArrayList<>();
                        parameters.add(mManager = Variables.of(SimpleOrmTypes.SQLITE_DATABASE_MANAGER));
                        return parameters;
                    }

                    @Override
                    protected void write(Block block) {
                        final Set<EntityInfo> entityInfos = databaseInfo.getEntityInfos();
                        final List<Query> tableQueries = new ArrayList<>();
                        final List<Query> triggerQueries = new ArrayList<>();
                        for (EntityInfo entityInfo : entityInfos) {
                            final CreateQueries createQueries = QueryFactory.createQueriesFor(entityInfo);
                            tableQueries.addAll(createQueries.getTableQueries());
                            triggerQueries.addAll(createQueries.getTriggerQueries());
                        }

                        boolean appendNewLine = false;
                        for (Query query : tableQueries) {
                            if (appendNewLine) {
                                block.newLine();
                            } else {
                                appendNewLine = true;
                            }
                            block.append(query.execute(mManager)).append(";");
                        }
                        for (Query query : triggerQueries) {
                            if (appendNewLine) {
                                block.newLine();
                            } else {
                                appendNewLine = true;
                            }
                            block.append(query.execute(mManager)).append(";");
                        }
                    }
                })
                .build());

        builder.addMethod(new Method.Builder()
                .setName("onUpgrade")
                .setModifiers(EnumSet.of(Modifier.PROTECTED))
                .setCode(new ExecutableBuilder() {

                    private Variable mParamManager;
                    private Variable mOldVersion;
                    private Variable mNewVersion;

                    @Override
                    protected List<Variable> createParameters() {
                        final List<Variable> parameters = new ArrayList<>();
                        parameters.add(mParamManager = Variables.of(SimpleOrmTypes.SQLITE_DATABASE_MANAGER));
                        parameters.add(mOldVersion = Variables.of(Types.Primitives.INTEGER));
                        parameters.add(mNewVersion = Variables.of(Types.Primitives.INTEGER));
                        return parameters;
                    }

                    @Override
                    protected void write(Block block) {

                    }
                })
                .build());

        return builder.build();
    }
}
