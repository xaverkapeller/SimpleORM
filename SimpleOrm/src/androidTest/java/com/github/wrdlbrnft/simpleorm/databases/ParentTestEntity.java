package com.github.wrdlbrnft.simpleorm.databases;

import com.github.wrdlbrnft.simpleorm.annotations.AddedInVersion;
import com.github.wrdlbrnft.simpleorm.annotations.Column;
import com.github.wrdlbrnft.simpleorm.annotations.Entity;
import com.github.wrdlbrnft.simpleorm.annotations.Id;

import java.util.List;

/**
 * Created with Android Studio
 * User: Xaver
 * Date: 07/09/16
 */

@Entity("Parent")
public interface ParentTestEntity {

    @Id
    Long getId();
    void setId(Long id);

    @Column("children")
    @AddedInVersion(3)
    List<ChildTestEntity> getChildren();
}
