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

import com.linecorp.android.featureflag.model.DisjunctionNormalForm.Disjunction
import com.linecorp.android.featureflag.model.FeatureFlagAppliedElement.Constant
import com.linecorp.android.featureflag.model.FeatureFlagAppliedElement.Variable
import com.linecorp.android.featureflag.model.FeatureFlagData.Value
import com.linecorp.android.featureflag.model.FlagLink
import com.linecorp.android.featureflag.utils.assertDisjunction
import com.linecorp.android.featureflag.utils.conjunctionOf
import com.linecorp.android.featureflag.utils.disjunctionOf
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe
import kotlin.test.assertEquals
import com.linecorp.android.featureflag.model.FeatureFlagAppliedElement as AppliedElement

/**
 * Tests for [FeatureFlagValueOptimizer].
 */
object FeatureFlagValueOptimizerTest : Spek({
    fun assertOptimization(
        expectedValue: Value,
        sourceDisjunction: Disjunction<AppliedElement>
    ) {
        val actualValue = FeatureFlagValueOptimizer.optimize(sourceDisjunction)
        if (expectedValue is Value.Links && actualValue is Value.Links) {
            assertDisjunction(expectedValue.linksDisjunction, actualValue.linksDisjunction)
        } else {
            assertEquals(expectedValue, actualValue)
        }
    }

    describe("Evaluated result is correct") {
        context("only single literal") {
            it("constant: true") {
                assertOptimization(
                    Value.True,
                    disjunctionOf(conjunctionOf(Constant(true)))
                )
            }
            it("constant: false") {
                assertOptimization(
                    Value.False,
                    disjunctionOf(conjunctionOf(Constant(false)))
                )
            }
            it("link: self module") {
                assertOptimization(
                    Value.Links(disjunctionOf(conjunctionOf(FlagLink("", "flagName")))),
                    disjunctionOf(conjunctionOf(Variable(FlagLink("", "flagName"))))
                )
            }
            it("link: another module") {
                assertOptimization(
                    Value.Links(disjunctionOf(conjunctionOf(FlagLink("anotherModule", "flagName")))),
                    disjunctionOf(conjunctionOf(Variable(FlagLink("anotherModule", "flagName"))))
                )
            }
        }
        context("conjunction optimization") {
            it("true & true => true") {
                assertOptimization(
                    Value.True,
                    disjunctionOf(conjunctionOf(Constant(true), Constant(true)))
                )
            }
            it("true & false => false") {
                assertOptimization(
                    Value.False,
                    disjunctionOf(conjunctionOf(Constant(true), Constant(false)))
                )
            }
            it("false & false => false") {
                assertOptimization(
                    Value.False,
                    disjunctionOf(conjunctionOf(Constant(false), Constant(false)))
                )
            }
            it("false & link => false") {
                assertOptimization(
                    Value.False,
                    disjunctionOf(conjunctionOf(Constant(false), Variable(FlagLink("", "flagName"))))
                )
            }
            it("true & link => link") {
                assertOptimization(
                    Value.Links(disjunctionOf(conjunctionOf(FlagLink("", "flagName")))),
                    disjunctionOf(conjunctionOf(Constant(true), Variable(FlagLink("", "flagName"))))
                )
            }
            it("true & false & link => false") {
                assertOptimization(
                    Value.False,
                    disjunctionOf(conjunctionOf(Constant(true), Constant(false), Variable(FlagLink("", "flagName"))))
                )
            }
            it("link1 & link2 => link1 & link2") {
                assertOptimization(
                    Value.Links(disjunctionOf(conjunctionOf(FlagLink("", "flagName1"), FlagLink("", "flagName2")))),
                    disjunctionOf(
                        conjunctionOf(Variable(FlagLink("", "flagName1")), Variable(FlagLink("", "flagName2")))
                    )
                )
            }
        }
        context("disjunction optimization") {
            it("true | true => true") {
                assertOptimization(
                    Value.True,
                    disjunctionOf(conjunctionOf(Constant(true)), conjunctionOf(Constant(true)))
                )
            }
            it("true | false => true") {
                assertOptimization(
                    Value.True,
                    disjunctionOf(conjunctionOf(Constant(true)), conjunctionOf(Constant(false)))
                )
            }
            it("false | false => false") {
                assertOptimization(
                    Value.False,
                    disjunctionOf(conjunctionOf(Constant(false)), conjunctionOf(Constant(false)))
                )
            }
            it("false | link => link") {
                assertOptimization(
                    Value.Links(disjunctionOf(conjunctionOf(FlagLink("", "flagName")))),
                    disjunctionOf(conjunctionOf(Constant(false)), conjunctionOf(Variable(FlagLink("", "flagName"))))
                )
            }
            it("true | link => true") {
                assertOptimization(
                    Value.True,
                    disjunctionOf(conjunctionOf(Constant(true)), conjunctionOf(Variable(FlagLink("", "flagName"))))
                )
            }
            it("true | false | link => true") {
                assertOptimization(
                    Value.True,
                    disjunctionOf(
                        conjunctionOf(Constant(true)),
                        conjunctionOf(Constant(false)),
                        conjunctionOf(Variable(FlagLink("", "flagName")))
                    )
                )
            }
            it("link1 | link2 => link1 | link2") {
                assertOptimization(
                    Value.Links(
                        disjunctionOf(
                            conjunctionOf(FlagLink("", "flagName1")),
                            conjunctionOf(FlagLink("", "flagName2"))
                        )
                    ),
                    disjunctionOf(
                        conjunctionOf(Variable(FlagLink("", "flagName1"))),
                        conjunctionOf(Variable(FlagLink("", "flagName2")))
                    )
                )
            }
        }
    }
})
