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

package com.linecorp.android.featureflag.ij.plugin.syntaxhighlight

import com.intellij.lexer.Lexer
import com.intellij.openapi.editor.HighlighterColors
import com.intellij.openapi.editor.colors.TextAttributesKey
import com.intellij.openapi.editor.colors.TextAttributesKey.createTextAttributesKey
import com.intellij.openapi.fileTypes.SyntaxHighlighterBase
import com.intellij.psi.TokenType
import com.intellij.psi.tree.IElementType
import com.linecorp.android.featureflag.ij.plugin.lexer.FeatureFlagLexer
import com.linecorp.android.featureflag.ij.plugin.psi.FeatureFlagTypes
import com.intellij.openapi.editor.DefaultLanguageHighlighterColors as DefaultHighlighterColors

/**
 * A syntax highlighter for the feature flag language.
 */
class FeatureFlagSyntaxHighlighter : SyntaxHighlighterBase() {

    override fun getHighlightingLexer(): Lexer = FeatureFlagLexer()

    override fun getTokenHighlights(tokenType: IElementType): Array<out TextAttributesKey> =
        when (tokenType) {
            FeatureFlagTypes.SEPARATOR -> SEPARATOR_KEYS
            FeatureFlagTypes.KEY -> KEY_KEYS
            FeatureFlagTypes.VALUE_STRING -> VALUE_STRING_KEYS
            FeatureFlagTypes.VALUE_VERSION -> VALUE_VERSION_KEYS
            FeatureFlagTypes.VALUE_USERNAME -> VALUE_USERNAME_KEYS
            FeatureFlagTypes.VALUE_REFERENCE_PACKAGE -> VALUE_REFERENCE_PACKAGE_KEYS
            FeatureFlagTypes.VALUE_REFERENCE_NAME -> VALUE_REFERENCE_NAME_KEYS
            FeatureFlagTypes.COMMENT -> COMMENT_KEYS
            FeatureFlagTypes.MODIFIER_OVERRIDABLE,
            FeatureFlagTypes.MODIFIER_PRIVATE,
            FeatureFlagTypes.MODIFIER_LITERALIZE -> MODIFIER_KEYS

            FeatureFlagTypes.COMMA -> COMMA_KEYS
            FeatureFlagTypes.AND -> AND_KEYS
            FeatureFlagTypes.COLON -> COLON_KEYS
            TokenType.BAD_CHARACTER -> BAD_CHAR_KEYS
            else -> EMPTY_KEYS
        }

    companion object {
        private val BAD_CHAR_KEYS: Array<TextAttributesKey> =
            textAttributesKeysOf("FeatureFlag_BAD_CHARACTER" to HighlighterColors.BAD_CHARACTER)

        private val SEPARATOR_KEYS: Array<TextAttributesKey> =
            textAttributesKeysOf("FeatureFlag_SEPARATOR" to DefaultHighlighterColors.OPERATION_SIGN)

        private val KEY_KEYS: Array<TextAttributesKey> =
            textAttributesKeysOf("FeatureFlag_KEY" to DefaultHighlighterColors.IDENTIFIER)

        private val VALUE_STRING_KEYS: Array<TextAttributesKey> =
            textAttributesKeysOf("FeatureFlag_VALUE_STRING" to DefaultHighlighterColors.STRING)

        private val VALUE_VERSION_KEYS: Array<TextAttributesKey> =
            textAttributesKeysOf("FeatureFlag_VALUE_VERSION" to DefaultHighlighterColors.NUMBER)

        private val VALUE_USERNAME_KEYS: Array<TextAttributesKey> =
            textAttributesKeysOf(
                "FeatureFlag_VALUE_USERNAME" to DefaultHighlighterColors.INSTANCE_FIELD
            )

        private val VALUE_REFERENCE_PACKAGE_KEYS: Array<TextAttributesKey> =
            textAttributesKeysOf(
                "FeatureFlag_VALUE_REFERENCE_PACKAGE" to DefaultHighlighterColors.STATIC_FIELD
            )

        private val VALUE_REFERENCE_NAME_KEYS: Array<TextAttributesKey> =
            textAttributesKeysOf(
                "FeatureFlag_VALUE_REFERENCE_VALUE" to DefaultHighlighterColors.STRING
            )

        private val COMMENT_KEYS: Array<TextAttributesKey> =
            textAttributesKeysOf("FeatureFlag_COMMENT" to DefaultHighlighterColors.LINE_COMMENT)

        private val MODIFIER_KEYS: Array<TextAttributesKey> =
            textAttributesKeysOf("FeatureFlag_MODIFIER" to DefaultHighlighterColors.KEYWORD)

        private val COMMA_KEYS: Array<TextAttributesKey> =
            textAttributesKeysOf("FeatureFlag_COMMA" to DefaultHighlighterColors.COMMA)

        private val AND_KEYS: Array<TextAttributesKey> =
            textAttributesKeysOf("FeatureFlag_AND" to DefaultHighlighterColors.MARKUP_TAG)

        private val COLON_KEYS: Array<TextAttributesKey> =
            textAttributesKeysOf("FeatureFlag_COLON" to DefaultHighlighterColors.MARKUP_TAG)

        private val EMPTY_KEYS: Array<TextAttributesKey> = emptyArray()

        private fun textAttributesKeysOf(
            vararg keys: Pair<String, TextAttributesKey>
        ): Array<TextAttributesKey> =
            Array(keys.size) { createTextAttributesKey(keys[it].first, keys[it].second) }
    }
}
