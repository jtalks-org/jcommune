// Copyright (C) 2010 Google Inc.
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
            // Whitespace is made up of spaces, tabs and newline characters.
            [PR['PR_PLAIN'],       /^[\t\n\r \xA0]+/, null, '\t\n\r \xA0'],
            // Not escaped as a string.  See note on minimalism above.
            [PR['PR_PLAIN'],       /^(?:\"(?:[^\"\\]|\\[\s\S])*(?:\"|$)|\'(?:[^\'\\]|\\[\s\S])+(?:\'|$)|`[^`]*(?:`|$))/, null, '"\'']
        ],
        [
            // Block comments are delimited by /* and */.
            // Single-line comments begin with // and extend to the end of a line.
            [PR['PR_COMMENT'],     /^(?:\/\/[^\r\n]*|\/\*[\s\S]*?\*\/)/],
            [PR['PR_PLAIN'],       /^(?:[^\/\"\'`]|\/(?![\/\*]))+/i]
        ]),
    ['go']);