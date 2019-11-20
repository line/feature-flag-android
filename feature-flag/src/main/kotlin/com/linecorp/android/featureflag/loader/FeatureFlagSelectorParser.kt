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

import com.linecorp.android.featureflag.loader.FeatureFlagSelectorParser.parse
import com.linecorp.android.featureflag.model.DisjunctionNormalForm.Conjunction
import com.linecorp.android.featureflag.model.DisjunctionNormalForm.Disjunction
import com.linecorp.android.featureflag.model.FeatureFlagElement
import com.linecorp.android.featureflag.model.FlagLink
import org.intellij.lang.annotations.Language

/**
 * A parser of a raw feature flag value string.
 *
 * Call [parse] to convert a raw value string to a flag value model, [Disjunction] of
 * [FeatureFlagElement].
 * The given raw string must be in disjunctive normal form of elements,
 * where disjunction is represented by `','` while conjunction by `'&'`.
 *
 * Then, a raw string looks like as follows.
 * `"Element & Element & ..., Element & ..., ..."`
 *
 * Here, there are four types of elements
 *
 * - User: Starts with `@`. It declares a task executor user name to switch the flag value.
 *         It the current build task is executed by the specified user, the flag is turned on.
 * - Link: Contains `:`. It represents a link to another flag. The left operand of `:` is the Gradle
 *         module name containing the linked flag, and the right one is the flag name.
 *         If the module name is empty, the linked flag module is the same as the current module.
 * - Version: Ends with `~`. It specifies version to switch the flag. If the current version is the
 *            same or later than the specified version, the flag is turned on.
 * - Phase: Non-blank string without any marker. It represents a phase which can be configured in
 *          the `build.gradle`.
 *
 * For example, [parse] converts `"RELEASE & 1.0.0~ & :ANOTHER_FLAG, @USER"` to
 * `Disjunction(Conjunction(Phase("RELEASE"), Version("1.0.0"), Link("", "ANOTHER_FLAG")), Conjunction(User("USER")))`
 */
internal object FeatureFlagSelectorParser {

    private const val DISJUNCTION_DELIMITER = ","
    private const val CONJUNCTION_DELIMITER = "&"

    @Language("RegExp")
    private val ELEMENT_PARSERS: Sequence<ElementParserEntry> = elementParsersOf(
        """@(\S*)""" to ::parseUserElement,
        """(\S*):(\S*)""" to ::parseLinkElement,
        """(\S*)~""" to ::parseVersionElement,
        """(\S*)""" to ::parsePhaseElement
    )

    /**
     * Creates feature flag value model [Disjunction], a disjunctive normal form, from a raw value
     * string. Each value element string is also converted to [FeatureFlagElement].
     *
     * If the string has an invalid element like a blank element or a empty user name, this throws
     * [IllegalArgumentException].
     */
    fun parse(rawValue: String): Disjunction<FeatureFlagElement> =
        Disjunction(rawValue.split(DISJUNCTION_DELIMITER).map(::parseConjunction))

    private fun parseConjunction(rawValue: String): Conjunction<FeatureFlagElement> =
        Conjunction(rawValue.split(CONJUNCTION_DELIMITER).map(::parseSelectorElement))

    private fun parseSelectorElement(rawElement: String): FeatureFlagElement = ELEMENT_PARSERS
        .mapNotNull { it.parseElementOrNull(rawElement.trim()) }
        .first()

    private fun parseUserElement(regexMatchResult: MatchResult): FeatureFlagElement.User {
        val value = regexMatchResult.groupValues[1]
        require(value.isNotEmpty()) { "Missing user name in user element." }
        return FeatureFlagElement.User(value)
    }

    private fun parseLinkElement(regexMatchResult: MatchResult): FeatureFlagElement.Link {
        val moduleName = regexMatchResult.groupValues[1]
        val flagName = regexMatchResult.groupValues[2]
        require(flagName.isNotEmpty()) { "Missing link value in link element." }
        return FeatureFlagElement.Link(FlagLink(moduleName, flagName))
    }

    private fun parseVersionElement(regexMatchResult: MatchResult): FeatureFlagElement.Version {
        val value = regexMatchResult.groupValues[1]
        require(value.isNotEmpty()) { "Missing version value in version element." }
        return FeatureFlagElement.Version(value)
    }

    private fun parsePhaseElement(regexMatchResult: MatchResult): FeatureFlagElement.Phase {
        val value = regexMatchResult.groupValues[1]
        require(value.isNotEmpty()) { "An invalid blank element exists." }
        return FeatureFlagElement.Phase(value)
    }

    private fun elementParsersOf(
        vararg items: Pair<String, (MatchResult) -> FeatureFlagElement>
    ): Sequence<ElementParserEntry> = items.map(ElementParserEntry.Companion::create).asSequence()

    private class ElementParserEntry(
        private val predicateRegex: Regex,
        private val parser: (MatchResult) -> FeatureFlagElement
    ) {
        fun parseElementOrNull(rawElement: String): FeatureFlagElement? =
            predicateRegex.matchEntire(rawElement)?.let(parser::invoke)

        companion object {
            fun create(
                item: Pair<String, (MatchResult) -> FeatureFlagElement>
            ): ElementParserEntry = ElementParserEntry(item.first.toRegex(), item.second)
        }
    }
}
