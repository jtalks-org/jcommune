package org.jtalks.jcommune.service;

import org.springframework.security.core.context.SecurityContext;

/**
 * Abstract away SecurityContextHolder singleton.
 *
 * @author Kirill Afonin
 */
public interface SecurityContextFacade {

    /**
     * @return <code>SecurityContext</code>
     */
    SecurityContext getContext();

    /**
     * @param securityContext <code>SecurityContext</code> to set.
     */
    void setContext(SecurityContext securityContext);
}
