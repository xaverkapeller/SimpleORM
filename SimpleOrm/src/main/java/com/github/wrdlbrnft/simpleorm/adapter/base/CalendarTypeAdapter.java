package com.github.wrdlbrnft.simpleorm.adapter.base;

import com.github.wrdlbrnft.simpleorm.adapter.ValueConverter;
import com.github.wrdlbrnft.simpleorm.annotations.ColumnTypeAdapter;

import java.util.Calendar;
import java.util.Date;

/**
 * Created with Android Studio
 * User: Xaver
 * Date: 27/08/16
 */
@ColumnTypeAdapter
public class CalendarTypeAdapter implements ValueConverter<Date, Calendar> {

    @Override
    public Calendar convertTo(Date input) {
        if (input == null) {
            return null;
        }
        final Calendar calendar = Calendar.getInstance();
        calendar.setTime(input);
        return calendar;
    }

    @Override
    public Date convertFrom(Calendar input) {
        if (input == null) {
            return null;
        }
        return input.getTime();
    }
}
