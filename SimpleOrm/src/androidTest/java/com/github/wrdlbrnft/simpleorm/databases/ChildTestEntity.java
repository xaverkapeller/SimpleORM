package com.github.wrdlbrnft.simpleorm.databases;

import com.github.wrdlbrnft.simpleorm.annotations.AddedInVersion;
import com.github.wrdlbrnft.simpleorm.annotations.Column;
import com.github.wrdlbrnft.simpleorm.annotations.Entity;
import com.github.wrdlbrnft.simpleorm.annotations.Id;

/**
 * Created with Android Studio
 * User: Xaver
 * Date: 07/09/16
 */

@Entity("child")
@AddedInVersion(3)
public interface ChildTestEntity {

    @Id
    Long getId();
    void setId(Long id);

    @Column("text")
    String getText();

    @Column("value")
    double getValue();
}
