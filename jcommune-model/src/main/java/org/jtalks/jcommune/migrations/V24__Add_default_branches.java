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
package org.jtalks.jcommune.migrations;

import com.googlecode.flyway.core.migration.java.JavaMigration;
import org.springframework.jdbc.core.JdbcTemplate;


public class V24__Add_default_branches implements JavaMigration {
    /**
     * Executes this migration. The execution will automatically take place within a transaction, when the underlying
     * database supports it.
     *
     * @param jdbcTemplate The jdbcTemplate to use to execute statements.
     * @throws Exception when the migration failed.
     */
    @Override
    public void migrate(JdbcTemplate jdbcTemplate) throws Exception {
        Long branchCount = jdbcTemplate.queryForLong("select count(branch_id) from branches");

        if (branchCount == 0) {
            jdbcTemplate.execute("insert into SECTIONS (SECTION_ID, UUID, NAME, POSITION, DESCRIPTION) " +
                    "values(1,'1','Sample section', 1, 'Some description here')");
            jdbcTemplate.execute("insert into SECTIONS (SECTION_ID, UUID, NAME, POSITION, DESCRIPTION) " +
                    "values(2,'2','Another section', 2, 'Whatever else')");
            jdbcTemplate.execute("insert into BRANCHES (UUID, NAME, DESCRIPTION, POSITION, SECTION_ID) " +
                    "values('3', 'A cool branch', 'More information', 0, 1)");
            jdbcTemplate.execute("insert into BRANCHES (UUID, NAME, DESCRIPTION, POSITION, SECTION_ID) " +
                    "values('4', 'The second branch', 'More information', 1, 1)");
            jdbcTemplate.execute("insert into BRANCHES (UUID, NAME, DESCRIPTION, POSITION, SECTION_ID) " +
                    "values('5', 'One more branch', 'More information', 0, 2)");
            jdbcTemplate.execute("insert into BRANCHES (UUID, NAME, DESCRIPTION, POSITION, SECTION_ID) " +
                    "values('6', 'The last, but not least', 'More information', 1, 2)");
        }
    }

}
