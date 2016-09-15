package com.github.wrdlbrnft.simpleorm.databases;

import com.github.wrdlbrnft.simpleorm.annotations.Column;
import com.github.wrdlbrnft.simpleorm.annotations.Entity;
import com.github.wrdlbrnft.simpleorm.annotations.Id;

/**
 * Created with Android Studio
 * User: Xaver
 * Date: 07/09/16
 */
@Entity("Test")
public interface SimpleTestEntity {
    @Id
    Long getId();
    void setId(Long id);

    @Column("text")
    String getText();

    @Column("enabled")
    boolean isEnabled();
    void setEnabled(boolean value);
}
