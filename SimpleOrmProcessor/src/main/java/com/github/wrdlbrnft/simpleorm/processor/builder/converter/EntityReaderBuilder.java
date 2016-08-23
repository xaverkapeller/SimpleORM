package com.github.wrdlbrnft.simpleorm.processor.builder.converter;

import com.github.wrdlbrnft.codebuilder.annotations.Annotations;
import com.github.wrdlbrnft.codebuilder.code.Block;
import com.github.wrdlbrnft.codebuilder.executables.ExecutableBuilder;
import com.github.wrdlbrnft.codebuilder.executables.Method;
import com.github.wrdlbrnft.codebuilder.implementations.Implementation;
import com.github.wrdlbrnft.codebuilder.types.Types;
import com.github.wrdlbrnft.codebuilder.variables.Variable;
import com.github.wrdlbrnft.simpleorm.processor.SimpleOrmTypes;
import com.github.wrdlbrnft.simpleorm.processor.analyzer.entity.EntityInfo;

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

public class EntityReaderBuilder {

    private final ProcessingEnvironment mProcessingEnvironment;

    public EntityReaderBuilder(ProcessingEnvironment processingEnvironment) {
        mProcessingEnvironment = processingEnvironment;
    }

    public Implementation build(final EntityInfo entityInfo) {
        final Implementation.Builder builder = new Implementation.Builder();
        builder.setExtendedType(SimpleOrmTypes.ENTITY_READER.asType());

        builder.addMethod(new Method.Builder()
                .setName("read")
                .addAnnotation(Annotations.forType(Override.class))
                .setModifiers(EnumSet.of(Modifier.PUBLIC))
                .setReturnType(Types.of(entityInfo.getEntityElement()))
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
                .setName("moveToNext")
                .addAnnotation(Annotations.forType(Override.class))
                .setModifiers(EnumSet.of(Modifier.PUBLIC))
                .setReturnType(Types.Primitives.BOOLEAN)
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
                .setName("moveToFirst")
                .addAnnotation(Annotations.forType(Override.class))
                .setModifiers(EnumSet.of(Modifier.PUBLIC))
                .setReturnType(Types.Primitives.BOOLEAN)
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
                .setName("close")
                .setModifiers(EnumSet.of(Modifier.PUBLIC))
                .addAnnotation(Annotations.forType(Override.class))
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

        return builder.build();
    }
}
