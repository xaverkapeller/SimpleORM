package com.github.wrdlbrnft.simpleorm.processor.builder.entitybuilder;

import com.github.wrdlbrnft.codebuilder.code.Block;
import com.github.wrdlbrnft.codebuilder.code.BlockWriter;
import com.github.wrdlbrnft.codebuilder.code.CodeElement;
import com.github.wrdlbrnft.codebuilder.elements.values.Values;
import com.github.wrdlbrnft.codebuilder.executables.ExecutableBuilder;
import com.github.wrdlbrnft.codebuilder.executables.Method;
import com.github.wrdlbrnft.codebuilder.implementations.Implementation;
import com.github.wrdlbrnft.codebuilder.types.Type;
import com.github.wrdlbrnft.codebuilder.types.Types;
import com.github.wrdlbrnft.codebuilder.util.Utils;
import com.github.wrdlbrnft.codebuilder.variables.Field;
import com.github.wrdlbrnft.codebuilder.variables.Variable;
import com.github.wrdlbrnft.codebuilder.variables.Variables;
import com.github.wrdlbrnft.simpleorm.processor.analyzer.entity.ColumnInfo;
import com.github.wrdlbrnft.simpleorm.processor.analyzer.entity.EntityInfo;
import com.github.wrdlbrnft.simpleorm.processor.builder.entity.EntityImplementationBuilder;
import com.github.wrdlbrnft.simpleorm.processor.builder.entity.EntityImplementationInfo;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;

/**
 * Created with Android Studio
 * User: Xaver
 * Date: 27/08/16
 */

public class EntityBuilderBuilder {

    private final ProcessingEnvironment mProcessingEnv;
    private final EntityImplementationBuilder mImplementationBuilder;

    public EntityBuilderBuilder(ProcessingEnvironment processingEnv) {
        mProcessingEnv = processingEnv;
        mImplementationBuilder = new EntityImplementationBuilder(processingEnv);
    }

    public Implementation build(EntityInfo info) {
        final EntityImplementationInfo implementationInfo = mImplementationBuilder.build(info);

        final TypePlaceHolder typePlaceHolder = new TypePlaceHolder();

        final Implementation.Builder builder = new Implementation.Builder();
        builder.setName(Utils.createGeneratedClassName(info.getEntityElement(), "", "Builder"));
        builder.setModifiers(EnumSet.of(Modifier.PUBLIC, Modifier.FINAL));
        builder.addNestedImplementation(implementationInfo.getImplementation());

        final List<ColumnInfo> constructorParameters = implementationInfo.getConstructorParameters();
        final CodeElement[] parameters = new CodeElement[constructorParameters.size()];
        for (int i = 0, size = constructorParameters.size(); i < size; i++) {
            final ColumnInfo columnInfo = constructorParameters.get(i);
            final Type type = columnInfo.getObjectType();

            final Field field = new Field.Builder()
                    .setModifiers(EnumSet.of(Modifier.PRIVATE))
                    .setType(type)
                    .build();
            builder.addField(field);
            parameters[i] = field;

            final String setterName = parseSetterName(columnInfo);
            final String variableName = parseVariableName(setterName);
            builder.addMethod(new Method.Builder()
                    .setModifiers(EnumSet.of(Modifier.PUBLIC))
                    .setName(setterName)
                    .setReturnType(typePlaceHolder)
                    .setCode(new ExecutableBuilder() {

                        private Variable mValue;

                        @Override
                        protected List<Variable> createParameters() {
                            final List<Variable> parameters = new ArrayList<>();
                            parameters.add(mValue = new Variable.Builder()
                                    .setType(type)
                                    .setName(variableName)
                                    .build());
                            return parameters;
                        }

                        @Override
                        protected void write(Block block) {
                            block.set(field, mValue).append(";").newLine();
                            block.append("return ").append(Values.ofThis()).append(";");
                        }
                    })
                    .build());
        }

        builder.addMethod(new Method.Builder()
                .setName("build")
                .setModifiers(EnumSet.of(Modifier.PUBLIC))
                .setReturnType(Types.of(info.getEntityElement()))
                .setCode(new ExecutableBuilder() {
                    @Override
                    protected List<Variable> createParameters() {
                        return new ArrayList<>();
                    }

                    @Override
                    protected void write(Block block) {
                        block.append("return ").append(implementationInfo.getImplementation().newInstance(parameters)).append(";");
                    }
                })
                .build());

        final Implementation implementation = builder.build();
        typePlaceHolder.setType(implementation);
        return implementation;
    }

    private String parseVariableName(String setterName) {
        if (setterName.startsWith("set")) {
            return setterName.substring(3, 4).toLowerCase() + setterName.substring(4, setterName.length());
        }
        return setterName;
    }

    private static class TypePlaceHolder extends BlockWriter implements Type {

        private Type mType;

        @Override
        protected void write(Block block) {
            block.append(mType);
        }

        public void setType(Type type) {
            mType = type;
        }

        @Override
        public CodeElement newInstance(CodeElement... codeElements) {
            return mType.newInstance(codeElements);
        }

        @Override
        public CodeElement classObject() {
            return mType.classObject();
        }
    }

    private String parseSetterName(ColumnInfo info) {
        final ExecutableElement getter = info.getGetterElement();
        final ExecutableElement setter = info.getSetterElement();

        if (setter != null) {
            return setter.getSimpleName().toString();
        }

        final String getterName = getter.getSimpleName().toString();
        if (getterName.startsWith("is")) {
            return "set" + getterName.substring(2, getterName.length());
        }

        if (getterName.startsWith("get")) {
            return "set" + getterName.substring(3, getterName.length());
        }

        throw new IllegalStateException("Failed to parse identifier from " + getterName);
    }
}
