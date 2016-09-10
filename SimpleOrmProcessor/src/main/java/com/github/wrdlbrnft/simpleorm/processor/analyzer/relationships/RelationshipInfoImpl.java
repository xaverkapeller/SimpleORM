package com.github.wrdlbrnft.simpleorm.processor.analyzer.relationships;

import com.github.wrdlbrnft.simpleorm.processor.analyzer.entity.ColumnInfo;
import com.github.wrdlbrnft.simpleorm.processor.analyzer.entity.EntityInfo;

import java.util.List;

/**
 * Created with Android Studio
 * User: Xaver
 * Date: 02/09/16
 */
class RelationshipInfoImpl implements RelationshipInfo {

    private final long mId;
    private final EntityInfo mParentEntityInfo;
    private final ColumnInfo mColumnInfo;
    private final EntityInfo mChildEntityInfo;
    private List<RelationshipInfo> mRelationshipInfos;

    RelationshipInfoImpl(long id, EntityInfo parentEntityInfo, ColumnInfo columnInfo, EntityInfo childEntityInfo) {
        mId = id;
        mParentEntityInfo = parentEntityInfo;
        mChildEntityInfo = childEntityInfo;
        mColumnInfo = columnInfo;
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

    public void setRelationshipInfos(List<RelationshipInfo> relationshipInfos) {
        mRelationshipInfos = relationshipInfos;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        RelationshipInfoImpl that = (RelationshipInfoImpl) o;

        if (mId != that.mId) return false;
        if (mParentEntityInfo != null ? !mParentEntityInfo.equals(that.mParentEntityInfo) : that.mParentEntityInfo != null)
            return false;
        if (mColumnInfo != null ? !mColumnInfo.equals(that.mColumnInfo) : that.mColumnInfo != null)
            return false;
        return mChildEntityInfo != null ? mChildEntityInfo.equals(that.mChildEntityInfo) : that.mChildEntityInfo == null;

    }

    @Override
    public int hashCode() {
        int result = (int) (mId ^ (mId >>> 32));
        result = 31 * result + (mParentEntityInfo != null ? mParentEntityInfo.hashCode() : 0);
        result = 31 * result + (mColumnInfo != null ? mColumnInfo.hashCode() : 0);
        result = 31 * result + (mChildEntityInfo != null ? mChildEntityInfo.hashCode() : 0);
        return result;
    }
}
