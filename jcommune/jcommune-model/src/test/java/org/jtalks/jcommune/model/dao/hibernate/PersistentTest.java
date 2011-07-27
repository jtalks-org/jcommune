package org.jtalks.jcommune.model.dao.hibernate;

import org.jtalks.jcommune.model.entity.Persistent;
import org.testng.annotations.Test;

import static org.testng.Assert.*;

/**
 * @author Kirill Afonin
 */
public class PersistentTest {
    private class PersistentObject extends Persistent {
        public PersistentObject(String uuid) {
            this.setUuid(uuid);
        }
        
        public PersistentObject() {
            this.setUuid(java.util.UUID.randomUUID().toString());
        }
    }

    private Persistent first;
    private Persistent second;
    private Persistent third;

    @Test
    public void testEqualsSymmetry() {
        String uuid = java.util.UUID.randomUUID().toString();
        
        first = new PersistentObject(uuid);
        second = new PersistentObject(uuid);

        assertTrue(first.equals(second));
        assertTrue(second.equals(first));
    }

    @Test
    public void testEqualsReflexivity() {
        first = new PersistentObject();

        assertTrue(first.equals(first));
    }

    @Test
    public void testEqualsNull() {
        first = new PersistentObject();

        assertFalse(first.equals(null));
    }

    @Test
    public void testEqualsTransitivity() {
        String uuid = java.util.UUID.randomUUID().toString();
        
        first = new PersistentObject(uuid);
        second = new PersistentObject(uuid);
        third = new PersistentObject(uuid);

        assertTrue(first.equals(second));
        assertTrue(second.equals(third));
        assertTrue(first.equals(third));
    }

    @Test
    public void testEqualsWhenDifferentId() {
        first = new PersistentObject("id1");
        second = new PersistentObject("id2");

        assertFalse(first.equals(second));
        assertFalse(second.equals(first));
    }

    @Test
    public void testEqualsWhenDifferentClasses() {
        String id = "id";
        first = new PersistentObject(id);
        second = new Persistent() {
        };
        second.setUuid(id);

        assertFalse(first.equals(second));
    }

    @Test
    public void testHashCode() {
        first = new PersistentObject("uid1");
        second = new PersistentObject("uid1");

        assertEquals(first.hashCode(), second.hashCode());
    }
}
