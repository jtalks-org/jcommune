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
package org.jtalks.jcommune.web.validation;

import org.jtalks.jcommune.model.dao.ValidatorDao;
import org.springframework.beans.factory.annotation.Autowired;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * Chcks string value for existence in a database.
 * @see Exists
 *
 * @author Evgeniy Naumenko
 */
public class ExistenceValidator implements ConstraintValidator<Exists, Object> {

    private String hql;

    private ValidatorDao<String> dao;

    /**
     * @param dao session factory fro database requests
     */
    @Autowired
    public ExistenceValidator(ValidatorDao<String> dao) {
        this.dao = dao;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void initialize(Exists annotation) {
        this.hql = annotation.hql();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        return !dao.isResultSetEmpty(hql, value.toString());
    }
}
