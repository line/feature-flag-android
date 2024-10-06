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
import com.intellij.psi.PsiComment
import com.intellij.psi.PsiElement
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
        return virtualFiles.asSequence()
            .filterNotNull()
            .mapNotNull { psiManager.findFile(it) as? FeatureFlagFile }
            .mapNotNull { PsiTreeUtil.getChildrenOfType(it, FeatureFlagProperty::class.java) }
            .flatMap { properties -> properties.filter { it.key == key } }
            .toList()
    }

    fun findProperties(project: Project): List<FeatureFlagProperty> {
        val psiManager = PsiManager.getInstance(project)
        val virtualFiles = FileTypeIndex.getFiles(
            FeatureFlagFileType,
            GlobalSearchScope.allScope(project)
        )
        return virtualFiles.asSequence()
            .filterNotNull()
            .mapNotNull { psiManager.findFile(it) as? FeatureFlagFile }
            .mapNotNull { PsiTreeUtil.getChildrenOfType(it, FeatureFlagProperty::class.java) }
            .flatMap { it.asList() }
            .toList()
    }

    fun findDocumentationComment(property: FeatureFlagProperty): String {
        val result = mutableListOf<String>()
        var element: PsiElement? = property.prevSibling
        while (element is PsiComment) {
            result.add(0, element.getText().replaceFirst("#", ""))
            element = element.prevSibling
        }
        val commentBuilder = StringBuilder()
        var currentLineSpace: Int? = null
        for (line in result) {
            val lineSpace = line.takeWhile { it == ' ' }.length
            if (currentLineSpace == null) {
                commentBuilder.append(line.substring(lineSpace).trimEnd())
                currentLineSpace = lineSpace
            } else if (lineSpace == currentLineSpace + 1) {
                commentBuilder.append(line.substring(currentLineSpace).trimEnd())
            } else {
                commentBuilder.append("\n")
                commentBuilder.append(line.substring(lineSpace).trimEnd())
                currentLineSpace = lineSpace
            }
        }
        return commentBuilder.toString()
    }
}
