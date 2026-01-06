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
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import com.linecorp.android.featureflag.model.FeatureFlagAppliedElement as AppliedElement

/**
 * Tests for [FeatureFlagValueOptimizer].
 */
class FeatureFlagValueOptimizerTest : FunSpec({
    fun assertOptimization(
        expectedValue: Value,
        sourceDisjunction: Disjunction<AppliedElement>
    ) {
        val actualValue = FeatureFlagValueOptimizer.optimize(sourceDisjunction)
        if (expectedValue is Value.Links && actualValue is Value.Links) {
            assertDisjunction(expectedValue.linksDisjunction, actualValue.linksDisjunction)
        } else {
            actualValue shouldBe expectedValue
        }
    }

    context("Evaluated result is correct") {
        context("only single literal") {
            test("constant: true") {
                assertOptimization(
                    Value.True,
                    disjunctionOf(conjunctionOf(Constant(true)))
                )
            }
            test("constant: false") {
                assertOptimization(
                    Value.False,
                    disjunctionOf(conjunctionOf(Constant(false)))
                )
            }
            test("link: self module") {
                assertOptimization(
                    Value.Links(disjunctionOf(conjunctionOf(FlagLink("", "flagName")))),
                    disjunctionOf(conjunctionOf(Variable(FlagLink("", "flagName"))))
                )
            }
            test("link: another module") {
                assertOptimization(
                    Value.Links(
                        disjunctionOf(conjunctionOf(FlagLink("anotherModule", "flagName")))
                    ),
                    disjunctionOf(conjunctionOf(Variable(FlagLink("anotherModule", "flagName"))))
                )
            }
        }
        context("conjunction optimization") {
            test("true & true => true") {
                assertOptimization(
                    Value.True,
                    disjunctionOf(conjunctionOf(Constant(true), Constant(true)))
                )
            }
            test("true & false => false") {
                assertOptimization(
                    Value.False,
                    disjunctionOf(conjunctionOf(Constant(true), Constant(false)))
                )
            }
            test("false & false => false") {
                assertOptimization(
                    Value.False,
                    disjunctionOf(conjunctionOf(Constant(false), Constant(false)))
                )
            }
            test("false & link => false") {
                assertOptimization(
                    Value.False,
                    disjunctionOf(
                        conjunctionOf(
                            Constant(false),
                            Variable(FlagLink("", "flagName"))
                        )
                    )
                )
            }
            test("true & link => link") {
                assertOptimization(
                    Value.Links(disjunctionOf(conjunctionOf(FlagLink("", "flagName")))),
                    disjunctionOf(conjunctionOf(Constant(true), Variable(FlagLink("", "flagName"))))
                )
            }
            test("true & false & link => false") {
                assertOptimization(
                    Value.False,
                    disjunctionOf(
                        conjunctionOf(
                            Constant(true),
                            Constant(false),
                            Variable(FlagLink("", "flagName"))
                        )
                    )
                )
            }
            test("link1 & link2 => link1 & link2") {
                assertOptimization(
                    Value.Links(
                        disjunctionOf(
                            conjunctionOf(
                                FlagLink("", "flagName1"),
                                FlagLink("", "flagName2")
                            )
                        )
                    ),
                    disjunctionOf(
                        conjunctionOf(
                            Variable(FlagLink("", "flagName1")),
                            Variable(FlagLink("", "flagName2"))
                        )
                    )
                )
            }
        }
        context("disjunction optimization") {
            test("true | true => true") {
                assertOptimization(
                    Value.True,
                    disjunctionOf(conjunctionOf(Constant(true)), conjunctionOf(Constant(true)))
                )
            }
            test("true | false => true") {
                assertOptimization(
                    Value.True,
                    disjunctionOf(conjunctionOf(Constant(true)), conjunctionOf(Constant(false)))
                )
            }
            test("false | false => false") {
                assertOptimization(
                    Value.False,
                    disjunctionOf(conjunctionOf(Constant(false)), conjunctionOf(Constant(false)))
                )
            }
            test("false | link => link") {
                assertOptimization(
                    Value.Links(disjunctionOf(conjunctionOf(FlagLink("", "flagName")))),
                    disjunctionOf(
                        conjunctionOf(Constant(false)),
                        conjunctionOf(Variable(FlagLink("", "flagName")))
                    )
                )
            }
            test("true | link => true") {
                assertOptimization(
                    Value.True,
                    disjunctionOf(
                        conjunctionOf(Constant(true)),
                        conjunctionOf(Variable(FlagLink("", "flagName")))
                    )
                )
            }
            test("true | false | link => true") {
                assertOptimization(
                    Value.True,
                    disjunctionOf(
                        conjunctionOf(Constant(true)),
                        conjunctionOf(Constant(false)),
                        conjunctionOf(Variable(FlagLink("", "flagName")))
                    )
                )
            }
            test("link1 | link2 => link1 | link2") {
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
