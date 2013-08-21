package org.jtalks.jcommune.web.interceptors;

import org.jtalks.jcommune.model.entity.JCUser;
import org.jtalks.jcommune.model.entity.Language;
import org.jtalks.jcommune.service.UserService;
import org.jtalks.jcommune.web.dto.EditUserProfileDto;
import org.springframework.web.servlet.i18n.CookieLocaleResolver;
import org.springframework.web.servlet.i18n.LocaleChangeInterceptor;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Locale;

public class LocaleInterceptor extends LocaleChangeInterceptor {
    UserService userService;
    CookieLocaleResolver localeResolver;

    public LocaleInterceptor(UserService userService, CookieLocaleResolver localeResolver) {
        this.userService=userService;
        this.localeResolver = localeResolver;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws ServletException {
        super.preHandle(request, response, handler);
        JCUser jcuser=null;
        try{
            jcuser = userService.getCurrentUser();
        }catch(NullPointerException npe){
            return true;
        }

        // get user language and set in to LocaleResolver
        if(!jcuser.isAnonymous() && request.getParameter(getParamName())==null){

            if(request.getParameter(getParamName())!=null && (!jcuser.getLanguage().getLocale().toString().equals(request.getParameter(getParamName())))){
                return true;
            }

            String newLocale = jcuser.getLanguage().getLocale().toString();
            if (newLocale != null) {
                if (localeResolver == null) {
                    throw new IllegalStateException("No LocaleResolver found: not in a DispatcherServlet request?");
                }
                localeResolver.setLocale(request, response, jcuser.getLanguage().getLocale());
            }
            return true;
        }

        // save language into db, on change
        if(!jcuser.isAnonymous()){
            Language languageFromRequest = Language.byLocale(new Locale(request.getParameter(getParamName())));
            try{
                jcuser.setLanguage(languageFromRequest);
                userService.saveEditedUserProfile(jcuser.getId(), new EditUserProfileDto(jcuser).getUserInfoContainer());
            }catch (Exception e){
                throw new ServletException("Language save failed.");
            }
        }
        return true;
    }

}

