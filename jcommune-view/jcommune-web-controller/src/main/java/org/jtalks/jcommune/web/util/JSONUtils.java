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
import java.io.StringWriter;
import java.util.Map;

import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.map.ObjectMapper;

/**
 * 
 * @author Anuar_Nurmakanov
 *
 */
public class JSONUtils {
    
    /**
     * Used for prepare JSON string from Map<String, String>
     *
     * @param value a map of values
     * @return JSON string
     * @throws IOException defined in the JsonFactory implementation, caller must implement exception processing
     */
    public String prepareJSONString(Map<String, String> value) throws IOException {
        JsonFactory jsonFactory = new JsonFactory();
        StringWriter stringWriter = new StringWriter();
        ObjectMapper objectMapper = new ObjectMapper();
        JsonGenerator jgen = jsonFactory.createJsonGenerator(stringWriter);
        objectMapper.writeValue(jgen, value);

        return stringWriter.toString();
    }
}
