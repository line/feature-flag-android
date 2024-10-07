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

import com.intellij.ide.projectView.PresentationData
import com.intellij.ui.RowIcon
import com.intellij.util.ui.EmptyIcon
import com.linecorp.android.featureflag.ij.plugin.icon.FeatureFlagIcons
import com.linecorp.android.featureflag.ij.plugin.psi.FeatureFlagModifierType
import javax.swing.Icon

/**
 * A presentation data of single property in structure view.
 */
class FeatureFlagPropertyStructureViewPresentation(
    private val flagName: String,
    private val modifier: FeatureFlagModifierType?
) : PresentationData() {

    override fun getPresentableText(): String = flagName

    override fun getIcon(open: Boolean): Icon {
        val modifierIcon = when (modifier) {
            FeatureFlagModifierType.OVERRIDABLE -> FeatureFlagIcons.STRUCTURE_VIEW_OVERRIDABLE
            FeatureFlagModifierType.PRIVATE -> FeatureFlagIcons.STRUCTURE_VIEW_PRIVATE
            FeatureFlagModifierType.LITERALIZE -> FeatureFlagIcons.STRUCTURE_VIEW_LITERALIZE
            null -> EmptyIcon.ICON_16
        }
        return RowIcon(FeatureFlagIcons.SINGLE_PROPERTY, modifierIcon)
    }
}
