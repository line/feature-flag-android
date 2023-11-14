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

import com.github.zafarkhaja.semver.Version

/**
 * A model class of the current build information: build phase, version, and user.
 */
internal class BuildEnvironment(
    /**
     * A map from the build phase strings defined in the build script to boolean values.
     *
     * The result of `phasesMap[phaseString]` is one of the followings.
     * - true: Flags with the given phase are enabled.
     * - false: Flags with the given phase are disabled.
     * - null: The given phase is undefined in the build script.
     */
    val phasesMap: Map<String, Boolean>,
    /**
     * A version of this module defined in the build script.
     */
    val applicationVersion: Version,
    /**
     * An account name of the current task executor.
     */
    val userName: String
)
