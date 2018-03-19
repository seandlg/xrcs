package com.example.android.xrcs;

import android.provider.BaseColumns;
import com.example.android.xrcs.data.WorkoutContract;
import org.junit.Test;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class ContractClassUnitTest {
    @Test
    public void inner_class_exists() throws Exception {
        Class[] innerClasses = WorkoutContract.class.getDeclaredClasses();
        assertEquals("There should be 1 Inner class inside the contract class", 1, innerClasses.length);
    }

    @Test
    public void inner_class_type_correct() throws Exception {
        Class[] innerClasses = WorkoutContract.class.getDeclaredClasses();
        assertEquals("Cannot find inner class to complete unit test", 1, innerClasses.length);
        Class entryClass = innerClasses[0];
        assertTrue("Inner class should implement the BaseColumns interface", BaseColumns.class.isAssignableFrom(entryClass));
        assertTrue("Inner class should be final", Modifier.isFinal(entryClass.getModifiers()));
        assertTrue("Inner class should be static", Modifier.isStatic(entryClass.getModifiers()));
    }

    @Test
    public void inner_class_members_correct() throws Exception {
        Class[] innerClasses = WorkoutContract.class.getDeclaredClasses();
        assertEquals("Cannot find inner class to complete unit test", 1, innerClasses.length);
        Class entryClass = innerClasses[0];
        Field[] allFields = entryClass.getDeclaredFields();
        assertEquals("There should be exactly 9 String members in the inner class", 9, allFields.length);
        for (Field field : allFields) {
            assertTrue("All members in the contract class should be Strings", field.getType()==String.class);
            assertTrue("All members in the contract class should be final", Modifier.isFinal(field.getModifiers()));
            assertTrue("All members in the contract class should be static", Modifier.isStatic(field.getModifiers()));
        }
    }
}
