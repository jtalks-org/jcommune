package org.jtalks.jcommune.model.dao.hibernate;

import org.jtalks.jcommune.model.entity.Persistent;
import org.testng.annotations.Test;

import static org.testng.Assert.*;

/**
 * @author Kirill Afonin
 */
public class PersistentTest {
    private class PersistentObject extends Persistent {
        public PersistentObject(long id) {
            this.setId(id);
        }
    }

    private Persistent first;
    private Persistent second;
    private Persistent third;

    @Test
    public void testEqualsSymmetry() {
        first = new PersistentObject(1L);
        second = new PersistentObject(1L);

        assertTrue(first.equals(second));
        assertTrue(second.equals(first));
    }

    @Test
    public void testEqualsReflexivity() {
        first = new PersistentObject(1L);

        assertTrue(first.equals(first));
    }

    @Test
    public void testEqualsNull() {
        first = new PersistentObject(1L);

        assertFalse(first.equals(null));
    }

    @Test
    public void testEqualsTransitivity() {
        first = new PersistentObject(1L);
        second = new PersistentObject(1L);
        third = new PersistentObject(1L);

        assertTrue(first.equals(second));
        assertTrue(second.equals(third));
        assertTrue(first.equals(third));
    }

    @Test
    public void testEqualsWhenDifferentId() {
        first = new PersistentObject(1L);
        second = new PersistentObject(3L);

        assertFalse(first.equals(second));
        assertFalse(second.equals(first));
    }

    @Test
    public void testEqualsWhenDifferentClasses() {
        first = new PersistentObject(1L);
        second = new Persistent() {
        };
        second.setId(1L);

        assertFalse(first.equals(second));
    }

    @Test
    public void testHashCode() {
        first = new PersistentObject(1L);
        second = new PersistentObject(1L);

        assertEquals(first.hashCode(), second.hashCode());
    }
}
