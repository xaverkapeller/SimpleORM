package com.github.wrdlbrnft.simpleorm.processor.builder.entity;

import com.github.wrdlbrnft.codebuilder.code.Block;
import com.github.wrdlbrnft.codebuilder.code.BlockWriter;
import com.github.wrdlbrnft.codebuilder.code.CodeElement;
import com.github.wrdlbrnft.codebuilder.elements.ifs.TernaryIf;
import com.github.wrdlbrnft.codebuilder.elements.values.Values;
import com.github.wrdlbrnft.codebuilder.executables.ExecutableBuilder;
import com.github.wrdlbrnft.codebuilder.executables.Method;
import com.github.wrdlbrnft.codebuilder.executables.Methods;
import com.github.wrdlbrnft.codebuilder.types.Types;
import com.github.wrdlbrnft.codebuilder.util.Operators;
import com.github.wrdlbrnft.codebuilder.util.Utils;
import com.github.wrdlbrnft.codebuilder.variables.Field;
import com.github.wrdlbrnft.codebuilder.variables.Variable;
import com.github.wrdlbrnft.codebuilder.variables.Variables;

import java.util.ArrayList;
import java.util.List;

import javax.lang.model.type.TypeMirror;

/**
 * Created with Android Studio
 * User: Xaver
 * Date: 07/09/16
 */
class HashCodeExecutableBuilder extends ExecutableBuilder {

    private static final Method METHOD_DOUBLE_TO_LONG_BITS = Methods.stub("doubleToLongBits");
    private static final Method METHOD_FLOAT_TO_INT_BITS = Methods.stub("floatToIntBits");

    private final List<FieldInfo> mFieldInfos;

    HashCodeExecutableBuilder(List<FieldInfo> fieldInfos) {
        mFieldInfos = fieldInfos;
    }

    @Override
    protected List<Variable> createParameters() {
        return new ArrayList<>();
    }

    @Override
    protected void write(Block block) {
        final Variable varResult = Variables.of(Types.Primitives.INTEGER);

        for (int i = 0, count = mFieldInfos.size(); i < count; i++) {
            final FieldInfo fieldInfo = mFieldInfos.get(i);

            if (i > 0) {
                block.set(varResult, Operators.operate(
                        Operators.operate(Values.of(31), "*", varResult),
                        "+",
                        createHashCodeStatement(fieldInfo)
                ));
            } else {
                block.set(varResult, createHashCodeStatement(fieldInfo));
            }
            block.append(";").newLine();
        }

        block.append("return ").append(varResult).append(";");
    }

    private CodeElement createHashCodeStatement(FieldInfo fieldInfo) {
        final Field field = fieldInfo.getField();
        final TypeMirror type = fieldInfo.getBaseType();

        if (Utils.isSameType(type, int.class)) {
            return field;
        }

        if (Utils.isSameType(type, long.class)) {
            return new LongToIntegerHashConversion(field);
        }

        if (Utils.isSameType(type, double.class)) {
            return new BlockWriter() {
                @Override
                protected void write(Block block) {
                    block.append(new LongToIntegerHashConversion(METHOD_DOUBLE_TO_LONG_BITS.callOnTarget(Types.Boxed.DOUBLE, field)));
                }
            };
        }

        if (Utils.isSameType(type, float.class)) {
            return new BlockWriter() {
                @Override
                protected void write(Block block) {
                    block.append(METHOD_DOUBLE_TO_LONG_BITS.callOnTarget(Types.Boxed.DOUBLE, field));
                }
            };
        }

        if (Utils.isSameType(type, boolean.class)) {
            return new BracedStatement(new TernaryIf.Builder()
                    .setComparison(field)
                    .setTrueBlock(Values.of(1))
                    .setFalseBlock(Values.of(0))
                    .build());
        }

        return new BracedStatement(new TernaryIf.Builder()
                .setComparison(Operators.operate(field, "!=", Values.ofNull()))
                .setTrueBlock(Methods.HASH_CODE.callOnTarget(field))
                .setFalseBlock(Values.of(0))
                .build());
    }

    private static class LongToIntegerHashConversion extends BlockWriter {

        private final CodeElement mLong;

        private LongToIntegerHashConversion(CodeElement aLong) {
            mLong = aLong;
        }

        @Override
        protected void write(Block block) {
            block.append(Types.asCast(Types.Primitives.INTEGER)).append(" (")
                    .append(Operators.operate(mLong, "^", new BracedStatement(Operators.operate(mLong, ">>>", Values.of(32)))))
                    .append(")");
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