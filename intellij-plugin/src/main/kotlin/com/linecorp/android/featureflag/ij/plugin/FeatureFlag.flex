/*
 * Copyright 2024 LY Corporation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.linecorp.android.featureflag.ij.plugin.lexer;

import com.intellij.lexer.FlexLexer;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.TokenType;
import com.linecorp.android.featureflag.ij.plugin.psi.FeatureFlagTypes;
import kotlin.text.StringsKt;

%%

%class FeatureFlagFlexLexer
%implements FlexLexer
%unicode
%function advance
%type IElementType
%eof{  return;
%eof}

LINE_TERMINATOR = \r|\n|\r\n
INPUT_CHARACTER = [^\r\n]
WHITE_SPACE     = {LINE_TERMINATOR} | [ \t\f]

CRLF=\R
VALUE_CHARACTER=[^ \t\f\n\f\\#,&]
PHASE_CHARACTERS = [a-zA-Z_-][a-zA-Z0-9_-]*
VERSION_CHARACTERS = [a-zA-Z0-9_\.\~\-]
LINE_COMMENT = #[^\r\n]*[\r\n]?
SEPARATOR = "="
KEY_CHARACTERS = [a-zA-Z_][a-zA-Z0-9_]*
MODIFIER_OVERRIDABLE = "OVERRIDABLE"
MODIFIER_PRIVATE = "PRIVATE"
MODIFIER_LITERALIZE = "LITERALIZE"
COMMA = ","
AND = "&"
COLON = ":"

%state WAITING_SEPARATOR
%state WAITING_VALUE
%state WAITING_NEXT_VALUE_OR_CRLF_OR_COMMENT
%state WAITING_VALUE_REFERENCE_SEPARATOR
%state WAITING_VALUE_REFERENCE_NAME

%%

<YYINITIAL> {LINE_COMMENT}                                  { yybegin(YYINITIAL); return FeatureFlagTypes.COMMENT; }

<YYINITIAL> {MODIFIER_OVERRIDABLE}                          { yybegin(YYINITIAL); return FeatureFlagTypes.MODIFIER_OVERRIDABLE; }

<YYINITIAL> {MODIFIER_PRIVATE}                              { yybegin(YYINITIAL); return FeatureFlagTypes.MODIFIER_PRIVATE; }

<YYINITIAL> {MODIFIER_LITERALIZE}                           { yybegin(YYINITIAL); return FeatureFlagTypes.MODIFIER_LITERALIZE; }

<YYINITIAL> {KEY_CHARACTERS}                                { yybegin(WAITING_SEPARATOR); return FeatureFlagTypes.KEY; }

<WAITING_SEPARATOR> {SEPARATOR}                             { yybegin(WAITING_VALUE); return FeatureFlagTypes.SEPARATOR; }

<WAITING_VALUE> {VALUE_CHARACTER}+                          {
                                                                CharSequence text = yytext().toString();
                                                                if (StringsKt.startsWith(text, '@', false)) {
                                                                    yybegin(WAITING_NEXT_VALUE_OR_CRLF_OR_COMMENT);
                                                                    return FeatureFlagTypes.VALUE_USERNAME;
                                                                } else if (StringsKt.endsWith(text, '~', false)) {
                                                                    yybegin(WAITING_NEXT_VALUE_OR_CRLF_OR_COMMENT);
                                                                    return FeatureFlagTypes.VALUE_VERSION;
                                                                }
                                                                int colonIndex = text.toString().indexOf(':');
                                                                if (colonIndex == -1) {
                                                                    yybegin(WAITING_NEXT_VALUE_OR_CRLF_OR_COMMENT);
                                                                    return FeatureFlagTypes.VALUE_STRING;
                                                                }
                                                                yypushback(text.length() - colonIndex);
                                                                yybegin(WAITING_VALUE_REFERENCE_SEPARATOR);
                                                                return FeatureFlagTypes.VALUE_REFERENCE_PACKAGE;
                                                            }

<WAITING_VALUE_REFERENCE_SEPARATOR>     {COLON}             { yybegin(WAITING_VALUE_REFERENCE_NAME); return FeatureFlagTypes.COLON; }

<WAITING_VALUE_REFERENCE_NAME>          {VALUE_CHARACTER}+  { yybegin(WAITING_NEXT_VALUE_OR_CRLF_OR_COMMENT); return FeatureFlagTypes.VALUE_REFERENCE_NAME; }

<WAITING_NEXT_VALUE_OR_CRLF_OR_COMMENT> {COMMA}             { yybegin(WAITING_VALUE); return FeatureFlagTypes.COMMA; }

<WAITING_NEXT_VALUE_OR_CRLF_OR_COMMENT> {AND}               { yybegin(WAITING_VALUE); return FeatureFlagTypes.AND; }

<WAITING_NEXT_VALUE_OR_CRLF_OR_COMMENT> {LINE_TERMINATOR}+  { yybegin(YYINITIAL); return TokenType.WHITE_SPACE; }

<WAITING_NEXT_VALUE_OR_CRLF_OR_COMMENT> {LINE_COMMENT}      { yybegin(YYINITIAL); return FeatureFlagTypes.COMMENT; }

{WHITE_SPACE}+                                              { return TokenType.WHITE_SPACE; }

{LINE_TERMINATOR}                                           { yybegin(YYINITIAL); return TokenType.WHITE_SPACE; }

[^]                                                         { return TokenType.BAD_CHARACTER; }
