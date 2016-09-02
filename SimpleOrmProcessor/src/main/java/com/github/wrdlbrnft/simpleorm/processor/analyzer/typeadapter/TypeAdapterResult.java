package com.github.wrdlbrnft.simpleorm.processor.analyzer.typeadapter;

import com.github.wrdlbrnft.simpleorm.processor.analyzer.entity.ColumnType;

import java.util.List;

import javax.lang.model.type.TypeMirror;

/**
 * Created with Android Studio
 * User: Xaver
 * Date: 27/08/16
 */

public interface TypeAdapterResult {

    enum Type {
        OBJECT,
        LIST
    }

    List<TypeAdapterInfo> getAdapters();
    ColumnType getColumnType();
    TypeMirror getTypeMirror();
    Type getResultType();
}
