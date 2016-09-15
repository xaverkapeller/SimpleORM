package com.github.wrdlbrnft.simpleorm;

import com.github.wrdlbrnft.simpleorm.databases.ChildTestEntityBuilder;
import com.github.wrdlbrnft.simpleorm.databases.ComplexEntity;
import com.github.wrdlbrnft.simpleorm.databases.ComplexEntityBuilder;
import com.github.wrdlbrnft.simpleorm.databases.ParentTestEntityBuilder;
import com.github.wrdlbrnft.simpleorm.databases.SimpleTestEntity;
import com.github.wrdlbrnft.simpleorm.databases.SimpleTestEntityBuilder;

import java.util.Arrays;

/**
 * Created with Android Studio
 * User: Xaver
 * Date: 11/09/16
 */

public class TestData {

    public static final String PASSWORD = "35yhugnuah03y]ing";

    public static final ComplexEntity ENTITY_NO_CHILDREN = new ComplexEntityBuilder()
            .setValue(27)
            .setText("Asdf")
            .build();

    public static final ComplexEntity ENTITY_NO_CHILDREN_ALTERNATIVE = new ComplexEntityBuilder()
            .setValue(47)
            .setText("asdf")
            .build();

    public static final ComplexEntity ENTITY_WITH_CHILDREN_A = new ComplexEntityBuilder()
            .setValue(37)
            .setText("jklö")
            .setEntities(Arrays.asList(
                    new ParentTestEntityBuilder()
                            .setChildren(Arrays.asList(
                                    new ChildTestEntityBuilder()
                                            .setText("Test")
                                            .setValue(0.3)
                                            .build(),
                                    new ChildTestEntityBuilder()
                                            .setText("tttt")
                                            .setValue(0.111)
                                            .build()
                            ))
                            .build(),
                    new ParentTestEntityBuilder()
                            .setChildren(Arrays.asList(
                                    new ChildTestEntityBuilder()
                                            .setText("35487")
                                            .setValue(3.141592653589793)
                                            .build(),
                                    new ChildTestEntityBuilder()
                                            .setText("sdfg")
                                            .setValue(-10.0)
                                            .build()
                            ))
                            .build()
            ))
            .build();

    public static final ComplexEntity ENTITY_WITH_CHILDREN_B = new ComplexEntityBuilder()
            .setValue(37)
            .setText("jklö")
            .setEntities(Arrays.asList(
                    new ParentTestEntityBuilder()
                            .setChildren(Arrays.asList(
                                    new ChildTestEntityBuilder()
                                            .setText("Test")
                                            .setValue(0.3)
                                            .build(),
                                    new ChildTestEntityBuilder()
                                            .setText("tttt")
                                            .setValue(0.111)
                                            .build()
                            ))
                            .build(),
                    new ParentTestEntityBuilder()
                            .setChildren(Arrays.asList(
                                    new ChildTestEntityBuilder()
                                            .setText("35487")
                                            .setValue(3.141592653589793)
                                            .build(),
                                    new ChildTestEntityBuilder()
                                            .setText("sdfg")
                                            .setValue(-10.0)
                                            .build()
                            ))
                            .build()
            ))
            .build();

    public static final long SIMPLE_ENTITY_ID = 47L;

    public static final SimpleTestEntity SIMPLE_ENTITY_WITH_ID = new SimpleTestEntityBuilder()
            .setId(SIMPLE_ENTITY_ID)
            .setText("qwerty")
            .setEnabled(false)
            .build();

    public static final SimpleTestEntity SIMPLE_ENTITY = new SimpleTestEntityBuilder()
            .setId(SIMPLE_ENTITY_ID)
            .setText("qwerty")
            .setEnabled(true)
            .build();
}
