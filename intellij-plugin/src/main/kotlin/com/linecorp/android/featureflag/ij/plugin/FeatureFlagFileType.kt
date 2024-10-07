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

import com.intellij.openapi.fileTypes.LanguageFileType
import com.linecorp.android.featureflag.ij.plugin.icon.FeatureFlagIcons
import javax.swing.Icon

/**
 * An implementation of [LanguageFileType] for feature flag language.
 */
object FeatureFlagFileType : LanguageFileType(FeatureFlagLanguage) {

    override fun getName(): String = "FeatureFlag File"

    override fun getDescription(): String = "FeatureFlag language file"

    override fun getDefaultExtension(): String = "ff"

    override fun getIcon(): Icon = FeatureFlagIcons.FILE
}
