// Copyright (C) 2009 Google Inc.
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//      http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

// The comments inline below refer to productions in the CSS specification's
// lexical grammar.  See link above.
PR['registerLangHandler'](
    PR['createSimpleLexer'](
        // Shortcut patterns.
        [
            // The space production <s>
            [PR['PR_PLAIN'],       /^[ \t\r\n\f]+/, null, ' \t\r\n\f']
        ],
        // Fall-through patterns.
        [
            // Quoted strings.  <string1> and <string2>
            [PR['PR_STRING'],
                /^\"(?:[^\n\r\f\\\"]|\\(?:\r\n?|\n|\f)|\\[\s\S])*\"/, null],
            [PR['PR_STRING'],
                /^\'(?:[^\n\r\f\\\']|\\(?:\r\n?|\n|\f)|\\[\s\S])*\'/, null],
            ['lang-css-str', /^url\(([^\)\"\']+)\)/i],
            [PR['PR_KEYWORD'],
                /^(?:url|rgb|\!important|@import|@page|@media|@charset|inherit)(?=[^\-\w]|$)/i,
                null],
            // A property name -- an identifier followed by a colon.
            ['lang-css-kw', /^(-?(?:[_a-z]|(?:\\[0-9a-f]+ ?))(?:[_a-z0-9\-]|\\(?:\\[0-9a-f]+ ?))*)\s*:/i],
            // A C style block comment.  The <comment> production.
            [PR['PR_COMMENT'], /^\/\*[^*]*\*+(?:[^\/*][^*]*\*+)*\//],
            // Escaping text spans
            [PR['PR_COMMENT'], /^(?:<!--|-->)/],
            // A number possibly containing a suffix.
            [PR['PR_LITERAL'], /^(?:\d+|\d*\.\d+)(?:%|[a-z]+)?/i],
            // A hex color
            [PR['PR_LITERAL'], /^#(?:[0-9a-f]{3}){1,2}\b/i],
            // An identifier
            [PR['PR_PLAIN'],
                /^-?(?:[_a-z]|(?:\\[\da-f]+ ?))(?:[_a-z\d\-]|\\(?:\\[\da-f]+ ?))*/i],
            // A run of punctuation
            [PR['PR_PUNCTUATION'], /^[^\s\w\'\"]+/]
        ]),
    ['css']);
// Above we use embedded languages to highlight property names (identifiers
// followed by a colon) differently from identifiers in values.
PR['registerLangHandler'](
    PR['createSimpleLexer']([],
        [
            [PR['PR_KEYWORD'],
                /^-?(?:[_a-z]|(?:\\[\da-f]+ ?))(?:[_a-z\d\-]|\\(?:\\[\da-f]+ ?))*/i]
        ]),
    ['css-kw']);
// The content of an unquoted URL literal like url(http://foo/img.png) should
// be colored as string content.  This language handler is used above in the
// URL production to do so.
PR['registerLangHandler'](
    PR['createSimpleLexer']([],
        [
            [PR['PR_STRING'], /^[^\)\"\']+/]
        ]),
    ['css-str']);