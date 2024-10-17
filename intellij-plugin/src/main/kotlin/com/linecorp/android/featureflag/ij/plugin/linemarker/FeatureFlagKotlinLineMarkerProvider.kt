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

package com.linecorp.android.featureflag.ij.plugin.linemarker

import com.intellij.psi.PsiElement
import org.jetbrains.kotlin.idea.references.mainReference
import org.jetbrains.kotlin.psi.KtNameReferenceExpression
import com.intellij.psi.PsiField
import com.intellij.psi.impl.source.tree.LeafPsiElement
import org.jetbrains.kotlin.lexer.KtTokens

/**
 * An implementation of [FeatureFlagLineMarkerProvider] for Kotlin.
 */
class FeatureFlagKotlinLineMarkerProvider : FeatureFlagLineMarkerProvider() {

    override fun isFeatureFlagPropertyIdentifier(element: PsiElement): Boolean {
        if (element !is LeafPsiElement || element.elementType != KtTokens.IDENTIFIER) {
            return false
        }
        val entireFlagNameReferenceExpression =
            element.parent as? KtNameReferenceExpression ?: return false
        val resolvedField =
            entireFlagNameReferenceExpression.mainReference.resolve() as? PsiField ?: return false
        return isFeatureFlagField(resolvedField)
    }
}
