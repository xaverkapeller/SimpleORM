package com.github.wrdlbrnft.simpleorm.processor.builder.entitymanager;

import com.github.wrdlbrnft.codebuilder.code.Block;
import com.github.wrdlbrnft.codebuilder.executables.Constructor;
import com.github.wrdlbrnft.codebuilder.executables.ExecutableBuilder;
import com.github.wrdlbrnft.codebuilder.executables.Method;
import com.github.wrdlbrnft.codebuilder.executables.Methods;
import com.github.wrdlbrnft.codebuilder.implementations.Implementation;
import com.github.wrdlbrnft.codebuilder.types.TypeParameter;
import com.github.wrdlbrnft.codebuilder.types.Types;
import com.github.wrdlbrnft.codebuilder.variables.Variable;
import com.github.wrdlbrnft.codebuilder.variables.Variables;
import com.github.wrdlbrnft.simpleorm.processor.SimpleOrmTypes;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Modifier;

/**
 * Created with Android Studio
 * User: Xaver
 * Date: 17/07/16
 */
public class EntityManagerBuilder {

    private final ProcessingEnvironment mProcessingEnvironment;

    public EntityManagerBuilder(ProcessingEnvironment processingEnvironment) {
        mProcessingEnvironment = processingEnvironment;
    }

    public Implementation build() {
        final TypeParameter typeParameter = Types.randomTypeParameter();

        final Implementation.Builder builder = new Implementation.Builder();
        builder.addTypeParameter(typeParameter);
        builder.setExtendedType(SimpleOrmTypes.BASE_ENTITY_MANAGER.asType());
        builder.setModifiers(EnumSet.of(Modifier.PRIVATE, Modifier.STATIC));

        builder.addConstructor(new Constructor.Builder()
                .setModifiers(EnumSet.of(Modifier.PUBLIC))
                .setCode(new ExecutableBuilder() {

                    private Variable mParamProvider;

                    @Override
                    protected List<Variable> createParameters() {
                        final List<Variable> parameters = new ArrayList<>();
                        parameters.add(mParamProvider = Variables.of(SimpleOrmTypes.SQLITE_PROVIDER.asType()));
                        return parameters;
                    }

                    @Override
                    protected void write(Block block) {
                        block.append(Methods.SUPER.call(mParamProvider)).append(";");
                    }
                })
                .build());

        builder.addMethod(new Method.Builder()
                .setName("performSave")
                .setModifiers(EnumSet.of(Modifier.PROTECTED))
                .setCode(new ExecutableBuilder() {
                    @Override
                    protected List<Variable> createParameters() {
                        return new ArrayList<>();
                    }

                    @Override
                    protected void write(Block block) {

                    }
                })
                .build());

        builder.addMethod(new Method.Builder()
                .setName("performDelete")
                .setModifiers(EnumSet.of(Modifier.PROTECTED))
                .build());

        builder.addMethod(new Method.Builder()
                .setName("performQuery")
                .setModifiers(EnumSet.of(Modifier.PROTECTED))
                .setReturnType(Types.generic(SimpleOrmTypes.ENTITY_READER.asType(), typeParameter))
                .build());

        return builder.build();
    }
}
