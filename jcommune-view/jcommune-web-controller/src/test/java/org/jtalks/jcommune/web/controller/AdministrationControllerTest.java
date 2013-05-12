package org.jtalks.jcommune.web.controller;

import org.jtalks.common.model.entity.Component;
import org.jtalks.common.model.entity.ComponentType;
import org.jtalks.jcommune.service.ComponentService;
import org.jtalks.jcommune.service.transactional.TransactionalComponentService;
import org.springframework.mock.web.MockHttpSession;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import javax.servlet.http.HttpServletRequest;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.testng.Assert.assertEquals;

/**
 * @author Andrei Alikov
 */
public class AdministrationControllerTest {

    //
    private AdministrationController administrationController;

    @BeforeMethod
    public void init() {
        ComponentService componentService = mock(ComponentService.class);
        Component component = new Component("Forum", "Cool Forum", ComponentType.FORUM);
        component.setId(42);

        administrationController = new AdministrationController(componentService);
    }

    @Test
    public void enterAdminModeShouldReturnPreviousPageRedirect() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        String initialPage = "/topics/2";
        when(request.getHeader("Referer")).thenReturn(initialPage);
        when(request.getSession()).thenReturn(new MockHttpSession());

        String resultUrl = administrationController.enterAdministrationMode(request);

        assertEquals(resultUrl, "redirect:" + initialPage);
    }

    @Test
    public void exitAdminModeShouldReturnPreviousPageRedirect() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        String initialPage = "/topics/2";
        when(request.getHeader("Referer")).thenReturn(initialPage);
        when(request.getSession()).thenReturn(new MockHttpSession());

        String resultUrl = administrationController.exitAdministrationMode(request);

        assertEquals(resultUrl, "redirect:" + initialPage);
    }
}
