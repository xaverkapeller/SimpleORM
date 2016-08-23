package com.github.wrdlbrnft.simpleorm.processor;

import com.github.wrdlbrnft.codebuilder.util.TypeDefinition;

/**
 * Created with Android Studio
 * User: Xaver
 * Date: 09/07/16
 */
public class SimpleOrmTypes {

    public static TypeDefinition REPOSITORY = new TypeDefinition("com.github.wrdlbrnft.simpleorm", "Repository");

    public static TypeDefinition SQLITE_PROVIDER = new TypeDefinition("com.github.wrdlbrnft.simpleorm.database", "SQLiteProvider");
    public static TypeDefinition ENCRYPTED_SQLITE_PROVIDER = new TypeDefinition("com.github.wrdlbrnft.simpleorm.database", "EncryptedSQLiteProvider");
    public static TypeDefinition BASE_ENCRYPTED_SQLITE_PROVIDER = new TypeDefinition("com.github.wrdlbrnft.simpleorm.database.encrypted", "BaseEncryptedSQLiteProvider");
    public static TypeDefinition BASE_PLAIN_SQLITE_PROVIDER = new TypeDefinition("com.github.wrdlbrnft.simpleorm.database.plain", "BasePlainSQLiteProvider");

    public static TypeDefinition SQLITE_DATABASE_MANAGER = new TypeDefinition("com.github.wrdlbrnft.simpleorm.database", "SQLiteDatabaseManager");

    public static TypeDefinition WRITABLE_SQLITE_WRAPPER = new TypeDefinition("com.github.wrdlbrnft.simpleorm.database", "WritableSQLiteWrapper");
    public static TypeDefinition SAVE_PARAMETERS = new TypeDefinition("com.github.wrdlbrnft.simpleorm.entities", "SaveParameters");
    public static TypeDefinition REMOVE_PARAMETERS = new TypeDefinition("com.github.wrdlbrnft.simpleorm.entities", "RemoveParameters");

    public static TypeDefinition BASE_ENTITY_MANAGER = new TypeDefinition("com.github.wrdlbrnft.simpleorm.entities", "BaseEntityManager");
    public static TypeDefinition ENTITY_READER = new TypeDefinition("com.github.wrdlbrnft.simpleorm.entities", "EntityReader");
    public static TypeDefinition ENTITY_SAVER = new TypeDefinition("com.github.wrdlbrnft.simpleorm.entities", "EntitySaver");
    public static TypeDefinition ENTITY_REMOVER = new TypeDefinition("com.github.wrdlbrnft.simpleorm.entities", "EntityRemover");
    public static TypeDefinition CONTENT_VALUES = new TypeDefinition("android.content", "ContentValues");

    public static TypeDefinition BOOLEAN_FIELD = new TypeDefinition("com.github.wrdlbrnft.simpleorm.fields", "BooleanField");
    public static TypeDefinition BOOLEAN_FIELD_IMPL = new TypeDefinition("com.github.wrdlbrnft.simpleorm.fields.impl", "BooleanFieldImpl");

    public static TypeDefinition DATE_FIELD = new TypeDefinition("com.github.wrdlbrnft.simpleorm.fields", "DateField");
    public static TypeDefinition DATE_FIELD_IMPL = new TypeDefinition("com.github.wrdlbrnft.simpleorm.fields.impl", "DateFieldImpl");

    public static TypeDefinition DOUBLE_FIELD = new TypeDefinition("com.github.wrdlbrnft.simpleorm.fields", "DoubleField");
    public static TypeDefinition DOUBLE_FIELD_IMPL = new TypeDefinition("com.github.wrdlbrnft.simpleorm.fields.impl", "DoubleFieldImpl");

    public static TypeDefinition ENTITY_FIELD = new TypeDefinition("com.github.wrdlbrnft.simpleorm.fields", "EntityField");
    public static TypeDefinition ENTITY_FIELD_IMPL = new TypeDefinition("com.github.wrdlbrnft.simpleorm.fields.impl", "EntityFieldImpl");

    public static TypeDefinition FLOAT_FIELD = new TypeDefinition("com.github.wrdlbrnft.simpleorm.fields", "FloatField");
    public static TypeDefinition FLOAT_FIELD_IMPL = new TypeDefinition("com.github.wrdlbrnft.simpleorm.fields.impl", "FloatFieldImpl");

    public static TypeDefinition INT_FIELD = new TypeDefinition("com.github.wrdlbrnft.simpleorm.fields", "IntField");
    public static TypeDefinition INT_FIELD_IMPL = new TypeDefinition("com.github.wrdlbrnft.simpleorm.fields.impl", "IntFieldImpl");

    public static TypeDefinition LONG_FIELD = new TypeDefinition("com.github.wrdlbrnft.simpleorm.fields", "LongField");
    public static TypeDefinition LONG_FIELD_IMPL = new TypeDefinition("com.github.wrdlbrnft.simpleorm.fields.impl", "LongFieldImpl");

    public static TypeDefinition STRING_FIELD = new TypeDefinition("com.github.wrdlbrnft.simpleorm.fields", "StringField");
    public static TypeDefinition STRING_FIELD_IMPL = new TypeDefinition("com.github.wrdlbrnft.simpleorm.fields.impl", "StringFieldImpl");
}
