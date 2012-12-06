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
package org.jtalks.jcommune.web.dto.json;

/**
 * AJAX response class to send fail JSON response
 * 
 * @author Vyacheslav Mishcheryakov
 *
 */
public class FailJsonResponse extends JsonResponse {
    
    private String reason;

    public FailJsonResponse(String reason, Object result) {
        this(reason);
        setResult(result);
    }
    
    public FailJsonResponse(String reason) {
        super(JsonResponse.RESPONSE_STATUS_FAIL);
        this.reason = reason;
    }



    /**
     * @return the reason
     */
    public String getReason() {
        return reason;
    }

    /**
     * @param reason the reason to set
     */
    public void setReason(String reason) {
        this.reason = reason;
    }
    
}
