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
 * Flag properties of a feature in a specific phase.
 * Mainly, this consists of the flag name and value representing if the flag is enabled.
 */
internal data class FeatureFlagData(
    val name: String,
    val value: Value,
    val options: Set<FeatureFlagOption>
) {
    /**
     * A value of this flag entry. The value is a single boolean value or the disjunctive normal form of [FlagLink]s.
     */
    sealed class Value {
        /**
         * A value to enable the flag.
         */
        object True : Value()

        /**
         * A value to disable the flag.
         */
        object False : Value()

        /**
         * A value decided by other flags.
         * The flags are represented with with the disjunctive normal form of [FlagLink]s.
         */
        data class Links(
            val linksDisjunction: DisjunctionNormalForm.Disjunction<FlagLink>
        ) : Value()
    }
}
