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

package com.linecorp.android.featureflag

import com.linecorp.android.featureflag.model.BuildVariant
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

/**
 * Tests for [FeatureFlagPlugin].
 */
class FeatureFlagPluginTest : FunSpec({
    val plugin = FeatureFlagPlugin()

    fun setOfBuildTypes(vararg buildTypeString: String): Set<BuildVariant.Element> =
        buildTypeString.map { BuildVariant.Element.BuildType(it) }.toSet()

    fun setOfProductFlavors(vararg productFlavorStrings: String): Set<BuildVariant.Element> =
        productFlavorStrings.map { BuildVariant.Element.Flavor(it) }.toSet()

    fun buildVariantOf(buildTypeString: String, vararg productFlavorStrings: String): BuildVariant =
        BuildVariant(
            BuildVariant.Element.BuildType(buildTypeString),
            productFlavorStrings.map { BuildVariant.Element.Flavor(it) }.toSet()
        )

    context("getPhaseMap") {
        context("returns correct value with buildType") {
            val phases = mapOf(
                "DEV" to setOfBuildTypes("dev"),
                "BETA" to setOfBuildTypes("beta", "dev"),
                "RC" to setOfBuildTypes("rc", "beta", "dev"),
                "RELEASE" to setOfBuildTypes("release", "rc", "beta", "dev")
            )
            test("in DEV") {
                val expected = mapOf(
                    "DEV" to true,
                    "BETA" to true,
                    "RC" to true,
                    "RELEASE" to true
                )
                plugin.getPhaseMap(phases, buildVariantOf("dev")) shouldBe expected
            }
            test("in BETA") {
                val expected = mapOf(
                    "DEV" to false,
                    "BETA" to true,
                    "RC" to true,
                    "RELEASE" to true
                )
                plugin.getPhaseMap(phases, buildVariantOf("beta")) shouldBe expected
            }
            test("in RC") {
                val expected = mapOf(
                    "DEV" to false,
                    "BETA" to false,
                    "RC" to true,
                    "RELEASE" to true
                )
                plugin.getPhaseMap(phases, buildVariantOf("rc")) shouldBe expected
            }
            test("in RELEASE") {
                val expected = mapOf(
                    "DEV" to false,
                    "BETA" to false,
                    "RC" to false,
                    "RELEASE" to true
                )
                plugin.getPhaseMap(phases, buildVariantOf("release")) shouldBe expected
            }
            test("unknown phase") {
                val expected = mapOf(
                    "DEV" to false,
                    "BETA" to false,
                    "RC" to false,
                    "RELEASE" to false
                )
                plugin.getPhaseMap(phases, buildVariantOf("INVALID")) shouldBe expected
            }
        }
        context("returns correct value with flavor") {
            val phases = mapOf(
                "DEV" to setOfProductFlavors("dev"),
                "BETA" to setOfProductFlavors("beta", "dev"),
                "RC" to setOfProductFlavors("rc", "beta", "dev"),
                "RELEASE" to setOfProductFlavors("release", "rc", "beta", "dev")
            )
            test("in DEV") {
                val expected = mapOf(
                    "DEV" to true,
                    "BETA" to true,
                    "RC" to true,
                    "RELEASE" to true
                )
                plugin.getPhaseMap(phases, buildVariantOf("BUILD_TYPE", "dev")) shouldBe expected
            }
            test("in BETA") {
                val expected = mapOf(
                    "DEV" to false,
                    "BETA" to true,
                    "RC" to true,
                    "RELEASE" to true
                )
                plugin.getPhaseMap(phases, buildVariantOf("BUILD_TYPE", "beta")) shouldBe expected
            }
            test("in RC") {
                val expected = mapOf(
                    "DEV" to false,
                    "BETA" to false,
                    "RC" to true,
                    "RELEASE" to true
                )
                plugin.getPhaseMap(phases, buildVariantOf("BUILD_TYPE", "rc")) shouldBe expected
            }
            test("in RELEASE") {
                val expected = mapOf(
                    "DEV" to false,
                    "BETA" to false,
                    "RC" to false,
                    "RELEASE" to true
                )
                plugin.getPhaseMap(
                    phases,
                    buildVariantOf("BUILD_TYPE", "release")
                ) shouldBe expected
            }
            test("unknown phase") {
                val expected = mapOf(
                    "DEV" to false,
                    "BETA" to false,
                    "RC" to false,
                    "RELEASE" to false
                )
                plugin.getPhaseMap(
                    phases,
                    buildVariantOf("BUILD_TYPE", "INVALID")
                ) shouldBe expected
            }
        }
    }
})
