package com.github.wrdlbrnft.simpleorm;

import android.content.Context;
import android.support.test.InstrumentationRegistry;

import com.github.wrdlbrnft.simpleorm.databases.ComplexEntity;
import com.github.wrdlbrnft.simpleorm.databases.F;
import com.github.wrdlbrnft.simpleorm.databases.ParentTestEntity;
import com.github.wrdlbrnft.simpleorm.databases.SimpleTestEntity;
import com.github.wrdlbrnft.simpleorm.databases.TestDatabase;
import com.github.wrdlbrnft.simpleorm.databases.TestDatabaseFactory;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

/**
 * Created with Android Studio
 * User: Xaver
 * Da te: 10/09/16
 */

public class GeneralTests {

    private TestDatabase mDatabase;

    @Before
    public void setUp() {
        final Context context = InstrumentationRegistry.getContext();
        mDatabase = TestDatabaseFactory.newInstance(context, TestData.PASSWORD);
        mDatabase.complexEntities().save()
                .entity(TestData.ENTITY_NO_CHILDREN)
                .entity(TestData.ENTITY_NO_CHILDREN_ALTERNATIVE)
                .entity(TestData.ENTITY_WITH_CHILDREN_A)
                .entity(TestData.ENTITY_WITH_CHILDREN_B)
                .commit().now();
        mDatabase.parentEntities().save()
                .entity(TestData.PARENT_A)
                .commit().now();
        mDatabase.simpleEntities().save()
                .entity(TestData.SIMPLE_ENTITY)
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
        Assert.assertFalse(list.isEmpty());
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
        Assert.assertFalse(list.isEmpty());
        for (ComplexEntity entity : list) {
            final String text = entity.getText();
            Assert.assertNotNull(text);
            Assert.assertTrue(text.toLowerCase().startsWith("a"));
        }
    }

    @Test
    public void testEndsWith() {
        final List<ComplexEntity> list = mDatabase.complexEntities().find()
                .where(F.complexentity.text).endsWith("f")
                .getList().now();

        Assert.assertNotNull(list);
        Assert.assertFalse(list.isEmpty());
        for (ComplexEntity entity : list) {
            final String text = entity.getText();
            Assert.assertNotNull(text);
            Assert.assertTrue(text.toLowerCase().endsWith("f"));
        }
    }

    @Test
    public void testContains() {
        final List<ComplexEntity> list = mDatabase.complexEntities().find()
                .where(F.complexentity.text).contains("sd")
                .getList().now();

        Assert.assertNotNull(list);
        Assert.assertFalse(list.isEmpty());
        for (ComplexEntity entity : list) {
            final String text = entity.getText();
            Assert.assertNotNull(text);
            Assert.assertTrue(text.toLowerCase().contains("sd"));
        }
    }

    @Test
    public void testGreaterThan() {
        final List<ComplexEntity> list = mDatabase.complexEntities().find()
                .where(F.complexentity.value).isGreaterThan(27L)
                .getList().now();

        Assert.assertNotNull(list);
        Assert.assertFalse(list.isEmpty());
        for (ComplexEntity entity : list) {
            Assert.assertTrue(entity.getValue() > 27L);
        }
    }

    @Test
    public void testGreaterThanOrEqual() {
        final List<ComplexEntity> list = mDatabase.complexEntities().find()
                .where(F.complexentity.value).isGreaterThanOrEqualTo(37L)
                .getList().now();

        Assert.assertNotNull(list);
        Assert.assertFalse(list.isEmpty());
        for (ComplexEntity entity : list) {
            Assert.assertTrue(entity.getValue() >= 37L);
        }
    }

    @Test
    public void testLessThan() {
        final List<ComplexEntity> list = mDatabase.complexEntities().find()
                .where(F.complexentity.value).isLessThan(37L)
                .getList().now();

        Assert.assertNotNull(list);
        Assert.assertFalse(list.isEmpty());
        for (ComplexEntity entity : list) {
            Assert.assertTrue(entity.getValue() < 37L);
        }
    }

    @Test
    public void testLessThanOrEqual() {
        final List<ComplexEntity> list = mDatabase.complexEntities().find()
                .where(F.complexentity.value).isLessThanOrEqualTo(27L)
                .getList().now();

        Assert.assertNotNull(list);
        Assert.assertFalse(list.isEmpty());
        for (ComplexEntity entity : list) {
            Assert.assertTrue(entity.getValue() <= 27L);
        }
    }

    @Test
    public void testSaveWithId() {
        mDatabase.simpleEntities().save()
                .entity(TestData.SIMPLE_ENTITY_WITH_ID)
                .commit();
        Assert.assertEquals(TestData.SIMPLE_ENTITY_ID, (long) TestData.SIMPLE_ENTITY_WITH_ID.getId());
    }

    @Test
    public void testIsTrue() {
        final List<SimpleTestEntity> list = mDatabase.simpleEntities().find()
                .where(F.simpletestentity.enabled).isTrue()
                .getList().now();

        Assert.assertNotNull(list);
        Assert.assertFalse(list.isEmpty());
        for (SimpleTestEntity entity : list) {
            Assert.assertTrue(entity.isEnabled());
        }
    }

    @Test
    public void testIsBooleanEqual() {
        final List<SimpleTestEntity> list = mDatabase.simpleEntities().find()
                .where(F.simpletestentity.enabled).isEqualTo(true)
                .getList().now();

        Assert.assertNotNull(list);
        Assert.assertFalse(list.isEmpty());
        for (SimpleTestEntity entity : list) {
            Assert.assertTrue(entity.isEnabled());
        }
    }

    @Test
    public void testMappingTriggers() {

        final ComplexEntity entity = mDatabase.complexEntities().find()
                .where(F.complexentity.id).isEqualTo(TestData.ENTITY_WITH_CHILDREN_B.getId())
                .getFirst().now();

        final ParentTestEntity childEntity = TestData.ENTITY_WITH_CHILDREN_B.getEntities().get(0);
        mDatabase.parentEntities().remove()
                .entity(childEntity)
                .commit().now();

        final ComplexEntity entityAfterRemove = mDatabase.complexEntities().find()
                .where(F.complexentity.id).isEqualTo(TestData.ENTITY_WITH_CHILDREN_B.getId())
                .getFirst().now();

        mDatabase.parentEntities().save()
                .entity(childEntity)
                .commit().now();

        final ComplexEntity entityAfterSave = mDatabase.complexEntities().find()
                .where(F.complexentity.id).isEqualTo(TestData.ENTITY_WITH_CHILDREN_B.getId())
                .getFirst().now();

        Assert.assertEquals(2, entity.getEntities().size());
        Assert.assertEquals(1, entityAfterRemove.getEntities().size());
        Assert.assertEquals(1, entityAfterSave.getEntities().size());
    }

    @Test
    public void testMappingUpdates() {
        final ParentTestEntity entity = mDatabase.parentEntities().find()
                .where(F.parenttestentity.id).isEqualTo(TestData.PARENT_A.getId())
                .getFirst().now();

        entity.getChildren().remove(0);
        entity.getChildren().add(TestData.CHILD_C);

        mDatabase.parentEntities().save()
                .entity(entity)
                .commit().now();

        final ParentTestEntity afterUpdate = mDatabase.parentEntities().find()
                .where(F.parenttestentity.id).isEqualTo(TestData.PARENT_A.getId())
                .getFirst().now();

        Assert.assertEquals(entity, afterUpdate);
    }
}