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

var myeditor, ifm;
var body_id, textboxelement;
var content;
var isIE = /msie|MSIE/.test(navigator.userAgent);
var isChrome = /Chrome/.test(navigator.userAgent);
var isSafari = /Safari/.test(navigator.userAgent) && !isChrome;
var browser = isIE || window.opera;
var textRange;
var enter = 0;
var editorVisible = false;
var enableWysiwyg = false;

function rep(re, str) {
    content = content.replace(re, str);
}

function initEditor(textarea_id, wysiwyg) {
    if (wysiwyg != undefined)
        enableWysiwyg = wysiwyg;
    else
        enableWysiwyg = true;
    body_id = textarea_id;
    textboxelement = document.getElementById(body_id);
    textboxelement.setAttribute('class', 'editorBBCODE');
    textboxelement.className = "editorBBCODE";
    if (enableWysiwyg) {
        ifm = document.createElement("iframe");
        ifm.setAttribute("id", "rte");
        ifm.setAttribute("class", "editorBBCODE");
        ifm.setAttribute("frameborder", "1");
        ifm.width = '90%';
        ifm.height = 400;
        textboxelement.parentNode.insertBefore(ifm, textboxelement);
        textboxelement.style.display = 'none';
        if (ifm) {
            ShowEditor();
        } else
            setTimeout('ShowEditor()', 100);
    }
}

function getStyle(el, styleProp) {
    var x = document.getElementById(el);
    if (x.currentStyle)
        var y = x.currentStyle[styleProp];
    else if (window.getComputedStyle)
        var y = document.defaultView.getComputedStyle(x, null).getPropertyValue(styleProp);
    return y;
}

function ShowEditor() {
    if (!enableWysiwyg) return;
    editorVisible = true;
    content = document.getElementById(body_id).value;
    myeditor = ifm.contentWindow.document;
    bbcode2html();
    myeditor.designMode = "on";
    myeditor.open();
    myeditor.write('<html style="background: #f8f8f8;background-image: none;">' +
        '<head><link href="/jcommune/resources/css/screen.css" rel="Stylesheet" type="text/css" /></head>');
    myeditor.write('<body style="height: 100%;width: 100%;margin:0px 0px 0px 0px;background: #f8f8f8;background-image: none;" class="editorWYSIWYG">');
    myeditor.write(content);
    myeditor.write('</body></html>');
    myeditor.close();
    if (myeditor.attachEvent) {
        if (parent.ProcessKeyPress)
            myeditor.attachEvent("onkeydown", parent.ProcessKeyPress);
        myeditor.attachEvent("onkeypress", kp);
    }
    else if (myeditor.addEventListener) {
        if (parent.ProcessKeyPress)
            myeditor.addEventListener("keydown", parent.ProcessKeyPress, true);
        myeditor.addEventListener("keypress", kp, true);
    }
}

function SwitchEditor() {
    if (editorVisible) {
        doCheck();
        ifm.style.display = 'none';
        textboxelement.style.display = '';
        editorVisible = false;
    }
    else {
        if (enableWysiwyg && ifm) {
            ifm.style.display = '';
            textboxelement.style.display = 'none';
            ShowEditor();
            editorVisible = true;
        }
    }
}

function html2bbcode() {
    rep(/<img\s[^<>]*?src=\"?([^<>]*?)\"?(\s[^<>]*)?\/?>/gi, "[img]$1[/img]");
    rep(/<\/(strong|b)>/gi, "[/b]");
    rep(/<(strong|b)(\s[^<>]*)?>/gi, "[b]");
    rep(/<\/(em|i)>/gi, "[/i]");
    rep(/<(em|i)(\s[^<>]*)?>/gi, "[i]");
    rep(/<\/u>/gi, "[/u]");
    rep(/<\/s>/gi, "[/s]");
    rep(/\n/gi, " ");
    rep(/\r/gi, " ");
    rep(/<u(\s[^<>]*)?>/gi, "[u]");
    rep(/<s(\s[^<>]*)?>/gi, "[s]");

    rep(/<br(\s[^<>]*)?>/gi, "\n");
    /*rep(/<p(\s[^<>]*)?>/gi, "");
     rep(/<\/p>/gi, "\n");*/
    rep(/<ul>/gi, "[list]");
    rep(/<\/ul>/gi, "[/list]");
    rep(/<li>/gi, "[*]");
    rep(/<\/li>/gi, "");
    rep(/&nbsp;/gi, " ");
    rep(/&quot;/gi, "\"");
    rep(/&amp;/gi, "&");
    var sc, sc2;
    do {
        sc = content;
        rep(/<font\s[^<>]*?color=\"?([^<>]*?)\"?(\s[^<>]*)?>([^<>]*?)<\/font>/gi, "[color=$1]$3[/color]");
        rep(/<font\s[^<>]*?class=\"textSize(\d*)\"[^<>]*?>([^<>]*?)<\/font>/gi, "[size=$1]$2[/size]");
        rep(/<font\s[^<>]*?class=\"marginLeft(\d*)\"[^<>]*?>([^<>]*?)<\/font>/gi, "[indent=$1]$2[/indent]");
        rep(/<p\s[^<>]*?class=\"leftText\"([^<>]*)?>([^<>]*?)<\/p>/gi, "[left]$2[/left]");
        rep(/<p\s[^<>]*?class=\"rightText\"([^<>]*)?>([^<>]*?)<\/p>/gi, "[right]$2[/right]");
        rep(/<p\s[^<>]*?class=\"centerText\"([^<>]*)?>([^<>]*?)<\/p>/gi, "[center]$2[/center]");
        rep(/<font\s[^<>]*?class=\"highlight\"([^<>]*)?>([^<>]*?)<\/font>/gi, "[highlight]$2[/highlight]");
        rep(/<div\s[^<>]*?class=\"code\"([^<>]*)?>([^<>]*?)<\/div>/gi, "[code]$2[/code]");
        rep(/<div\s[^<>]*?class=\"quote\"([^<>]*)?><div\s[^<>]*?class=\"quote_title\"[^<>]*?>Quote:<\/div><blockquote>([^<>]*?)<\/blockquote><\/div>/gi, "[quote]$2[/quote]");
        rep(/<div\s[^<>]*?class=\"quote\"([^<>]*)?><div\s[^<>]*?class=\"quote_title\"[^<>]*?>([^<>]*?):<\/div><blockquote>([^<>]*?)<\/blockquote><\/div>/gi, "[quote=\"$2\"]$3[/quote]");

        if (sc == content)
            rep(/<font[^<>]*>([^<>]*?)<\/font>/gi, "$1");
        rep(/<a\s[^<>]*?href=\"?([^<>]*?)\"?(\s[^<>]*)?>([^<>]*?)<\/a>/gi, "[url=$1]$3[/url]");
        sc2 = content;
        rep(/<(span|blockquote|pre)\s[^<>]*?style=\"?font-weight: ?bold;?\"?\s*([^<]*?)<\/\1>/gi, "[b]<$1 style=$2</$1>[/b]");
        rep(/<(span|blockquote|pre)\s[^<>]*?style=\"?font-weight: ?normal;?\"?\s*([^<]*?)<\/\1>/gi, "<$1 style=$2</$1>");
        rep(/<(span|blockquote|pre)\s[^<>]*?style=\"?font-style: ?italic;?\"?\s*([^<]*?)<\/\1>/gi, "[i]<$1 style=$2</$1>[/i]");
        rep(/<(span|blockquote|pre)\s[^<>]*?style=\"?font-style: ?normal;?\"?\s*([^<]*?)<\/\1>/gi, "<$1 style=$2</$1>");
        rep(/<(span|blockquote|pre)\s[^<>]*?style=\"?text-decoration: ?underline;?\"?\s*([^<]*?)<\/\1>/gi, "[u]<$1 style=$2</$1>[/u]");
        rep(/<(span|blockquote|pre)\s[^<>]*?style=\"?text-decoration: ?none;?\"?\s*([^<]*?)<\/\1>/gi, "<$1 style=$2</$1>");
        rep(/<(span|blockquote|pre)\s[^<>]*?style=\"?color: ?([^<>]*?);\"?\s*([^<]*?)<\/\1>/gi, "[color=$2]<$1 style=$3</$1>[/color]");
        rep(/<(span|blockquote|pre)\s[^<>]*?style=\"?font-family: ?([^<>]*?);\"?\s*([^<]*?)<\/\1>/gi, "[font=$2]<$1 style=$3</$1>[/font]");
        rep(/<(blockquote|pre)\s[^<>]*?style=\"?\"? (class=|id=)([^<>]*)>([^<>]*?)<\/\1>/gi, "<$1 $2$3>$4</$1>");
        rep(/<span\s[^<>]*?style=\"?\"?>([^<>]*?)<\/span>/gi, "$1");
        if (sc2 == content) {
            rep(/<span[^<>]*>([^<>]*?)<\/span>/gi, "$1");
            sc2 = content;
        }
    } while (sc != content)

    rep(/<div><br(\s[^<>]*)?>/gi, "<div>");//chrome-safari fix to prevent double linefeeds
    rep(/<\/div>\s*<div([^<>]*)>/gi, "</span>\n<span$1>");//chrome-safari fix to prevent double linefeeds
    rep(/<div([^<>]*)>/gi, "\n<span$1>");
    rep(/<\/div>/gi, "</span>\n");

    rep(/<[^<>]*>/gi, "");
    rep(/&lt;/gi, "<");
    rep(/&gt;/gi, ">");

    do {
        sc = content;
        rep(/\[(b|i|u)\]\[quote([^\]]*)\]([\s\S]*?)\[\/quote\]\[\/\1\]/gi, "[quote$2][$1]$3[/$1][/quote]");
        rep(/\[color=([^\]]*)\]\[quote([^\]]*)\]([\s\S]*?)\[\/quote\]\[\/color\]/gi, "[quote$2][color=$1]$3[/color][/quote]");
        rep(/\[(b|i|u)\]\[code\]([\s\S]*?)\[\/code\]\[\/\1\]/gi, "[code][$1]$2[/$1][/code]");
        rep(/\[color=([^\]]*)\]\[code\]([\s\S]*?)\[\/code\]\[\/color\]/gi, "[code][color=$1]$2[/color][/code]");
    } while (sc != content)

    //clean up empty tags
    do {
        sc = content;
        rep(/\[b\]\[\/b\]/gi, "");
        rep(/\[i\]\[\/i\]/gi, "");
        rep(/\[u\]\[\/u\]/gi, "");
        rep(/\[quote[^\]]*\]\[\/quote\]/gi, "");
        rep(/\[code\]\[\/code\]/gi, "");
        rep(/\[url=([^\]]+)\]\[\/url\]/gi, "");
        rep(/\[size=([^\]]+)\]\[\/size\]/gi, "");
        rep(/\[img\]\[\/img\]/gi, "");
        rep(/\[color=([^\]]*)\]\[\/color\]/gi, "");
    } while (sc != content)
}

function closeTags() {
    var currentContent = document.getElementById(body_id).value;
    currentContent = closeAllTags(currentContent, 'b');
    currentContent = closeAllTags(currentContent, 'i');
    currentContent = closeAllTags(currentContent, 'u');
    currentContent = closeAllTags(currentContent, 's');
    currentContent = closeAllTags(currentContent, 'left');
    currentContent = closeAllTags(currentContent, 'center');
    currentContent = closeAllTags(currentContent, 'rigth');
    currentContent = closeAllTags(currentContent, 'quote');
    currentContent = closeAllTags(currentContent, 'code');
    currentContent = closeAllTags(currentContent, 'img');
    currentContent = closeAllTags(currentContent, 'highlight');
    currentContent = closeAllTags(currentContent, 'list');

    currentContent = closeAllTags(currentContent, 'color');
    currentContent = closeAllTags(currentContent, 'size');
    currentContent = closeAllTags(currentContent, 'indent');
    currentContent = closeAllTags(currentContent, 'url');
    currentContent = currentContent.replace(/\[size\]/gi, '[size=10]');
    currentContent = currentContent.replace(/\[color\]/gi, '[color=000000]');
    currentContent = currentContent.replace(/\[url\]/gi, '[url=]');
    currentContent = currentContent.replace(/\[indent\]/gi, '[indent=15]');

    content = currentContent;
    document.getElementById(body_id).value = content;
}

function closeAllTags(text, tag) {
    var currentText = text;

    var regPrefix = new RegExp('\\[' + tag + '[^\\[^\\]]*\\]', 'ig');

    var regTags = new RegExp('(\\[' + tag + '[^\\[^\\]]*\\])([\\s\\S]*)(\\[\\/' + tag + '\\])([\\s\\S]*)', 'ig');

    var openTag = new RegExp('\\[' + tag + '(=[^\\[^\\]]*)?\\]', 'ig');
    var closeTag = new RegExp('\\[\\/' + tag + '\\]', 'ig');

    var prefIndex = currentText.search(regPrefix);
    var prefix = "";
    var postfix = "";

    var result = regTags.exec(currentText);
    if (result != null) {
        postfix = closeAllTags(result[4], tag);
        prefix = closeAllTags(currentText.substring(0, prefIndex), tag);
        while (result != null) {
            currentText = result[1] + closeAllTags(result[2], tag) + result[3];
            result = regTags.exec(currentText);
        }
        currentText = prefix + currentText + postfix;
    } else {

        var closeTagResult = closeTag.exec(currentText);
        if (closeTagResult != null) {
            while (closeTagResult != null) {

                var regAbstactTag = /\[[^\[^\]]*\]/gi;

                var intInd = closeTag.lastIndex;
                var tempText = currentText.substring(0, intInd - 3 - tag.length);
                var regAbstactTagRes = regAbstactTag.exec(tempText);
                if (regAbstactTagRes != null) {
                    while (regAbstactTagRes != null) {
                        var regAbstactTagIndex = regAbstactTag.lastIndex;
                        var regAbstactTagRes2 = regAbstactTag.exec(tempText);
                        if (regAbstactTagRes != null && regAbstactTagRes2 == null) {
                            var prefAndTag = tempText.substring(0, regAbstactTagIndex);
                            var cont = tempText.substring(regAbstactTagIndex, intInd - 3 - tag.length);
                            var postText = currentText.substring(intInd - 3 - tag.length, currentText.length);
                            currentText = prefAndTag + "[" + tag + "]" + cont + postText;
                        }
                        regAbstactTagRes = regAbstactTagRes2;
                    }
                } else {
                    currentText = "[" + tag + "]" + currentText;
                }
                closeTagResult = closeTag.exec(currentText);
            }
        } else {
            var openTagResult = openTag.exec(currentText);
            while (openTagResult != null) {
                var regAbstactTag1 = /\[[^\[^\]]*\]/gi;
                var intInd1 = openTag.lastIndex;
                var tempText1 = currentText.substring(intInd1, currentText.length);
                var regAbstactTagRes1 = regAbstactTag1.exec(tempText1);
                if (regAbstactTagRes1 != null) {
                    var regAbstactTagIndex1 = regAbstactTag1.lastIndex;
                    var prefAndTag1 = currentText.substring(0, intInd1+tempText1.length-regAbstactTagRes1[0].length);
                    var cont1 = tempText1.substring(regAbstactTagIndex1-regAbstactTagRes1[0].length, tempText1.length);
                    currentText = prefAndTag1  + "[/" + tag + "]"+ cont1;

                } else {
                    currentText = currentText + "[/" + tag + "]";
                }
                openTagResult = openTag.exec(currentText);
            }
        }
    }
    return currentText;
}

function bbcode2html() {
    // removing html tags
    rep(/\</gi, "&lt;");
    rep(/\>/gi, "&gt;");

    rep(/([\s\S]*?)\s*\[\/list\]/gi, "$1</li>[/list]");
    rep(/\[\*\]([\s\S]*?)\s*\[\*\]/gi, "<li>$1</li><li>");
    rep(/\[\*\]([\s\S]*?)\s*<\/li>/gi, "<li>$1</li>");
    rep(/\[list\]/gi, "<ul>");
    rep(/\[\/list\]/gi, "</ul>");
    rep(/\n/gi, "<br />");
    if (browser) {
        rep(/\[b\]/gi, "<strong>");
        rep(/\[\/b\]/gi, "</strong>");
        rep(/\[i\]/gi, "<em>");
        rep(/\[\/i\]/gi, "</em>");
        rep(/\[u\]/gi, "<u>");
        rep(/\[\/u\]/gi, "</u>");
        rep(/\[s\]/gi, "<s>");
        rep(/\[\/s\]/gi, "</s>");
    } else {
        rep(/\[b\]/gi, "<span style=\"font-weight: bold;\">");
        rep(/\[i\]/gi, "<span style=\"font-style: italic;\">");
        rep(/\[u\]/gi, "<span style=\"text-decoration: underline;\">");
        rep(/\[s\]/gi, "<span style=\"text-decoration: line-through;\">");
        rep(/\[\/(b|i|u|s)\]/gi, "</span>");
    }

    rep(/\[left\]/gi, '<p class="leftText">');
    rep(/\[right\]/gi, '<p class="rightText">');
    rep(/\[center\]/gi, '<p class="centerText">');
    rep(/\[\/(left|right|center)\]/gi, "</p>");

    rep(/\[quote="([^\[\]]*?)"\](.*?)\[\/quote\]/gi, '<div class="quote"><div class="quote_title">$1:</div><blockquote>$2</blockquote></div>');
    rep(/\[quote\]/gi, '<div class="quote"><div class="quote_title">Quote:</div><blockquote>');
    rep(/\[\/quote\]/gi, "</blockquote></div>");
    rep(/\[code\]/gi, '<div class="code">');
    rep(/\[\/code\]/gi, "</div>");

    rep(/\[highlight\]/gi, '<font class="highlight">');
    rep(/\[\/highlight\]/gi, "</font>");

    rep(/\[img\]([^\"]*?)\[\/img\]/gi, "<img src=\"$1\" />");
    var sc;
    do {
        sc = content;
        rep(/\[url=([^\]]+)\]([\s\S]*?)\[\/url\]/gi, "<a href=\"$1\">$2</a>");
        rep(/\[url\]([\s\S]*?)\[\/url\]/gi, "<a href=\"$1\">$1</a>");
        rep(/\[size=([^\]]+)\]([\s\S]*?)\[\/size\]/gi, '<font class="textSize$1">$2</font>');
        rep(/\[indent=([^\]]+)\]([\s\S]*?)\[\/indent\]/gi, '<font class="marginLeft$1">$2</font>');
        if (browser) {
            rep(/\[color=([^\]]*?)\]([\s\S]*?)\[\/color\]/gi, "<font color=\"$1\">$2</font>");
            rep(/\[font=([^\]]*?)\]([\s\S]*?)\[\/font\]/gi, "<font face=\"$1\">$2</font>");
        } else {
            rep(/\[color=([^\]]*?)\]([\s\S]*?)\[\/color\]/gi, "<span style=\"color: $1;\">$2</span>");
            rep(/\[font=([^\]]*?)\]([\s\S]*?)\[\/font\]/gi, "<span style=\"font-family: $1;\">$2</span>");
        }
    } while (sc != content);
}

function doCheck() {
    if (!editorVisible) {
        ShowEditor();
    }
    content = myeditor.body.innerHTML;
    html2bbcode();
    document.getElementById(body_id).value = content;
}

function stopEvent(evt) {
    evt || window.event;
    if (evt.stopPropagation) {
        evt.stopPropagation();
        evt.preventDefault();
    } else if (typeof evt.cancelBubble != "undefined") {
        evt.cancelBubble = true;
        evt.returnValue = false;
    }
    return false;
}

function doQuote() {
    if (editorVisible) {
        ifm.contentWindow.focus();
        if (isIE) {
            textRange = ifm.contentWindow.document.selection.createRange();
            var newTxt = "[quote]" + textRange.text + "[/quote]";
            textRange.text = newTxt;
        }
        else {
            var edittext = ifm.contentWindow.getSelection().getRangeAt(0);
            var original = edittext.toString();
            edittext.deleteContents();
            edittext.insertNode(document.createTextNode("[quote]" + original + "[/quote]"));
        }
    }
    else {
        AddTag('[quote]', '[/quote]');
    }
}

function kp(e) {
    if (isIE)
        var k = e.keyCode;
    else
        var k = e.which;
    if (k == 13) {
        if (isIE) {
            var r = myeditor.selection.createRange();
            if (r.parentElement().tagName.toLowerCase() != "li") {
                r.pasteHTML('<br/>');
                if (r.move('character'))
                    r.move('character', -1);
                r.select();
                stopEvent(e);
                return false;
            }
        }
    } else
        enter = 0;
}

function InsertSmile(txt) {
    InsertText(txt);
    document.getElementById('divSmilies').style.display = 'none';
}
function InsertYoutube() {
    InsertText("http://www.youtube.com/watch?v=XXXXXXXXXXX");
}
function InsertText(txt) {
    if (editorVisible)
        insertHtml(txt);
    else
        textboxelement.value += txt;
}

function doClick(command) {
    if (editorVisible) {
        ifm.contentWindow.focus();
        myeditor.execCommand(command, false, null);
    }
    else {
        switch (command) {
            case 'bold':
                AddTag('[b]', '[/b]');
                break;
            case 'code':
                AddTag('[code]', '[/code]');
                break;
            case 'italic':
                AddTag('[i]', '[/i]');
                break;
            case 'underline':
                AddTag('[u]', '[/u]');
                break;
            case 'line-through':
                AddTag('[s]', '[/s]');
                break;
            case 'highlight':
                AddTag('[highlight]', '[/highlight]');
                break;
            case 'left':
                AddTag('[left]', '[/left]');
                break;
            case 'right':
                AddTag('[right]', '[/right]');
                break;
            case 'center':
                AddTag('[center]', '[/center]');
                break;
            case 'InsertUnorderedList':
                AddList('[list][*]', '[/list]');
                break;
            case 'listElement':
                AddTag('[*]', '');
                break;
        }
    }
}

function doColor(color) {
    ifm.contentWindow.focus();
    if (isIE) {
        textRange = ifm.contentWindow.document.selection.createRange();
        textRange.select();
    }
    myeditor.execCommand('forecolor', false, color);
}

function doSize() {
    var listSizes = document.getElementById("select_size");
    var selectedIndex = listSizes.selectedIndex;
    if (selectedIndex >= 0) {
        var size = listSizes.options[selectedIndex].value;
        ifm.contentWindow.focus();
        if (editorVisible) {
            myeditor.execCommand('FontSize', false, size);
        } else {
            if (size > 0)
                AddTag('[size=' + size + ']', '[/size]');
        }
    }
}

function resetSizeSelector() {
    var listSizes = document.getElementById("select_size");
    listSizes.options[0].selected = 'selected';
}

function resetIndentSelector() {
    var listIndents = document.getElementById("select_indent");
    listIndents.options[0].selected = 'selected';
}

function doIndent() {
    var listIndents = document.getElementById("select_indent");
    var selectedIndex = listIndents.selectedIndex;
    if (selectedIndex >= 0) {
        var indent = listIndents.options[selectedIndex].value;
        ifm.contentWindow.focus();
        if (editorVisible) {
            myeditor.execCommand('FontIndent', false, indent);
        } else {
            if (indent > 0)
                AddTag('[indent=' + indent + ']', '[/indent]');
        }
    }
}

function doLink() {
    ifm.contentWindow.focus();
    var mylink = prompt("Enter a URL:", "http://");
    if (editorVisible) {
        if ((mylink != null) && (mylink != "")) {
            if (isIE) { //IE
                var range = ifm.contentWindow.document.selection.createRange();
                if (range.text == '') {
                    range.pasteHTML("<a href='" + mylink + "'>" + mylink + "</a>");
                }
                else
                    myeditor.execCommand("CreateLink", false, mylink);
            }
            else if (window.getSelection) { //FF
                var userSelection = ifm.contentWindow.getSelection().getRangeAt(0);
                if (userSelection.toString().length == 0)
                    myeditor.execCommand('inserthtml', false, "<a href='" + mylink + "'>" + mylink + "</a>");
                else
                    myeditor.execCommand("CreateLink", false, mylink);
            }
            else
                myeditor.execCommand("CreateLink", false, mylink);
        }
    }
    else {
        if ((mylink != null) && (mylink != "")) {
            AddTag('[url=' + mylink + ']', '[/url]');
        }
    }
}

function doImage() {
    ifm.contentWindow.focus();
    myimg = prompt('Enter Image URL:', 'http://');
    if (editorVisible) {
        if ((myimg != null) && (myimg != "")) {
            myeditor.execCommand('InsertImage', false, myimg);
        }
    }
    else {
        if ((myimg != null) && (myimg != "")) {
            AddTag('[img]' + myimg + '[/img]', '');
        }
    }
}

function insertHtml(html) {
    ifm.contentWindow.focus();
    if (isIE)
        ifm.contentWindow.document.selection.createRange().pasteHTML(html);
    else
        myeditor.execCommand('inserthtml', false, html);
}

//textarea-mode functions
function MozillaInsertText(element, text, pos) {
    element.value = element.value.slice(0, pos) + text + element.value.slice(pos);
}

function AddTag(t1, t2) {
    var element = textboxelement;
    if (isIE) {
        if (document.selection) {
            element.focus();

            var txt = element.value;
            var str = document.selection.createRange();

            if (str.text == "") {
                str.text = t1 + t2;
            }
            else if (txt.indexOf(str.text) >= 0) {
                str.text = t1 + str.text + t2;
            }
            else {
                element.value = txt + t1 + t2;
            }
            str.select();
        }
    }
    else if (typeof(element.selectionStart) != 'undefined') {
        var sel_start = element.selectionStart;
        var sel_end = element.selectionEnd;
        MozillaInsertText(element, t1, sel_start);
        MozillaInsertText(element, t2, sel_end + t1.length);
        element.selectionStart = sel_start;
        element.selectionEnd = sel_end + t1.length + t2.length;
        element.focus();
    }
    else {
        element.value = element.value + t1 + t2;
    }
}

function AddList(t1, t2) {
    var element = textboxelement;
    if (isIE) {
        if (document.selection) {
            element.focus();

            var txt = element.value;
            var str = document.selection.createRange();

            if (str.text == "") {
                str.text = t1 + t2;
            }
            else if (txt.indexOf(str.text) >= 0) {
                str.text = t1 + str.text + t2;
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


            str.select();
        }
    }
    else if (typeof(element.selectionStart) != 'undefined') {
        var sel_start = element.selectionStart;
        var sel_end = element.selectionEnd;
        MozillaInsertText(element, t1, sel_start);
        MozillaInsertText(element, t2, sel_end + t1.length);

        element.selectionStart = sel_start;
        element.selectionEnd = sel_end + t1.length + t2.length;

        sel_start = element.selectionStart;
        sel_end = element.selectionEnd;

        var value = element.value.substring(sel_start, sel_end);
        var nPos = value.indexOf("\n", '[list]'.length);
        if (nPos > 0) {
            value = value.replace(/\n/gi, "[*]");
        }
        var elvalue = element.value;
        value = value.replace(/\[list\]\[\*\]/gi, '[list]\n[*]');
        value = value.replace(/\[\/list\]/gi, '\n[/list]');
        element.value = elvalue.substring(0, sel_start) + value + elvalue.substring(sel_end, elvalue.length);


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
    if (editorVisible) doColor(val); else AddTag('[color=' + val + ']', '[/color]');
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
    if (pigMent != 'x') {
        setCCbldID2(pigMent, textBoxId);
        setCCbldSty2(Samp, 'bc', pigMent);
    }
    setCCbldSty2('colorpicker201', 'vs', 'hidden');
    setCCbldSty2('colorpicker201', 'ds', 'none');
}

function showColorGrid2(Sam, textBoxId) {
    var objX = new Array('00', '33', '66', '99', 'CC', 'FF');
    var c = 0;
    var xl = '"' + Sam + '","x", "' + textBoxId + '"';
    var mid = '';
    mid += '<table bgcolor="#FFFFFF" border="0" cellpadding="0" cellspacing="0" style="border:solid 0px #F0F0F0;padding:2px;"><tr>';
    mid += "<td colspan='9' align='left' style='margin:0;padding:2px;height:12px;' ><input class='o5582n66' type='text' size='12' id='o5582n66' value='#FFFFFF'><input class='o5582n66a' type='text' size='2' style='width:14px;' id='o5582n66a' onclick='javascript:alert(\"click on selected swatch below...\");' value='' style='border:solid 1px #666;'></td><td colspan='9' align='right'><a class='o5582n66' href='javascript:onclick=putOBJxColor2(" + xl + ")'><span class='a01p3'>" + clos1 + "</span></a></td></tr><tr>";
    var br = 1;
    for (o = 0; o < 6; o++) {
        mid += '</tr><tr>';
        for (y = 0; y < 6; y++) {
            if (y == 3) {
                mid += '</tr><tr>';
            }
            for (x = 0; x < 6; x++) {
                var grid = '';
                grid = objX[o] + objX[y] + objX[x];
                var b = "'" + Sam + "','" + grid + "', '" + textBoxId + "'";
                mid += '<td class="o5582brd" style="background-color:#' + grid + '"><a class="o5582n66"  href="javascript:onclick=putOBJxColor2(' + b + ');" onmouseover=javascript:document.getElementById("o5582n66").value="#' + grid + '";javascript:document.getElementById("o5582n66a").style.backgroundColor="#' + grid + '";  title="#' + grid + '"><div style="width:12px;height:14px;"></div></a></td>';
                c++;
            }
        }
    }
    mid += "</tr></table>";
    //var ttop=getTop2();
    //setCCbldSty2('colorpicker201','tp',ttop);
    //document.getElementById('colorpicker201').style.left=getLeft2();
    document.getElementById('colorpicker201').innerHTML = mid;
    setCCbldSty2('colorpicker201', 'vs', 'visible');
    setCCbldSty2('colorpicker201', 'ds', 'inline');
}