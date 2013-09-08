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

import org.jtalks.common.model.entity.Entity;
import org.jtalks.jcommune.model.dao.ValidatorDao;
import org.jtalks.jcommune.model.validation.annotations.Unique;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * Ensures uniqueness of the field value given using an hql query provided.
 *
 * @author Evgeniy Naumenko
 * @see Unique
 */
public class UniqueValidator implements ConstraintValidator<Unique, String>, ApplicationContextAware {

    private Class<? extends Entity> entity;
    private String field;
    private boolean ignoreCase;

    private ValidatorDao<String> dao;

    /**
     * {@inheritDoc}
     */
    @Override
    public void initialize(Unique annotation) {
        this.entity = annotation.entity();
        this.field = annotation.field();
        this.ignoreCase = annotation.ignoreCase();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        // null value should be processed by other constraint validators
        return value == null || dao.isResultSetEmpty(entity, field, value, ignoreCase);
    }

    /**
     * We use {@link ApplicationContextAware} because Spring won't inject parameters into constructor or setter if they
     * are not marked as @Autowired. Since we do not use autowiring here, this does not suits us.<br/>
     * If we really would want to use a normal injection, we'd need to override
     * {@link org.springframework.validation.beanvalidation.SpringConstraintValidatorFactory} so that it works with
     * non-autowired BeanFactory. This would result in overall complication of project configuration and therefore
     * it was decided that {@link ApplicationContextAware} is less painful for now.
     */
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        //noinspection unchecked
        dao = (ValidatorDao<String>) applicationContext.getBean(ValidatorDao.class);
    }
}
