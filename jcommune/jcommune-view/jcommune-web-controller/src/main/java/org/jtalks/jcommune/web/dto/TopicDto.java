/**

 JTalks for uniting people
 Copyright (C) 2011  JavaTalks Team

 This library is free software; you can redistribute it and/or
 modify it under the terms of the GNU Lesser General Public
 License as published by the Free Software Foundation; either
 version 2.1 of the License, or (at your option) any later version.

 This library is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 Lesser General Public License for more details.

 You should have received a copy of the GNU Lesser General Public
 License along with this library; if not, write to the Free Software
 Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 Also add information on how to contact you by electronic and paper mail.
 */
package org.jtalks.jcommune.web.dto;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;


public class TopicDto {

    @NotNull(message = "Length should be bigger then 5 and less then 255")
    @Size(min = 5, max = 255)
    private String topicName;

    @NotNull(message = "Length should be bigger then 3 and less then 50")
    @Size(min = 2, max = 1000)
    private String bodyText;

    public String getTopicName() {
        return topicName;
    }

    public void setTopicName(String topicName) {
        this.topicName = topicName;
    }

    public String getBodyText() {
        return bodyText;
    }

    public void setBodyText(String bodyText) {
        this.bodyText = bodyText;
    }

}