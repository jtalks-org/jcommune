package org.jtalks.jcommune.service.security;

import org.springframework.security.core.context.SecurityContext;
import org.testng.annotations.Test;

import static org.mockito.Mockito.mock;
import static org.testng.Assert.assertTrue;

public class SecurityContextHoldeFacadeTest {
    @Test
    public void testSetGet() {
        SecurityContextFacade facade = new SecurityContextHolderFacade();
        SecurityContext context = mock(SecurityContext.class);

        facade.setContext(context);

        assertTrue(context == facade.getContext());
    }
}
