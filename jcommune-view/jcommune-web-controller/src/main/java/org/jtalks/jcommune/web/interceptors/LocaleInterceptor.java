package org.jtalks.jcommune.web.interceptors;

import org.jtalks.jcommune.model.entity.Language;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;
import org.springframework.web.servlet.i18n.LocaleChangeInterceptor;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class LocaleInterceptor extends HandlerInterceptorAdapter {

    private LocaleChangeInterceptor springInterceptor;

    public LocaleInterceptor(LocaleChangeInterceptor springInterceptor) {
        this.springInterceptor = springInterceptor;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws ServletException {
        String settedLocale = request.getParameter(this.springInterceptor.getParamName());
        if (settedLocale != null) {
            Boolean isValid = false;
            for (Language lang : Language.values()) {
                if (lang.getLanguageCode().equals(settedLocale)) {
                    isValid = true;
                    break;
                }
            }
            if (!isValid) return true;
        }
        return this.springInterceptor.preHandle(request, response, handler);
    }

}
