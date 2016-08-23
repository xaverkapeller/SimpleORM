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
class DatabaseInfoImpl implements DatabaseInfo {

    private final String mDatabaseName;
    private final int mDatabaseVersion;
    private final ExecutableElement mChangePasswordMethod;
    private final boolean mEncrypted;
    private final TypeElement mTypeElement;
    private final List<RepositoryInfo> mRepositoryInfos;
    private final Set<EntityInfo> mEntityInfos;

    DatabaseInfoImpl(String databaseName, int databaseVersion, ExecutableElement changePasswordMethod, boolean encrypted, TypeElement typeElement, List<RepositoryInfo> repositoryInfos, Set<EntityInfo> entityInfos) {
        mDatabaseName = databaseName;
        mDatabaseVersion = databaseVersion;
        mChangePasswordMethod = changePasswordMethod;
        mEncrypted = encrypted;
        mTypeElement = typeElement;
        mRepositoryInfos = repositoryInfos;
        mEntityInfos = entityInfos;
    }

    @Override
    public String getDatabaseName() {
        return mDatabaseName;
    }

    @Override
    public int getDatabaseVersion() {
        return mDatabaseVersion;
    }

    @Override
    public ExecutableElement getChangePasswordMethod() {
        return mChangePasswordMethod;
    }

    @Override
    public boolean isEncrypted() {
        return mEncrypted;
    }

    @Override
    public TypeElement getTypeElement() {
        return mTypeElement;
    }

    @Override
    public List<RepositoryInfo> getRepositoryInfos() {
        return mRepositoryInfos;
    }

    @Override
    public Set<EntityInfo> getEntityInfos() {
        return mEntityInfos;
    }
}
