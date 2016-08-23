package com.github.wrdlbrnft.simpleorm.processor.builder.databases.factory;

import com.github.wrdlbrnft.codebuilder.code.Block;
import com.github.wrdlbrnft.codebuilder.code.CodeElement;
import com.github.wrdlbrnft.codebuilder.elements.ifs.TernaryIf;
import com.github.wrdlbrnft.codebuilder.elements.values.Values;
import com.github.wrdlbrnft.codebuilder.executables.ExecutableBuilder;
import com.github.wrdlbrnft.codebuilder.executables.Method;
import com.github.wrdlbrnft.codebuilder.executables.Methods;
import com.github.wrdlbrnft.codebuilder.implementations.Implementation;
import com.github.wrdlbrnft.codebuilder.types.Types;
import com.github.wrdlbrnft.codebuilder.util.Operators;
import com.github.wrdlbrnft.codebuilder.util.ProcessingHelper;
import com.github.wrdlbrnft.codebuilder.util.Utils;
import com.github.wrdlbrnft.codebuilder.variables.Variable;
import com.github.wrdlbrnft.simpleorm.processor.analyzer.databases.DatabaseInfo;
import com.github.wrdlbrnft.simpleorm.processor.builder.databases.implementation.DatabaseImplementationBuilder;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Modifier;

/**
 * Created with Android Studio
 * User: Xaver
 * Date: 13/07/16
 */

public class DatabaseFactoryBuilder {

    private static final Method TO_CHAR_ARRAY = Methods.stub("toCharArray");

    private final ProcessingEnvironment mProcessingEnvironment;
    private final ProcessingHelper mProcessingHelper;
    private final DatabaseImplementationBuilder mDatabaseImplementationBuilder;

    public DatabaseFactoryBuilder(ProcessingEnvironment processingEnv) {
        mProcessingEnvironment = processingEnv;
        mProcessingHelper = ProcessingHelper.from(processingEnv);
        mDatabaseImplementationBuilder = new DatabaseImplementationBuilder(processingEnv);
    }

    public Implementation build(final DatabaseInfo databaseInfo) {

        final Implementation.Builder builder = new Implementation.Builder();
        builder.setModifiers(EnumSet.of(Modifier.PUBLIC, Modifier.FINAL));
        builder.setName(Utils.createGeneratedClassName(databaseInfo.getTypeElement(), "", "Factory"));

        final Implementation databaseImpl = mDatabaseImplementationBuilder.build(databaseInfo);
        builder.addNestedImplementation(databaseImpl);

        final Method newInstanceMethod = new Method.Builder()
                .setName("newInstance")
                .setModifiers(EnumSet.of(Modifier.PUBLIC, Modifier.STATIC))
                .setReturnType(Types.of(databaseInfo.getTypeElement()))
                .setCode(new ExecutableBuilder() {

                    private Variable mParamContext;
                    private Variable mParamPassword;

                    @Override
                    protected List<Variable> createParameters() {
                        final List<Variable> parameters = new ArrayList<>();
                        parameters.add(mParamContext = new Variable.Builder()
                                .setType(Types.Android.CONTEXT)
                                .setName("context")
                                .build());

                        if (databaseInfo.isEncrypted()) {
                            parameters.add(mParamPassword = new Variable.Builder()
                                    .setType(Types.of(char[].class))
                                    .setName("password")
                                    .build());
                        }

                        return parameters;
                    }

                    @Override
                    protected void write(Block block) {
                        final CodeElement openHelperInstance = databaseInfo.isEncrypted()
                                ? databaseImpl.newInstance(mParamContext, performNullCheck(mParamPassword, mParamPassword, Types.arrayOf(Types.of(char.class)).newInstance(Values.of(0))))
                                : databaseImpl.newInstance(mParamContext);
                        block.append("return ").append(openHelperInstance).append(";");
                    }
                })
                .build();
        builder.addMethod(newInstanceMethod);

        if (databaseInfo.isEncrypted()) {

            builder.addMethod(new Method.Builder()
                    .setName("newInstance")
                    .setModifiers(EnumSet.of(Modifier.PUBLIC, Modifier.STATIC))
                    .setReturnType(Types.of(databaseInfo.getTypeElement()))
                    .setCode(new ExecutableBuilder() {

                        private Variable mParamContext;
                        private Variable mParamPassword;

                        @Override
                        protected List<Variable> createParameters() {
                            final List<Variable> parameters = new ArrayList<>();
                            parameters.add(mParamContext = new Variable.Builder()
                                    .setType(Types.Android.CONTEXT)
                                    .setName("context")
                                    .build());
                            parameters.add(mParamPassword = new Variable.Builder()
                                    .setType(Types.STRING)
                                    .setName("password")
                                    .build());
                            return parameters;
                        }

                        @Override
                        protected void write(Block block) {
                            block.append("return ").append(newInstanceMethod.call(
                                    mParamContext,
                                    performNullCheck(mParamPassword, TO_CHAR_ARRAY.callOnTarget(mParamPassword), Types.arrayOf(Types.of(char.class)).newInstance(Values.of(0)))
                            )).append(";");
                        }
                    })
                    .build());
        }

        return builder.build();
    }

    private CodeElement performNullCheck(CodeElement value, CodeElement trueValue, CodeElement falseBlock) {
        return new TernaryIf.Builder()
                .setComparison(Operators.operate(value, "!=", Values.ofNull()))
                .setTrueBlock(trueValue)
                .setFalseBlock(falseBlock)
                .build();
    }
}
