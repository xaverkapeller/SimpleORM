package com.github.wrdlbrnft.simpleorm.processor.analyzer.typeadapter;

import com.github.wrdlbrnft.simpleorm.processor.analyzer.entity.ColumnType;

import java.util.List;

/**
 * Created with Android Studio
 * User: Xaver
 * Date: 27/08/16
 */

public interface TypeAdapterResult {
    List<TypeAdapterInfo> getAdapters();
    ColumnType getColumnType();
}
