package com.github.wrdlbrnft.simpleorm.processor.analyzer.databases;

import com.github.wrdlbrnft.simpleorm.processor.analyzer.entity.EntityInfo;

import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;

/**
 * Created with Android Studio
 * User: Xaver
 * Date: 10/07/16
 */

public interface RepositoryInfo {
    TypeElement getEntityType();
    ExecutableElement getMethod();
    EntityInfo getEntityInfo();
}
