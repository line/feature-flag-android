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

import com.linecorp.android.featureflag.model.FeatureFlagElement.Link
import com.linecorp.android.featureflag.model.FeatureFlagElement.Phase
import com.linecorp.android.featureflag.model.FeatureFlagElement.User
import com.linecorp.android.featureflag.model.FeatureFlagElement.Version
import com.linecorp.android.featureflag.model.FlagLink
import com.linecorp.android.featureflag.utils.assertDisjunction
import com.linecorp.android.featureflag.utils.assertFailureMessage
import com.linecorp.android.featureflag.utils.conjunctionOf
import com.linecorp.android.featureflag.utils.disjunctionOf
import io.kotest.core.spec.style.FunSpec
import java.io.File

/**
 * Tests for [FeatureFlagSelectorParser].
 * All the text resources are in "tests/FeatureFlagSelectorParser/" directory.
 */
class FeatureFlagSelectorParserTest : FunSpec({
    fun loadLinesFromFile(name: String): List<String> {
        val url = checkNotNull(
            javaClass.classLoader.getResource("tests/FeatureFlagSelectorParserTest/$name")
        )
        return File(url.toURI()).bufferedReader().lineSequence().toList()
    }

    context("Parsed result is correct") {
        context("on each element type") {
            val expectedResults = listOf(
                disjunctionOf(conjunctionOf(Phase("PHASE"))),
                disjunctionOf(conjunctionOf(User("USER"))),
                disjunctionOf(conjunctionOf(Link(FlagLink("", "SELF_LINK")))),
                disjunctionOf(conjunctionOf(Link(FlagLink("MODULE", "LINK")))),
                disjunctionOf(conjunctionOf(Version("1.2.3"))),
                disjunctionOf(conjunctionOf(Phase("PHASE1"), Phase("PHASE2")))
            )
            val testCases = loadLinesFromFile("VALUE_VALID_ELEMENT_TYPES")

            testCases.forEachIndexed { index, testValue ->
                test(testValue) {
                    assertDisjunction(
                        expectedResults[index],
                        FeatureFlagSelectorParser.parse(testValue)
                    )
                }
            }
        }

        context("with link to nested module") {
            val expected = disjunctionOf(conjunctionOf(Link(FlagLink("NESTED:MODULE", "LINK"))))

            val testCases = loadLinesFromFile("VALUE_VALID_NESTED_MODULE_LINK")

            testCases.forEach { testValue ->
                test(testValue) {
                    assertDisjunction(expected, FeatureFlagSelectorParser.parse(testValue))
                }
            }
        }

        context("with disjunction and space") {
            val expected =
                disjunctionOf(conjunctionOf(Phase("PHASE1")), conjunctionOf(Phase("PHASE2")))

            val testCases = loadLinesFromFile("VALUE_VALID_DISJUNCTION_WITH_SPACE")

            testCases.forEach { testValue ->
                test(testValue) {
                    assertDisjunction(expected, FeatureFlagSelectorParser.parse(testValue))
                }
            }
        }

        context("with conjunction and space") {
            val expected = disjunctionOf(conjunctionOf(Phase("PHASE1"), Phase("PHASE2")))

            val testCases = loadLinesFromFile("VALUE_VALID_CONJUNCTION_WITH_SPACE")

            testCases.forEach { testValue ->
                test(testValue) {
                    assertDisjunction(expected, FeatureFlagSelectorParser.parse(testValue))
                }
            }
        }

        context("on combination of disjunction and conjunction") {
            val expectedResults = listOf(
                disjunctionOf(
                    conjunctionOf(Phase("PHASE1")),
                    conjunctionOf(Phase("PHASE2"), Phase("PHASE3"))
                ),
                disjunctionOf(
                    conjunctionOf(Phase("PHASE1"), Phase("PHASE2")),
                    conjunctionOf(Phase("PHASE3"))
                ),
                disjunctionOf(
                    conjunctionOf(Phase("PHASE1"), Phase("PHASE2")),
                    conjunctionOf(Phase("PHASE3"), Phase("PHASE4"))
                )
            )

            val testCases = loadLinesFromFile("VALUE_VALID_DISJUNCTION_AND_CONJUNCTION")

            testCases.forEachIndexed { index, testValue ->
                test(testValue) {
                    assertDisjunction(
                        expectedResults[index],
                        FeatureFlagSelectorParser.parse(testValue)
                    )
                }
            }
        }
    }

    context("Parsing is failed") {
        context("on each element type") {
            test("User element with no value") {
                assertFailureMessage<IllegalArgumentException>(
                    "Missing user name in user element."
                ) {
                    FeatureFlagSelectorParser.parse(
                        loadLinesFromFile("VALUE_INVALID_ELEMENT_USER")[0]
                    )
                }
            }
            test("Link element with no value") {
                assertFailureMessage<IllegalArgumentException>(
                    "Missing link value in link element."
                ) {
                    FeatureFlagSelectorParser.parse(
                        loadLinesFromFile("VALUE_INVALID_ELEMENT_LINK_SELF")[0]
                    )
                }
            }
            test("Link element with no value") {
                assertFailureMessage<IllegalArgumentException>(
                    "Missing link value in link element."
                ) {
                    FeatureFlagSelectorParser.parse(
                        loadLinesFromFile("VALUE_INVALID_ELEMENT_LINK_ANOTHER")[0]
                    )
                }
            }
            test("Version element with no value") {
                assertFailureMessage<IllegalArgumentException>(
                    "Missing version value in version element."
                ) {
                    FeatureFlagSelectorParser.parse(
                        loadLinesFromFile("VALUE_INVALID_ELEMENT_VERSION")[0]
                    )
                }
            }
        }
        context("on conjunction") {
            test("with no value") {
                assertFailureMessage<IllegalArgumentException>(
                    "An invalid blank element exists."
                ) {
                    FeatureFlagSelectorParser.parse(
                        loadLinesFromFile("VALUE_INVALID_CONJUNCTION_EMPTY")[0]
                    )
                }
            }
            test("with only one sides value") {
                assertFailureMessage<IllegalArgumentException>(
                    "An invalid blank element exists."
                ) {
                    FeatureFlagSelectorParser.parse(
                        loadLinesFromFile("VALUE_INVALID_CONJUNCTION_ONE_SIDE")[0]
                    )
                }
            }
        }
        context("on disjunction") {
            test("with no value") {
                assertFailureMessage<IllegalArgumentException>(
                    "An invalid blank element exists."
                ) {
                    FeatureFlagSelectorParser.parse(
                        loadLinesFromFile("VALUE_INVALID_DISJUNCTION_EMPTY")[0]
                    )
                }
            }
            test("with only one sides value") {
                assertFailureMessage<IllegalArgumentException>(
                    "An invalid blank element exists."
                ) {
                    FeatureFlagSelectorParser.parse(
                        loadLinesFromFile("VALUE_INVALID_DISJUNCTION_ONE_SIDE")[0]
                    )
                }
            }
        }
    }
})
