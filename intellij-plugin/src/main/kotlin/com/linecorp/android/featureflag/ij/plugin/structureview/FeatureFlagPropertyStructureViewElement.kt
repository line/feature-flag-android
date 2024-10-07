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

package com.linecorp.android.featureflag.ij.plugin.structureview

import com.intellij.ide.structureView.StructureViewTreeElement
import com.intellij.ide.util.treeView.smartTree.TreeElement
import com.intellij.navigation.ItemPresentation
import com.linecorp.android.featureflag.ij.plugin.psi.FeatureFlagProperty

/**
 * A data representing a single property of feature flag for the structure view.
 */
class FeatureFlagPropertyStructureViewElement(
    private val psiElement: FeatureFlagProperty
) : StructureViewTreeElement {

    override fun getValue(): Any = psiElement

    override fun navigate(requestFocus: Boolean) = psiElement.navigate(requestFocus)

    override fun canNavigate(): Boolean = psiElement.canNavigate()

    override fun canNavigateToSource(): Boolean = psiElement.canNavigateToSource()

    fun getModifierSortKey(): Int {
        val modifier = psiElement.modifier?.modifierType
        return modifier?.ordinal ?: -1
    }

    override fun getPresentation(): ItemPresentation =
        FeatureFlagPropertyStructureViewPresentation(
            psiElement.key.orEmpty(),
            psiElement.modifier?.modifierType
        )

    override fun getChildren(): Array<TreeElement> = emptyArray()
}
