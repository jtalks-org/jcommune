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
package org.jtalks.jcommune.plugin.api.web.dto.json;

import java.util.ArrayList;
import java.util.List;

import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;

/**
 * AJAX response class to send validation error JSON response
 * 
 * @author Vyacheslav Mishcheryakov
 *
 */
public class FailValidationJsonResponse extends FailJsonResponse {

    /**
     * Creates object and add validation error information from bindingResult
     * to JSON result field 
     * 
     * @param validationErrors list of validation errors. Not null
     */
    public FailValidationJsonResponse(List<ObjectError> validationErrors) {
        super(JsonResponseReason.VALIDATION);
        
        List<ValidationError> errors = new ArrayList<>();
        for (ObjectError error : validationErrors) {
            String field = null;
            if (error instanceof FieldError) {
                field = ((FieldError)error).getField();
            }
            errors.add(new ValidationError(field, error.getDefaultMessage()));
        }
        setResult(errors);
    }
    
    

}
