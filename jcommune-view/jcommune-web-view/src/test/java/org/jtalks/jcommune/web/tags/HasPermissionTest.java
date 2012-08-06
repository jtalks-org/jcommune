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

import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.testng.Assert.assertEquals;

import java.io.Serializable;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.Tag;

import org.jtalks.common.service.security.SecurityContextFacade;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.web.context.WebApplicationContext;
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
    
    @Mock
    private WebApplicationContext ctx;
    
    @Mock
    private PermissionEvaluator aclEvaluator;
    
    @Mock
    private SecurityContextFacade securityContextFacade;
    
    @Mock
    private SecurityContext securityContext;
    
    @BeforeMethod
    public void setUp() {
        initMocks(this);
        when(securityContextFacade.getContext()).thenReturn(securityContext);
        
        tag = new HasPermission();
        
        tag.setApplicationContext(ctx);
        tag.setAclEvaluator(aclEvaluator);
        tag.setSecurityContextFacade(securityContextFacade);
        
    }
    
    @Test
    public void testTargetIdNotSpecified() throws JspException {
        tag.setTargetType(TARGET_TYPE);
        tag.setPermission(PERMISSION);
        assertEquals(tag.doStartTag(), Tag.SKIP_BODY);
    }
    
    @Test
    public void testTargetTypeNotSpecified() throws JspException {
        tag.setTargetId(TARGET_ID);
        tag.setPermission(PERMISSION);
        assertEquals(tag.doStartTag(), Tag.SKIP_BODY);
    }
    
    @Test
    public void testTargetTypeBlank() throws JspException {
        tag.setTargetId(TARGET_ID);
        tag.setTargetType("");
        tag.setPermission(PERMISSION);
        assertEquals(tag.doStartTag(), Tag.SKIP_BODY);
    }
    
    @Test
    public void testPermissionNotSpecified() throws JspException {
        tag.setTargetId(1L);
        tag.setTargetType("BRANCH");
        assertEquals(tag.doStartTag(), Tag.SKIP_BODY);
    }
    
    @Test
    public void testPermissionBlank() throws JspException {
        tag.setTargetId(1L);
        tag.setTargetType("BRANCH");
        tag.setPermission("");
        assertEquals(tag.doStartTag(), Tag.SKIP_BODY);
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
    
}
