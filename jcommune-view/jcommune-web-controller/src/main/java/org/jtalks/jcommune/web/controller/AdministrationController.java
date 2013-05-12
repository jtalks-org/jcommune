package org.jtalks.jcommune.web.controller;

import org.jtalks.jcommune.service.ComponentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;

/**
 * @author Andrei Alikov
 * Controller for processing forum administration related requests
 */
@Controller
public class AdministrationController {
    private static final String ADMIN_ATTRIBUTE_NAME = "adminMode";

    private ComponentService componentService;

    /**
     * Creates instance of the service
     * @param componentService service to work with the forum component
     */
    @Autowired
    public AdministrationController(ComponentService componentService) {
        this.componentService = componentService;
    }

    /**
     * Change mode to Administrator mode in which
     * @param request Client request
     * @return redirect back to previous page
     */
    @RequestMapping(value = "/admin/enter", method = RequestMethod.GET)
    @PreAuthorize("hasPermission(#componentService.componentOfForum.id, 'COMPONENT', 'GeneralPermission.ADMIN')")
    public String enterAdministrationMode(HttpServletRequest request) {
        request.getSession().setAttribute(ADMIN_ATTRIBUTE_NAME, true);

        return getRedirectToPrevPage(request);
    }

    /**
     * Return back from Administrator mode to Normal mode
     * @param request Client request
     * @return redirect back to previous page
     */
    @RequestMapping(value = "/admin/exit", method = RequestMethod.GET)
    public String exitAdministrationMode(HttpServletRequest request) {
        request.getSession().removeAttribute(ADMIN_ATTRIBUTE_NAME);

        return getRedirectToPrevPage(request);
    }

    /**
     * returns redirect string to previous page
     * @param request Client HTTP request
     */
    private String getRedirectToPrevPage(HttpServletRequest request) {
        return "redirect:" + request.getHeader("Referer");
    }
}
