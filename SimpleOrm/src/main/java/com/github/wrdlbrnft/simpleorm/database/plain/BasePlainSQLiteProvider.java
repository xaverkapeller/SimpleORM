package com.github.wrdlbrnft.simpleorm.database.plain;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.github.wrdlbrnft.simpleorm.database.ReadableSQLiteWrapper;
import com.github.wrdlbrnft.simpleorm.database.SQLiteDatabaseManager;
import com.github.wrdlbrnft.simpleorm.database.SQLiteProvider;
import com.github.wrdlbrnft.simpleorm.database.WritableSQLiteWrapper;

/**
 * Created with Android Studio
 * User: Xaver
 * Date: 10/07/16
 */
public abstract class BasePlainSQLiteProvider extends SQLiteOpenHelper implements SQLiteProvider {

    public BasePlainSQLiteProvider(Context context, String name, int version) {
        super(context, name, null, version);
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
        final SQLiteDatabase database = getReadableDatabase();
        return new ReadableSQLiteWrapperImpl(database);
    }

    @Override
    public final WritableSQLiteWrapper getWritableWrapper() {
        final SQLiteDatabase database = getWritableDatabase();
        return new WritableSQLiteWrapperImpl(database);
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
