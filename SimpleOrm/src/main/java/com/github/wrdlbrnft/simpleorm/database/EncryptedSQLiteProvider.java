package com.github.wrdlbrnft.simpleorm.database;

/**
 * Created with Android Studio
 * User: Xaver
 * Date: 17/07/16
 */

public interface EncryptedSQLiteProvider extends SQLiteProvider {
    void changePassword(char[] newPassword);
    void changePassword(String newPassword);
}
