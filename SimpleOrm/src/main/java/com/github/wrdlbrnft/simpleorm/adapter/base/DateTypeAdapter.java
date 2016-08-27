package com.github.wrdlbrnft.simpleorm.adapter.base;

import com.github.wrdlbrnft.simpleorm.adapter.ValueConverter;
import com.github.wrdlbrnft.simpleorm.annotations.ColumnTypeAdapter;

import java.util.Date;

/**
 * Created with Android Studio
 * User: Xaver
 * Date: 27/08/16
 */
@ColumnTypeAdapter
public class DateTypeAdapter implements ValueConverter<Long, Date> {

    @Override
    public Date convertTo(Long input) {
        if(input == null) {
            return null;
        }
        return new Date(input);
    }

    @Override
    public Long convertFrom(Date input) {
        if(input == null) {
            return null;
        }

        return input.getTime();
    }
}
