package com.github.wrdlbrnft.simpleorm.databases;

import com.github.wrdlbrnft.simpleorm.annotations.Column;
import com.github.wrdlbrnft.simpleorm.annotations.Entity;
import com.github.wrdlbrnft.simpleorm.annotations.Id;

import java.util.List;

/**
 * Created with Android Studio
 * User: Xaver
 * Date: 10/09/16
 */

@Entity("Complex")
public interface ComplexEntity {

    @Id
    Long getId();
    void setId(Long id);

    @Column("text")
    String getText();

    @Column("value")
    long getValue();

    @Column("entities")
    List<ParentTestEntity> getEntities();

    @Column("child")
    ChildTestEntity getChild();
}
