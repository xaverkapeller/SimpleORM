package com.github.wrdlbrnft.simpleorm;

import com.github.wrdlbrnft.simpleorm.fields.BooleanField;
import com.github.wrdlbrnft.simpleorm.fields.DateField;
import com.github.wrdlbrnft.simpleorm.fields.DoubleField;
import com.github.wrdlbrnft.simpleorm.fields.FloatField;
import com.github.wrdlbrnft.simpleorm.fields.IntField;
import com.github.wrdlbrnft.simpleorm.fields.LongField;
import com.github.wrdlbrnft.simpleorm.fields.StringField;
import com.github.wrdlbrnft.simpleorm.selection.remove.BooleanRemoveBuilder;
import com.github.wrdlbrnft.simpleorm.selection.remove.DateRemoveBuilder;
import com.github.wrdlbrnft.simpleorm.selection.remove.DoubleRemoveBuilder;
import com.github.wrdlbrnft.simpleorm.selection.remove.FloatRemoveBuilder;
import com.github.wrdlbrnft.simpleorm.selection.remove.IntRemoveBuilder;
import com.github.wrdlbrnft.simpleorm.selection.remove.LongRemoveBuilder;
import com.github.wrdlbrnft.simpleorm.selection.remove.StringRemoveBuilder;

import java.util.List;

/**
 * Created with Android Studio
 * User: Xaver
 * Date: 10/07/16
 */

public interface RemoveTransaction<T> {
    RemoveTransaction<T> entity(T entity);
    RemoveTransaction<T> entities(List<T> entities);
    DateRemoveBuilder<T> where(DateField<T> field);
    StringRemoveBuilder<T> where(StringField<T> field);
    BooleanRemoveBuilder<T> where(BooleanField<T> field);
    DoubleRemoveBuilder<T> where(DoubleField<T> field);
    FloatRemoveBuilder<T> where(FloatField<T> field);
    IntRemoveBuilder<T> where(IntField<T> field);
    LongRemoveBuilder<T> where(LongField<T> field);
    RemoveTransaction<T> and();
    RemoveTransaction<T> all();
    RemoveTransaction<T> or();
    Remover<T> commit();
}
