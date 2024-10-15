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

import com.intellij.codeInsight.daemon.RelatedItemLineMarkerInfo
import com.intellij.codeInsight.daemon.RelatedItemLineMarkerProvider
import com.intellij.codeInsight.navigation.NavigationGutterIconBuilder
import com.intellij.psi.PsiElement
import com.linecorp.android.featureflag.ij.plugin.FeatureFlagBundle
import com.linecorp.android.featureflag.ij.plugin.icon.FeatureFlagIcons
import com.linecorp.android.featureflag.ij.plugin.FeatureFlagUtil

/**
 * Class for adding an icon to the gutter to jump to the referenced feature flag from the source
 * code.
 */
abstract class FeatureFlagLineMarkerProvider : RelatedItemLineMarkerProvider() {

    abstract fun findFeatureFlagElement(element: PsiElement): PsiElement?

    override fun collectNavigationMarkers(
        element: PsiElement,
        result: MutableCollection<in RelatedItemLineMarkerInfo<*>>
    ) {
        val valueElement = findFeatureFlagElement(element) ?: return
        val properties = FeatureFlagUtil.findProperties(element.project, valueElement.text)
        if (properties.isEmpty()) {
            return
        }
        val builder = NavigationGutterIconBuilder.create(FeatureFlagIcons.SINGLE_GUTTER)
            .setTargets(properties)
            .setTooltipText(FeatureFlagBundle.message("lineMarker.tooltip"))
        result.add(builder.createLineMarkerInfo(valueElement))
    }

    protected companion object {
        const val FEATURE_FLAG_CLASS_SUFFIX = "FeatureFlag"
    }
}
