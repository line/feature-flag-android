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
import org.jetbrains.kotlin.idea.caches.resolve.analyze
import org.jetbrains.kotlin.psi.KtDotQualifiedExpression
import org.jetbrains.kotlin.psi.KtNameReferenceExpression
import org.jetbrains.kotlin.resolve.BindingContext

/**
 * An implementation of [FeatureFlagLineMarkerProvider] for Kotlin.
 */
class FeatureFlagKotlinLineMarkerProvider : FeatureFlagLineMarkerProvider() {

    override fun findFeatureFlagElement(element: PsiElement): PsiElement? {
        if (element !is KtNameReferenceExpression) {
            return null
        }
        val parent = element.parent as? KtDotQualifiedExpression ?: return null

        if (!element.text.endsWith(FEATURE_FLAG_CLASS_SUFFIX)) {
            // In case of import alias or type alias.
            val originalTypeName =
                element.analyze().get(BindingContext.REFERENCE_TARGET, element)?.name?.asString()
            if (originalTypeName != FEATURE_FLAG_CLASS_SUFFIX) {
                return null
            }
        }

        val featureFlagWholeElement = parent.takeUnless { parent.text.endsWith(element.text) }
            ?: parent.parent as? KtDotQualifiedExpression
            ?: return null
        val flagNameReferenceExpression =
            featureFlagWholeElement.lastChild as? KtNameReferenceExpression ?: return null
        return flagNameReferenceExpression.getIdentifier()
    }
}
