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

import com.linecorp.android.featureflag.model.FeatureFlagEntry
import io.kotest.assertions.throwables.shouldThrowWithMessage
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import java.io.File

/**
 * Tests for [FeatureFlagFileTokenizer].
 * All the text resources are in "tests/FeatureFlagFileTokenizerTest/" directory.
 */
class FeatureFlagFileTokenizerTest : FunSpec({

    fun loadSequenceFromFile(name: String): Sequence<String> {
        val url = checkNotNull(
            javaClass.classLoader.getResource("tests/FeatureFlagFileTokenizerTest/$name")
        )
        return File(url.toURI()).bufferedReader().lineSequence()
    }

    context("Parsed result is correct") {
        test("with options") {
            FeatureFlagFileTokenizer.parse(loadSequenceFromFile("FLAG_VALID_OPTION")) shouldBe
                listOf(
                    FeatureFlagEntry("FLAG_1", "VALUE", "OPTION"),
                    FeatureFlagEntry("FLAG_2", "VALUE", "OPTION"),
                    FeatureFlagEntry("FLAG_3", "VALUE", "OPTION1 OPTION2"),
                    FeatureFlagEntry("FLAG_4", "VALUE", "OPTION1  OPTION2")
                )
        }
        test("with name") {
            FeatureFlagFileTokenizer.parse(loadSequenceFromFile("FLAG_VALID_NAME")) shouldBe
                listOf(
                    FeatureFlagEntry("FLAG_1", "VALUE", ""),
                    FeatureFlagEntry("FLAG_2", "VALUE", ""),
                    FeatureFlagEntry("FLAG_3", "VALUE", "")
                )
        }
        test("with value") {
            FeatureFlagFileTokenizer.parse(loadSequenceFromFile("FLAG_VALID_VALUE")) shouldBe
                listOf(
                    FeatureFlagEntry("FLAG_1", "VALUE", ""),
                    FeatureFlagEntry("FLAG_2", "VALUE", ""),
                    FeatureFlagEntry("FLAG_3", "VALUE VALUE", ""),
                    FeatureFlagEntry("FLAG_4", "VALUE", ""),
                    FeatureFlagEntry("FLAG_5", "VALUE=VALUE", "")
                )
        }
        test("with empty") {
            FeatureFlagFileTokenizer.parse(loadSequenceFromFile("FLAG_VALID_EMPTY")) shouldBe
                emptyList()
        }
    }

    context("Parsing is failed") {
        test("if a line has't key-value pair") {
            shouldThrowWithMessage<IllegalArgumentException>("Couldn't parse a line: INVALID_LINE") {
                FeatureFlagFileTokenizer.parse(loadSequenceFromFile("FLAG_INVALID_NO_KEY_VALUE"))
            }
        }
        test("if a key is empty") {
            shouldThrowWithMessage<IllegalArgumentException>("Couldn't parse a line: =VALUE") {
                FeatureFlagFileTokenizer.parse(loadSequenceFromFile("FLAG_INVALID_EMPTY_KEY"))
            }
        }
        test("if a key is blank") {
            shouldThrowWithMessage<IllegalArgumentException>("Couldn't parse a line:  =VALUE") {
                FeatureFlagFileTokenizer.parse(loadSequenceFromFile("FLAG_INVALID_BLANK_KEY"))
            }
        }
        test("if a value is empty") {
            shouldThrowWithMessage<IllegalArgumentException>("Couldn't parse a line: KEY=") {
                FeatureFlagFileTokenizer.parse(loadSequenceFromFile("FLAG_INVALID_EMPTY_VALUE"))
            }
        }
        test("if a value is blank") {
            shouldThrowWithMessage<IllegalStateException>("Value mustn't be empty: KEY= ") {
                FeatureFlagFileTokenizer.parse(loadSequenceFromFile("FLAG_INVALID_BLANK_VALUE"))
            }
        }
    }
})
