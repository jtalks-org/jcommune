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
package org.jtalks.jcommune.test.utils;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.jtalks.jcommune.plugin.api.web.dto.json.JsonResponse;

import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Mikhail Stryzhonok
 */
public class JsonResponseUtils {
    private static  ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    public static String jsonResponseToString(JsonResponse response) {
        StringWriter writer = new StringWriter();
        try {
            OBJECT_MAPPER.writeValue(writer, response);
        } finally {
            return writer.toString();
        }
    }

    public static List<String> fetchErrorMessagesFromJsonString(String jsonString) {
        List<String> result = new ArrayList<>();
        JsonNode rootNode;
        try {
            rootNode = OBJECT_MAPPER.readValue(jsonString, JsonNode.class);
        } catch (IOException e) {
            return result;
        }
        JsonNode resultNode = rootNode.get("result");
        if (resultNode.isArray()) {
            for (JsonNode itemNode : resultNode) {
                result.add(itemNode.get("defaultMessage").getTextValue());
            }
        }
        return result;
    }
}
