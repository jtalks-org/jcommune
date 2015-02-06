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
package org.jtalks.jcommune.plugin.api.dto.json;

import static org.testng.AssertJUnit.assertNull;
import static org.testng.AssertJUnit.assertEquals;

import org.jtalks.jcommune.plugin.api.web.dto.json.FailJsonResponse;
import org.jtalks.jcommune.plugin.api.web.dto.json.JsonResponseReason;
import org.jtalks.jcommune.plugin.api.web.dto.json.JsonResponseStatus;
import org.testng.annotations.Test;

public class FailJsonResponseTest {
    
    private static final String RESULT = "result";
    
    @Test
    public void testConstructorWithReason() {
        FailJsonResponse response = new FailJsonResponse(JsonResponseReason.VALIDATION);
        
        assertEquals(response.getStatus(), JsonResponseStatus.FAIL);
        assertEquals(response.getReason(), JsonResponseReason.VALIDATION);
        assertNull(response.getResult());
    }
    
    @Test
    public void testConstructorWithReasonAndResult() {
        FailJsonResponse response = new FailJsonResponse(JsonResponseReason.VALIDATION, RESULT);
        
        assertEquals(response.getStatus(), JsonResponseStatus.FAIL);
        assertEquals(response.getReason(), JsonResponseReason.VALIDATION);
        assertEquals(response.getResult(), RESULT);
    }

}
