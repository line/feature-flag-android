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

package com.linecorp.android.featureflag.ij.plugin.navigationbar

import com.intellij.ide.navigationToolbar.StructureAwareNavBarModelExtension
import com.intellij.lang.Language
import com.linecorp.android.featureflag.ij.plugin.psi.FeatureFlagFile
import com.linecorp.android.featureflag.ij.plugin.FeatureFlagLanguage
import com.linecorp.android.featureflag.ij.plugin.icon.FeatureFlagIcons
import com.linecorp.android.featureflag.ij.plugin.psi.FeatureFlagProperty
import javax.swing.Icon

/**
 * A class for providing navigation bar data when opening feature flag file.
 */
class FeatureFlagStructureAwareNavbar : StructureAwareNavBarModelExtension() {

    override val language: Language = FeatureFlagLanguage

    override fun getPresentableText(any: Any?): String? {
        if (any is FeatureFlagFile) {
            return any.name
        }
        if (any is FeatureFlagProperty) {
            return any.key
        }
        return null
    }

    override fun getIcon(any: Any?): Icon? {
        if (any is FeatureFlagProperty) {
            return FeatureFlagIcons.SINGLE_PROPERTY
        }
        return null
    }
}
