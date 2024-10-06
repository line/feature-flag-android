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

package com.linecorp.android.featureflag.ij.plugin.gotosymbol

import com.intellij.navigation.ChooseByNameContributorEx
import com.intellij.navigation.NavigationItem
import com.intellij.psi.search.GlobalSearchScope
import com.intellij.util.Processor
import com.intellij.util.indexing.FindSymbolParameters
import com.intellij.util.indexing.IdFilter
import com.linecorp.android.featureflag.ij.plugin.FeatureFlagUtil

/**
 * A class for providing feature flag properties for "Navigate | Symbol" search results.
 */
class FeatureFlagChooseByNameContributor : ChooseByNameContributorEx {

    override fun processNames(
        processor: Processor<in String?>,
        scope: GlobalSearchScope,
        filter: IdFilter?
    ) {
        val project = checkNotNull(scope.project)
        val properties = FeatureFlagUtil.findProperties(project)
        for (property in properties) {
            processor.process(property.getKey())
        }
    }

    override fun processElementsWithName(
        name: String,
        processor: Processor<in NavigationItem?>,
        parameters: FindSymbolParameters
    ) {
        val properties = FeatureFlagUtil.findProperties(parameters.project, name)
        for (property in properties) {
            processor.process(property)
        }
    }
}
