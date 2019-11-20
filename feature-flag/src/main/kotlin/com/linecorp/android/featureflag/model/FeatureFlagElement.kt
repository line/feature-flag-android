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
 * A data model to specify a condition to enable a feature flag.
 */
internal sealed class FeatureFlagElement {
    /**
     * An element to specify a build phase set to enable the flag.
     */
    data class Phase(val phase: String) : FeatureFlagElement()

    /**
     * An element to specify a user name of the current task executor to enable the flag.
     */
    data class User(val name: String) : FeatureFlagElement()

    /**
     * An element to specify another flag to delegate this flag enability.
     */
    data class Link(val link: FlagLink) : FeatureFlagElement()

    /**
     * An element to specify a version lower bound to enable the flag.
     */
    data class Version(val version: String) : FeatureFlagElement()
}
