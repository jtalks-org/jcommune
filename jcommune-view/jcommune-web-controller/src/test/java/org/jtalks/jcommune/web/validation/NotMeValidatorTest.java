package org.jtalks.jcommune.web.validation;

import org.jtalks.jcommune.model.entity.JCUser;
import org.jtalks.jcommune.service.nontransactional.SecurityService;
import org.mockito.Mock;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

/**
 * @author Evgeniy Naumenko
 */
public class NotMeValidatorTest {

    @Mock
    private SecurityService service;

    private NotMeValidator validator;

    private String username = "username";
    private JCUser user = new JCUser(username, null, null);

    @BeforeMethod
    public void setUp() throws Exception {
        initMocks(this);
        validator = new NotMeValidator(service);
    }

    @Test
    public void testIsValid() throws Exception {
        when(service.getCurrentUser()).thenReturn(user);

        assertTrue(validator.isValid("other name", null));
    }

    @Test
    public void testIsValidForAnonymous() throws Exception {
        when(service.getCurrentUser()).thenReturn(null);

        assertTrue(validator.isValid(username, null));
    }

    @Test
    public void testIsValidForTheSameUser() throws Exception {
        when(service.getCurrentUser()).thenReturn(user);

        assertFalse(validator.isValid(username, null));
    }
}
