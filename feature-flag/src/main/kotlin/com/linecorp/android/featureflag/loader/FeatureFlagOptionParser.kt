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

package com.linecorp.android.featureflag.loader

import com.linecorp.android.featureflag.loader.FeatureFlagOptionParser.parse
import com.linecorp.android.featureflag.model.FeatureFlagOption
import java.util.EnumSet

/**
 * A parser of space-separated feature flag options.
 *
 * Call [parse] with a raw option string to convert it to a [Set] of [FeatureFlagOption].
 */
internal object FeatureFlagOptionParser {

    private val OPTION_DELIMITER = """\s+""".toRegex()
    internal val OPTION_MAPPING = mapOf(
        "OVERRIDABLE" to FeatureFlagOption.OVERRIDABLE,
        "PRIVATE" to FeatureFlagOption.PRIVATE,
        "LITERALIZE" to FeatureFlagOption.LITERALIZE
    )

    /**
     * Converts space-separated raw options to a [Set] of [FeatureFlagOption].
     *
     * Each raw option should be separated by space characters represented by `\s` of `Regex`.
     * Duplicated options are unified.
     * If an invalid option string is given, this throws [IllegalArgumentException].
     */
    fun parse(rawOption: String): Set<FeatureFlagOption> {
        val enumList = rawOption
            .split(OPTION_DELIMITER)
            .filter(String::isNotBlank)
            .map(::parseOption)
        return if (enumList.isEmpty()) emptySet() else EnumSet.copyOf(enumList)
    }

    private fun parseOption(rawOption: String): FeatureFlagOption =
        requireNotNull(OPTION_MAPPING[rawOption]) { "A specified option is undefined: $rawOption" }
}
