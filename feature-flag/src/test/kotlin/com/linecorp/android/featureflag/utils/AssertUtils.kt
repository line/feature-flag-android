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

package com.linecorp.android.featureflag.utils

import com.linecorp.android.featureflag.model.DisjunctionNormalForm
import io.kotest.matchers.shouldBe

/**
 * Asserts that the given two [DisjunctionNormalForm.Disjunction]s are equal.
 * In other words, returns true if and only if equals returns true for each conjunction pair in
 * zipped disjunctions.
 */
internal fun <T> assertDisjunction(
    expected: DisjunctionNormalForm.Disjunction<out T>,
    actual: DisjunctionNormalForm.Disjunction<out T>
) {
    actual.values.size shouldBe expected.values.size
    expected.values.zip(actual.values).forEach { (expectedConjunction, actualConjunction) ->
        actualConjunction.values shouldBe expectedConjunction.values
    }
}
