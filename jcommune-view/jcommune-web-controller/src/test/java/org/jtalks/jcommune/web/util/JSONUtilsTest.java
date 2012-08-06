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
package org.jtalks.jcommune.web.util;

import java.io.IOException;
import java.util.Collections;
import java.util.Map;

import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.map.ObjectMapper;
import org.testng.Assert;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 * 
 * @author Anuar Nurmakanov
 *
 */
public class JSONUtilsTest {
    private JSONUtils jsonUtils;
    
    @BeforeTest
    public void init() {
        JsonFactory jsonFactory = new JsonFactory();
        ObjectMapper objectMapper = new ObjectMapper();
        this.jsonUtils = new JSONUtils(jsonFactory, objectMapper);
    }
    
    @Test(dataProvider = "prepareJSONStringDataProvider")
    public void testPrepareJSONString(
            Map<String, String> willBeConverted, String expectedResultOfConvert) throws IOException {
        String actualResultOfConvert = jsonUtils.prepareJSONString(willBeConverted);
        
        Assert.assertEquals(actualResultOfConvert, expectedResultOfConvert);
    }
    
    @DataProvider(name = "prepareJSONStringDataProvider")
    public Object[][] prepareJSONStringDataProvider() {
        return new Object[][] {
                {Collections.singletonMap("forum", "larks"), "{\"forum\":\"larks\"}"},
                {Collections.singletonMap("admin", "poulpe"), "{\"admin\":\"poulpe\"}"},
        };
    }
}
