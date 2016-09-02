package com.github.wrdlbrnft.simpleorm.processor.builder.entitymanager;

import com.github.wrdlbrnft.codebuilder.arrays.Arrays;
import com.github.wrdlbrnft.codebuilder.code.Block;
import com.github.wrdlbrnft.codebuilder.code.CodeElement;
import com.github.wrdlbrnft.codebuilder.elements.values.Values;
import com.github.wrdlbrnft.codebuilder.executables.ExecutableBuilder;
import com.github.wrdlbrnft.codebuilder.types.Type;
import com.github.wrdlbrnft.codebuilder.types.Types;
import com.github.wrdlbrnft.codebuilder.variables.Variable;
import com.github.wrdlbrnft.codebuilder.variables.Variables;
import com.github.wrdlbrnft.simpleorm.processor.SimpleOrmTypes;
import com.github.wrdlbrnft.simpleorm.processor.analyzer.entity.ColumnInfo;
import com.github.wrdlbrnft.simpleorm.processor.analyzer.entity.ColumnType;
import com.github.wrdlbrnft.simpleorm.processor.analyzer.entity.EntityInfo;
import com.github.wrdlbrnft.simpleorm.processor.builder.entitymanager.relationships.RelationshipInfo;

import java.util.ArrayList;
import java.util.List;

import javax.lang.model.element.Modifier;

import static com.github.wrdlbrnft.simpleorm.processor.builder.entitymanager.EntityManagerBuilder.METHOD_GET_LIMIT;
import static com.github.wrdlbrnft.simpleorm.processor.builder.entitymanager.EntityManagerBuilder.METHOD_GET_ORDER_BY;
import static com.github.wrdlbrnft.simpleorm.processor.builder.entitymanager.EntityManagerBuilder.METHOD_GET_SELECTION;
import static com.github.wrdlbrnft.simpleorm.processor.builder.entitymanager.EntityManagerBuilder.METHOD_GET_SELECTION_ARGS;
import static com.github.wrdlbrnft.simpleorm.processor.builder.entitymanager.EntityManagerBuilder.METHOD_QUERY;

/**
 * Created with Android Studio
 * User: Xaver
 * Date: 02/09/16
 */

class PerformQueryExecutableBuilder extends ExecutableBuilder {

    private final EntityInfo mEntityInfo;
    private final List<RelationshipInfo> mRelationshipInfos;
    private final EntityIteratorInfo mEntityIteratorInfo;

    private Variable mReadableSQLiteWrapper;
    private Variable mQueryParameters;

    PerformQueryExecutableBuilder(EntityInfo entityInfo, List<RelationshipInfo> relationshipInfos, EntityIteratorInfo entityIteratorInfo) {
        mEntityInfo = entityInfo;
        mRelationshipInfos = relationshipInfos;
        mEntityIteratorInfo = entityIteratorInfo;
    }

    @Override
    protected List<Variable> createParameters() {
        final List<Variable> parameters = new ArrayList<>();
        parameters.add(mReadableSQLiteWrapper = Variables.of(SimpleOrmTypes.READABLE_SQLITE_WRAPPER));
        parameters.add(mQueryParameters = Variables.of(SimpleOrmTypes.QUERY_PARAMETERS));
        return parameters;
    }

    @Override
    protected void write(Block block) {
        final Variable selection = Variables.of(SimpleOrmTypes.SELECTION, Modifier.FINAL);
        block.set(selection, METHOD_GET_SELECTION.callOnTarget(mQueryParameters)).append(";").newLine();

        final List<CodeElement> columns = new ArrayList<>();
        for (ColumnInfo columnInfo : mEntityInfo.getColumns()) {
            if (columnInfo.getColumnType() == ColumnType.ENTITY) {
                continue;
            }
            columns.add(Values.of(columnInfo.getColumnName()));
        }

        final Variable wrapper = Variables.of(SimpleOrmTypes.CURSOR_WRAPPER, Modifier.FINAL);
        block.set(wrapper, METHOD_QUERY.callOnTarget(mReadableSQLiteWrapper,
                Values.of(mEntityInfo.getTableName()),
                Arrays.of(Types.STRING, columns),
                METHOD_GET_SELECTION.callOnTarget(selection, Values.of(mEntityInfo.getTableName())),
                METHOD_GET_SELECTION_ARGS.callOnTarget(selection),
                Values.ofNull(),
                Values.ofNull(),
                METHOD_GET_ORDER_BY.callOnTarget(mQueryParameters),
                METHOD_GET_LIMIT.callOnTarget(mQueryParameters)
        )).append(";").newLine();

        final Type type = mEntityIteratorInfo.getImplementation();
        block.append("return ").append(type.newInstance(wrapper)).append(";");
    }
}
