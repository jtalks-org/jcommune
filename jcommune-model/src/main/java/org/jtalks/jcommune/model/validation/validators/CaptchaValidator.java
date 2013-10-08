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
package org.jtalks.jcommune.model.validation.validators;

import com.google.code.kaptcha.Constants;
import org.apache.commons.lang.StringUtils;
import org.jtalks.jcommune.model.validation.annotations.Captcha;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * Validates if captcha field is valid. For details refer to
 * {@link Captcha}
 */
public class CaptchaValidator implements ConstraintValidator<Captcha, String> {

    /**
     * {@inheritDoc}
     */
    @Override
    public void initialize(Captcha constraintAnnotation) {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (StringUtils.isEmpty(value)) {
            return false;
        }
        RequestAttributes attributes = RequestContextHolder.currentRequestAttributes();
        HttpServletRequest httpServletRequest = ((ServletRequestAttributes) attributes).getRequest();
        String sessionCaptchaId = (String) httpServletRequest.getSession().getAttribute(Constants.KAPTCHA_SESSION_KEY);
        return StringUtils.equals(sessionCaptchaId, value);
    }
}
