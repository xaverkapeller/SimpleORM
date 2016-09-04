package com.github.wrdlbrnft.simpleorm.processor.builder.entitymanager.relationships;

import com.github.wrdlbrnft.simpleorm.processor.analyzer.entity.ColumnInfo;
import com.github.wrdlbrnft.simpleorm.processor.analyzer.entity.ColumnType;
import com.github.wrdlbrnft.simpleorm.processor.analyzer.entity.EntityInfo;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.processing.ProcessingEnvironment;

/**
 * Created with Android Studio
 * User: Xaver
 * Date: 02/09/16
 */

public class RelationshipAnalyzer {

    private final ProcessingEnvironment mProcessingEnv;

    public RelationshipAnalyzer(ProcessingEnvironment processingEnv) {
        mProcessingEnv = processingEnv;
    }

    public List<RelationshipInfo> analyze(EntityInfo entityInfo) {
        return analyze(entityInfo, new HashSet<RelationshipInfo>());
    }

    private List<RelationshipInfo> analyze(EntityInfo entityInfo, Set<RelationshipInfo> handledRelationships) {
        final List<RelationshipInfo> relationshipInfos = new ArrayList<>();

        for (ColumnInfo columnInfo : entityInfo.getColumns()) {
            if (columnInfo.getColumnType() != ColumnType.ENTITY) {
                continue;
            }

            final EntityInfo childEntityInfo = columnInfo.getChildEntityInfo();
            final RelationshipInfoImpl relationshipInfo = new RelationshipInfoImpl(
                    entityInfo,
                    columnInfo,
                    childEntityInfo
            );
            if (handledRelationships.add(relationshipInfo)) {
                relationshipInfo.setRelationshipInfos(analyze(childEntityInfo, handledRelationships));
                relationshipInfos.add(relationshipInfo);
            }
        }

        return relationshipInfos;
    }
}
