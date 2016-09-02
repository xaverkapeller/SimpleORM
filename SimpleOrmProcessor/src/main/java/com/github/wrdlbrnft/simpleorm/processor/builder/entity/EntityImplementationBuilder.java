package com.github.wrdlbrnft.simpleorm.processor.builder.entity;

import com.github.wrdlbrnft.codebuilder.code.Block;
import com.github.wrdlbrnft.codebuilder.code.BlockWriter;
import com.github.wrdlbrnft.codebuilder.executables.Constructor;
import com.github.wrdlbrnft.codebuilder.executables.ExecutableBuilder;
import com.github.wrdlbrnft.codebuilder.executables.Method;
import com.github.wrdlbrnft.codebuilder.implementations.Implementation;
import com.github.wrdlbrnft.codebuilder.types.Type;
import com.github.wrdlbrnft.codebuilder.types.Types;
import com.github.wrdlbrnft.codebuilder.variables.Field;
import com.github.wrdlbrnft.codebuilder.variables.Variable;
import com.github.wrdlbrnft.codebuilder.variables.Variables;
import com.github.wrdlbrnft.simpleorm.processor.analyzer.entity.ColumnInfo;
import com.github.wrdlbrnft.simpleorm.processor.analyzer.entity.EntityInfo;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.VariableElement;

/**
 * Created with Android Studio
 * User: Xaver
 * Date: 11/07/16
 */

public class EntityImplementationBuilder {

    private final ProcessingEnvironment mProcessingEnvironment;

    public EntityImplementationBuilder(ProcessingEnvironment processingEnvironment) {
        mProcessingEnvironment = processingEnvironment;
    }

    public EntityImplementationInfo build(final EntityInfo info) {
        final Implementation.Builder builder = new Implementation.Builder();
        builder.setModifiers(EnumSet.of(Modifier.PRIVATE, Modifier.STATIC));
        builder.addImplementedType(Types.of(info.getEntityElement()));

        final List<ColumnInfo> constructorParameters = new ArrayList<>();
        final Map<ColumnInfo, Field> fieldMap = new HashMap<>();
        for (ColumnInfo columnInfo : info.getColumns()) {
            constructorParameters.add(columnInfo);
            fieldMap.put(columnInfo, implementMethod(builder, columnInfo));
        }

        builder.addConstructor(new Constructor.Builder()
                .setCode(new ExecutableBuilder() {

                    private final Map<ColumnInfo, Variable> mParameterMap = new HashMap<>();

                    @Override
                    protected List<Variable> createParameters() {
                        final List<Variable> parameters = new ArrayList<>();
                        for (ColumnInfo columnInfo : constructorParameters) {
                            final Variable parameter = Variables.of(columnInfo.getObjectType());
                            parameters.add(parameter);
                            mParameterMap.put(columnInfo, parameter);
                        }
                        return parameters;
                    }

                    @Override
                    protected void write(Block block) {
                        boolean insertNewLine = false;
                        for (ColumnInfo columnInfo : constructorParameters) {
                            final Variable parameter = mParameterMap.get(columnInfo);
                            final Field field = fieldMap.get(columnInfo);

                            if (insertNewLine) {
                                block.newLine();
                            } else {
                                insertNewLine = true;
                            }

                            block.set(field, parameter).append(";");
                        }
                    }
                })
                .build());

        return new EntityImplementationInfoImpl(builder.build(), constructorParameters);
    }

    private Field implementMethod(Implementation.Builder builder, ColumnInfo info) {
        final Field field = new Field.Builder()
                .setModifiers(EnumSet.of(Modifier.PRIVATE))
                .setType(info.getObjectType())
                .build();
        builder.addField(field);

        final ExecutableElement getter = info.getGetterElement();
        if (getter != null) {
            builder.addMethod(new Method.Builder()
                    .setReturnType(Types.of(getter.getReturnType()))
                    .setName(getter.getSimpleName().toString())
                    .setModifiers(EnumSet.of(Modifier.PUBLIC))
                    .setCode(new ArrayList<Variable>(), new BlockWriter() {
                        @Override
                        protected void write(Block block) {
                            block.append("return ").append(field).append(";");
                        }
                    })
                    .build());
        }

        final ExecutableElement setter = info.getSetterElement();
        if (setter != null) {
            final List<? extends VariableElement> parameters = setter.getParameters();
            final Type setterType = Types.of(parameters.get(0).asType());
            builder.addMethod(new Method.Builder()
                    .setName(setter.getSimpleName().toString())
                    .setModifiers(EnumSet.of(Modifier.PUBLIC))
                    .setCode(new ExecutableBuilder() {

                        private Variable mValue;

                        @Override
                        protected List<Variable> createParameters() {
                            final List<Variable> parameters = new ArrayList<>();
                            parameters.add(mValue = Variables.of(setterType));
                            return parameters;
                        }

                        @Override
                        protected void write(Block block) {
                            block.set(field, mValue).append(";");
                        }
                    })
                    .build());
        }

        return field;
    }
}
