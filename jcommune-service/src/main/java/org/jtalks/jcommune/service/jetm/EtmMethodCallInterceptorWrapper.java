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
package org.jtalks.jcommune.service.jetm;

import etm.contrib.aop.aopalliance.EtmMethodCallInterceptor;
import etm.core.monitor.EtmMonitor;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.aop.support.AopUtils;

import java.lang.reflect.Method;

/**
 * Wrapper for JETM Method Call Interceptor.
 *
 * We should use it because JETM generates proxy name by default. See parent class.
 *
 * Current class has override method calculateName and overloading calculateShortName.
 * We can't override calculateShortName only, because we need type Object in input params instead of Class
 */
public class EtmMethodCallInterceptorWrapper extends EtmMethodCallInterceptor {

    public EtmMethodCallInterceptorWrapper(EtmMonitor aEtmMonitor) {
        super(aEtmMonitor);
    }

    /**
     * Calculate EtmPoint name based on the method invocation.
     *
     * @param aMethodInvocation The method invocation.
     * @return The name of the EtmPoint.
     */
    @Override
    protected String calculateName(MethodInvocation aMethodInvocation) {

        try {

            Object target = aMethodInvocation.getThis();
            Method method = aMethodInvocation.getMethod();

            return calculateShortName(target) + "::" + method.getName();

        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Get target class name
     *
     * @param target
     *
     * @return String
     *
     * @throws Exception
     */
    protected String calculateShortName(Object target) throws Exception {
        return AopUtils.getTargetClass(target).getSimpleName();
    }

}
