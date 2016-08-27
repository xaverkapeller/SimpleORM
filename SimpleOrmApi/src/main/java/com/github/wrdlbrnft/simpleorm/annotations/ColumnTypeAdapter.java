package com.github.wrdlbrnft.simpleorm.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created with Android Studio
 * User: Xaver
 * Date: 17/07/16
 */
@Retention(RetentionPolicy.SOURCE)
@Target({ElementType.TYPE})
public @interface ColumnTypeAdapter {
}
