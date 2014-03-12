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
package org.jtalks.jcommune.web.tags;

import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.testng.Assert.assertEquals;

import java.io.Serializable;

import javax.servlet.ServletContext;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.Tag;

import org.jtalks.common.service.security.SecurityContextFacade;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.springframework.beans.BeanUtils;
import org.springframework.mock.web.MockPageContext;
import org.springframework.mock.web.MockServletContext;
import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.GenericWebApplicationContext;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 * @author Vyacheslav Mishcheryakov
 */
public class HasPermissionTest {

    private static final Long TARGET_ID = 1L;
    private static final String TARGET_TYPE = "BRANCH";
    private static final String PERMISSION = "PERMISSION";
    
    
    private HasPermission tag;
    
    private MockPageContext pageContext;
    
    @Mock
    private PermissionEvaluator aclEvaluator;
    
    @Mock
    private SecurityContextFacade securityContextFacade;
    
    @Mock
    private SecurityContext securityContext;
    
    @Mock
    private Authentication authentication;
    
    @BeforeMethod
    public void setUp() {
        initMocks(this);
        when(securityContextFacade.getContext()).thenReturn(securityContext);
        
        ServletContext servletContext = new MockServletContext();
        GenericWebApplicationContext wac = (GenericWebApplicationContext) BeanUtils
                .instantiateClass(GenericWebApplicationContext.class);
        wac.getBeanFactory().registerSingleton("aclEvaluator", aclEvaluator);
        wac.getBeanFactory().registerSingleton("securityContextFacade", 
                securityContextFacade);
        servletContext.setAttribute(
                WebApplicationContext.ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE, wac);
        pageContext = new MockPageContext(servletContext);
        
        tag = new HasPermission();
        
        tag.setPageContext(pageContext);
        
    }
    
    @Test(expectedExceptions=JspException.class)
    public void testTargetIdNotSpecified() throws JspException {
        tag.setTargetType(TARGET_TYPE);
        tag.setPermission(PERMISSION);
        tag.doStartTag();
    }
    
    @Test(expectedExceptions=JspException.class)
    public void testTargetTypeNotSpecified() throws JspException {
        tag.setTargetId(TARGET_ID);
        tag.setPermission(PERMISSION);
        tag.doStartTag();
    }
    
    @Test(expectedExceptions=JspException.class)
    public void testTargetTypeBlank() throws JspException {
        tag.setTargetId(TARGET_ID);
        tag.setTargetType("");
        tag.setPermission(PERMISSION);
        tag.doStartTag();
    }
    
    @Test(expectedExceptions=JspException.class)
    public void testPermissionNotSpecified() throws JspException {
        tag.setTargetId(1L);
        tag.setTargetType("BRANCH");
        tag.doStartTag();
    }
    
    @Test(expectedExceptions=JspException.class)
    public void testPermissionBlank() throws JspException {
        tag.setTargetId(1L);
        tag.setTargetType("BRANCH");
        tag.setPermission("");
        tag.doStartTag();
    }
    
    @Test
    public void testHasNoPermission() throws JspException {
        when(aclEvaluator.hasPermission(
                Matchers.any(Authentication.class), 
                Matchers.any(Serializable.class), 
                Matchers.anyString(), 
                Matchers.anyString()))
                .thenReturn(false);
        tag.setTargetId(1L);
        tag.setTargetType("BRANCH");
        tag.setPermission("PERMISSION");
        
        assertEquals(tag.doStartTag(), Tag.SKIP_BODY);
    }
    
    @Test
    public void testHasPermission() throws JspException {
        when(securityContextFacade.getContext().getAuthentication()).thenReturn(authentication);
        when(aclEvaluator.hasPermission(
                Matchers.any(Authentication.class), 
                Matchers.any(Serializable.class), 
                Matchers.anyString(), 
                Matchers.anyString()))
                .thenReturn(true);
        tag.setTargetId(1L);
        tag.setTargetType("BRANCH");
        tag.setPermission("PERMISSION");
        
        assertEquals(tag.doStartTag(), Tag.EVAL_BODY_INCLUDE);
    }
    
    @Test
    public void testHasPermissionWithNullAuthentication() throws JspException {
        when(securityContextFacade.getContext().getAuthentication()).thenReturn(null);
        when(aclEvaluator.hasPermission(
                Matchers.any(Authentication.class), 
                Matchers.any(Serializable.class), 
                Matchers.anyString(), 
                Matchers.anyString()))
                .thenReturn(true);
        tag.setTargetId(1L);
        tag.setTargetType("BRANCH");
        tag.setPermission("PERMISSION");
        
        assertEquals(tag.doStartTag(), Tag.SKIP_BODY);
    }
    
}
