package com.github.wrdlbrnft.simpleorm.processor.builder.entity;

import com.github.wrdlbrnft.codebuilder.code.Block;
import com.github.wrdlbrnft.codebuilder.code.BlockWriter;
import com.github.wrdlbrnft.codebuilder.code.CodeElement;
import com.github.wrdlbrnft.codebuilder.elements.ifs.If;
import com.github.wrdlbrnft.codebuilder.elements.ifs.TernaryIf;
import com.github.wrdlbrnft.codebuilder.elements.values.Value;
import com.github.wrdlbrnft.codebuilder.elements.values.Values;
import com.github.wrdlbrnft.codebuilder.executables.ExecutableBuilder;
import com.github.wrdlbrnft.codebuilder.executables.Method;
import com.github.wrdlbrnft.codebuilder.executables.Methods;
import com.github.wrdlbrnft.codebuilder.types.Type;
import com.github.wrdlbrnft.codebuilder.types.Types;
import com.github.wrdlbrnft.codebuilder.util.Operators;
import com.github.wrdlbrnft.codebuilder.util.Utils;
import com.github.wrdlbrnft.codebuilder.variables.Field;
import com.github.wrdlbrnft.codebuilder.variables.Variable;
import com.github.wrdlbrnft.codebuilder.variables.Variables;

import java.util.ArrayList;
import java.util.List;

import javax.lang.model.element.Modifier;

/**
 * Created with Android Studio
 * User: Xaver
 * Date: 07/09/16
 */
class EqualsExecutableBuilder extends ExecutableBuilder {

    private static final Method METHOD_COMPARE = Methods.stub("compare");

    private final List<FieldInfo> mFieldInfos;
    private final Type mEntityType;

    private Variable paramObject;

    EqualsExecutableBuilder(List<FieldInfo> fieldInfos, Type entityType) {
        mFieldInfos = fieldInfos;
        mEntityType = entityType;
    }

    @Override
    protected List<Variable> createParameters() {
        final List<Variable> parameters = new ArrayList<>();
        parameters.add(paramObject = Variables.of(Types.OBJECT));
        return parameters;
    }

    @Override
    protected void write(Block block) {
        block.append(new If.Builder()
                .add(Operators.operate(Values.ofThis(), "==", paramObject), new ReturnStatement(Values.of(true)))
                .add(Operators.operate(
                        Operators.operate(paramObject, "==", Values.ofNull()),
                        "||",
                        Values.invert(new BracedStatement(Operators.operate(paramObject, "instanceof", mEntityType)))
                ), new ReturnStatement(Values.of(false)))
                .build()
        ).newLine();

        final Variable otherInstance = Variables.of(mEntityType, Modifier.FINAL);
        block.set(otherInstance, new BlockWriter() {
            @Override
            protected void write(Block block) {
                block.append(Types.asCast(mEntityType)).append(" ").append(paramObject);
            }
        }).append(";").newLine();

        for (FieldInfo fieldInfo : mFieldInfos) {
            final Field field = fieldInfo.getField();
            final Variable otherInstanceVariable = Variables.of(Types.of(fieldInfo.getBaseType()), Modifier.FINAL);
            block.set(otherInstanceVariable, fieldInfo.getGetter().callOnTarget(otherInstance)).append(";").newLine();

            if (Utils.isSameType(fieldInfo.getBaseType(), int.class)
                    || Utils.isSameType(fieldInfo.getBaseType(), long.class)
                    || Utils.isSameType(fieldInfo.getBaseType(), boolean.class)) {
                final CodeElement primitiveEqualsComparison = createPrimitiveEqualsComparison(otherInstanceVariable, field);
                block.append(primitiveEqualsComparison).newLine();
            } else if (Utils.isSameType(fieldInfo.getBaseType(), double.class)) {
                final CodeElement doubleEqualsComparison = createDoubleEqualsComparison(otherInstanceVariable, field);
                block.append(doubleEqualsComparison).newLine();
            } else if (Utils.isSameType(fieldInfo.getBaseType(), float.class)) {
                final CodeElement doubleEqualsComparison = createFloatEqualsComparison(otherInstanceVariable, field);
                block.append(doubleEqualsComparison).newLine();
            } else {
                final CodeElement objectEqualsComparison = createObjectEqualsComparison(otherInstanceVariable, field);
                block.append(objectEqualsComparison).newLine();
            }
        }
        block.append(new ReturnStatement(Values.of(true)));
    }

    private CodeElement createPrimitiveEqualsComparison(CodeElement otherInstanceField, Field field) {
        final If.Builder builder = new If.Builder();
        builder.add(Operators.operate(field, "!=", otherInstanceField), new ReturnStatement(Values.of(false)));
        return builder.build();
    }

    private CodeElement createDoubleEqualsComparison(CodeElement otherInstanceField, Field field) {
        final If.Builder builder = new If.Builder();
        final CodeElement doubleCompareCall = METHOD_COMPARE.callOnTarget(Types.Boxed.DOUBLE, otherInstanceField, field);
        builder.add(Operators.operate(doubleCompareCall, "!=", Values.of(0)), new ReturnStatement(Values.of(false)));
        return builder.build();
    }

    private CodeElement createFloatEqualsComparison(CodeElement otherInstanceField, Field field) {
        final If.Builder builder = new If.Builder();
        final CodeElement floatCompareCall = METHOD_COMPARE.callOnTarget(Types.Boxed.FLOAT, otherInstanceField, field);
        builder.add(Operators.operate(floatCompareCall, "!=", Values.of(0)), new ReturnStatement(Values.of(false)));
        return builder.build();
    }

    private CodeElement createObjectEqualsComparison(CodeElement otherInstanceField, Field field) {
        final If.Builder builder = new If.Builder();
        builder.add(new TernaryIf.Builder()
                        .setComparison(Operators.operate(field, "!=", Values.ofNull()))
                        .setTrueBlock(Values.invert(Methods.EQUALS.callOnTarget(field, otherInstanceField)))
                        .setFalseBlock(Operators.operate(otherInstanceField, "!=", Values.ofNull()))
                        .build(),
                new ReturnStatement(Values.of(false))
        );
        return builder.build();
    }

    private static class ReturnStatement extends BlockWriter {

        private final Value mReturnValue;

        private ReturnStatement(Value returnValue) {
            mReturnValue = returnValue;
        }

        @Override
        protected void write(Block block) {
            block.append("return ").append(mReturnValue).append(";");
        }
    }

    private static class BracedStatement extends BlockWriter {

        private final CodeElement mStatement;

        private BracedStatement(CodeElement statement) {
            mStatement = statement;
        }

        @Override
        protected void write(Block block) {
            block.append("(").append(mStatement).append(")");
        }
    }
}