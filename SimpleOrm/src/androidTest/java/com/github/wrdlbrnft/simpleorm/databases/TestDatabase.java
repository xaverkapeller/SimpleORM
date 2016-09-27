package com.github.wrdlbrnft.simpleorm.databases;

import com.github.wrdlbrnft.simpleorm.Repository;
import com.github.wrdlbrnft.simpleorm.annotations.Database;

/**
 * Created with Android Studio
 * User: Xaver
 * Date: 07/09/16
 */

@Database(name = "TestDb2", version = 4, encrypted = true)
public interface TestDatabase {
    Repository<ComplexEntity> complexEntities();
    Repository<SimpleTestEntity> simpleEntities();
    Repository<ParentTestEntity> parentEntities();
    Repository<ChildTestEntity> childEntities();
}
