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

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.jtalks.jcommune.model.entity.PollOption;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Alexandre Teterin
 *         Date: 14.04.12
 */

public class PollUtil {

    public static DateTime parseDate(String date, String format) {
        DateTime result;
        try {
            if (date == null) {
                result = null;
            } else {
                result = DateTimeFormat.forPattern(format).parseDateTime(date);
            }
        } catch (IllegalArgumentException e) {
            result = new DateTime(0);
        }

        return result;
    }

    public static List<PollOption> parseOptions(String pollOptions) throws IOException {
        BufferedReader reader = new BufferedReader(new StringReader(pollOptions));
        String line;
        List<PollOption> result = new ArrayList<PollOption>();
        while ((line = reader.readLine()) != null) {

            if (!line.equals("")) {
                PollOption option = new PollOption(line);
                result.add(option);
            }
        }
        return result;
    }
}
