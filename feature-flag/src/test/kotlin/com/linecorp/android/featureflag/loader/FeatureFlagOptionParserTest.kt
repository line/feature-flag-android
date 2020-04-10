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

import com.linecorp.android.featureflag.model.FeatureFlagOption
import com.linecorp.android.featureflag.model.FeatureFlagOption.DEPRECATED
import com.linecorp.android.featureflag.model.FeatureFlagOption.OVERRIDABLE
import com.linecorp.android.featureflag.model.FeatureFlagOption.PRIVATE
import com.linecorp.android.featureflag.utils.assertFailureMessage
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe
import java.io.File
import kotlin.test.assertEquals

/**
 * Tests for [FeatureFlagOptionParser].
 * All the text resources are in "tests/FeatureFlagOptionParser/" directory.
 */
object FeatureFlagOptionParserTest : Spek({
    fun loadLinesFromFile(name: String): List<String> {
        val url = checkNotNull(javaClass.classLoader.getResource("tests/FeatureFlagOptionParserTest/$name"))
        return File(url.toURI()).bufferedReader().lineSequence().toList()
    }

    describe("Precondition") {
        it("A reverse map of Option enum is sufficient") {
            val actualEnums = FeatureFlagOption.values().toList()
            val reverseMapContainsEnums = FeatureFlagOptionParser.OPTION_MAPPING.values

            assertEquals(actualEnums.size, reverseMapContainsEnums.size)
            assertEquals(0, reverseMapContainsEnums.subtract(actualEnums).size)
            assertEquals(0, actualEnums.subtract(reverseMapContainsEnums).size)
        }
    }

    describe("Parsed result is correct") {
        context("with normal case") {
            val expectedResults = listOf(
                setOf(PRIVATE),
                setOf(PRIVATE, OVERRIDABLE),
                setOf(DEPRECATED),
                setOf(PRIVATE, DEPRECATED),
                setOf(PRIVATE, OVERRIDABLE, DEPRECATED)
            )
            val testCases = loadLinesFromFile("OPTION_VALID_NORMAL")

            testCases.forEachIndexed { index, testValue ->
                it(testValue) {
                    assertEquals(expectedResults[index], FeatureFlagOptionParser.parse(testValue))
                }
            }
        }

        context("with empty") {
            it("Empty option") {
                assertEquals(emptySet<FeatureFlagOption>(), FeatureFlagOptionParser.parse(""))
            }
        }

        context("with duplicated option") {
            val expectedResults = listOf(
                setOf(PRIVATE),
                setOf(PRIVATE, OVERRIDABLE),
                setOf(PRIVATE, OVERRIDABLE)
            )

            val testCases = loadLinesFromFile("OPTION_VALID_DUPLICATED")

            testCases.forEachIndexed { index, testValue ->
                it(testValue) {
                    assertEquals(expectedResults[index], FeatureFlagOptionParser.parse(testValue))
                }
            }
        }

        context("with extra spaces") {
            val expectedResults = listOf(
                setOf(PRIVATE),
                setOf(PRIVATE),
                setOf(PRIVATE, OVERRIDABLE),
                setOf(PRIVATE, OVERRIDABLE),
                setOf(PRIVATE, OVERRIDABLE)
            )

            val testCases = loadLinesFromFile("OPTION_VALID_SPACE")

            testCases.forEachIndexed { index, testValue ->
                it(""""$testValue"""") {
                    assertEquals(expectedResults[index], FeatureFlagOptionParser.parse(testValue))
                }
            }
        }
    }

    describe("Parsing is failed") {
        it("with undefined option") {
            assertFailureMessage<IllegalArgumentException>("A specified option is undefined: INVALID") {
                FeatureFlagOptionParser.parse(loadLinesFromFile("OPTION_INVALID_UNDEFINED_OPTION")[0])
            }
        }
    }
})
