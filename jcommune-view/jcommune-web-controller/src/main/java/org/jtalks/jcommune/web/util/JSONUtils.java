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
 * Help class that wraps all logic of working with JSON conversion. 
 * 
 * @author Anuar_Nurmakanov
 */
public class JSONUtils {
    private JsonFactory jsonFactory;
    private ObjectMapper objectMapper;
    
    /**
     * Constructs an instance with required fields.
     * 
     * @param jsonFactory used to configure writer (aka generator, {@link JsonGenerator})
     *                    instances.
     * @param objectMapper provides functionality for converting between Java objects
     *                     and matching JSON constructs
     */
    public JSONUtils(JsonFactory jsonFactory, ObjectMapper objectMapper) {
        this.jsonFactory = jsonFactory;
        this.objectMapper = objectMapper;
    }

    /**
     * Used for prepare JSON string from Map<String, String>
     *
     * @param value a map of values
     * @return JSON string
     * @throws IOException defined in the JsonFactory implementation, caller must implement exception processing
     */
    public String prepareJSONString(Map<String, String> value) throws IOException {
        StringWriter stringWriter = new StringWriter();
        JsonGenerator jgen = jsonFactory.createJsonGenerator(stringWriter);
        objectMapper.writeValue(jgen, value);
        return stringWriter.toString();
    }
}
