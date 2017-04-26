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

package org.jtalks.jcommune.web.controller.api;

import org.jtalks.common.validation.ValidationError;
import org.jtalks.common.validation.ValidationException;
import org.jtalks.jcommune.model.dto.SpamRuleDto;
import org.jtalks.jcommune.model.entity.SpamRule;
import org.jtalks.jcommune.plugin.api.exceptions.NotFoundException;
import org.jtalks.jcommune.plugin.api.web.dto.json.JsonResponse;
import org.jtalks.jcommune.plugin.api.web.dto.json.JsonResponseStatus;
import org.jtalks.jcommune.service.ComponentService;
import org.jtalks.jcommune.service.SpamProtectionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.validation.Validator;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Oleg Tkachenko
 */
@Controller
@RequestMapping(value = "/api/spam-rules")
public class SpamRulesApi {

    private final ComponentService componentService;
    private final SpamProtectionService spamProtectionService;
    private final Validator validator;

    @Autowired
    public SpamRulesApi(ComponentService componentService, SpamProtectionService spamProtectionService, Validator validator) {
        this.componentService = componentService;
        this.spamProtectionService = spamProtectionService;
        this.validator = validator;
    }

    @ResponseBody @RequestMapping(method = RequestMethod.GET)
    public JsonResponse getAll(){
        checkForAdminPermissions();
        List<SpamRuleDto> ruleDtos = SpamRuleDto.fromEntities(spamProtectionService.getAllRules());
        return new JsonResponse(JsonResponseStatus.SUCCESS, ruleDtos);
    }

    @ResponseBody @RequestMapping(method = RequestMethod.POST)
    public JsonResponse add(@Valid @RequestBody SpamRuleDto ruleDto, BindingResult result) throws org.jtalks.common.service.exceptions.NotFoundException, InterruptedException {
        checkForAdminPermissions();
        trimAndValidate(ruleDto, result);
        if (result.hasFieldErrors() || result.hasGlobalErrors()) {
            return new JsonResponse(JsonResponseStatus.FAIL, result.getAllErrors());
        }
        SpamRule spamRule = ruleDto.toEntity();
        return saveOrUpdateSpamRule(result, spamRule);
    }

    @ResponseBody @RequestMapping(value = "/{ruleId}", method = RequestMethod.GET)
    public JsonResponse get(@PathVariable("ruleId") long ruleId){
        checkForAdminPermissions();
        SpamRuleDto spamRule = null;
        try {
            spamRule = SpamRuleDto.fromEntity(spamProtectionService.get(ruleId));
        } catch (NotFoundException e) {
            return new JsonResponse(JsonResponseStatus.FAIL, e.getMessage());
        }
        return new JsonResponse(JsonResponseStatus.SUCCESS, spamRule);
    }

    @ResponseBody @RequestMapping(value = "/{ruleId}", method = RequestMethod.PUT)
    public JsonResponse edit(@Valid @RequestBody SpamRuleDto ruleDto, BindingResult result, @PathVariable("ruleId") long ruleId) {
        checkForAdminPermissions();
        trimAndValidate(ruleDto, result);
        if (result.hasFieldErrors() || result.hasGlobalErrors()) {
            return new JsonResponse(JsonResponseStatus.FAIL, result.getAllErrors());
        }
        SpamRule spamRule = ruleDto.toEntity();
        spamRule.setId(ruleId);
        return saveOrUpdateSpamRule(result, spamRule);
    }

    @ResponseBody @RequestMapping(value = "/{ruleId}", method = RequestMethod.DELETE)
    public JsonResponse delete(@PathVariable("ruleId") long ruleId) {
        checkForAdminPermissions();
        spamProtectionService.deleteRule(ruleId);
        return new JsonResponse(JsonResponseStatus.SUCCESS);
    }
    private JsonResponse saveOrUpdateSpamRule(BindingResult result, SpamRule spamRule) {
        try {
            spamProtectionService.saveOrUpdate(spamRule);
        } catch (org.jtalks.common.service.exceptions.NotFoundException e) {
            return new JsonResponse(JsonResponseStatus.FAIL, e.getMessage());
        } catch (ValidationException ex) {
            ArrayList<ObjectError> errors = new ArrayList<>();
            for (ValidationError validationError : ex.getErrors()) {
                errors.add(new FieldError(result.getObjectName(), validationError.getFieldName(), validationError.getErrorMessageCode()));
            }
            return new JsonResponse(JsonResponseStatus.FAIL, errors);
        }
        return new JsonResponse(JsonResponseStatus.SUCCESS, SpamRuleDto.fromEntity(spamRule));
    }

    private void trimAndValidate(SpamRuleDto ruleDto, BindingResult result) {
        if (result.hasFieldErrors() || result.hasGlobalErrors()) return;
        ruleDto.setRegex(ruleDto.getRegex().trim()).setDescription(ruleDto.getDescription().trim());
        validator.validate(ruleDto, result);
    }
    /**
     * Check if currently logged user has permissions for administrative
     * functions for forum
     */
    private void checkForAdminPermissions() {
        long forumId = componentService.getComponentOfForum().getId();
        componentService.checkPermissionsForComponent(forumId);
    }
}
