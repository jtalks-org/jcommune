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

import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.Tag;
import javax.servlet.jsp.tagext.TagSupport;

import org.jtalks.common.security.SecurityContextFacade;
import org.springframework.context.ApplicationContext;
import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.core.Authentication;
import org.springframework.web.context.support.WebApplicationContextUtils;

/**
 * An implementation of {@link Tag} that allows its body through if some 
 * authorizations are granted to the request's object.
 * 
 * @author Vyachelav Mishcheryakov
 */
@SuppressWarnings("serial")
public class HasPermission extends TagSupport {

    private ApplicationContext applicationContext;
    private PermissionEvaluator aclEvaluator;
    private SecurityContextFacade securityContextFacade;
    
    private Long targetId;
    private String targetType;
    private String permission;
    

    /**
     * @return the targetId
     */
    public Long getTargetId() {
        return targetId;
    }

    /**
     * @param targetId the targetId to set
     */
    public void setTargetId(Long targetId) {
        this.targetId = targetId;
    }

    /**
     * @return the targetType
     */
    public String getTargetType() {
        return targetType;
    }

    /**
     * @param targetType the targetType to set
     */
    public void setTargetType(String targetType) {
        this.targetType = targetType;
    }

    /**
     * @return the permission
     */
    public String getPermission() {
        return permission;
    }

    /**
     * @param permission the permission to set
     */
    public void setPermission(String permission) {
        this.permission = permission;
    }

    public int doStartTag() throws JspException {
        if (targetId == null 
                || targetType == null || "".equals(targetType) 
                || permission == null || "".equals(permission)) {
            return Tag.SKIP_BODY;
        }

        initializeIfRequired();
        
        Authentication authentication = securityContextFacade.getContext().getAuthentication();
        
        if (aclEvaluator.hasPermission(authentication, targetId, targetType, permission)) {
            return Tag.EVAL_BODY_INCLUDE;
        }

        return Tag.SKIP_BODY;
    }

   /**
    * Allows test cases to override where application context obtained from.
    *
    * @param pageContext so the <code>ServletContext</code> can be accessed as required by Spring's
    *        <code>WebApplicationContextUtils</code>
    *
    * @return the Spring application context (never <code>null</code>)
    */
   protected ApplicationContext getContext(PageContext pageContext) {
       ServletContext servletContext = pageContext.getServletContext();

       return WebApplicationContextUtils.getRequiredWebApplicationContext(
               servletContext);
   }
       
       

   private void initializeIfRequired() throws JspException {
       if (applicationContext != null) {
           return;
       }

       this.applicationContext = getContext(pageContext);

       aclEvaluator = getBeanOfType(PermissionEvaluator.class);
       
       securityContextFacade = getBeanOfType(SecurityContextFacade.class);
       
       if (securityContextFacade == null) {
           securityContextFacade = new SecurityContextFacade();
       }
   }

   private <T> T getBeanOfType(Class<T> type) throws JspException {
       Map<String, T> map = applicationContext.getBeansOfType(type);

       for (ApplicationContext context = applicationContext.getParent();
           context != null; context = context.getParent()) {
           map.putAll(context.getBeansOfType(type));
       }

       if (map.size() == 0) {
           return null;
       } else if (map.size() == 1) {
           return map.values().iterator().next();
       }

       throw new JspException("Found incorrect number of " + type.getSimpleName() +" instances in "
                   + "application context - you must have only have one!");
   }

}
