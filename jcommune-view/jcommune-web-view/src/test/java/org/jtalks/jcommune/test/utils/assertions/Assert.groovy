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
package org.jtalks.jcommune.test.utils.assertions

import org.jtalks.jcommune.plugin.api.web.dto.json.JsonResponse
import org.jtalks.jcommune.plugin.api.web.dto.json.JsonResponseStatus
import org.jtalks.jcommune.test.utils.exceptions.ProcessingException
import org.jtalks.jcommune.test.utils.exceptions.ValidationException
import org.jtalks.jcommune.test.utils.exceptions.WrongResponseException
import org.springframework.test.web.servlet.MvcResult
import org.springframework.validation.BindingResult

import static org.jtalks.jcommune.test.utils.JsonResponseUtils.fetchErrorMessagesFromJsonString
import static org.jtalks.jcommune.test.utils.JsonResponseUtils.jsonResponseToString
import static org.jtalks.jcommune.test.utils.JsonResponseUtils.searchMessageInResult

/**
 * @author Mikhail Stryzhonok
 */
class Assert {

    static def assertJsonResponseResult(MvcResult result) {
        def response = result.response.contentAsString
        if(!jsonResponseToString(new JsonResponse(JsonResponseStatus.SUCCESS)).equals(response)
                && response != null) {
            def defaultErrorMessages = fetchErrorMessagesFromJsonString(response)
            if (defaultErrorMessages.empty) {
                def message = searchMessageInResult(response)
                if (message != null && !message.empty) {
                    throw new ProcessingException(message)
                }
                throw new WrongResponseException(jsonResponseToString(new JsonResponse(JsonResponseStatus.SUCCESS)),
                        response)
            } else {
                def e = new ValidationException()
                e.addAllDefaultErrorMessages(defaultErrorMessages)
                throw e
            }
        }
    }

    static void assertView(MvcResult mvcResult, String expectedViewName) {
        def mav = mvcResult.modelAndView
        if (!expectedViewName.equals(mav.viewName)) {
            throw new WrongResponseException(expectedViewName, mvcResult.modelAndView.viewName)
        }
    }

    static void assertPageResult(MvcResult mvcResult, String bindingResultAttributeName) {
        def mav = mvcResult.modelAndView
        def result = mav.model.get(bindingResultAttributeName) as BindingResult
        if (result.hasErrors()) {
            def ex = new ValidationException()
            for (def error in result.allErrors) {
                ex.addDefaultErrorMessage(error.defaultMessage)
            }
            throw ex
        }
    }

}
