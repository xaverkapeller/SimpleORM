package com.github.wrdlbrnft.simpleorm.processor.builder.entitymanager.relationships;

import com.github.wrdlbrnft.simpleorm.processor.analyzer.entity.ColumnInfo;
import com.github.wrdlbrnft.simpleorm.processor.analyzer.entity.EntityInfo;

import java.util.List;

/**
 * Created with Android Studio
 * User: Xaver
 * Date: 02/09/16
 */
class RelationshipInfoImpl implements RelationshipInfo {

    private final EntityInfo mParentEntityInfo;
    private final ColumnInfo mColumnInfo;
    private final EntityInfo mChildEntityInfo;
    private final List<RelationshipInfo> mRelationshipInfos;

    RelationshipInfoImpl(EntityInfo parentEntityInfo, ColumnInfo columnInfo, EntityInfo childEntityInfo, List<RelationshipInfo> relationshipInfos) {
        mParentEntityInfo = parentEntityInfo;
        mChildEntityInfo = childEntityInfo;
        mColumnInfo = columnInfo;
        mRelationshipInfos = relationshipInfos;
    }

    public EntityInfo getParentEntityInfo() {
        return mParentEntityInfo;
    }

    public EntityInfo getChildEntityInfo() {
        return mChildEntityInfo;
    }

    @Override
    public ColumnInfo getColumnInfo() {
        return mColumnInfo;
    }

    public List<RelationshipInfo> getChildRelationshipInfos() {
        return mRelationshipInfos;
    }
}
