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

import com.github.zafarkhaja.semver.Version
import com.linecorp.android.featureflag.loader.FeatureFlagSelectorEvaluator.evaluate
import com.linecorp.android.featureflag.model.BuildEnvironment
import com.linecorp.android.featureflag.model.DisjunctionNormalForm.Conjunction
import com.linecorp.android.featureflag.model.DisjunctionNormalForm.Disjunction
import com.linecorp.android.featureflag.model.FeatureFlagAppliedElement
import com.linecorp.android.featureflag.model.FeatureFlagAppliedElement.Constant
import com.linecorp.android.featureflag.model.FeatureFlagAppliedElement.Variable
import com.linecorp.android.featureflag.model.FeatureFlagElement

/**
 * An evaluator of [FeatureFlagElement]s in [Disjunction].
 *
 * [evaluate] converts flag elements to a boolean value (except for [FeatureFlagElement.Link]) with
 * applying the current build environment.
 * The evaluation result keeps the same disjunctive normal form as the given structure.
 *
 * Here, a converted element is classified as one of the following two types.
 *
 * - Constant: Represents a boolean constant value. This is the evaluation result of
 *             [FeatureFlagElement.User], [FeatureFlagElement.Version], or [Element.Phase].
 * - Link: Represents a link to another flag. If the module name is empty, it is considered that the
 *         linked flag is in the same module.
 *
 * For example, [FeatureFlagSelectorEvaluator.evaluate] converts
 * `Disjunction(Conjunction(Phase("RELEASE"), Version("1.0.0"), Link("", "ANOTHER_FLAG")), Conjunction(User("USER")))`
 * to
 * `Disjunction(Conjunction(Constant(true), Constant(true), Link("", "ANOTHER_FLAG")), Constant(false))`
 * if the current phase is `RELEASE`, application version is `1.1.0`, and executor user name is
 * `USER_A`.
 */
internal object FeatureFlagSelectorEvaluator {

    /**
     * Converts [FeatureFlagElement]s in [value] to [FeatureFlagAppliedElement]s with applying
     * [buildEnviroment].
     *
     * If the given value has undefined `Phase` in `buildEnvironment`, this throws
     * [IllegalArgumentException].
     */
    fun evaluate(
        value: Disjunction<FeatureFlagElement>,
        buildEnvironment: BuildEnvironment
    ): Disjunction<FeatureFlagAppliedElement> {
        val evaluatedConjunctions = value.values
            .map { evaluateConjunction(it, buildEnvironment) }
        return Disjunction(evaluatedConjunctions)
    }

    private fun evaluateConjunction(
        selectorGroup: Conjunction<FeatureFlagElement>,
        buildEnvironment: BuildEnvironment
    ): Conjunction<FeatureFlagAppliedElement> {
        val evaluatedElements = selectorGroup.values
            .map { evaluateElement(it, buildEnvironment) }
        return Conjunction(evaluatedElements)
    }

    private fun evaluateElement(
        element: FeatureFlagElement,
        buildEnvironment: BuildEnvironment
    ): FeatureFlagAppliedElement = when (element) {
        is FeatureFlagElement.Phase -> Constant(isEnabledPhase(element, buildEnvironment.phasesMap))
        is FeatureFlagElement.User -> Constant(isEnabledUser(element, buildEnvironment.userName))
        is FeatureFlagElement.Link -> Variable(element.link)
        is FeatureFlagElement.Version -> Constant(
            isEnabledVersion(element, buildEnvironment.applicationVersion)
        )
    }

    private fun isEnabledPhase(
        element: FeatureFlagElement.Phase,
        phasesMap: Map<String, Boolean>
    ): Boolean = requireNotNull(phasesMap[element.phase]) { "Unknown phase: ${element.phase}" }

    private fun isEnabledUser(
        element: FeatureFlagElement.User,
        userName: String
    ): Boolean = element.name == userName

    private fun isEnabledVersion(
        element: FeatureFlagElement.Version,
        applicationVersion: Version
    ): Boolean = Version.valueOf(element.version) <= applicationVersion
}
