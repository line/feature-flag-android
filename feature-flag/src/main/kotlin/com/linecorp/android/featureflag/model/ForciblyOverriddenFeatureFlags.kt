//
// Copyright 2019 LINE Corporation
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
// http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
//

package com.linecorp.android.featureflag.model

import org.gradle.api.Project

/**
 * Sets of feature flags to be turned on or off forcibly.
 *
 * Call [ForciblyOverriddenFeatureFlags.Companion.parse] to create this value from a gradle property
 * options.
 */
internal data class ForciblyOverriddenFeatureFlags(
    val forciblyEnabledFlags: Set<String>,
    val forciblyDisabledFlags: Set<String>
) {
    companion object {
        private const val PARAMETER_FORCE_ENABLE = "featureFlagOn"
        private const val PARAMETER_FORCE_DISABLE = "featureFlagOff"

        /**
         * Parses custom feature flag options from gradle property.
         * (ex: ./gradlew :task -PfeatureFlagOn=FEATURE_AAA,FEATURE_BBB)
         */
        fun parse(project: Project): ForciblyOverriddenFeatureFlags =
            ForciblyOverriddenFeatureFlags(
                project.parseFeatureFlagInBuildParameter(PARAMETER_FORCE_ENABLE),
                project.parseFeatureFlagInBuildParameter(PARAMETER_FORCE_DISABLE)
            )

        private fun Project.parseFeatureFlagInBuildParameter(parameterName: String): Set<String> =
            properties[parameterName]?.toString()?.split(',')?.toSet() ?: emptySet()
    }
}
