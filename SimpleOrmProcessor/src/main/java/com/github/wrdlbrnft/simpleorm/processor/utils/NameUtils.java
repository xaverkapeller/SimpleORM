package com.github.wrdlbrnft.simpleorm.processor.utils;

/**
 * Created with Android Studio
 * User: Xaver
 * Date: 04/07/16
 */

public class NameUtils {

    public static String toSnakeCase(String text) {
        final StringBuilder builder = new StringBuilder();
        boolean insertUnderscoreCharacter = false;
        for (int i = 0, count = text.length(); i < count; i++) {
            final char character = text.charAt(i);
            if (Character.isUpperCase(character)) {
                if(insertUnderscoreCharacter) {
                    insertUnderscoreCharacter = false;
                    builder.append('_');
                }
            } else {
                insertUnderscoreCharacter = true;
            }
            builder.append(Character.toLowerCase(character));
        }

        return builder.toString();
    }
}
