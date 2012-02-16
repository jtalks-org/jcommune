/**
 * Copyright (C) 2011  JTalks.org Team
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */
package org.jtalks.jcommune.model.entity;

import org.testng.annotations.Test;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;

/**
 * @author Michael Gamov
 */
public class IdComparableEntityTest {

    /**
     * Dummy implementation of IdComparableEntity
     */
    private class IdComparableEntityImpl extends IdComparableEntity {
        public IdComparableEntityImpl(long id) {
            setId(id);            
        }
    }
                    
       
    @Test
    public void testEquals() {
        IdComparableEntityImpl a = new IdComparableEntityImpl(1L);
        IdComparableEntityImpl b = new IdComparableEntityImpl(1L);
        
        assertEquals(a, b);        
    }
    
    @Test
    public void testUnEquals () {
        IdComparableEntityImpl a = new IdComparableEntityImpl(1L);
        IdComparableEntityImpl b = new IdComparableEntityImpl(-1000L);

        assertFalse(a.equals(b));
    }
    
    @Test 
    public void testDifferentTypes() {
        IdComparableEntityImpl a = new IdComparableEntityImpl(1L);
        String b = "bbb";

        assertFalse(a.equals(b));
    }
    
    @Test
    public void testNull() {
        IdComparableEntityImpl a = new IdComparableEntityImpl(1L);
        IdComparableEntityImpl b = null;

        assertFalse(a.equals(b));
    }
    
}
