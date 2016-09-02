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

    public static DefinedType REPOSITORY = Types.of("com.github.wrdlbrnft.simpleorm", "Repository");
    public static DefinedType BASE_REPOSITORY = Types.of("com.github.wrdlbrnft.simpleorm.repository", "BaseRepository");
    public static DefinedType SELECTION = Types.of("com.github.wrdlbrnft.simpleorm.selection", "Selection");
    public static DefinedType SELECTION_BUILDER = Types.of("com.github.wrdlbrnft.simpleorm.selection", "Selection.Builder");

    public static DefinedType SQLITE_PROVIDER = Types.of("com.github.wrdlbrnft.simpleorm.database", "SQLiteProvider");
    public static DefinedType ENCRYPTED_SQLITE_PROVIDER = Types.of("com.github.wrdlbrnft.simpleorm.database", "EncryptedSQLiteProvider");
    public static DefinedType BASE_ENCRYPTED_SQLITE_PROVIDER = Types.of("com.github.wrdlbrnft.simpleorm.database.encrypted", "BaseEncryptedSQLiteProvider");
    public static DefinedType BASE_PLAIN_SQLITE_PROVIDER = Types.of("com.github.wrdlbrnft.simpleorm.database.plain", "BasePlainSQLiteProvider");

    public static DefinedType SQLITE_DATABASE_MANAGER = Types.of("com.github.wrdlbrnft.simpleorm.database", "SQLiteDatabaseManager");

    public static DefinedType WRITABLE_SQLITE_WRAPPER = Types.of("com.github.wrdlbrnft.simpleorm.database", "WritableSQLiteWrapper");
    public static DefinedType READABLE_SQLITE_WRAPPER = Types.of("com.github.wrdlbrnft.simpleorm.database", "ReadableSQLiteWrapper");
    public static DefinedType CURSOR_WRAPPER = Types.of("com.github.wrdlbrnft.simpleorm.database", "CursorWrapper");
    public static DefinedType SAVE_PARAMETERS = Types.of("com.github.wrdlbrnft.simpleorm.entities", "SaveParameters");
    public static DefinedType REMOVE_PARAMETERS = Types.of("com.github.wrdlbrnft.simpleorm.entities", "RemoveParameters");
    public static DefinedType QUERY_PARAMETERS = Types.of("com.github.wrdlbrnft.simpleorm.entities", "QueryParameters");

    public static DefinedType BASE_ENTITY_MANAGER = Types.of("com.github.wrdlbrnft.simpleorm.entities", "BaseEntityManager");
    public static DefinedType BASE_ENTITY_ITERATOR = Types.of("com.github.wrdlbrnft.simpleorm.entities", "BaseEntityIterator");
    public static DefinedType ENTITY_ITERATOR = Types.of("com.github.wrdlbrnft.simpleorm.entities", "EntityIterator");
    public static DefinedType ENTITY_SAVER = Types.of("com.github.wrdlbrnft.simpleorm.entities", "EntitySaver");
    public static DefinedType ENTITY_REMOVER = Types.of("com.github.wrdlbrnft.simpleorm.entities", "EntityRemover");
    public static DefinedType CONTENT_VALUES = Types.of("android.content", "ContentValues");

    public static DefinedType VALUE_CONVERTER = Types.of("com.github.wrdlbrnft.simpleorm.adapter", "ValueConverter");
    public static DefinedType DATE_TYPE_ADAPTER = Types.of("com.github.wrdlbrnft.simpleorm.adapter.base", "DateTypeAdapter");
    public static DefinedType CALENDAR_TYPE_ADAPTER = Types.of("com.github.wrdlbrnft.simpleorm.adapter.base", "CalendarTypeAdapter");

    public static DefinedType BOOLEAN_FIELD = Types.of("com.github.wrdlbrnft.simpleorm.fields", "BooleanField");
    public static DefinedType BOOLEAN_FIELD_IMPL = Types.of("com.github.wrdlbrnft.simpleorm.fields.impl", "BooleanFieldImpl");

    public static DefinedType DATE_FIELD = Types.of("com.github.wrdlbrnft.simpleorm.fields", "DateField");
    public static DefinedType DATE_FIELD_IMPL = Types.of("com.github.wrdlbrnft.simpleorm.fields.impl", "DateFieldImpl");

    public static DefinedType DOUBLE_FIELD = Types.of("com.github.wrdlbrnft.simpleorm.fields", "DoubleField");
    public static DefinedType DOUBLE_FIELD_IMPL = Types.of("com.github.wrdlbrnft.simpleorm.fields.impl", "DoubleFieldImpl");

    public static DefinedType ENTITY_FIELD = Types.of("com.github.wrdlbrnft.simpleorm.fields", "EntityField");
    public static DefinedType ENTITY_FIELD_IMPL = Types.of("com.github.wrdlbrnft.simpleorm.fields.impl", "EntityFieldImpl");

    public static DefinedType FLOAT_FIELD = Types.of("com.github.wrdlbrnft.simpleorm.fields", "FloatField");
    public static DefinedType FLOAT_FIELD_IMPL = Types.of("com.github.wrdlbrnft.simpleorm.fields.impl", "FloatFieldImpl");

    public static DefinedType INT_FIELD = Types.of("com.github.wrdlbrnft.simpleorm.fields", "IntField");
    public static DefinedType INT_FIELD_IMPL = Types.of("com.github.wrdlbrnft.simpleorm.fields.impl", "IntFieldImpl");

    public static DefinedType LONG_FIELD = Types.of("com.github.wrdlbrnft.simpleorm.fields", "LongField");
    public static DefinedType LONG_FIELD_IMPL = Types.of("com.github.wrdlbrnft.simpleorm.fields.impl", "LongFieldImpl");

    public static DefinedType STRING_FIELD = Types.of("com.github.wrdlbrnft.simpleorm.fields", "StringField");
    public static DefinedType STRING_FIELD_IMPL = Types.of("com.github.wrdlbrnft.simpleorm.fields.impl", "StringFieldImpl");
}
