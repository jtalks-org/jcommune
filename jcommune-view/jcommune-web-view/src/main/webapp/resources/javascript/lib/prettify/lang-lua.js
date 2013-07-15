// Copyright (C) 2008 Google Inc.
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

PR['registerLangHandler'](
    PR['createSimpleLexer'](
        [
            // Whitespace
            [PR['PR_PLAIN'],       /^[\t\n\r \xA0]+/, null, '\t\n\r \xA0'],
            // A double or single quoted, possibly multi-line, string.
            [PR['PR_STRING'],      /^(?:\"(?:[^\"\\]|\\[\s\S])*(?:\"|$)|\'(?:[^\'\\]|\\[\s\S])*(?:\'|$))/, null, '"\'']
        ],
        [
            // A comment is either a line comment that starts with two dashes, or
            // two dashes preceding a long bracketed block.
            [PR['PR_COMMENT'], /^--(?:\[(=*)\[[\s\S]*?(?:\]\1\]|$)|[^\r\n]*)/],
            // A long bracketed block not preceded by -- is a string.
            [PR['PR_STRING'],  /^\[(=*)\[[\s\S]*?(?:\]\1\]|$)/],
            [PR['PR_KEYWORD'], /^(?:and|break|do|else|elseif|end|false|for|function|if|in|local|nil|not|or|repeat|return|then|true|until|while)\b/, null],
            // A number is a hex integer literal, a decimal real literal, or in
            // scientific notation.
            [PR['PR_LITERAL'],
                /^[+-]?(?:0x[\da-f]+|(?:(?:\.\d+|\d+(?:\.\d*)?)(?:e[+\-]?\d+)?))/i],
            // An identifier
            [PR['PR_PLAIN'], /^[a-z_]\w*/i],
            // A run of punctuation
            [PR['PR_PUNCTUATION'], /^[^\w\t\n\r \xA0][^\w\t\n\r \xA0\"\'\-\+=]*/]
        ]),
    ['lua']);