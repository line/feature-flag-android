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

import com.linecorp.android.featureflag.loader.FeatureFlagFileTokenizer.parse
import com.linecorp.android.featureflag.model.FeatureFlagProperties

/**
 * A string tokenizer for raw string lines of feature flag properties.
 *
 * Call [parse] with a property line [Sequence] to tokenize the lines.
 * Each line must adhere to the following format:
 *
 * ```
 * [[Option] Name = Value] [# Comment]
 * ```
 *
 * A line may have two items: a name-value pair (`[Option] Name = Value`) and a comment
 * (`# Comment`).
 * A blank line is also valid.
 *
 * Here, a name-value pair consists of the following elements.
 *
 * - Option: Specifies the flag property such as mutability or visibility.
 * - Name: Declares the flag name.
 * - Value: Decides the flag boolean value.
 */
internal object FeatureFlagFileTokenizer {
    private const val COMMENT_MARKER = "#"
    private val FLAG_ENTRY_REGEX = """^(?:([^=]*)\s+)?(\S+)\s*=(.+)$""".toRegex()

    /**
     * Extracts tuples of a feature flag name, value, and option from a given raw line sequence.
     *
     * If the given sequence has a malformed line, this throws [IllegalArgumentException].
     */
    fun parse(lines: Sequence<String>): FeatureFlagProperties =
        FeatureFlagProperties(loadRawFeatureFlagEntries(lines))

    private fun loadRawFeatureFlagEntries(
        lines: Sequence<String>
    ): List<FeatureFlagProperties.Entry> = lines
        .map(::trimComment)
        .filterNot(String::isBlank)
        .map(::parseToRawFeatureFlagEntry)
        .toList()

    private fun trimComment(line: String): String = line.split(COMMENT_MARKER)[0]

    private fun parseToRawFeatureFlagEntry(line: String): FeatureFlagProperties.Entry {
        val groupValues = FLAG_ENTRY_REGEX.find(line)?.groupValues

        require(groupValues != null && groupValues.size == 4) { "Couldn't parse a line: $line" }
        check(groupValues[3].isNotBlank()) { "Value mustn't be empty: $line" }

        return FeatureFlagProperties.Entry(
            name = groupValues[2].trim(),
            value = groupValues[3].trim(),
            option = groupValues[1].trim()
        )
    }
}
