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

import com.linecorp.android.featureflag.model.DisjunctionNormalForm.Conjunction
import com.linecorp.android.featureflag.model.DisjunctionNormalForm.Disjunction
import com.linecorp.android.featureflag.model.FeatureFlagData
import com.linecorp.android.featureflag.model.FlagLink
import com.linecorp.android.featureflag.model.FeatureFlagAppliedElement as AppliedElement

/**
 * An optimizer of [AppliedElement]s in the disjunctive normal form.
 *
 * This calculates elements with logical operation of disjunction and conjunction. The result becomes
 * a single boolean value or the disjunctive normal form of [FlagLink]s.
 *
 * For example, [FeatureFlagSelectorEvaluator.evaluate] converts
 * `Disjunction(Conjunction(Constant(true), Constant(true), Link("", "ANOTHER_FLAG")), Conjunction(Constant(false)))`
 * to
 * `Disjunction(Conjunction(Link("", "ANOTHER_FLAG")))`
 */
internal object FeatureFlagValueOptimizer {

    fun optimize(disjunction: Disjunction<AppliedElement>): FeatureFlagData.Value {
        val optimizedDisjunction = disjunction.values.map { it.values.calculateConjunction() }
        return optimizedDisjunction.calculateDisjunction()
    }

    private fun List<ConjunctionCalculationResult>.calculateDisjunction(): FeatureFlagData.Value =
        when {
            any { it == ConjunctionCalculationResult.True } -> FeatureFlagData.Value.True
            all { it == ConjunctionCalculationResult.False } -> FeatureFlagData.Value.False
            else -> {
                val links = filterIsInstance<ConjunctionCalculationResult.Links>()
                    .map(ConjunctionCalculationResult.Links::links)
                FeatureFlagData.Value.Links(Disjunction(links))
            }
        }

    private fun List<AppliedElement>.calculateConjunction(): ConjunctionCalculationResult = when {
        all { it == AppliedElement.Constant(true) } -> ConjunctionCalculationResult.True
        any { it == AppliedElement.Constant(false) } -> ConjunctionCalculationResult.False
        else -> {
            val links =
                filterIsInstance<AppliedElement.Variable>().map(AppliedElement.Variable::link)
            ConjunctionCalculationResult.Links(Conjunction(links))
        }
    }

    private sealed class ConjunctionCalculationResult {
        object True : ConjunctionCalculationResult()
        object False : ConjunctionCalculationResult()
        class Links(val links: Conjunction<FlagLink>) : ConjunctionCalculationResult()
    }
}
