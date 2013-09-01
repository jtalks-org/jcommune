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
