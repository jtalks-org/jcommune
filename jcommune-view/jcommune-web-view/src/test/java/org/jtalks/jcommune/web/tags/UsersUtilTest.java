package org.jtalks.jcommune.web.tags;


import org.hibernate.ObjectNotFoundException;
import org.jtalks.jcommune.model.entity.JCUser;
import org.testng.annotations.Test;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.testng.Assert.assertTrue;
import static org.testng.Assert.assertFalse;

public class UsersUtilTest {

    @Test
    public void testIsExists() {
        JCUser user = new JCUser("user1", "user1@mail.org", "pass1");
        assertTrue(UsersUtil.isExists(user));
    }

    @Test
    public void testIfUserNull() {
        assertFalse(UsersUtil.isExists(null));
    }

    @Test
    public void testIfObjectNotFound() {
        JCUser user = mock(JCUser.class);
        when(user.getUsername()).thenThrow(new ObjectNotFoundException(0L, JCUser.class.getName()));
        assertFalse(UsersUtil.isExists(user));
    }
}
