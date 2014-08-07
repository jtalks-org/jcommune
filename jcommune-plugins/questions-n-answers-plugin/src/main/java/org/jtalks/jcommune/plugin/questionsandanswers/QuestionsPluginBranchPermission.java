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
package org.jtalks.jcommune.plugin.questionsandanswers;

import com.google.common.collect.Lists;
import org.jtalks.common.model.permissions.JtalksPermission;
import ru.javatalks.utils.general.Assert;

import javax.annotation.Nonnull;
import java.util.List;

/**
 * These are the permissions that relate only to branches and sections.
 * <p/>
 * Please follow the binary numeration in permissions
 * and do not create numbers more then 1 in comparing to existing ones
 * (i.e. yoo have 010, use 011, not 10010)
 *
 * @author Evgeniy Myslovets
 */
public enum QuestionsPluginBranchPermission implements JtalksPermission {

    /**
     * The ability of users to create question type topics
     */
    CREATE_QUESTIONS("11111", "CREATE_QUESTIONS");

    private final String name;
    private final int mask;

    /**
     * Constructs the whole object without symbol.
     *
     * @param mask a bit mask that represents the permission, can be negative only for restrictions (look at the class
     *             description). The integer representation of it is saved to the ACL tables of Spring Security.
     * @param name a textual representation of the permission (usually the same as the constant name), though the
     *             restriction usually starts with the 'RESTRICTION_' word
     */
    QuestionsPluginBranchPermission(int mask, @Nonnull String name) {
        this.mask = mask;
        throwIfNameNotValid(name);
        this.name = name;
    }

    /**
     * Takes a string bit mask.
     *
     * @param mask a bit mask that represents the permission. It's parsed into integer and saved into the ACL tables of
     *             Spring Security.
     * @param name a textual representation of the permission (usually the same as the constant name)
     * @throws NumberFormatException look at {@link Integer#parseInt(String, int)} for details on this as this method is
     *                               used underneath
     * @see QuestionsPluginBranchPermission#QuestionsPluginBranchPermission(int, String)
     * @see org.springframework.security.acls.domain.BasePermission
     */
    QuestionsPluginBranchPermission(@Nonnull String mask, @Nonnull String name) {
        throwIfNameNotValid(name);
        this.mask = Integer.parseInt(mask, 2);
        this.name = name;
    }

    /**
     * Gets the human readable textual representation of the restriction (usually the same as the constant name).
     *
     * @return the human readable textual representation of the restriction (usually the same as the constant name)
     */
    @Override
    public String getName() {
        return name;
    }

    /**
     * Throws exception if permission name is not valid.
     *
     * @param name a textual representation of the permission (usually the same as the constant name)
     */
    private void throwIfNameNotValid(String name) {
        Assert.throwIfNull(name, "The name can't be null");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getMask() {
        return mask;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getPattern() {
        return null;
    }

    /**
     * Finds permission by its int mask.
     *
     * @param mask a bit mask that represents the permission
     * @return permission with specified mask
     */
    public static QuestionsPluginBranchPermission findByMask(int mask) {
        for (QuestionsPluginBranchPermission nextPermission : values()) {
            if (mask == nextPermission.getMask()) {
                return nextPermission;
            }
        }
        return null;
    }

    /**
     * @return all questions-n-answers plugin branch permission
     */
    public static List<QuestionsPluginBranchPermission> getAllAsList() {
        return Lists.newArrayList(values());
    }
}
