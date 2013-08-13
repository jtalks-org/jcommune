package org.jtalks.jcommune.web.interceptors;

import org.jtalks.jcommune.model.entity.JCUser;
import org.jtalks.jcommune.model.entity.Language;
import org.jtalks.jcommune.service.UserService;
import org.springframework.web.servlet.i18n.LocaleChangeInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class LocaleInterceptor extends LocaleChangeInterceptor {
    private String paramName = DEFAULT_PARAM_NAME;
    UserService userService;

    public LocaleInterceptor(UserService userService) {
        super();
        super.setParamName(paramName);
        this.userService=userService;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        super.afterCompletion(request, response, handler, ex);
        JCUser jcuser = userService.getCurrentUser();
        if(!jcuser.isAnonymous()){
            try{
            jcuser.setLanguage(Language.valueOf(request.getParameter(paramName)));
            }catch (Exception e){
                throw new Exception("Languale save failed.");
            }
        }
    }
}

