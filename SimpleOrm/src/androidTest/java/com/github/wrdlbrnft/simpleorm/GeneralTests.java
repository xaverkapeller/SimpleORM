package com.github.wrdlbrnft.simpleorm;

import android.content.Context;
import android.support.test.InstrumentationRegistry;

import com.github.wrdlbrnft.simpleorm.databases.ChildTestEntityBuilder;
import com.github.wrdlbrnft.simpleorm.databases.ComplexEntity;
import com.github.wrdlbrnft.simpleorm.databases.ComplexEntityBuilder;
import com.github.wrdlbrnft.simpleorm.databases.F;
import com.github.wrdlbrnft.simpleorm.databases.ParentTestEntity;
import com.github.wrdlbrnft.simpleorm.databases.ParentTestEntityBuilder;
import com.github.wrdlbrnft.simpleorm.databases.SimpleTestEntity;
import com.github.wrdlbrnft.simpleorm.databases.SimpleTestEntityBuilder;
import com.github.wrdlbrnft.simpleorm.databases.TestDatabase;
import com.github.wrdlbrnft.simpleorm.databases.TestDatabaseFactory;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

/**
 * Created with Android Studio
 * User: Xaver
 * Date: 10/09/16
 */

public class GeneralTests {

    private static final String PASSWORD = "35yhugnuah03y]ing";

    private static final ComplexEntity ENTITY_NO_CHILDREN = new ComplexEntityBuilder()
            .setValue(27)
            .setText("Asdf")
            .build();

    private static final ComplexEntity ENTITY_NO_CHILDREN_ALTERNATIVE = new ComplexEntityBuilder()
            .setValue(47)
            .setText("asdf")
            .build();

    private static final ComplexEntity ENTITY_WITH_CHILDREN_A = new ComplexEntityBuilder()
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

    private static final ComplexEntity ENTITY_WITH_CHILDREN_B = new ComplexEntityBuilder()
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

    private static final long SIMPLE_ENTITY_ID = 47L;
    private static final SimpleTestEntity SIMPLE_ENTITY = new SimpleTestEntityBuilder()
            .setId(SIMPLE_ENTITY_ID)
            .setText("qwerty")
            .build();

    private TestDatabase mDatabase;

    @Before
    public void setUp() {
        final Context context = InstrumentationRegistry.getContext();
        mDatabase = TestDatabaseFactory.newInstance(context, PASSWORD);
        mDatabase.complexEntities().save()
                .entity(ENTITY_NO_CHILDREN)
                .entity(ENTITY_NO_CHILDREN_ALTERNATIVE)
                .entity(ENTITY_WITH_CHILDREN_A)
                .entity(ENTITY_WITH_CHILDREN_B)
                .commit().now();
    }

    @After
    public void tearDown() {
        mDatabase.complexEntities().remove().all().commit().now();
        mDatabase.parentEntities().remove().all().commit().now();
        mDatabase.simpleEntities().remove().all().commit().now();
        mDatabase.childEntities().remove().all().commit().now();
    }

    @Test
    public void testSimpleWhere() {

        final List<ComplexEntity> list = mDatabase.complexEntities().find()
                .where(F.complexentity.value).isEqualTo(27)
                .getList().now();

        Assert.assertNotNull(list);
        for (ComplexEntity entity : list) {
            Assert.assertEquals(27, entity.getValue());
        }
    }

    @Test
    public void testStartsWith() {

        final List<ComplexEntity> list = mDatabase.complexEntities().find()
                .where(F.complexentity.text).startsWith("a")
                .getList().now();

        Assert.assertNotNull(list);
        for (ComplexEntity entity : list) {
            final String text = entity.getText();
            Assert.assertNotNull(text);
            Assert.assertTrue(text.toLowerCase().startsWith("a"));
        }
    }

    @Test
    public void testSaveWithId() {
        mDatabase.simpleEntities().save()
                .entity(SIMPLE_ENTITY)
                .commit();
        Assert.assertEquals(SIMPLE_ENTITY_ID, (long) SIMPLE_ENTITY.getId());
    }

    @Test
    public void testMappingTriggers() {

        final ComplexEntity entity = mDatabase.complexEntities().find()
                .where(F.complexentity.id).isEqualTo(ENTITY_WITH_CHILDREN_B.getId())
                .getFirst().now();

        final ParentTestEntity childEntity = ENTITY_WITH_CHILDREN_B.getEntities().get(0);
        mDatabase.parentEntities().remove()
                .entity(childEntity)
                .commit();

        final ComplexEntity entityAfterRemove = mDatabase.complexEntities().find()
                .where(F.complexentity.id).isEqualTo(ENTITY_WITH_CHILDREN_B.getId())
                .getFirst().now();

        mDatabase.parentEntities().save()
                .entity(childEntity)
                .commit();

        final ComplexEntity entityAfterSave = mDatabase.complexEntities().find()
                .where(F.complexentity.id).isEqualTo(ENTITY_WITH_CHILDREN_B.getId())
                .getFirst().now();

        Assert.assertEquals(2, entity.getEntities().size());
        Assert.assertEquals(1, entityAfterRemove.getEntities().size());
        Assert.assertEquals(1, entityAfterSave.getEntities().size());
    }
}
