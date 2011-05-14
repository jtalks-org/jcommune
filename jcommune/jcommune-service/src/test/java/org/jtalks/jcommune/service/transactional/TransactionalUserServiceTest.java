package org.jtalks.jcommune.service.transactional;

import org.jtalks.jcommune.model.dao.UserDao;
import org.jtalks.jcommune.model.entity.User;
import org.jtalks.jcommune.service.UserService;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.mockito.Mockito.*;

/**
 * @author Kirill Afonin
 */
public class TransactionalUserServiceTest {
    private UserService userService;
    private UserDao userDao;

    @BeforeMethod
    public void setUp() throws Exception {
        userDao = mock(UserDao.class);
        userService = new TransactionalUserService(userDao);
    }

    @Test
    public void testGetByUsername() throws  Exception {
        final String USERNAME = "username";
        User user = new User();
        user.setUsername(USERNAME);
        when(userDao.getByUsername(USERNAME)).thenReturn(user);

        User result = userService.getByUsername(USERNAME);

        Assert.assertEquals(USERNAME, result.getUsername(), "Username not equals");
        verify(userDao, times(1)).getByUsername(USERNAME);
    }
}
