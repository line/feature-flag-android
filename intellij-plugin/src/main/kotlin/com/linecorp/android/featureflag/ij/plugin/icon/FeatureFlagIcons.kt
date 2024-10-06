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

package com.linecorp.android.featureflag.ij.plugin.icon

import com.intellij.openapi.util.IconLoader
import javax.swing.Icon

/**
 * A list of icons to be used in the plugin.
 */
object FeatureFlagIcons {
    val FILE: Icon =
        IconLoader.getIcon("icons/featureFlagFile.svg", FeatureFlagIcons::class.java)
    val SINGLE_GUTTER: Icon =
        IconLoader.getIcon("icons/featureFlagSingleGutter.svg", FeatureFlagIcons::class.java)
    val SINGLE_PROPERTY: Icon =
        IconLoader.getIcon("icons/featureFlagSingleProperty.svg", FeatureFlagIcons::class.java)
    val STRUCTURE_VIEW_OVERRIDABLE: Icon =
        IconLoader.getIcon(
            "icons/featureFlagStructureViewOverridable.svg",
            FeatureFlagIcons::class.java
        )
    val STRUCTURE_VIEW_LITERALIZE: Icon =
        IconLoader.getIcon(
            "icons/featureFlagStructureViewLiteralize.svg",
            FeatureFlagIcons::class.java
        )
    val STRUCTURE_VIEW_PRIVATE: Icon =
        IconLoader.getIcon(
            "icons/featureFlagStructureViewPrivate.svg",
            FeatureFlagIcons::class.java
        )
}
