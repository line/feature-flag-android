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

package com.linecorp.android.featureflag.ij.plugin.documentation

import com.intellij.lang.documentation.AbstractDocumentationProvider
import com.intellij.lang.documentation.DocumentationMarkup
import com.intellij.openapi.editor.DefaultLanguageHighlighterColors
import com.intellij.openapi.editor.richcopy.HtmlSyntaxInfoUtil
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiClass
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiField
import com.linecorp.android.featureflag.ij.plugin.FeatureFlagBundle
import com.linecorp.android.featureflag.ij.plugin.FeatureFlagLanguage
import com.linecorp.android.featureflag.ij.plugin.FeatureFlagUtil

/**
 * A class which provides documentation for feature flag reference in Java or Kotlin code.
 */
class FeatureFlagDocumentationProvider : AbstractDocumentationProvider() {

    override fun generateDoc(element: PsiElement, originalElement: PsiElement?): String? {
        val (propertyName, currentValue) = getFeatureFlagProperty(element) ?: return null
        val property = FeatureFlagUtil.findProperties(element.project, propertyName).firstOrNull()
            ?: return null
        val docComment = FeatureFlagUtil.findDocumentationComment(property)
        return renderFullDoc(element.project, property.text, currentValue, docComment)
    }

    override fun generateHoverDoc(element: PsiElement, originalElement: PsiElement?): String? =
        generateDoc(element, originalElement)

    override fun getQuickNavigateInfo(element: PsiElement, originalElement: PsiElement?): String? {
        val (propertyName, currentValue) = getFeatureFlagProperty(element) ?: return null
        val property = FeatureFlagUtil.findProperties(element.project, propertyName).firstOrNull()
            ?: return null
        val text = HtmlSyntaxInfoUtil.getHighlightedByLexerAndEncodedAsHtmlCodeSnippet(
            element.project,
            FeatureFlagLanguage,
            property.text,
            1.0f
        )
        val currentValueString = createCurrentValueHighlightedString(currentValue)
        val currentValueText = FeatureFlagBundle.message("documentation.valueInCurrentClasspath")
        return "$text<br>$currentValueText: $currentValueString"
    }

    private fun renderFullDoc(
        project: Project,
        definition: String,
        currentValue: Boolean?,
        docComment: String
    ): String = buildString {
        append(DocumentationMarkup.DEFINITION_START)
        append(
            HtmlSyntaxInfoUtil.getHighlightedByLexerAndEncodedAsHtmlCodeSnippet(
                project,
                FeatureFlagLanguage,
                definition,
                1.0f
            )
        )
        append(DocumentationMarkup.DEFINITION_END)
        append(DocumentationMarkup.CONTENT_START)
        append(docComment.applyWebLink().replace("\n", "<br>"))
        append(DocumentationMarkup.CONTENT_END)
        append(DocumentationMarkup.SECTIONS_START)
        append(DocumentationMarkup.SECTION_HEADER_START)
        append(FeatureFlagBundle.message("documentation.valueInCurrentClasspath"))
        append(":")
        append(DocumentationMarkup.SECTION_SEPARATOR)
        append("<p>${createCurrentValueHighlightedString(currentValue)}</p>")
        append(DocumentationMarkup.SECTION_END)
        append(DocumentationMarkup.SECTIONS_END)
    }

    private fun getFeatureFlagProperty(element: PsiElement): Pair<String, Boolean?>? {
        if (element !is PsiField) {
            return null
        }
        val parent = element.parent as? PsiClass ?: return null
        val className = parent.qualifiedName ?: return null
        if (!className.endsWith("FeatureFlag")) {
            return null
        }
        val currentValue = when (element.initializer?.text) {
            "true",
            "Boolean.valueOf(true)" -> true

            "false",
            "Boolean.valueOf(false)" -> false

            else -> null
        }
        return element.name to currentValue
    }

    private fun createCurrentValueHighlightedString(currentValue: Boolean?): String =
        if (currentValue != null) {
            HtmlSyntaxInfoUtil.getStyledSpan(
                DefaultLanguageHighlighterColors.KEYWORD,
                currentValue.toString(),
                1.0f
            )
        } else {
            FeatureFlagBundle.message("documentation.notGeneratedYet")
        }

    private fun String.applyWebLink(): String =
        replace(SIMPLE_URL_PATTERN) { "<a href=\"${it.value}\">${it.value}</a>" }

    companion object {
        private val SIMPLE_URL_PATTERN: Regex = """https?://\S*""".toRegex()
    }
}
