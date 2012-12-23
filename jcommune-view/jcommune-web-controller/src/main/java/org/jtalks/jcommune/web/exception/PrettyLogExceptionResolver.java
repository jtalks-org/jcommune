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
package org.jtalks.jcommune.web.exception;

import org.jtalks.jcommune.service.exceptions.NotFoundException;
import org.springframework.web.servlet.handler.SimpleMappingExceptionResolver;

import javax.servlet.http.HttpServletRequest;

/**
 * Catches all the exceptions thrown in controllers, logs them and directs to the error pages. The standard
 * {@link SimpleMappingExceptionResolver} wasn't sufficient because it was logging all exceptions as warnings while
 * some of them are expected and should be logged as INFO (such as 404 topic not found).
 *
 * @author Vitaliy Kravchenko
 */
public class PrettyLogExceptionResolver extends SimpleMappingExceptionResolver {
    /**
     * {@inheritDoc}
     */
    @Override
    protected void logException(Exception ex, HttpServletRequest request) {
        if (ex instanceof NotFoundException) {
            logger.info(ex.getMessage());
        } else {
            super.logException(ex, request);
        }
    }
}
