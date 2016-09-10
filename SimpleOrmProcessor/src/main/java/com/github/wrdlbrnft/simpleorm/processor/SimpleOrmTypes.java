package com.github.wrdlbrnft.simpleorm.processor;

import com.github.wrdlbrnft.codebuilder.types.DefinedType;
import com.github.wrdlbrnft.codebuilder.types.Type;
import com.github.wrdlbrnft.codebuilder.types.Types;

/**
 * Created with Android Studio
 * User: Xaver
 * Date: 09/07/16
 */
public class SimpleOrmTypes {

    public static final DefinedType REPOSITORY = Types.of("com.github.wrdlbrnft.simpleorm", "Repository");
    public static final DefinedType BASE_REPOSITORY = Types.of("com.github.wrdlbrnft.simpleorm.repository", "BaseRepository");
    public static final DefinedType SELECTION = Types.of("com.github.wrdlbrnft.simpleorm.selection", "Selection");
    public static final DefinedType SELECTION_BUILDER = Types.of("com.github.wrdlbrnft.simpleorm.selection", "Selection.Builder");

    public static final DefinedType STRING_BUILDER = Types.of(StringBuilder.class);
    public static final DefinedType COLLECTIONS = Types.of("java.util", "Collections");
    public static final DefinedType LONG_SPARSE_ARRAY_COMPAT = Types.of("com.github.wrdlbrnft.simpleorm.utils", "LongSparseArrayCompat");

    public static final DefinedType EXECUTOR = Types.of("java.util.concurrent", "Executor");
    public static final DefinedType EXECUTORS = Types.of("java.util.concurrent", "Executors");

    public static final DefinedType SQLITE_PROVIDER = Types.of("com.github.wrdlbrnft.simpleorm.database", "SQLiteProvider");
    public static final DefinedType ENCRYPTED_SQLITE_PROVIDER = Types.of("com.github.wrdlbrnft.simpleorm.database", "EncryptedSQLiteProvider");
    public static final DefinedType BASE_ENCRYPTED_SQLITE_PROVIDER = Types.of("com.github.wrdlbrnft.simpleorm.database.encrypted", "BaseEncryptedSQLiteProvider");
    public static final DefinedType BASE_PLAIN_SQLITE_PROVIDER = Types.of("com.github.wrdlbrnft.simpleorm.database.plain", "BasePlainSQLiteProvider");

    public static final DefinedType SQLITE_DATABASE_MANAGER = Types.of("com.github.wrdlbrnft.simpleorm.database", "SQLiteDatabaseManager");

    public static final DefinedType WRITABLE_SQLITE_WRAPPER = Types.of("com.github.wrdlbrnft.simpleorm.database", "WritableSQLiteWrapper");
    public static final DefinedType READABLE_SQLITE_WRAPPER = Types.of("com.github.wrdlbrnft.simpleorm.database", "ReadableSQLiteWrapper");
    public static final DefinedType CURSOR_WRAPPER = Types.of("com.github.wrdlbrnft.simpleorm.database", "CursorWrapper");
    public static final DefinedType SAVE_PARAMETERS = Types.of("com.github.wrdlbrnft.simpleorm.entities", "SaveParameters");
    public static final DefinedType REMOVE_PARAMETERS = Types.of("com.github.wrdlbrnft.simpleorm.entities", "RemoveParameters");
    public static final DefinedType QUERY_PARAMETERS = Types.of("com.github.wrdlbrnft.simpleorm.entities", "QueryParameters");

    public static final DefinedType ABSTRACT_LIST = Types.of("java.util", "AbstractList");

    public static final DefinedType BASE_ENTITY_MANAGER = Types.of("com.github.wrdlbrnft.simpleorm.entities", "BaseEntityManager");
    public static final DefinedType ENTITY_ITERATOR = Types.of("com.github.wrdlbrnft.simpleorm.entities", "EntityIterator");
    public static final DefinedType CONTENT_VALUES = Types.of("android.content", "ContentValues");

    public static final DefinedType VALUE_CONVERTER = Types.of("com.github.wrdlbrnft.simpleorm.adapter", "ValueConverter");
    public static final DefinedType DATE_TYPE_ADAPTER = Types.of("com.github.wrdlbrnft.simpleorm.adapter.base", "DateTypeAdapter");
    public static final DefinedType CALENDAR_TYPE_ADAPTER = Types.of("com.github.wrdlbrnft.simpleorm.adapter.base", "CalendarTypeAdapter");

    public static final DefinedType BOOLEAN_FIELD = Types.of("com.github.wrdlbrnft.simpleorm.fields", "BooleanField");
    public static final DefinedType BOOLEAN_FIELD_IMPL = Types.of("com.github.wrdlbrnft.simpleorm.fields.impl", "BooleanFieldImpl");

    public static final DefinedType DATE_FIELD = Types.of("com.github.wrdlbrnft.simpleorm.fields", "DateField");
    public static final DefinedType DATE_FIELD_IMPL = Types.of("com.github.wrdlbrnft.simpleorm.fields.impl", "DateFieldImpl");

    public static final DefinedType DOUBLE_FIELD = Types.of("com.github.wrdlbrnft.simpleorm.fields", "DoubleField");
    public static final DefinedType DOUBLE_FIELD_IMPL = Types.of("com.github.wrdlbrnft.simpleorm.fields.impl", "DoubleFieldImpl");

    public static final DefinedType ENTITY_FIELD = Types.of("com.github.wrdlbrnft.simpleorm.fields", "EntityField");
    public static final DefinedType ENTITY_FIELD_IMPL = Types.of("com.github.wrdlbrnft.simpleorm.fields.impl", "EntityFieldImpl");

    public static final DefinedType FLOAT_FIELD = Types.of("com.github.wrdlbrnft.simpleorm.fields", "FloatField");
    public static final DefinedType FLOAT_FIELD_IMPL = Types.of("com.github.wrdlbrnft.simpleorm.fields.impl", "FloatFieldImpl");

    public static final DefinedType INT_FIELD = Types.of("com.github.wrdlbrnft.simpleorm.fields", "IntField");
    public static final DefinedType INT_FIELD_IMPL = Types.of("com.github.wrdlbrnft.simpleorm.fields.impl", "IntFieldImpl");

    public static final DefinedType LONG_FIELD = Types.of("com.github.wrdlbrnft.simpleorm.fields", "LongField");
    public static final DefinedType LONG_FIELD_IMPL = Types.of("com.github.wrdlbrnft.simpleorm.fields.impl", "LongFieldImpl");

    public static final DefinedType STRING_FIELD = Types.of("com.github.wrdlbrnft.simpleorm.fields", "StringField");
    public static final DefinedType STRING_FIELD_IMPL = Types.of("com.github.wrdlbrnft.simpleorm.fields.impl", "StringFieldImpl");

    public static final DefinedType THROWABLE = Types.of(Throwable.class);
}
