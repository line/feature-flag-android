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

package com.linecorp.android.featureflag.ij.plugin.psi

import com.intellij.psi.tree.IElementType
import com.linecorp.android.featureflag.ij.plugin.FeatureFlagLanguage

/**
 * A class representing the type of token used by Grammar-Kit.
 */
class FeatureFlagTokenType(debugName: String) : IElementType(debugName, FeatureFlagLanguage) {
    override fun toString(): String = "FeatureFlagTokenType." + super.toString()
}
