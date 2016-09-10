package com.github.wrdlbrnft.simpleorm.processor.builder.entitymanager.relationships;

import com.github.wrdlbrnft.simpleorm.processor.analyzer.entity.EntityInfo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created with Android Studio
 * User: Xaver
 * Date: 04/09/16
 */

public class RelationshipTree {

    public interface Iterator {
        void onPathFound(List<RelationshipInfo> path);
    }

    public static void iterate(List<RelationshipInfo> relationshipInfos, Iterator iterator) {
        for (RelationshipInfo relationshipInfo : relationshipInfos) {
            walk(Collections.singletonList(relationshipInfo), iterator);
        }
    }

    private static void walk(List<RelationshipInfo> backlog, Iterator iterator) {
        final RelationshipInfo relationshipInfo = backlog.get(backlog.size() - 1);
        for (RelationshipInfo info : relationshipInfo.getChildRelationshipInfos()) {
            final List<RelationshipInfo> newBacklog = new ArrayList<>(backlog);
            newBacklog.add(info);
            walk(newBacklog, iterator);
        }

        iterator.onPathFound(backlog);
    }

    public static Set<EntityInfo> getUniqueEntities(List<RelationshipInfo> relationshipInfos) {
        final Set<EntityInfo> entityInfos = new HashSet<>();

        RelationshipTree.iterate(relationshipInfos, new Iterator() {
            @Override
            public void onPathFound(List<RelationshipInfo> path) {
                for (RelationshipInfo relationshipInfo : path) {
                    entityInfos.add(relationshipInfo.getParentEntityInfo());
                    entityInfos.add(relationshipInfo.getChildEntityInfo());
                }
            }
        });

        return entityInfos;
    }
}
