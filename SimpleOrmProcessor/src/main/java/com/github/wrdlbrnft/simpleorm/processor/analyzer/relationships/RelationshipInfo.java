package com.github.wrdlbrnft.simpleorm.processor.analyzer.relationships;

import com.github.wrdlbrnft.simpleorm.processor.analyzer.entity.ColumnInfo;
import com.github.wrdlbrnft.simpleorm.processor.analyzer.entity.EntityInfo;

import java.util.List;

/**
 * Created with Android Studio
 * User: Xaver
 * Date: 02/09/16
 */

public interface RelationshipInfo {
    EntityInfo getParentEntityInfo();
    ColumnInfo getColumnInfo();
    EntityInfo getChildEntityInfo();
    List<RelationshipInfo> getChildRelationshipInfos();
}
