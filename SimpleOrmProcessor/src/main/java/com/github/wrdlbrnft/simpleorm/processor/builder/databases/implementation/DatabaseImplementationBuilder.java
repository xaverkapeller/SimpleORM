package com.github.wrdlbrnft.simpleorm.processor.builder.databases.implementation;

import com.github.wrdlbrnft.codebuilder.annotations.Annotations;
import com.github.wrdlbrnft.codebuilder.code.Block;
import com.github.wrdlbrnft.codebuilder.code.CodeElement;
import com.github.wrdlbrnft.codebuilder.elements.values.Values;
import com.github.wrdlbrnft.codebuilder.executables.Constructor;
import com.github.wrdlbrnft.codebuilder.executables.ExecutableBuilder;
import com.github.wrdlbrnft.codebuilder.executables.Method;
import com.github.wrdlbrnft.codebuilder.executables.Methods;
import com.github.wrdlbrnft.codebuilder.implementations.Implementation;
import com.github.wrdlbrnft.codebuilder.types.Types;
import com.github.wrdlbrnft.codebuilder.variables.Field;
import com.github.wrdlbrnft.codebuilder.variables.Variable;
import com.github.wrdlbrnft.codebuilder.variables.Variables;
import com.github.wrdlbrnft.simpleorm.processor.SimpleOrmTypes;
import com.github.wrdlbrnft.simpleorm.processor.analyzer.databases.DatabaseInfo;
import com.github.wrdlbrnft.simpleorm.processor.analyzer.databases.RepositoryInfo;
import com.github.wrdlbrnft.simpleorm.processor.analyzer.entity.EntityInfo;
import com.github.wrdlbrnft.simpleorm.processor.builder.openhelper.SQLiteProviderBuilder;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;

/**
 * Created with Android Studio
 * User: Xaver
 * Date: 13/07/16
 */

public class DatabaseImplementationBuilder {

    private static final Method CHANGE_PASSWORD = Methods.stub("changePassword");

    private final ProcessingEnvironment mProcessingEnvironment;
    private final SQLiteProviderBuilder mSQLiteProviderBuilder;

    public DatabaseImplementationBuilder(ProcessingEnvironment processingEnv) {
        mProcessingEnvironment = processingEnv;
        mSQLiteProviderBuilder = new SQLiteProviderBuilder(processingEnv);
    }

    public Implementation build(final DatabaseInfo databaseInfo) {
        final TypeElement databaseTypeElement = databaseInfo.getTypeElement();

        final Implementation.Builder builder = new Implementation.Builder();
        builder.setModifiers(EnumSet.of(Modifier.PRIVATE, Modifier.STATIC, Modifier.FINAL));
        builder.addImplementedType(Types.of(databaseTypeElement));

        final Implementation openHelperImplementation = mSQLiteProviderBuilder.build(databaseInfo);
        builder.addNestedImplementation(openHelperImplementation);

        final Field providerField = new Field.Builder()
                .setType(databaseInfo.isEncrypted()
                        ? SimpleOrmTypes.ENCRYPTED_SQLITE_PROVIDER.asType()
                        : SimpleOrmTypes.SQLITE_PROVIDER.asType())
                .setModifiers(EnumSet.of(Modifier.PRIVATE, Modifier.FINAL))
                .build();
        builder.addField(providerField);

        final ExecutableElement changePasswordMethod = databaseInfo.getChangePasswordMethod();
        if (changePasswordMethod != null) {
            builder.addMethod(new Method.Builder()
                    .setName(changePasswordMethod.getSimpleName().toString())
                    .setModifiers(EnumSet.of(Modifier.PUBLIC))
                    .addAnnotation(Annotations.forType(Override.class))
                    .setCode(new ExecutableBuilder() {

                        private Variable mParamPassword;

                        @Override
                        protected List<Variable> createParameters() {
                            final List<Variable> parameters = new ArrayList<>();
                            parameters.add(mParamPassword = createParameter(changePasswordMethod));
                            return parameters;
                        }

                        @Override
                        protected void write(Block block) {
                            block.append(CHANGE_PASSWORD.callOnTarget(providerField, mParamPassword)).append(";");
                        }

                        private Variable createParameter(ExecutableElement changePasswordMethod) {
                            final List<? extends VariableElement> parameters = changePasswordMethod.getParameters();
                            final VariableElement parameter = parameters.get(0);
                            return new Variable.Builder()
                                    .setType(Types.of(parameter.asType()))
                                    .build();
                        }
                    })
                    .build());
        }

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
                        final CodeElement openHelperInstance = databaseInfo.isEncrypted()
                                ? openHelperImplementation.newInstance(mParamContext, mParamPassword)
                                : openHelperImplementation.newInstance(mParamContext);
                        block.set(providerField, openHelperInstance).append(";");
                    }
                })
                .build());

        for (RepositoryInfo repositoryInfo : databaseInfo.getRepositoryInfos()) {

            final EntityInfo entityInfo = repositoryInfo.getEntityInfo();


            final ExecutableElement method = repositoryInfo.getMethod();
            builder.addMethod(new Method.Builder()
                    .setName(method.getSimpleName().toString())
                    .setModifiers(EnumSet.of(Modifier.PUBLIC))
                    .addAnnotation(Annotations.forType(Override.class))
                    .setReturnType(Types.of(method.getReturnType()))
                    .setCode(new ExecutableBuilder() {
                        @Override
                        protected List<Variable> createParameters() {
                            return new ArrayList<>();
                        }

                        @Override
                        protected void write(Block block) {
                            block.append("return ").append(Values.ofNull()).append(";");
                        }
                    })
                    .build());
        }

        return builder.build();
    }
}
