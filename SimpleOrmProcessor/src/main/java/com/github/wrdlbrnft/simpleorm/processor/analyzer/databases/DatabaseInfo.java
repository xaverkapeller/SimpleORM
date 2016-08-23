package com.github.wrdlbrnft.simpleorm.processor.analyzer.databases;

import com.github.wrdlbrnft.simpleorm.processor.analyzer.entity.EntityInfo;

import java.util.List;
import java.util.Set;

import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;

/**
 * Created with Android Studio
 * User: Xaver
 * Date: 10/07/16
 */

public interface DatabaseInfo {
    String getDatabaseName();
    int getDatabaseVersion();
    ExecutableElement getChangePasswordMethod();
    boolean isEncrypted();
    TypeElement getTypeElement();
    List<RepositoryInfo> getRepositoryInfos();
    Set<EntityInfo> getEntityInfos();
}
