<%--

    Copyright (C) 2011  JTalks.org Team
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

--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%--
 This page contains localized messages to be used in JS files on a client side
--%>
$labelDeleteContactConfirmation = $.parseHTML('<spring:message code="label.deleteContactConfirmation" htmlEscape="true"/>')[0].data;
$labelDeleteCommentConfirmation = $.parseHTML('<spring:message code="label.deleteCommentConfirmation" htmlEscape="true"/>')[0].data;
$labelDeleteContactFailture = $.parseHTML('<spring:message code="label.deleteContactFailture" htmlEscape="true"/>')[0].data;

$labelDeletePmGroupConfirmation = $.parseHTML('<spring:message code="label.deletePMGroupConfirmation" htmlEscape="true"/>')[0].data;

$labelValidationUsercontactNotMatch = $.parseHTML('<spring:message code="validation.usercontact.notmatch" htmlEscape="true"/>')[0].data;
$labelContactType = $.parseHTML('<spring:message code="label.contact.type" htmlEscape="true"/>')[0].data;
$labelContactValue = $.parseHTML('<spring:message code="label.contact.value" htmlEscape="true"/>')[0].data;
$labelContactValueInfo = $.parseHTML('<spring:message code="label.contact.value.info" htmlEscape="true"/>')[0].data;
$labelContactsTipsDelete = $.parseHTML('<spring:message code="label.contacts.tips.delete" htmlEscape="true"/>')[0].data;
$labelContactsAddDialog = $.parseHTML('<spring:message code="label.contacts.addDialog" htmlEscape="true"/>')[0].data;

$labelRegistrationSuccess = $.parseHTML('<spring:message code="label.registration.success" htmlEscape="true"/>')[0].data;
$labelRegistrationFailture = $.parseHTML('<spring:message code="label.registration.failture" htmlEscape="true"/>')[0].data;
$labelRegistrationConnectionError = $.parseHTML('<spring:message code="label.registration.connection.error" htmlEscape="true"/>')[0].data;
$labelHoneypotCaptchaFilled = $.parseHTML('<spring:message code="label.honeypot.not.null" htmlEscape="true"/>')[0].data;
$labelAuthenticationConnectionError = $.parseHTML('<spring:message code="label.authentication.connection.error" htmlEscape="true"/>')[0].data;
$labelUrlHeader = $.parseHTML('<spring:message code="label.url.header" htmlEscape="true"/>')[0].data;
$labelUrlText = $.parseHTML('<spring:message code="label.url.text" htmlEscape="true"/>')[0].data;
$labelUrl = $.parseHTML('<spring:message code="label.url" htmlEscape="true"/>')[0].data;
$labelUrlInfo = $.parseHTML('<spring:message code="label.url.info" htmlEscape="true"/>')[0].data;
$labelUrlRequired = $.parseHTML('<spring:message code="label.url.required" htmlEscape="true"/>')[0].data;
$labelImgHeader = $.parseHTML('<spring:message code="label.img.header" htmlEscape="true"/>')[0].data;

$labelPreview= $.parseHTML('<spring:message code="label.answer.preview" htmlEscape="true"/>')[0].data;
$labelEdit= $.parseHTML('<spring:message code="label.edit" htmlEscape="true"/>')[0].data;
$labelDelete= $.parseHTML('<spring:message code="label.delete" htmlEscape="true"/>')[0].data;

$labelPollTitleWithEnding = $.parseHTML('<spring:message code="label.poll.title.with.ending" htmlEscape="true"/>')[0].data;
$labelPollVote = $.parseHTML('<spring:message code="label.poll.vote" htmlEscape="true"/>')[0].data;


$labelSelectedColor = $.parseHTML('<spring:message code="label.selected.color" htmlEscape="true"/>')[0].data;

$localeCode = $.parseHTML('<spring:message code="locale.code" htmlEscape="true"/>')[0].data;

$labelSubscribe = $.parseHTML('<spring:message code="label.subscribe" htmlEscape="true"/>')[0].data;
$labelSubscribeTooltip = $.parseHTML('<spring:message code="label.subscribe.tooltip" htmlEscape="true"/>')[0].data;
$labelUnsubscribe = $.parseHTML('<spring:message code="label.unsubscribe" htmlEscape="true"/>')[0].data;


$labelUnsubscribeTooltip = $.parseHTML('<spring:message code="label.unsubscribe.tooltip" htmlEscape="true"/>')[0].data;

$labelErrorsNotEmpty = $.parseHTML('<spring:message code="label.errors.not_empty" htmlEscape="true"/>')[0].data;
$labelDeleteAvatarConfirmation = $.parseHTML('<spring:message code="label.deleteAvatarConfirmation" htmlEscape="true"/>')[0].data;
$labelImageWrongSizeJs = $.parseHTML('<spring:message code="image.wrong.size.js" htmlEscape="true"/>')[0].data;
$fileIsEmpty = $.parseHTML('<spring:message code="image.wrong.empty" htmlEscape="true"/>')[0].data;

$labelNotSpecified = $.parseHTML('<spring:message code="label.not.specified" htmlEscape="true"/>')[0].data;

$labelError500Detail = $.parseHTML('<spring:message code="label.500.detail" htmlEscape="true"/>')[0].data;

$labelRegistration = $.parseHTML('<spring:message code="label.signup" htmlEscape="true"/>')[0].data;
$labelUsername = $.parseHTML('<spring:message code="label.username" htmlEscape="true"/>')[0].data;
$labelEmail = $.parseHTML('<spring:message code="label.tip.email" htmlEscape="true"/>')[0].data;
$labelPassword = $.parseHTML('<spring:message code="label.password" htmlEscape="true"/>')[0].data;
$labelPasswordConfirmation = $.parseHTML('<spring:message code="label.confirmation" htmlEscape="true"/>')[0].data;
$lableHoneypotCaptcha = $.parseHTML('<spring:message code="label.tip.honeypot.captcha" htmlEscape="true"/>')[0].data;
$signupButtonLabel = $.parseHTML('<spring:message code="label.signup" htmlEscape="true"/>')[0].data;
$labelCaptcha = $.parseHTML('<spring:message code="label.tip.captcha" htmlEscape="true"/>')[0].data;
$altCaptcha = $.parseHTML('<spring:message code="alt.captcha.image" htmlEscape="true" javaScriptEscape="true"/>')[0].data;
$altCaptchaRefresh = $.parseHTML('<spring:message code="alt.captcha.update" htmlEscape="true" javaScriptEscape="true"/>')[0].data;
$labelRememberMe = $.parseHTML('<spring:message code="label.auto_logon" htmlEscape="true"/>')[0].data;
$labelRestorePassword = $.parseHTML('<spring:message code="label.restorePassword.prompt" htmlEscape="true"/>')[0].data;
$labelSignin = $.parseHTML('<spring:message code="label.signin" htmlEscape="true"/>')[0].data;
$labelLoginError = $.parseHTML('<spring:message code="label.login_error" htmlEscape="true"/>')[0].data;

$labelCancel = $.parseHTML('<spring:message code="label.cancel" htmlEscape="true"/>')[0].data;
$labelOk = $.parseHTML('<spring:message code="label.ok" htmlEscape="true"/>')[0].data;
$labelTopicMove = $.parseHTML('<spring:message code="label.topic.move" htmlEscape="true"/>')[0].data;
$labelTopicMoveFull = $.parseHTML('<spring:message code="label.topic.move.full" htmlEscape="true"/>')[0].data;

$labelReviewSays = $.parseHTML('<spring:message code="label.review.says" htmlEscape="true"/>')[0].data;

$labelUnexpectedError = $.parseHTML('<spring:message code="label.unexpected.error" htmlEscape="true"/>')[0].data;
$labelAdd = $.parseHTML('<spring:message code="label.add" htmlEscape="true"/>')[0].data;
$labelEdit = $.parseHTML('<spring:message code="label.edit" htmlEscape="true"/>')[0].data;
$labelKeymapsReview = $.parseHTML('<spring:message code="label.keymaps.review" htmlEscape="true"/>')[0].data;
$labelTopicWasRemoved = $.parseHTML('<spring:message code="label.topicWasRemoved" htmlEscape="true"/>')[0].data;
$labelYouDontHavePermissions = $.parseHTML('<spring:message code="label.topicYouDontHavePermissions" htmlEscape="true"/>')[0].data;

$labelLinksEditor=$.parseHTML('<spring:message code="label.linksEditor" htmlEscape="true"/>')[0].data;
$labelTitle=$.parseHTML('<spring:message code="label.title" htmlEscape="true"/>')[0].data;
$labelHint=$.parseHTML('<spring:message code="label.hint" htmlEscape="true"/>')[0].data;
$labelSave=$.parseHTML('<spring:message code="label.save" htmlEscape="true"/>')[0].data;
$labelDeleteMainLink=$.parseHTML('<spring:message code="label.deleteMainLink" htmlEscape="true"/>')[0].data;
$labelErrorLinkSave = $.parseHTML('<spring:message code="label.link.error.save" htmlEscape="true"/>')[0].data;
$labelErrorLinkDelete = $.parseHTML('<spring:message code="label.link.error.save" htmlEscape="true"/>')[0].data;
$linksEditIcon = $.parseHTML('<spring:message code="label.linksEditIcon" htmlEscape="true"/>')[0].data;
$linksRemoveIcon = $.parseHTML('<spring:message code="label.linksRemoveIcon" htmlEscape="true"/>')[0].data;
$capsLock = $.parseHTML('<spring:message code="label.tips.capsLock" htmlEscape="true"/>')[0].data;

$labelAddReviewComment = $.parseHTML('<spring:message code="label.addReviewComment" htmlEscape="true"/>')[0].data;

$labelSaveChanges = $.parseHTML('<spring:message code="label.save_changes" htmlEscape="true"/>')[0].data;

$labelForumDescription = $.parseHTML('<spring:message code="label.forum.description" htmlEscape="true"/>')[0].data;
$labelForumTitle = $.parseHTML('<spring:message code="label.forum.title" htmlEscape="true"/>')[0].data;
$labelLogoTooltip = $.parseHTML('<spring:message code="label.logo.tooltip" htmlEscape="true"/>')[0].data;
$labelAdministration = $.parseHTML('<spring:message code="label.administration" htmlEscape="true"/>')[0].data;

$labelDeleteLogoConfirmation = $.parseHTML('<spring:message code="label.deleteLogoConfirmation" htmlEscape="true"/>')[0].data;
$labelUploadLogo = $.parseHTML('<spring:message code="label.uploadLogo" htmlEscape="true"/>')[0].data;
$labelUploadTitle = $.parseHTML('<spring:message code="label.uploadTitle" htmlEscape="true"/>')[0].data;
$labelRemoveLogo = $.parseHTML('<spring:message code="label.deleteLogo" htmlEscape="true"/>')[0].data;
$labelUploadFavIcon = $.parseHTML('<spring:message code="label.uploadFavIcon" htmlEscape="true"/>')[0].data;
$labelRemoveFavIcon = $.parseHTML('<spring:message code="label.deleteFavIcon" htmlEscape="true"/>')[0].data;
$labelDeleteIconConfirmation = $.parseHTML('<spring:message code="label.deleteIconConfirmation" htmlEscape="true"/>')[0].data;
$labelDummyTextBBCode = $.parseHTML('<spring:message code="label.dummyTextBBCode" htmlEscape="true"/>')[0].data;

$labelBranchName = $.parseHTML('<spring:message code="label.branchName" htmlEscape="true"/>')[0].data;
$labelBranchDescription = $.parseHTML('<spring:message code="label.branchDescription" htmlEscape="true"/>')[0].data;
$labelTitlePrefix = $.parseHTML('<spring:message code="label.titlePrefix" htmlEscape="true"/>')[0].data;
$labelTitlePrefixHint = $.parseHTML('<spring:message code="label.titlePrefixHint" htmlEscape="true"/>')[0].data;

$labelShowDetails = $.parseHTML('<spring:message code="label.showDetails" htmlEscape="true"/>')[0].data;
$labelHideDetails = $.parseHTML('<spring:message code="label.hideDetails" htmlEscape="true"/>')[0].data;
$labelPluginConfigError = $.parseHTML('<spring:message code="label.pluginConfigError" htmlEscape="true"/>')[0].data;
$labelCloseDialog = $.parseHTML('<spring:message code="label.closeDialog" htmlEscape="true"/>')[0].data;

$copyrightLabel = $.parseHTML('<spring:message code="label.copyright" htmlEscape="true"/>')[0].data;
$copyrightHint = $.parseHTML('<spring:message code="label.copyrightHint" htmlEscape="true"/>')[0].data;

$isPasswordChangedMessage = $.parseHTML('<spring:message code="user.security.message.changed_password" htmlEscape="true"/>')[0].data;
$allowPermission = $.parseHTML('<spring:message code="permissions.allow" htmlEscape="true"/>')[0].data;
$restrictPermission = $.parseHTML('<spring:message code="permissions.restrict" htmlEscape="true"/>')[0].data;

$permissionsGroupAvailable = $.parseHTML('<spring:message code="permissions.group.available" htmlEscape="true"/>')[0].data;
$permissionsGroupAlreadyAdded = $.parseHTML('<spring:message code="permissions.group.already.added" htmlEscape="true"/>')[0].data;
$labelFailedToLoadGroups = $.parseHTML('<spring:message code="permissions.group.load.failed" htmlEscape="true"/>')[0].data;

$pluginStatusActivated = $.parseHTML('<spring:message code="label.plugins.plugin.status.activated" htmlEscape="true"/>')[0].data;
$pluginStatusDeactivated = $.parseHTML('<spring:message code="label.plugins.plugin.status.deactivated" htmlEscape="true"/>')[0].data;
$pluginStatusFailed = $.parseHTML('<spring:message code="label.plugins.plugin.status.failed" htmlEscape="true"/>')[0].data;