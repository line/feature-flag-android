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

/**
 * A data model of feature flag element with applying the current build environment information.
 * As the result, elements except for [Variable] are represented by a simple boolean type, [Constant].
 */
internal sealed class FeatureFlagAppliedElement {
    /**
     * An element to specify a constant boolean value. Converted from [FeatureFlagElement.Phase],
     * [FeatureFlagElement.User], and [FeatureFlagElement.Version] with current build environment information.
     */
    data class Constant(val value: Boolean) : FeatureFlagAppliedElement()

    /**
     * An element to specify another flag to delegate this flag enability.
     */
    data class Variable(val link: FlagLink) : FeatureFlagAppliedElement()
}
