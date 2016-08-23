package com.github.wrdlbrnft.simpleorm.database.encrypted;

import android.content.Context;

import com.github.wrdlbrnft.simpleorm.database.EncryptedSQLiteProvider;
import com.github.wrdlbrnft.simpleorm.database.ReadableSQLiteWrapper;
import com.github.wrdlbrnft.simpleorm.database.SQLiteDatabaseManager;
import com.github.wrdlbrnft.simpleorm.database.WritableSQLiteWrapper;

import net.sqlcipher.database.SQLiteDatabase;
import net.sqlcipher.database.SQLiteOpenHelper;

/**
 * Created with Android Studio
 * User: Xaver
 * Date: 10/07/16
 */
public abstract class BaseEncryptedSQLiteProvider extends SQLiteOpenHelper implements EncryptedSQLiteProvider {

    private final char[] mPassword;

    public BaseEncryptedSQLiteProvider(Context context, String name, int version, char[] password) {
        super(context, name, null, version);
        SQLiteDatabase.loadLibs(context);
        mPassword = password;
    }

    @Override
    public final void onCreate(SQLiteDatabase sqLiteDatabase) {
        final SQLiteDatabaseManager manager = new SQLiteDatabaseManagerImpl(sqLiteDatabase);
        onCreate(manager);
    }

    @Override
    public final void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        final SQLiteDatabaseManager manager = new SQLiteDatabaseManagerImpl(sqLiteDatabase);
        onUpgrade(manager, oldVersion, newVersion);
    }

    protected abstract void onCreate(SQLiteDatabaseManager manager);
    protected abstract void onUpgrade(SQLiteDatabaseManager manager, int oldVersion, int newVersion);

    @Override
    public final ReadableSQLiteWrapper getReadableWrapper() {
        final SQLiteDatabase database = getReadableDatabase(mPassword);
        return new ReadableSQLiteWrapperImpl(database);
    }

    @Override
    public final WritableSQLiteWrapper getWritableWrapper() {
        final SQLiteDatabase database = getWritableDatabase(mPassword);
        return new WritableSQLiteWrapperImpl(database);
    }

    @Override
    public void changePassword(char[] newPassword) {
        final SQLiteDatabase database = getWritableDatabase(mPassword);
        database.changePassword(newPassword);
    }

    @Override
    public void changePassword(String newPassword) {
        final SQLiteDatabase database = getWritableDatabase(mPassword);
        database.changePassword(newPassword);
    }

    private static class SQLiteDatabaseManagerImpl implements SQLiteDatabaseManager {

        private final SQLiteDatabase mDatabase;

        private SQLiteDatabaseManagerImpl(SQLiteDatabase database) {
            mDatabase = database;
        }

        @Override
        public void execSql(String sql) {
            mDatabase.execSQL(sql);
        }
    }
}
