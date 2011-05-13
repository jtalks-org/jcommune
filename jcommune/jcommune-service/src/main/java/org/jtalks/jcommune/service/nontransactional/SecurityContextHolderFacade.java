package org.jtalks.jcommune.service.nontransactional;

import org.jtalks.jcommune.service.SecurityContextFacade;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * Default implementation of {@link SecurityContextFacade}
 *
 * @author Kirill Afonin
 */
public class SecurityContextHolderFacade implements SecurityContextFacade {

    /**
     * @return <code>SecurityContext</code> from <code>SecurityContextHolder</code>
     */
    public SecurityContext getContext() {
        return SecurityContextHolder.getContext();
    }

    /**
     * Set <code>SecurityContext</code> to  <code>SecurityContextHolder</code>
     * @param securityContext <code>SecurityContext</code> to set.
     */
    public void setContext(SecurityContext securityContext) {
        SecurityContextHolder.setContext(securityContext);
    }
}
