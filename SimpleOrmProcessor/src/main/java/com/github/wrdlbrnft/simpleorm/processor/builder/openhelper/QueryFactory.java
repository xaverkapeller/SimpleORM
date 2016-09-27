package com.github.wrdlbrnft.simpleorm.processor.builder.openhelper;

import com.github.wrdlbrnft.codebuilder.code.CodeElement;
import com.github.wrdlbrnft.codebuilder.elements.values.Values;
import com.github.wrdlbrnft.codebuilder.executables.Method;
import com.github.wrdlbrnft.codebuilder.executables.Methods;
import com.github.wrdlbrnft.codebuilder.util.MapBuilder;
import com.github.wrdlbrnft.codebuilder.variables.Variable;
import com.github.wrdlbrnft.simpleorm.processor.analyzer.entity.ColumnInfo;
import com.github.wrdlbrnft.simpleorm.processor.analyzer.entity.ColumnType;
import com.github.wrdlbrnft.simpleorm.processor.analyzer.entity.Constraint;
import com.github.wrdlbrnft.simpleorm.processor.analyzer.entity.EntityInfo;
import com.github.wrdlbrnft.simpleorm.processor.analyzer.entity.VersionInfo;
import com.github.wrdlbrnft.simpleorm.processor.utils.MappingTables;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created with Android Studio
 * User: Xaver
 * Date: 10/09/16
 */

class QueryFactory {

    private static final Method METHOD_EXEC_SQL = Methods.stub("execSql");

    private static final Map<ColumnType, String> SQL_TYPE_MAP = new MapBuilder<ColumnType, String>()
            .put(ColumnType.PRIMITIVE_BOOLEAN, "INTEGER")
            .put(ColumnType.BOOLEAN, "INTEGER")
            .put(ColumnType.PRIMITIVE_DOUBLE, "REAL")
            .put(ColumnType.DOUBLE, "REAL")
            .put(ColumnType.PRIMITIVE_FLOAT, "REAL")
            .put(ColumnType.FLOAT, "REAL")
            .put(ColumnType.PRIMITIVE_INT, "INTEGER")
            .put(ColumnType.INT, "INTEGER")
            .put(ColumnType.DATE, "INTEGER")
            .put(ColumnType.STRING, "TEXT")
            .put(ColumnType.ENTITY, "INTEGER")
            .put(ColumnType.PRIMITIVE_LONG, "INTEGER")
            .put(ColumnType.LONG, "INTEGER")
            .build();

    public static CreateQueries createQueriesFor(EntityInfo entityInfo) {
        final List<Query> tableQueries = new ArrayList<>();
        final List<Query> triggerQueries = new ArrayList<>();
        final StringBuilder builder = new StringBuilder();

        builder.append("CREATE TABLE ").append(entityInfo.getTableName()).append(" (");

        boolean appendSeparator = false;
        for (ColumnInfo column : entityInfo.getColumns()) {
            if (column.getColumnType() == ColumnType.ENTITY) {
                final VersionInfo columnVersionInfo = column.getVersionInfo();
                tableQueries.add(new QueryImpl(MappingTables.createMappingTableStatement(entityInfo, column), columnVersionInfo));
                triggerQueries.add(new QueryImpl(MappingTables.createMappingTableTriggerStatement(entityInfo, column), columnVersionInfo));
                continue;
            }

            if (appendSeparator) {
                builder.append(", ");
            } else {
                appendSeparator = true;
            }
            appendColumn(builder, column);
        }

        builder.append(");");
        tableQueries.add(new QueryImpl(builder.toString(), entityInfo.getVersionInfo()));
        return new CreateQueriesImpl(tableQueries, triggerQueries);
    }

    private static void appendColumn(StringBuilder builder, ColumnInfo column) {
        builder.append(column.getColumnName()).append(" ").append(SQL_TYPE_MAP.get(column.getColumnType()));

        for (Constraint constraint : column.getConstraints()) {
            builder.append(" ").append(constraint.getSqlKeyword());
        }
    }

    private static class QueryImpl implements Query {

        private final String mQueryString;
        private final VersionInfo mVersionInfo;

        private QueryImpl(String queryString, VersionInfo versionInfo) {
            mQueryString = queryString;
            mVersionInfo = versionInfo;
        }

        @Override
        public CodeElement execute(Variable manager) {
            return METHOD_EXEC_SQL.callOnTarget(manager, Values.of(mQueryString));
        }

        @Override
        public VersionInfo getVersionInfo() {
            return mVersionInfo;
        }
    }

    private static class CreateQueriesImpl implements CreateQueries {

        private final List<Query> mTableQueries;
        private final List<Query> mTriggerQueries;

        private CreateQueriesImpl(List<Query> tableQueries, List<Query> triggerQueries) {
            mTableQueries = tableQueries;
            mTriggerQueries = triggerQueries;
        }

        @Override
        public List<Query> getTableQueries() {
            return mTableQueries;
        }

        @Override
        public List<Query> getTriggerQueries() {
            return mTriggerQueries;
        }
    }
}
