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

import com.linecorp.android.featureflag.model.ApplicationVersion
import com.linecorp.android.featureflag.model.BuildEnvironment
import com.linecorp.android.featureflag.model.DisjunctionNormalForm.Disjunction
import com.linecorp.android.featureflag.model.FeatureFlagAppliedElement
import com.linecorp.android.featureflag.model.FeatureFlagElement
import com.linecorp.android.featureflag.model.FlagLink
import com.linecorp.android.featureflag.model.VersionNotation
import com.linecorp.android.featureflag.utils.assertDisjunction
import com.linecorp.android.featureflag.utils.conjunctionOf
import com.linecorp.android.featureflag.utils.disjunctionOf
import io.kotest.assertions.throwables.shouldThrowWithMessage
import io.kotest.core.spec.style.FunSpec

/**
 * Tests for [FeatureFlagSelectorEvaluator].
 */
class FeatureFlagSelectorEvaluatorTest : FunSpec({
    fun assertEvaluation(
        expectedDisjunction: Disjunction<FeatureFlagAppliedElement>,
        disjunction: Disjunction<FeatureFlagElement>,
        phasesMap: Map<String, Boolean>,
        applicationVersion: String,
        userName: String
    ) = assertDisjunction(
        expectedDisjunction,
        FeatureFlagSelectorEvaluator.evaluate(
            disjunction,
            BuildEnvironment(
                phasesMap,
                ApplicationVersion.from(applicationVersion, VersionNotation.SEM_VER),
                userName
            )
        )
    )

    context("Evaluated result is correct") {
        context("on each element") {
            fun assertSingleLiteralEvaluation(
                expectedElement: FeatureFlagAppliedElement,
                sourceElement: FeatureFlagElement,
                phasesMap: Map<String, Boolean> = emptyMap(),
                applicationVersion: String = "1.0.0",
                userName: String = "USERNAME"
            ) = assertEvaluation(
                disjunctionOf(conjunctionOf(expectedElement)),
                disjunctionOf(conjunctionOf(sourceElement)),
                phasesMap,
                applicationVersion,
                userName
            )

            test("enabled by phase") {
                assertSingleLiteralEvaluation(
                    FeatureFlagAppliedElement.Constant(true),
                    FeatureFlagElement.Phase("PHASE"),
                    phasesMap = mapOf("PHASE" to true)
                )
            }
            test("disabled by phase") {
                assertSingleLiteralEvaluation(
                    FeatureFlagAppliedElement.Constant(false),
                    FeatureFlagElement.Phase("DISABLED_PHASE"),
                    phasesMap = mapOf("DISABLED_PHASE" to false)
                )
            }
            test("enabled by user") {
                assertSingleLiteralEvaluation(
                    FeatureFlagAppliedElement.Constant(true),
                    FeatureFlagElement.User("USERNAME"),
                    userName = "USERNAME"
                )
            }
            test("disabled by user") {
                assertSingleLiteralEvaluation(
                    FeatureFlagAppliedElement.Constant(false),
                    FeatureFlagElement.User("DISABLED_USERNAME"),
                    userName = "USERNAME"
                )
            }
            test("enabled by version: same version") {
                assertSingleLiteralEvaluation(
                    FeatureFlagAppliedElement.Constant(true),
                    FeatureFlagElement.Version("1.2.3"),
                    applicationVersion = "1.2.3"
                )
            }
            test("enabled by version: newer patch version") {
                assertSingleLiteralEvaluation(
                    FeatureFlagAppliedElement.Constant(true),
                    FeatureFlagElement.Version("1.2.2"),
                    applicationVersion = "1.2.3"
                )
            }
            test("enabled by version: newer minor version") {
                assertSingleLiteralEvaluation(
                    FeatureFlagAppliedElement.Constant(true),
                    FeatureFlagElement.Version("1.1.0"),
                    applicationVersion = "1.2.3"
                )
            }
            test("enabled by version: newer major version") {
                assertSingleLiteralEvaluation(
                    FeatureFlagAppliedElement.Constant(true),
                    FeatureFlagElement.Version("0.0.1"),
                    applicationVersion = "1.2.3"
                )
            }
            test("disabled by version: older patch version") {
                assertSingleLiteralEvaluation(
                    FeatureFlagAppliedElement.Constant(false),
                    FeatureFlagElement.Version("1.2.4"),
                    applicationVersion = "1.2.3"
                )
            }
            test("disabled by version: older minor version") {
                assertSingleLiteralEvaluation(
                    FeatureFlagAppliedElement.Constant(false),
                    FeatureFlagElement.Version("1.3.0"),
                    applicationVersion = "1.2.3"
                )
            }
            test("disabled by version: older major version") {
                assertSingleLiteralEvaluation(
                    FeatureFlagAppliedElement.Constant(false),
                    FeatureFlagElement.Version("2.0.0"),
                    applicationVersion = "1.2.3"
                )
            }
            test("link to self module flag") {
                assertSingleLiteralEvaluation(
                    FeatureFlagAppliedElement.Variable(FlagLink("", "flagValue")),
                    FeatureFlagElement.Link(FlagLink("", "flagValue"))
                )
            }
            test("link to another module flag") {
                assertSingleLiteralEvaluation(
                    FeatureFlagAppliedElement.Variable(FlagLink("anotherModule", "flagValue")),
                    FeatureFlagElement.Link(FlagLink("anotherModule", "flagValue"))
                )
            }
        }
    }

    context("Model structure is same") {
        fun assertStructureEvaluation(
            expectedDisjunction: Disjunction<FeatureFlagAppliedElement>,
            sourceDisjunction: Disjunction<FeatureFlagElement>
        ) = assertEvaluation(
            expectedDisjunction,
            sourceDisjunction,
            mapOf("PHASE" to false),
            "0.0.1",
            ""
        )

        context("multiple elements in conjunction") {
            assertStructureEvaluation(
                disjunctionOf(
                    conjunctionOf(
                        FeatureFlagAppliedElement.Constant(false),
                        FeatureFlagAppliedElement.Constant(false)
                    )
                ),
                disjunctionOf(
                    conjunctionOf(
                        FeatureFlagElement.Phase("PHASE"),
                        FeatureFlagElement.Phase("PHASE")
                    )
                )
            )
        }

        context("multiple conjunction in disjunction") {
            assertStructureEvaluation(
                disjunctionOf(
                    conjunctionOf(FeatureFlagAppliedElement.Constant(false)),
                    conjunctionOf(FeatureFlagAppliedElement.Constant(false))
                ),
                disjunctionOf(
                    conjunctionOf(FeatureFlagElement.Phase("PHASE")),
                    conjunctionOf(FeatureFlagElement.Phase("PHASE"))
                )
            )
        }

        context("multiple elements in conjunction and multiple conjunction in disjunction") {
            assertStructureEvaluation(
                disjunctionOf(
                    conjunctionOf(
                        FeatureFlagAppliedElement.Constant(false),
                        FeatureFlagAppliedElement.Constant(false)
                    ),
                    conjunctionOf(FeatureFlagAppliedElement.Constant(false))
                ),
                disjunctionOf(
                    conjunctionOf(
                        FeatureFlagElement.Phase("PHASE"),
                        FeatureFlagElement.Phase("PHASE")
                    ),
                    conjunctionOf(FeatureFlagElement.Phase("PHASE"))
                )
            )
        }
    }

    context("Evaluating is failed") {
        test("Unknown phase") {
            shouldThrowWithMessage<IllegalArgumentException>("Unknown phase: PHASE") {
                FeatureFlagSelectorEvaluator.evaluate(
                    disjunctionOf(conjunctionOf(FeatureFlagElement.Phase("PHASE"))),
                    BuildEnvironment(
                        emptyMap(),
                        ApplicationVersion.from("1.0.0", VersionNotation.SEM_VER),
                        ""
                    )
                )
            }
        }
    }
})
