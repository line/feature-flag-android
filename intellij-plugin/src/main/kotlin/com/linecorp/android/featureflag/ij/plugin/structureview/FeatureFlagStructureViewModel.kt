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

import com.intellij.icons.AllIcons
import com.intellij.ide.structureView.StructureViewModel
import com.intellij.ide.structureView.StructureViewModelBase
import com.intellij.ide.structureView.StructureViewTreeElement
import com.intellij.ide.util.treeView.smartTree.ActionPresentation
import com.intellij.ide.util.treeView.smartTree.ActionPresentationData
import com.intellij.ide.util.treeView.smartTree.Sorter
import com.intellij.openapi.editor.Editor
import com.intellij.psi.PsiFile
import com.linecorp.android.featureflag.ij.plugin.FeatureFlagBundle
import com.linecorp.android.featureflag.ij.plugin.psi.FeatureFlagProperty

/**
 * A view model for the structure view of the feature flag file.
 */
class FeatureFlagStructureViewModel(
    editor: Editor?,
    psiFile: PsiFile
) : StructureViewModelBase(psiFile, editor, FeatureFlagFileStructureViewElement(psiFile)),
    StructureViewModel.ElementInfoProvider {

    override fun getSorters(): Array<Sorter> = arrayOf(Sorter.ALPHA_SORTER, ModifierSorter)

    override fun isAlwaysShowsPlus(element: StructureViewTreeElement?): Boolean = false

    override fun isAlwaysLeaf(element: StructureViewTreeElement): Boolean =
        element is FeatureFlagPropertyStructureViewElement

    override fun getSuitableClasses(): Array<Class<*>> = arrayOf(FeatureFlagProperty::class.java)

    /**
     * A sorter for [FeatureFlagPropertyStructureViewElement].
     */
    private object ModifierSorter : Sorter {

        private val PRESENTATION = ActionPresentationData(
            FeatureFlagBundle.message("structureView.modifier"),
            null,
            AllIcons.ObjectBrowser.Sorted
        )

        override fun getComparator(): Comparator<*> = ModifierComparator

        override fun isVisible(): Boolean = true

        override fun toString(): String = name

        override fun getPresentation(): ActionPresentation = PRESENTATION

        override fun getName(): String = "FEATURE_FLAG_MODIFIER_COMPARATOR"

        /**
         * A comparator for [FeatureFlagPropertyStructureViewElement].
         */
        private object ModifierComparator : Comparator<Any> {
            override fun compare(left: Any, right: Any): Int {
                val leftModifier =
                    (left as? FeatureFlagPropertyStructureViewElement)?.getModifierSortKey() ?: -1
                val rightModifier =
                    (right as? FeatureFlagPropertyStructureViewElement)?.getModifierSortKey() ?: -1
                return leftModifier.compareTo(rightModifier)
            }
        }
    }
}
