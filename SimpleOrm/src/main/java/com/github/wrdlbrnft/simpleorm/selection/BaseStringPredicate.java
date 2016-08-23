package com.github.wrdlbrnft.simpleorm.selection;

import android.support.annotation.NonNull;

import com.github.wrdlbrnft.simpleorm.fields.Field;
import com.github.wrdlbrnft.simpleorm.selection.predicates.StringPredicate;

/**
 * Created with Android Studio
 * User: Xaver
 * Date: 09/07/16
 */
public class BaseStringPredicate<T, P> extends BasePredicate<T, P, String> implements StringPredicate<T, P> {

    public BaseStringPredicate(P builder, Selection.Builder selectionBuilder, Field<T, String> field) {
        super(builder, selectionBuilder, field);
    }

    @Override
    public P contains(@NonNull String text) {
        return appendStatement("LIKE", "%" + text + "%");
    }

    @Override
    public P startsWith(@NonNull String text) {
        return appendStatement("LIKE", text + "%");
    }

    @Override
    public P endsWith(@NonNull String text) {
        return appendStatement("LIKE", "%" + text);
    }
}
