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
package org.jtalks.jcommune.web.dto;

/**
 * Data transfer object for sending an information about error, that will be
 * displayed on the client side.
 * 
 * @author Anuar_Nurmakanov
 */
public class OperationResultDto {
    private boolean success;
    private String message;
    
    /**
     * Constructs an instance with required fields.
     * 
     * @param success true, if the operation completed successfully,
     *                otherwise false
     * @param message message that briefly describes the result of operation
     */
    public OperationResultDto(boolean success, String message) {
        this.success = success;
        this.message = message;
    }

    /**
     * Returns the result of operation. True if the operation
     * completed successfully, otherwise false. 
     * 
     * @return the result of operation
     */
    public boolean isSuccess() {
        return success;
    }

    /**
     * Sets the result of operation. True if the operation
     * completed successfully, otherwise false.
     * 
     * @param success the result of operation
     */
    public void setSuccess(boolean success) {
        this.success = success;
    }

    /**
     * Gets message that briefly describes the result of operation.
     * 
     * @return message that briefly describes the result of operation
     */
    public String getMessage() {
        return message;
    }

    /**
     * Sets message that briefly describes the result of operation.
     * 
     * @param message  message that briefly describes the result of operation
     */
    public void setMessage(String message) {
        this.message = message;
    }
}
