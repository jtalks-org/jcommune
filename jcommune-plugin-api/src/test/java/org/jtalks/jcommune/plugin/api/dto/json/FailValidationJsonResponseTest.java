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

import static org.testng.AssertJUnit.assertEquals;
import java.util.ArrayList;
import java.util.List;

import org.jtalks.jcommune.plugin.api.web.dto.json.FailValidationJsonResponse;
import org.jtalks.jcommune.plugin.api.web.dto.json.JsonResponseReason;
import org.jtalks.jcommune.plugin.api.web.dto.json.JsonResponseStatus;
import org.jtalks.jcommune.plugin.api.web.dto.json.ValidationError;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.testng.annotations.Test;

public class FailValidationJsonResponseTest {

    private static final String OBJECT_NAME = "object";
    private static final String OBJECT_MESSAGE = "message1";
    private static final String FIELD_MESSAGE = "message2";
    private static final String FIELD = "field";

    @SuppressWarnings("unchecked")
    @Test
    public void testConstructorWithBindingResult() {
        List<ObjectError> allErrors = new ArrayList<>();
        allErrors.add(new ObjectError(OBJECT_NAME, OBJECT_MESSAGE));
        allErrors.add(new FieldError(OBJECT_NAME, FIELD, FIELD_MESSAGE));
        
        FailValidationJsonResponse response = new FailValidationJsonResponse(allErrors);
        
        List<ValidationError> result = (List<ValidationError>) response.getResult();
        
        assertEquals(response.getStatus(), JsonResponseStatus.FAIL);
        assertEquals(response.getReason(), JsonResponseReason.VALIDATION);
        assertEquals(result.size(), 2);
        assertEquals(result.get(0).getField(), null);
        assertEquals(result.get(0).getMessage(), OBJECT_MESSAGE);
        assertEquals(result.get(1).getField(), FIELD);
        assertEquals(result.get(1).getMessage(), FIELD_MESSAGE);
    }
}
