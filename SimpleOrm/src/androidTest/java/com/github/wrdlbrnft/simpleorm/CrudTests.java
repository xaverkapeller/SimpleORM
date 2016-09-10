package com.github.wrdlbrnft.simpleorm;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.LargeTest;

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

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.security.SecureRandom;
import java.util.Collections;
import java.util.Random;

/**
 * Created with Android Studio
 * User: Xaver
 * Date: 07/09/16
 */

@LargeTest
@RunWith(AndroidJUnit4.class)
public class CrudTests {

    private static final Random RANDOM = new SecureRandom();

    private static final String PASSWORD = "35yhugnuah03y]ing";

    @Test
    public void simpleCrudTest() {
        final Context context = InstrumentationRegistry.getContext();
        final TestDatabase database = TestDatabaseFactory.newInstance(context, PASSWORD);

        final SimpleTestEntity entity = new SimpleTestEntityBuilder()
                .setText(String.valueOf(System.currentTimeMillis()))
                .build();

        database.testEntities().save()
                .entity(entity)
                .commit().now();

        final SimpleTestEntity queriedEntity = database.testEntities().find()
                .where(F.simpletestentity.id).isEqualTo(entity.getId())
                .getFirst().now();

        database.testEntities().remove()
                .entity(queriedEntity)
                .commit();

        final SimpleTestEntity nullEntity = database.testEntities().find()
                .where(F.simpletestentity.id).isEqualTo(entity.getId())
                .getFirst().now();

        Assert.assertEquals(entity, queriedEntity);
        Assert.assertNull(nullEntity);
    }

    @Test
    public void parentCrudTest() {
        final Context context = InstrumentationRegistry.getContext();
        final TestDatabase database = TestDatabaseFactory.newInstance(context, PASSWORD);

        final ParentTestEntity entity = new ParentTestEntityBuilder()
                .setChildren(Collections.singletonList(
                        new ChildTestEntityBuilder()
                                .setText(String.valueOf(System.currentTimeMillis()))
                                .setValue(RANDOM.nextDouble())
                                .build()
                ))
                .build();

        database.parentEntities().save()
                .entity(entity)
                .commit().now();

        final ParentTestEntity queriedEntity = database.parentEntities().find()
                .where(F.parenttestentity.id).isEqualTo(entity.getId())
                .getFirst().now();

        database.parentEntities().remove()
                .entity(queriedEntity)
                .commit();

        final ParentTestEntity nullEntity = database.parentEntities().find()
                .where(F.parenttestentity.id).isEqualTo(entity.getId())
                .getFirst().now();

        Assert.assertEquals(entity, queriedEntity);
        Assert.assertNull(nullEntity);
    }

    @Test
    public void complexCrudTests() {
        final Context context = InstrumentationRegistry.getContext();
        final TestDatabase database = TestDatabaseFactory.newInstance(context, PASSWORD);

        final ComplexEntity entity = new ComplexEntityBuilder()
                .setText(String.valueOf(System.currentTimeMillis()))
                .setValue(RANDOM.nextLong())
                .setEntities(Collections.singletonList(new ParentTestEntityBuilder()
                        .setChildren(Collections.singletonList(
                                new ChildTestEntityBuilder()
                                        .setText(String.valueOf(System.currentTimeMillis()))
                                        .setValue(RANDOM.nextDouble())
                                        .build()
                        ))
                        .build()))
                .build();

        database.complexEntities().save()
                .entity(entity)
                .commit().now();

        final ComplexEntity queriedEntity = database.complexEntities().find()
                .where(F.complexentity.id).isEqualTo(entity.getId())
                .getFirst().now();

        database.complexEntities().remove()
                .entity(queriedEntity)
                .commit();

        final ComplexEntity nullEntity = database.complexEntities().find()
                .where(F.complexentity.id).isEqualTo(entity.getId())
                .getFirst().now();

        Assert.assertEquals(entity, queriedEntity);
        Assert.assertNull(nullEntity);
    }
}
