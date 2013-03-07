--
-- Copyright (C) 2011  JTalks.org Team
-- This library is free software; you can redistribute it and/or
-- modify it under the terms of the GNU Lesser General Public
-- License as published by the Free Software Foundation; either
-- version 2.1 of the License, or (at your option) any later version.
-- This library is distributed in the hope that it will be useful,
-- but WITHOUT ANY WARRANTY; without even the implied warranty of
-- MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
-- Lesser General Public License for more details.
-- You should have received a copy of the GNU Lesser General Public
-- License along with this library; if not, write to the Free Software
-- Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
--

INSERT INTO `DEFAULT_PROPERTIES` (`UUID`, `BASE_COMPONENT_TYPE`, `NAME`, `VALUE`, `VALIDATION_RULE`) VALUES
	(UUID(), 'FORUM', 'cmp.sape_account_id', '', '');
INSERT INTO `DEFAULT_PROPERTIES` (`UUID`, `BASE_COMPONENT_TYPE`, `NAME`, `VALUE`, `VALIDATION_RULE`) VALUES
	(UUID(), 'FORUM', 'cmp.sape_on_main_page_enable', 'false', '/(true)|(false)/');
INSERT INTO `DEFAULT_PROPERTIES` (`UUID`, `BASE_COMPONENT_TYPE`, `NAME`, `VALUE`, `VALIDATION_RULE`) VALUES
	(UUID(), 'FORUM', 'cmp.sape_links_count', '6', '');
INSERT INTO `DEFAULT_PROPERTIES` (`UUID`, `BASE_COMPONENT_TYPE`, `NAME`, `VALUE`, `VALIDATION_RULE`) VALUES
	(UUID(), 'FORUM', 'cmp.sape_host_url', '', '');
INSERT INTO `DEFAULT_PROPERTIES` (`UUID`, `BASE_COMPONENT_TYPE`, `NAME`, `VALUE`, `VALIDATION_RULE`) VALUES
	(UUID(), 'FORUM', 'cmp.sape_timeout', '1000', '');
INSERT INTO `DEFAULT_PROPERTIES` (`UUID`, `BASE_COMPONENT_TYPE`, `NAME`, `VALUE`, `VALIDATION_RULE`) VALUES
	(UUID(), 'FORUM', 'cmp.sape_show_dummy_links', 'false', '/(true)|(false)/');
INSERT INTO `DEFAULT_PROPERTIES` (`UUID`, `BASE_COMPONENT_TYPE`, `NAME`, `VALUE`, `VALIDATION_RULE`) VALUES
	(UUID(), 'FORUM', 'cmp.sape_enable_service', 'false', '/(true)|(false)/');

INSERT INTO PROPERTIES
    (UUID, NAME, VALUE, CMP_ID, VALIDATION_RULE)
SELECT
    UUID(), dp.NAME, dp.VALUE, c.CMP_ID, dp.VALIDATION_RULE
FROM
    DEFAULT_PROPERTIES dp left join PROPERTIES p on dp.NAME = p.NAME
        join COMPONENTS c on c.COMPONENT_TYPE = dp.BASE_COMPONENT_TYPE
WHERE
    p.NAME is NULL;