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

import javax.servlet.ServletContext;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.Tag;
import javax.servlet.jsp.tagext.TagSupport;

import org.jtalks.common.service.security.SecurityContextFacade;
import org.springframework.context.ApplicationContext;
import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.core.Authentication;
import org.springframework.web.context.support.WebApplicationContextUtils;

/**
 * An implementation of {@link Tag} that allows its body through if some 
 * authorizations are granted to the request's object.
 * Typical use case: <br>
 *  <code> 
 *  &lt;jtalks:haspermission targetId="${topic.branch.id}" targetType="BRANCH"<br>
 *      &nbsp;&nbsp;&nbsp;&nbsp;permission="BranchPermission.DELETE_OWN_POSTS"&gt;
 *      <br>&nbsp;&nbsp;&nbsp;&nbsp;Some jsp code<br>
 *  &lt;/jtalks:haspermission&gt;
 *  </code>
 * 
 * @author Vyacheslav Mishcheryakov
 */
@SuppressWarnings("serial")
public class HasPermission extends TagSupport {

    private transient ApplicationContext applicationContext;
    private transient PermissionEvaluator aclEvaluator;
    private transient SecurityContextFacade securityContextFacade;
    
    /** Identifier of object to check permission */
    private Long targetId;
    
    /** Type of object (e.g. BRANCH) */
    private String targetType;
    
    /** Full qualified name of permission. See 
     * {@link org.jtalks.common.model.permissions.JtalksPermission} and its
     * subclasses
     */
    private String permission;
    
    /**
     * @param targetId the ID of object to check permission
     */
    public void setTargetId(Long targetId) {
        this.targetId = targetId;
    }

    /**
     * @param targetType type of targeted object
     */
    public void setTargetType(String targetType) {
        this.targetType = targetType;
    }

    /**
     * @param permission Full qualified name of permission
     */
    public void setPermission(String permission) {
        this.permission = permission;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int doStartTag() throws JspException {
        if (isAnyParameterMissed()) {
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
     * Checks if any required tag parameter is missed or blank
     * @return true if any required parameter is not specified.
     */
    private boolean isAnyParameterMissed() {
        boolean isTargetIdMissed = targetId == null;
        boolean isTargetTypeMissed = targetType == null || "".equals(targetType);
        boolean isPermissionMissed = permission == null || "".equals(permission);
        return  isTargetIdMissed || isTargetTypeMissed || isPermissionMissed;
    }

   /**
    * Obtain Spring's application context, since we can't use DI in tags. 
    *
    * @param servletContext <code>ServletContext</code> as required by Spring's
    *        <code>WebApplicationContextUtils</code>
    *
    * @return the Spring application context (never <code>null</code>)
    */
    private ApplicationContext getContext(ServletContext servletContext) {
        return WebApplicationContextUtils.getRequiredWebApplicationContext(
               servletContext);
    }
   
    /**
     * Sets Spring's application context. Created to be used in tests.
     * @param ctx - context
     */
    public void setApplicationContext(ApplicationContext ctx) {
        applicationContext = ctx;
    }
       
    /**
     * @param aclEvaluator the aclEvaluator to set
     */
    public void setAclEvaluator(PermissionEvaluator aclEvaluator) {
        this.aclEvaluator = aclEvaluator;
    }

    /**
     * @param securityContextFacade the securityContextFacade to set
     */
    public void setSecurityContextFacade(SecurityContextFacade securityContextFacade) {
        this.securityContextFacade = securityContextFacade;
    }

    /** 
     * Fetches all required beans from Spring context if needed
     * @throws JspException 
     *
     */
    private void initializeIfRequired() {
        if (applicationContext == null) {
            this.applicationContext = getContext(pageContext.getServletContext());
        
            aclEvaluator = applicationContext.getBean(PermissionEvaluator.class);
           
            securityContextFacade = applicationContext.getBean(
                    SecurityContextFacade.class);           
            if (securityContextFacade == null) {
                securityContextFacade = new org.jtalks.jcommune.service.security.SecurityContextFacade();
            }
        }
    }
    
}
