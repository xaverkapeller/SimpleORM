package com.github.wrdlbrnft.simpleorm.processor.utils;

import com.github.wrdlbrnft.codebuilder.code.Block;
import com.github.wrdlbrnft.codebuilder.code.CodeElement;
import com.github.wrdlbrnft.codebuilder.elements.values.Values;
import com.github.wrdlbrnft.codebuilder.executables.Method;
import com.github.wrdlbrnft.codebuilder.executables.Methods;
import com.github.wrdlbrnft.codebuilder.types.Types;
import com.github.wrdlbrnft.codebuilder.variables.Variable;
import com.github.wrdlbrnft.codebuilder.variables.Variables;
import com.github.wrdlbrnft.simpleorm.processor.SimpleOrmTypes;
import com.github.wrdlbrnft.simpleorm.processor.analyzer.entity.ColumnInfo;
import com.github.wrdlbrnft.simpleorm.processor.analyzer.entity.EntityInfo;

import javax.lang.model.element.Modifier;

/**
 * Created with Android Studio
 * User: Xaver
 * Date: 18/07/16
 */

public class MappingTables {

    public static final String COLUMN_PARENT_ID = "ParentId";
    public static final String COLUMN_CHILD_ID = "ChildId";
    public static final String COLUMN_NONCE = "Nonce";

    private static final Method METHOD_PUT = Methods.stub("put");
    private static final Method METHOD_VALUE_OF = Methods.stub("valueOf");

    public static String getTableName(EntityInfo entity, ColumnInfo column) {
        return "_" + entity.getTableName() + "_" + column.getColumnName() + "_Mapping";
    }

    public static String createMappingTableStatement(EntityInfo entity, ColumnInfo column) {
        return "CREATE TABLE " + MappingTables.getTableName(entity, column) + " (" +
                COLUMN_PARENT_ID + " INTEGER, " +
                COLUMN_CHILD_ID + " INTEGER," +
                COLUMN_NONCE + " TEXT UNIQUE" +
                ");";
    }

    public static Variable appendContentValuesForMapping(Block block, Variable parentId, Variable childId) {
        final Variable values = Variables.of(SimpleOrmTypes.CONTENT_VALUES, Modifier.FINAL);
        block.set(values, SimpleOrmTypes.CONTENT_VALUES.newInstance()).append(";").newLine();
        block.append(METHOD_PUT.callOnTarget(values, Values.of(MappingTables.COLUMN_PARENT_ID), parentId)).append(";").newLine();
        block.append(METHOD_PUT.callOnTarget(values, Values.of(MappingTables.COLUMN_CHILD_ID), childId)).append(";").newLine();
        block.append(METHOD_PUT.callOnTarget(values, Values.of(MappingTables.COLUMN_NONCE), createNonce(parentId, childId))).append(";").newLine();
        return values;
    }

    private static CodeElement createNonce(Variable parentId, Variable childId) {
        final Block block = new Block();
        block.append(METHOD_VALUE_OF.callOnTarget(Types.STRING, parentId));
        block.append(" + ").append(Values.of("|")).append(" + ");
        block.append(METHOD_VALUE_OF.callOnTarget(Types.STRING, childId));
        return block;
    }
}
