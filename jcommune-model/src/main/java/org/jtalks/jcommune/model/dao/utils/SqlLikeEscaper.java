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
package org.jtalks.jcommune.model.dao.utils;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

/**
 * Escapes symbols like {@code %, _, !} which are not being escaped by Prepared Statements in <i>like</i> statements.
 * For instance if we have a query {@code from Entity where name like ?}, then if we pass % sign, it will return all the
 * records from table even though we were searching only for a name which contains % symbol. In such situations you can
 * use this utility to escape those symbols if you need this.
 *
 * @author Anton Kolyaev
 */
public final class SqlLikeEscaper {
    private static final List<String> CONTROL_SYMBOLS = new ArrayList<String>();

    static {
        CONTROL_SYMBOLS.add("|");//escape symbol
        CONTROL_SYMBOLS.add("%");//anything
        CONTROL_SYMBOLS.add("_");//anyone
        CONTROL_SYMBOLS.add("!");//not
        CONTROL_SYMBOLS.add("^");//not
        CONTROL_SYMBOLS.add("[");//array_start
        CONTROL_SYMBOLS.add("]");//array_finish
    }

    /**
     * Just a utility, should not been instantiated.
     */
    private SqlLikeEscaper() {
    }

    /**
     * Escapes control characters for SQL query. Will do nothing if null or empty string was passed.
     *
     * @param toEscape string to be escaped
     * @return the escaped version of the specified string
     */
    public static String escapeControlCharacters(@Nullable String toEscape) {
        if(toEscape == null || toEscape.isEmpty()){
            return toEscape;
        }
        for (String controlSymbol : CONTROL_SYMBOLS) {
            toEscape = toEscape.replace(controlSymbol, "|" + controlSymbol);
        }
        return toEscape;
    }
}
