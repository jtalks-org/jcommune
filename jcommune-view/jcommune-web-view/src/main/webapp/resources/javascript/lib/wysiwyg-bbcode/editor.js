/*
 WYSIWYG-BBCODE editor
 Copyright (c) 2009, Jitbit Sotware, http://www.jitbit.com/
 PROJECT HOME: http://wysiwygbbcode.codeplex.com/
 All rights reserved.

 Redistribution and use in source and binary forms, with or without
 modification, are permitted provided that the following conditions are met:
 * Redistributions of source code must retain the above copyright
 notice, this list of conditions and the following disclaimer.
 * Redistributions in binary form must reproduce the above copyright
 notice, this list of conditions and the following disclaimer in the
 documentation and/or other materials provided with the distribution.
 * Neither the name of the <organization> nor the
 names of its contributors may be used to endorse or promote products
 derived from this software without specific prior written permission.

 THIS SOFTWARE IS PROVIDED BY Jitbit Software ''AS IS'' AND ANY
 EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 DISCLAIMED. IN NO EVENT SHALL Jitbit Software BE LIABLE FOR ANY
 DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

var body_id, textboxelement;
var baseHtmlElement_id, baseDivElement;
var html_content_id, htmlcontentelement;
var content;
var isIE = /msie|MSIE/.test(navigator.userAgent);
var editorVisible = false;

function BBtag(name) {
    this.name = name;
}

function findTag(tagName) {
    for (var i = 0; i < bbtags.length; i++) {
        if (bbtags[i].name == tagName) {
            return bbtags[i];
        }
    }
    return null;
}

var bbtags = [
    new BBtag("b"),
    new BBtag("i"),
    new BBtag("u"),
    new BBtag("s"),
    new BBtag("left"),
    new BBtag("center"),
    new BBtag("right"),
    new BBtag("quote"),
    new BBtag("code"),
    new BBtag("img"),
    new BBtag("highlight"),
    new BBtag("list"),
    new BBtag("color"),
    new BBtag("size"),
    new BBtag("indent"),
    new BBtag("url") ];

function initEditor(textAreaId, htmlAreaId, baseDivId) {
    body_id = textAreaId;
    baseHtmlElement_id = htmlAreaId;
    html_content_id = baseDivId;
    textboxelement = document.getElementById(textAreaId);
    baseDivElement = document.getElementById(htmlAreaId);
    htmlcontentelement = document.getElementById(baseDivId);
    htmlcontentelement.style.display = "none";
    content = textboxelement.value;
    editorVisible = false;
}

/**
 * Changes visual editor representation when toggling
 * preview mode.
 */
function SwitchEditor(allowedUrls) {
    if (editorVisible) { // exit preview
        textboxelement.style.display = "";
        htmlcontentelement.style.display = "none";
        editorVisible = false;
        $('.hide-on-preview').show();
        $('.show-on-preview').hide();
        $('#preview')[0].value = $labelPreview;
        $('.keymaps-caption').show();
        $('#postBody').focus();
    }
    else { // enter preview
        content = textboxelement.value;
        bbcode2html(allowedUrls);
    }
}

// open tag regexp
var patternForOpenBBtag = "\\[([^\\/\\[\\]]*?)(=[^\\[\\]]*)?\\]";

// placeholders for special cases used internally during conversions.
// e.g. when user inputs characters %5D they will be replaced by 
// placeholder. And now when we send request to convert BB codes to html
// server will not treat these symbols as ]
var closeBracketCodePlaceholder = "@w0956756wo@";
var openBracketCodePlaceholder = "@ywdffgg434y@";
var slashCodePlaceholder = "14@123435vggv4f";
var lowerThenPlaceholder = "gertfgertgf@@@@@#4324234";

function bbcode2html(allowedUrls) {
    elId = "#" + html_content_id;
    var textdata = " " + textboxelement.value;
    textdata = textdata.replace(/%5D/gi, closeBracketCodePlaceholder);
    textdata = textdata.replace(/%5B/gi, openBracketCodePlaceholder);
    textdata = textdata.replace(/%22/gi, slashCodePlaceholder);
    textdata = textdata.replace(/</gi, lowerThenPlaceholder);
    textdata = encodeURI(textdata);
    textdata = textdata.replace(/%5D/gi, "]");
    textdata = textdata.replace(/%5B/gi, "[");
    textdata = textdata.replace(/%22/gi, "\"");
    textdata = textdata.replace(/%20/gi, " ");
    previewUrl = allowedUrls[0];
    var segment = $.url($(elId).closest("form").attr("action")).segment();
    for(var a=0; a<segment.length; a++) {
        if($.inArray(segment[a], allowedUrls) != -1) {
            previewUrl = segment[a];
            break;
        }
    }
    $.ajax({
        type:"POST",
        url:$root + '/' + previewUrl + '/bbToHtml', //todo
        dataType: 'json',
        data:{bodyText: decodeURI(textdata)},
        success:function (data) {
            var result = data.html;
            if(data.is_invalid) {
                ErrorUtils.removeErrorMessage(elId);
                for(var a=0; a<data.errors.length; a++) {
                    ErrorUtils.addErrorMessage(elId, data.errors[a].defaultMessage);
                }
            } else {
                $('.keymaps-caption').hide();
                $(".show-on-preview").show();
                $(".hide-on-preview").hide();
                $("#preview")[0].value = $labelEdit;

                result = result.replace(new RegExp(closeBracketCodePlaceholder, 'gi'), "%5D");
                result = result.replace(new RegExp(openBracketCodePlaceholder, 'gi'), "%5B");
                result = result.replace(new RegExp(slashCodePlaceholder, 'gi'), "%22");
                result = result.replace(new RegExp(lowerThenPlaceholder, 'gi'), "&lt;");

                editorVisible = true;

			    //enable code highlight
			    prettyPrint();

                //enable image preview
                $('a.pretty-photo').prettyPhoto({social_tools:false});
                $(elId).html(result.trim());
                htmlcontentelement.style.display = "";
                textboxelement.style.display = "none";
                ErrorUtils.removeErrorMessage(elId);
                //do code highlight
                prettyPrint(null, '#htmlContent');
            }
        }
    });
}

function XMLtoString(elem) {
    var serialized;

    try {
        // XMLSerializer exists in current Mozilla browsers
        serializer = new XMLSerializer();
        serialized = serializer.serializeToString(elem);
    }
    catch (e) {
        // Internet Explorer has a different approach to serializing XML
        serialized = elem.xml;
    }
    return serialized;
}

function doQuote() {
    if (!editorVisible) {
        addTag('[quote]', '[/quote]');
    }
}

function doClick(command) {
    if (!editorVisible) {
        switch (command) {
            case 'bold':
                addTag('[b]', '[/b]');
                break;
            case 'italic':
                addTag('[i]', '[/i]');
                break;
            case 'underline':
                addTag('[u]', '[/u]');
                break;
            case 'line-through':
                addTag('[s]', '[/s]');
                break;
            case 'highlight':
                addTag('[highlight]', '[/highlight]');
                break;
            case 'left':
                addTag('[left]', '[/left]');
                break;
            case 'right':
                addTag('[right]', '[/right]');
                break;
            case 'center':
                addTag('[center]', '[/center]');
                break;
            case 'InsertUnorderedList':
                AddList('[list][*]', '[/list]');
                break;
            case 'listElement':
                if (isInTag('[list]', '[/list]')) {
                    addTag('[*]','');
                } else {
                    AddList('[list][*]', '[/list]');
                }
                break;
        }
    }
}

function isInTag(t1, t2) {
     var selectionStart = textboxelement.selectionStart;
     var beforeText = textboxelement.value.substring(0,selectionStart);
     var afterText = textboxelement.value.substring(selectionStart + 1);
     var openIndex = beforeText.indexOf(t1);
     var closeIndex = -1;
     if (openIndex != -1) {
         closeIndex = beforeText.indexOf(t2, openIndex);
     }
     var open = false;
     while (openIndex != -1) {
         if (closeIndex == -1) {
             open = true;
             break;
         }
         openIndex = beforeText.indexOf(t1, openIndex + 1);
         closeIndex = beforeText.indexOf(t2, closeIndex + 1);
     }
     if (open && (afterText.indexOf(t2) != -1)) {
         return true;
     } else {
         return false;
     }
}


function doSize(selectedElement) {
    if (!editorVisible) {
		var size = $(selectedElement).parent().attr('data-value');
        addTag('[size=' + size + ']', '[/size]');
    }
}

function doCode(selectedElement) {
    if (!editorVisible) {
		var code = $(selectedElement).parent().attr('data-value');
        addTag('[code=' + code + ']', '[/code]');
    }
}

function doIndent(selectedElement) {
    if (!editorVisible) {
        var indent = $(selectedElement).parent().attr('data-value');
        addTag('[indent=' + indent + ']', '[/indent]');
    }
}

var mylink = '';

function doLink() {
    mylink = '';
    var str;
    var element = textboxelement;
    var selection = getInputSelection(textboxelement);
    if (isIE) {
        str = document.selection.createRange().text;
    } else if (typeof(element.selectionStart) != 'undefined') {
        var sel_start = element.selectionStart;
        var sel_end = element.selectionEnd;
        str = element.value.substring(sel_start, sel_end);
    }
    if (!editorVisible) {
        var urlAltIdClass = 'first';
        var urlIdClass = '';
        if(str.length > 0) {
            urlAltIdClass = '';
            urlIdClass = 'first';
        }
        var bodyContent = createFormRow($labelUrlText, str, 'urlAltId', $labelUrlInfo, urlAltIdClass) +
        createFormRow($labelUrl, '', 'urlId', $labelUrlRequired, urlIdClass);
        var footerContent = '' +
            '<button id="bb-link-cancel" class="btn">' + $labelCancel + '</button> \
            <button id="bb-link-ok" class="btn btn-primary">' + $labelOk + '</button>';

        var submitFunc = function(e){
            e.preventDefault();
            var input = $("#urlId");
            if ($('#urlAltId')) {
                mylink = $('#urlAltId').val();
                var link = $.trim(input.val());
                if ((link != null) && (link != '')) {
                    if (mylink == null || mylink == '') {
                        mylink = link;
                    }
                    jDialog.closeDialog();
                    addTag('[url=' + link + ']', '[/url]', selection);
                    textboxelement.focus();
                } else {
                    ErrorUtils.removeErrorMessage('#urlId', $labelErrorsNotEmpty);
                    ErrorUtils.addErrorMessage('#urlId', $labelErrorsNotEmpty);
                    input.focus();
                    return false;
                }
            }
        };

        jDialog.createDialog({
            title: $labelUrlHeader,
            bodyContent: bodyContent,
            footerContent: footerContent,
            maxWidth: 450,
            tabNavigation: ['#urlAltId', '#urlId', '#bb-link-ok','#bb-link-cancel'],
            handlers: {
                '#bb-link-cancel': {'static':'close'},
                '#bb-link-ok': {'click': submitFunc}
            }
        });
    }
}

function createFormRow(text, value, idForElement, info, cls) {
    return '' +
        '<div class="control-group">' +
    		'<label for="' + idForElement + '" class="control-label">' + text + '</label>' +
    		'<div class="controls">' +
    			'<input id="' + idForElement + '" class="dialog-input ' + cls + '" type="text" value="' +
    				value + '" name="' + idForElement + '">' +
    				'<br>' +
    	    '</div>' +
    	    '<span class="dialog-info">' + info + '</span>' +
        '</div>';

}

function doImage() {
    if (!editorVisible) {
        var selection = getInputSelection(textboxelement);
        var bodyContent = createFormRow($labelUrl, '', 'imgId', $labelUrlRequired, '');
        var footerContent = '' +
            '<button id="bb-img-cancel" class="btn">' + $labelCancel + '</button> \
            <button id="bb-img-ok" class="btn btn-primary">' + $labelOk + '</button>';

        var submitFunc = function(e){
            e.preventDefault();
            var input = $('#imgId');
            myimg = $.trim(input.val());
            if ((myimg != null) && (myimg != '')) {
                jDialog.closeDialog();
                addTag('[img]' + myimg, '[/img]', selection);
                textboxelement.focus();
            }else {
                ErrorUtils.removeErrorMessage('#imgId', $labelErrorsNotEmpty);
                ErrorUtils.addErrorMessage('#imgId', $labelErrorsNotEmpty);
                input.focus();
                return false;
            }
        };

        jDialog.createDialog({
            title: $labelImgHeader,
            bodyContent: bodyContent,
            footerContent: footerContent,
            maxWidth: 400,
            tabNavigation: ['#imgId', '#bb-img-ok','#bb-img-cancel'],
            handlers: {
                '#bb-img-ok': {'click': submitFunc},
                '#bb-img-cancel': {'static':'close'}
            }
        });
    }
}

/**
 * Inserts text to element from specified position.
 *
 * @param element element for text inserting
 * @param text text to insert
 * @param pos begin position for inserting text
 */
function insertText(element, text, pos) {
    element.value = element.value.slice(0, pos) + text + element.value.slice(pos);
}

/**
 * Replaces an arbitrary text with new one.
 *
 * @param element element for text replacing
 * @param text text to insert
 * @param fromPos old text begin position
 * @param toPos old text last position
 */
function replaceText(element, text, fromPos, toPos) {
    element.value = element.value.slice(0, fromPos) + text + element.value.slice(toPos);
}

/**
 * Inserts bb tag.
 *
 * @param t1 start tag
 * @param t2 end tag
 * @param selection selection text
 */
function addTag(t1, t2, selection) {
    var element = textboxelement;
    var dummyText = $labelDummyTextBBCode;

    if (isIE) {
        if (document.selection) {
            element.focus();
            if (selection) {
                setInputSelection(element, selection.start, selection.end)
            }
            var txt = element.value;
            var str = document.selection.createRange();
            var sel_start = element.selectionStart;
            var sel_end = element.selectionEnd;

            if (t2 == "[/img]") {
                str.text = str.text + t1 + t2;
                sel_end = sel_end + t1.length + t2.length;
                sel_start = sel_end;
            } else if (t2 == "[/url]") {
                str.text = t1 + mylink + t2;
                sel_start = sel_start + t1.length;
                sel_end = sel_start + mylink.length;
            } else if (str.text == "") {
                str.text = t1 + dummyText + t2;
                sel_start = sel_start + t1.length;
                sel_end = sel_start + dummyText.length;
            } else if (txt.indexOf(str.text) >= 0) {
                str.text = t1 + str.text + t2;
                sel_start = sel_start + t1.length;
                sel_end = sel_end + t1.length;
            } else {
                element.value = txt + t1 + dummyText + t2;
            }
            window.setTimeout(function(){
                element.selectionStart = sel_start;
                element.selectionEnd = sel_end;
            }, 1);

            if ($.browser.msie  && parseInt($.browser.version, 10) < 9)  {
                str.select();
            }

            element.focus();
        }
    } else if (typeof(element.selectionStart) != 'undefined') {
        sel_start = element.selectionStart;
        sel_end = element.selectionEnd;

        if (element.value == "" && $.browser.opera) {
            // for Opera browser null value (textarea empty) for selectionStart and selectionEnd is '20'
            sel_start = sel_start - 20;
            sel_end = sel_end - 20;
        }

        insertText(element, t1, sel_start);
        if (t2 == "[/img]") {
            insertText(element, t2, sel_end + t1.length);
            sel_end = sel_end + t1.length;
            sel_start = sel_start + t2.length-1;
        } else if (t2 == "[/url]") {
            replaceText(element, mylink + t2, sel_start + t1.length, sel_end + t1.length);
            sel_start = sel_start + t1.length;
            sel_end = sel_start + mylink.length;
        } else if (element.value.substring(sel_start, sel_end).length == 0) {
            insertText(element, dummyText + t2, sel_end + t1.length);
            sel_start = sel_start + t1.length;
            sel_end = sel_start + dummyText.length;
        } else {
            insertText(element, t2, sel_end + t1.length);
            sel_start = sel_start + t1.length;
            sel_end = sel_end + t1.length;
        }
        // needed for correct focus after inserting tags (Chrome)
        window.setTimeout(function(){
            element.selectionStart = sel_start;
            element.selectionEnd = sel_end;
        }, 1);
        element.focus();
    } else {
        if (t2 == "[/url]") {
            element.value = element.value + t1 + mylink + t2;
        } else {
            element.value = element.value + t1 + t2;
        }
    }
}

function AddList(t1, t2) {
    var element = textboxelement;
    var dummyText = $labelDummyTextBBCode;

    if (isIE) {
        if (document.selection) {
            element.focus();

            var txt = element.value;
            var str = document.selection.createRange();
            var sel_start = element.selectionStart;
            var sel_end = element.selectionEnd;

            if (str.text == "") {
                str.text = t1 + dummyText+ t2;
                sel_start = sel_start + t1.length;
                sel_end = sel_start + dummyText.length;
            }
            else if (txt.indexOf(str.text) >= 0) {
                str.text = t1 + str.text + t2;
                sel_start = sel_start + t1.length;
                sel_end = sel_end + t1.length;
            }
            else {
                element.value = txt + t1 + t2;
            }

            var value1 = str.text;
            var nPos1 = value1.indexOf("\n", '[list]'.length);
            if (nPos1 > 0) {
                value1 = value1.replace(/\n/gi, "[*]");
            }
            str.text = value1;

            element.selectionStart = sel_start;
            element.selectionEnd = sel_end;
            if ($.browser.msie  && parseInt($.browser.version, 10) < 9)  {
                str.select();
            }
            element.focus();
        }
    }
    else if (typeof(element.selectionStart) != 'undefined') {
        var sel_start = element.selectionStart;
        var sel_end = element.selectionEnd;
        if (element.value == "" && $.browser.opera) {
            // for Opera browser null value (textarea empty) for selectionStart and selectionEnd is '20'
            sel_start = sel_start - 20;
            sel_end = sel_end - 20;
        }
        var needDummy = (sel_start == sel_end);
        var str1 = needDummy ? dummyText + t2 : t2;
        insertText(element, t1, sel_start);
        insertText(element, str1, sel_end + t1.length);

        sel_end = sel_end + t1.length + t2.length + (needDummy ? dummyText.length : 0);

        var value = element.value.substring(sel_start, sel_end);
        var nPos = value.indexOf("\n", '[list]'.length);
        if (nPos > 0) {
            value = value.replace(/\n/gi, "[*]");
        }
        var elvalue = element.value;
        value = value.replace(/\[list\]\[\*\]/gi, '[list]\n[*]');
        value = value.replace(/\[\/list\]/gi, '\n[/list]');
        element.value = elvalue.substring(0, sel_start) + value + elvalue.substring(sel_end, elvalue.length);

        element.selectionStart = sel_start + t1.length + "\n".length;
        element.selectionEnd = sel_end - t2.length + "\n".length;

        element.focus();
    }
    else {
        element.value = element.value + t1 + t2;
    }
}

//=======color picker
function getScrollY() {
    var scrOfX = 0, scrOfY = 0;
    if (typeof (window.pageYOffset) == 'number') {
        scrOfY = window.pageYOffset;
        scrOfX = window.pageXOffset;
    } else if (document.body && (document.body.scrollLeft || document.body.scrollTop)) {
        scrOfY = document.body.scrollTop;
        scrOfX = document.body.scrollLeft;
    } else if (document.documentElement && (document.documentElement.scrollLeft || document.documentElement.scrollTop)) {
        scrOfY = document.documentElement.scrollTop;
        scrOfX = document.documentElement.scrollLeft;
    }
    return scrOfY;
}

document.write("<style type='text/css'>.colorpicker201{visibility:hidden;display:none;position:absolute;background:#FFF;z-index:999;filter:progid:DXImageTransform.Microsoft.Shadow(color=#D0D0D0,direction=135);}.o5582brd{padding:0;width:12px;height:14px;border-bottom:solid 1px #DFDFDF;border-right:solid 1px #DFDFDF;}a.o5582n66,.o5582n66,.o5582n66a{font-family:arial,tahoma,sans-serif;text-decoration:underline;font-size:9px;color:#666;border:none;}.o5582n66,.o5582n66a{text-align:center;text-decoration:none;}a:hover.o5582n66{text-decoration:none;color:#FFA500;cursor:pointer;}.a01p3{padding:1px 4px 1px 2px;background:whitesmoke;border:solid 1px #DFDFDF;}</style>");

function getTop2() {
    csBrHt = 0;
    if (typeof (window.innerWidth) == 'number') {
        csBrHt = window.innerHeight;
    } else if (document.documentElement && (document.documentElement.clientWidth || document.documentElement.clientHeight)) {
        csBrHt = document.documentElement.clientHeight;
    } else if (document.body && (document.body.clientWidth || document.body.clientHeight)) {
        csBrHt = document.body.clientHeight;
    }
    ctop = ((csBrHt / 2) - 115) + getScrollY();
    return ctop;
}
var nocol1 = "&#78;&#79;&#32;&#67;&#79;&#76;&#79;&#82;",
    clos1 = "X";

function getLeft2() {
    var csBrWt = 0;
    if (typeof (window.innerWidth) == 'number') {
        csBrWt = window.innerWidth;
    } else if (document.documentElement && (document.documentElement.clientWidth || document.documentElement.clientHeight)) {
        csBrWt = document.documentElement.clientWidth;
    } else if (document.body && (document.body.clientWidth || document.body.clientHeight)) {
        csBrWt = document.body.clientWidth;
    }
    cleft = (csBrWt / 2) - 125;
    return cleft;
}

//function setCCbldID2(val, textBoxID) { document.getElementById(textBoxID).value = val; }
function setCCbldID2(val) {
    if (!editorVisible)
        addTag('[color=' + val + ']', '[/color]');
}

function setCCbldSty2(objID, prop, val) {
    switch (prop) {
        case "bc":
            if (objID != 'none') {
                document.getElementById(objID).style.backgroundColor = val;
            }
            ;
            break;
        case "vs":
            document.getElementById(objID).style.visibility = val;
            break;
        case "ds":
            document.getElementById(objID).style.display = val;
            break;
        case "tp":
            document.getElementById(objID).style.top = val;
            break;
        case "lf":
            document.getElementById(objID).style.left = val;
            break;
    }
}

function putOBJxColor2(Samp, pigMent, textBoxId) {
    //document.getElementById("o5582n66").value='#' + pigMent;
    javascript:document.getElementById("o5582n66a").style.backgroundColor = '#' + pigMent;
    // title='#' + pigMent;
}

function showColorGrid2(Sam, textBoxId) {
    if (!editorVisible) {
        var selection = getInputSelection(textboxelement);
        var objX = new Array('00', '33', '66', '99', 'CC', 'FF');
        var c = 0;
        var xl = '"' + Sam + '","x", "' + textBoxId + '"';

        var bodyContent = '';
        bodyContent += '<table border="0" cellpadding="0" cellspacing="0" style="border:solid 0px #F0F0F0;padding:2px;table-layout:fixed;width:350px;"><tr>';
        var br = 1;
        for (o = 0; o < 6; o++) {
            bodyContent += '</tr><tr>';
            for (y = 0; y < 6; y++) {
                if (y == 3) {
                    bodyContent += '</tr><tr>';
                }
                for (x = 0; x < 6; x++) {
                    var grid = '';
                    grid = objX[o] + objX[y] + objX[x];
                    var b = "'" + Sam + "','" + grid + "', '" + textBoxId + "'";
                    bodyContent += '<td class="o5582brd" style="background-color:#' + grid + '"><a class="o5582n66"  href="javascript:onclick=putOBJxColor2(' + b + ');"><div style="width:12px;height:14px;"></div></a></td>';
                    c++;
                }
            }
        }
        bodyContent += '</tr></table>';

        var titleContent = '<div style="height: 30px"> \
            <div class="left-aligned">' + $labelSelectedColor + '</div> \
            <div id="o5582n66a" class="left-aligned dialog-color-picker"></div></div>';

        var footerContent = '' +
            '<button id="bb-color-cancel" class="btn">' + $labelCancel + '</button> \
            <button id="bb-color-ok" class="btn btn-primary">' + $labelOk + '</button>';

        var submitFunc = function (e) {
            e.preventDefault();
            var rgb_color = $('#o5582n66a').css('backgroundColor');
            var hex_color = getHexRGBColor(rgb_color);
            jDialog.closeDialog();
            addTag('[color=' + hex_color + ']', '[/color]', selection);
            textboxelement.focus();
        }

        jDialog.createDialog({
            title: titleContent,
            bodyContent: bodyContent,
            footerContent: footerContent,
            maxWidth: 380,
            fisrtFocus: false,
            tabNavigation: ['#bb-color-ok','#bb-color-cancel'],
            handlers: {
                '#bb-color-ok': {'click': submitFunc},
                '#bb-color-cancel': {'static':'close'}
            }
        });
    }

    function getHexRGBColor(color) {
        color = color.replace(/\s/g, '');
        var aRGB = color.match(/^rgb\((\d{1,3}[%]?),(\d{1,3}[%]?),(\d{1,3}[%]?)\)$/i);

        if (aRGB) {
            color = '';
            for (var i = 1; i <= 3; i++) color += Math.round((aRGB[i][aRGB[i].length - 1] == "%" ? 2.55 : 1) * parseInt(aRGB[i])).toString(16).replace(/^(.)$/, '0$1');
        }
        else color = color.replace(/^#?([\da-f])([\da-f])([\da-f])$/i, '$1$1$2$2$3$3');

        return color.toUpperCase();
    }
}

$(document).ready(function() {
    // id: action
    var actionsMap = {
        format_u: function() {doClick('underline');},
        format_i: function() {doClick('italic');},
        format_b: function() {doClick('bold');},
        format_s: function() {doClick('line-through');},
        format_img: function() {doImage();},
        format_url: function() {doLink();},
        select_color: function() {showColorGrid2('none');},
        format_highlight: function() {doClick('highlight');},
        format_left: function(){doClick('left');},
        format_center: function(){doClick('center');},
        format_right: function(){doClick('right');},
        format_list: function(){doClick('InsertUnorderedList');},
        format_listeq: function(){doClick('listElement');},
        format_quote: function(){doQuote();}
    }

    // assign onclick action to each button
    for (var k in actionsMap) {
        if (actionsMap.hasOwnProperty(k)) {
           $('#' + k).click(actionsMap[k]);
        }
    }

    $('#select_size a').click(function() {
            doSize(this);
    });

    $('#select_code a').click(function() {
            doCode(this);
    });

    $('#select_indent a').click(function() {
            doIndent(this);
    });
});

/**
 * Returns textarea selection.
 * @param {Object} textarea
 * @returns {&#123;start, end&#125;}
 */
function getInputSelection(el) {
    var start = 0, end = 0, normalizedValue, range,
        textInputRange, len, endRange;

    if (typeof el.selectionStart == "number" && typeof el.selectionEnd == "number") {
        start = el.selectionStart;
        end = el.selectionEnd;
    } else {
        range = document.selection.createRange();

        if (range && range.parentElement() == el) {
            len = el.value.length;
            normalizedValue = el.value.replace(/\r\n/g, "\n");

            // Create a working TextRange that lives only in the input
            textInputRange = el.createTextRange();
            textInputRange.moveToBookmark(range.getBookmark());

            // Check if the start and end of the selection are at the very end
            // of the input, since moveStart/moveEnd doesn't return what we want
            // in those cases
            endRange = el.createTextRange();
            endRange.collapse(false);

            if (textInputRange.compareEndPoints("StartToEnd", endRange) > -1) {
                start = end = len;
            } else {
                start = -textInputRange.moveStart("character", -len);
                start += normalizedValue.slice(0, start).split("\n").length - 1;

                if (textInputRange.compareEndPoints("EndToEnd", endRange) > -1) {
                    end = len;
                } else {
                    end = -textInputRange.moveEnd("character", -len);
                    end += normalizedValue.slice(0, end).split("\n").length - 1;
                }
            }
        }
    }

    return {
        start: start,
        end: end
    };
}

/**
 * Apply selection to the text element.
 * @param {Object} textarea
 * @param {int} startOffset
 * @param {int} endOffset
 */
function setInputSelection(el, startOffset, endOffset) {
    if (typeof el.selectionStart == "number" && typeof el.selectionEnd == "number") {
        el.selectionStart = startOffset;
        el.selectionEnd = endOffset;
    } else {
        var range = el.createTextRange();
        var startCharMove = offsetToRangeCharacterMove(el, startOffset);
        range.collapse(true);
        if (startOffset == endOffset) {
            range.move("character", startCharMove);
        } else {
            range.moveEnd("character", offsetToRangeCharacterMove(el, endOffset));
            range.moveStart("character", startCharMove);
        }
        range.select();
    }
}

function offsetToRangeCharacterMove(el, offset) {
    return offset - (el.value.slice(0, offset).split("\r\n").length - 1);
}
