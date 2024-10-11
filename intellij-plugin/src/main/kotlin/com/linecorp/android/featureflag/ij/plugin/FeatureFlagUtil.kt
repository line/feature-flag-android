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

package com.linecorp.android.featureflag.ij.plugin

import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.PsiComment
import com.intellij.psi.PsiManager
import com.intellij.psi.search.FileTypeIndex
import com.intellij.psi.search.GlobalSearchScope
import com.intellij.psi.util.PsiTreeUtil
import com.linecorp.android.featureflag.ij.plugin.psi.FeatureFlagFile
import com.linecorp.android.featureflag.ij.plugin.psi.FeatureFlagProperty

/**
 * A collection of utility functions for feature flag plugin.
 */
object FeatureFlagUtil {
    fun findProperties(project: Project, key: String): List<FeatureFlagProperty> {
        val psiManager = PsiManager.getInstance(project)
        val virtualFiles = FileTypeIndex.getFiles(
            FeatureFlagFileType,
            GlobalSearchScope.allScope(project)
        )
        return virtualFiles.getAllFeatureFlagProperties(psiManager)
            .flatMap { properties -> properties.filter { it.key == key } }
            .toList()
    }

    fun findProperties(project: Project): List<FeatureFlagProperty> {
        val psiManager = PsiManager.getInstance(project)
        val virtualFiles = FileTypeIndex.getFiles(
            FeatureFlagFileType,
            GlobalSearchScope.allScope(project)
        )
        return virtualFiles.getAllFeatureFlagProperties(psiManager)
            .flatMap { it.asList() }
            .toList()
    }

    fun findDocumentationComment(property: FeatureFlagProperty): String {
        val commentLines =
            generateSequence(property.prevSibling as? PsiComment) { it.prevSibling as? PsiComment }
                .map { it.text.replaceFirst("#", "") }
                .toList()
                .reversed()
        val commentBuilder = StringBuilder()
        var currentParagraphIndentDepth: Int? = null
        for (line in commentLines) {
            val indentDepth = line.takeWhile { it == ' ' }.length
            // If a line break is included, it is basically recognized as a new line, but if a
            // single space is added at the beginning, it is recognized as a continuation of the
            // previous line.
            when (currentParagraphIndentDepth) {
                null -> currentParagraphIndentDepth = indentDepth
                indentDepth - 1 -> commentBuilder.append(" ")
                else -> commentBuilder.append("\n")
            }
            commentBuilder.append(line.drop(indentDepth).trimEnd())
        }
        return commentBuilder.toString()
    }

    private fun Collection<VirtualFile?>.getAllFeatureFlagProperties(
        psiManager: PsiManager
    ): Sequence<Array<FeatureFlagProperty>> = asSequence()
        .filterNotNull()
        .mapNotNull { psiManager.findFile(it) as? FeatureFlagFile }
        .mapNotNull { PsiTreeUtil.getChildrenOfType(it, FeatureFlagProperty::class.java) }
}
