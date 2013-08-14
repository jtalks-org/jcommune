package org.jtalks.jcommune.web.interceptors;

import org.jtalks.jcommune.model.entity.JCUser;
import org.jtalks.jcommune.model.entity.Language;
import org.jtalks.jcommune.service.UserService;
import org.springframework.web.servlet.i18n.LocaleChangeInterceptor;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Locale;

public class LocaleInterceptor extends LocaleChangeInterceptor {
    UserService userService;

    public LocaleInterceptor(UserService userService) {
        this.userService=userService;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws ServletException {
        super.preHandle(request, response, handler);
        JCUser jcuser = userService.getCurrentUser();
        if(!jcuser.isAnonymous()){
            try{
            jcuser.setLanguage(Language.valueOf(request.getParameter(getParamName())));
            }catch (Exception e){
                throw new ServletException("Languale save failed.");
            }
        }
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        super.afterCompletion(request, response, handler, ex);
        JCUser jcuser = userService.getCurrentUser();
        Language currentLanguage = jcuser.getLanguage();
        Language expectedLanguage =(Language)Language.valueOf(response.getLocale().toString());
        try{
        expectedLanguage =Language.valueOf((String)request.getAttribute(getParamName()));
        }catch (NullPointerException npe){
            expectedLanguage = null;
        }

        try{
        if(!currentLanguage.equals(expectedLanguage) && !jcuser.isAnonymous()){
            jcuser.setLanguage(expectedLanguage);
        }
        }catch (Exception e){
            throw new ServletException("SETTING LANGUAGE FAILED");
        }
    }
}

