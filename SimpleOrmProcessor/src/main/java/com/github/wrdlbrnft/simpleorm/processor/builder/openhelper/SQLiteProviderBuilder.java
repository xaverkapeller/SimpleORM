package com.github.wrdlbrnft.simpleorm.processor.builder.openhelper;

import com.github.wrdlbrnft.codebuilder.code.Block;
import com.github.wrdlbrnft.codebuilder.elements.values.Values;
import com.github.wrdlbrnft.codebuilder.executables.Constructor;
import com.github.wrdlbrnft.codebuilder.executables.ExecutableBuilder;
import com.github.wrdlbrnft.codebuilder.executables.Method;
import com.github.wrdlbrnft.codebuilder.executables.Methods;
import com.github.wrdlbrnft.codebuilder.implementations.Implementation;
import com.github.wrdlbrnft.codebuilder.types.Type;
import com.github.wrdlbrnft.codebuilder.types.Types;
import com.github.wrdlbrnft.codebuilder.util.MapBuilder;
import com.github.wrdlbrnft.codebuilder.variables.Variable;
import com.github.wrdlbrnft.codebuilder.variables.Variables;
import com.github.wrdlbrnft.simpleorm.processor.SimpleOrmTypes;
import com.github.wrdlbrnft.simpleorm.processor.analyzer.databases.DatabaseInfo;
import com.github.wrdlbrnft.simpleorm.processor.analyzer.entity.ColumnInfo;
import com.github.wrdlbrnft.simpleorm.processor.analyzer.entity.ColumnType;
import com.github.wrdlbrnft.simpleorm.processor.analyzer.entity.Constraint;
import com.github.wrdlbrnft.simpleorm.processor.analyzer.entity.EntityInfo;
import com.github.wrdlbrnft.simpleorm.processor.utils.MappingTables;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Modifier;

/**
 * Created with Android Studio
 * User: Xaver
 * Date: 08/07/16
 */

public class SQLiteProviderBuilder {

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

    private final ProcessingEnvironment mProcessingEnvironment;

    public SQLiteProviderBuilder(ProcessingEnvironment processingEnvironment) {
        mProcessingEnvironment = processingEnvironment;
    }

    public Implementation build(final DatabaseInfo databaseInfo) {
        final Implementation.Builder builder = new Implementation.Builder();
        final Type providerType = databaseInfo.isEncrypted()
                ? SimpleOrmTypes.BASE_ENCRYPTED_SQLITE_PROVIDER.asType()
                : SimpleOrmTypes.BASE_PLAIN_SQLITE_PROVIDER.asType();
        builder.setExtendedType(providerType);
        builder.setModifiers(EnumSet.of(Modifier.PRIVATE, Modifier.STATIC, Modifier.FINAL));

        builder.addConstructor(new Constructor.Builder()
                .setModifiers(EnumSet.of(Modifier.PRIVATE))
                .setCode(new ExecutableBuilder() {

                    private Variable mParamContext;
                    private Variable mParamPassword;

                    @Override
                    protected List<Variable> createParameters() {
                        final List<Variable> parameters = new ArrayList<>();
                        parameters.add(mParamContext = Variables.of(Types.Android.CONTEXT));

                        if (databaseInfo.isEncrypted()) {
                            parameters.add(mParamPassword = Variables.of(Types.of(char[].class)));
                        }

                        return parameters;
                    }

                    @Override
                    protected void write(Block block) {
                        if (databaseInfo.isEncrypted()) {
                            block.append(Methods.SUPER.call(
                                    mParamContext,
                                    Values.of(databaseInfo.getDatabaseName()),
                                    Values.of(databaseInfo.getDatabaseVersion()),
                                    mParamPassword
                            )).append(";");
                        } else {
                            block.append(Methods.SUPER.call(
                                    mParamContext,
                                    Values.of(databaseInfo.getDatabaseName()),
                                    Values.of(databaseInfo.getDatabaseVersion())
                            )).append(";");
                        }
                    }
                })
                .build());

        builder.addMethod(new Method.Builder()
                .setName("onCreate")
                .setModifiers(EnumSet.of(Modifier.PROTECTED))
                .setCode(new ExecutableBuilder() {

                    private Variable mParamManager;

                    @Override
                    protected List<Variable> createParameters() {
                        final List<Variable> parameters = new ArrayList<>();
                        parameters.add(mParamManager = Variables.of(SimpleOrmTypes.SQLITE_DATABASE_MANAGER.asType()));
                        return parameters;
                    }

                    @Override
                    protected void write(Block block) {
                        final Set<EntityInfo> entityInfos = databaseInfo.getEntityInfos();
                        boolean appendNewLine = false;
                        for (EntityInfo entityInfo : entityInfos) {
                            final List<String> statements = getCreateTableStatement(entityInfo);
                            for (String statement : statements) {
                                if (appendNewLine) {
                                    block.newLine();
                                } else {
                                    appendNewLine = true;
                                }
                                block.append(METHOD_EXEC_SQL.callOnTarget(mParamManager, Values.of(statement))).append(";");
                            }
                        }
                    }
                })
                .build());

        builder.addMethod(new Method.Builder()
                .setName("onUpgrade")
                .setModifiers(EnumSet.of(Modifier.PROTECTED))
                .setCode(new ExecutableBuilder() {

                    private Variable mParamManager;
                    private Variable mOldVersion;
                    private Variable mNewVersion;

                    @Override
                    protected List<Variable> createParameters() {
                        final List<Variable> parameters = new ArrayList<>();
                        parameters.add(mParamManager = Variables.of(SimpleOrmTypes.SQLITE_DATABASE_MANAGER.asType()));
                        parameters.add(mOldVersion = Variables.of(Types.Primitives.INTEGER));
                        parameters.add(mNewVersion = Variables.of(Types.Primitives.INTEGER));
                        return parameters;
                    }

                    @Override
                    protected void write(Block block) {

                    }
                })
                .build());

        return builder.build();
    }

    private List<String> getCreateTableStatement(EntityInfo entityInfo) {
        final List<String> createQueries = new ArrayList<>();
        final StringBuilder builder = new StringBuilder();

        builder.append("CREATE TABLE ").append(entityInfo.getTableName()).append(" (");

        boolean appendSeparator = false;
        for (ColumnInfo column : entityInfo.getColumns()) {
            if (column.getColumnType() == ColumnType.ENTITY) {
                createQueries.add(getCreateMappingTableStatement(entityInfo, column));
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
        createQueries.add(builder.toString());
        return createQueries;
    }

    private String getCreateMappingTableStatement(EntityInfo entity, ColumnInfo column) {
        return "CREATE TABLE " + MappingTables.getTableName(entity, column) + " (" +
                "_id INTEGER PRIMARY KEY, " +
                "ParentId INTEGER, " +
                "ChildId INTEGER" +
                ");";
    }

    private void appendColumn(StringBuilder builder, ColumnInfo column) {
        builder.append(column.getColumnName()).append(" ").append(SQL_TYPE_MAP.get(column.getColumnType()));

        for (Constraint constraint : column.getConstraints()) {
            builder.append(" ").append(constraint.getSqlKeyword());
        }
    }
}
