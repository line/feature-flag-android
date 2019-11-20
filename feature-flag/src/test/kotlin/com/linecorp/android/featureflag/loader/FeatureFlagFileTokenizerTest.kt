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

import com.linecorp.android.featureflag.model.FeatureFlagProperties
import com.linecorp.android.featureflag.model.FeatureFlagProperties.Entry
import com.linecorp.android.featureflag.utils.assertFailureMessage
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe
import java.io.File
import kotlin.test.assertEquals

/**
 * Tests for [FeatureFlagFileTokenizer].
 * All the text resources are in "tests/FeatureFlagFileTokenizerTest/" directory.
 */
object FeatureFlagFileTokenizerTest : Spek({

    fun loadSequenceFromFile(name: String): Sequence<String> {
        val url = checkNotNull(javaClass.classLoader.getResource("tests/FeatureFlagFileTokenizerTest/$name"))
        return File(url.toURI()).bufferedReader().lineSequence()
    }

    fun createFeatureFlagProperties(vararg entries: Entry): FeatureFlagProperties =
        FeatureFlagProperties(entries.toList())

    describe("Parsed result is correct") {
        it("with options") {
            assertEquals(
                createFeatureFlagProperties(
                    Entry("FLAG_1", "VALUE", "OPTION"),
                    Entry("FLAG_2", "VALUE", "OPTION"),
                    Entry("FLAG_3", "VALUE", "OPTION1 OPTION2"),
                    Entry("FLAG_4", "VALUE", "OPTION1  OPTION2")
                ),
                FeatureFlagFileTokenizer.parse(loadSequenceFromFile("FLAG_VALID_OPTION"))
            )
        }
        it("with name") {
            assertEquals(
                createFeatureFlagProperties(
                    Entry("FLAG_1", "VALUE", ""),
                    Entry("FLAG_2", "VALUE", ""),
                    Entry("FLAG_3", "VALUE", "")
                ),
                FeatureFlagFileTokenizer.parse(loadSequenceFromFile("FLAG_VALID_NAME"))
            )
        }
        it("with value") {
            assertEquals(
                createFeatureFlagProperties(
                    Entry("FLAG_1", "VALUE", ""),
                    Entry("FLAG_2", "VALUE", ""),
                    Entry("FLAG_3", "VALUE VALUE", ""),
                    Entry("FLAG_4", "VALUE", ""),
                    Entry("FLAG_5", "VALUE=VALUE", "")
                ),
                FeatureFlagFileTokenizer.parse(loadSequenceFromFile("FLAG_VALID_VALUE"))
            )
        }
        it("with empty") {
            assertEquals(
                createFeatureFlagProperties(),
                FeatureFlagFileTokenizer.parse(loadSequenceFromFile("FLAG_VALID_EMPTY"))
            )
        }
    }

    describe("Parsing is failed") {
        it("if a line has't key-value pair") {
            assertFailureMessage<IllegalArgumentException>("Couldn't parse a line: INVALID_LINE") {
                FeatureFlagFileTokenizer.parse(loadSequenceFromFile("FLAG_INVALID_NO_KEY_VALUE"))
            }
        }
        it("if a key is empty") {
            assertFailureMessage<IllegalArgumentException>("Couldn't parse a line: =VALUE") {
                FeatureFlagFileTokenizer.parse(loadSequenceFromFile("FLAG_INVALID_EMPTY_KEY"))
            }
        }
        it("if a key is blank") {
            assertFailureMessage<IllegalArgumentException>("Couldn't parse a line:  =VALUE") {
                FeatureFlagFileTokenizer.parse(loadSequenceFromFile("FLAG_INVALID_BLANK_KEY"))
            }
        }
        it("if a value is empty") {
            assertFailureMessage<IllegalArgumentException>("Couldn't parse a line: KEY=") {
                FeatureFlagFileTokenizer.parse(loadSequenceFromFile("FLAG_INVALID_EMPTY_VALUE"))
            }
        }
        it("if a value is blank") {
            assertFailureMessage<IllegalStateException>("Value mustn't be empty: KEY= ") {
                FeatureFlagFileTokenizer.parse(loadSequenceFromFile("FLAG_INVALID_BLANK_VALUE"))
            }
        }
    }
})
