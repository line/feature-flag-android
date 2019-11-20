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

package com.linecorp.android.featureflag.model

/**
 * A structure of the disjunctive normal form of [T].
 * A form instance is represented by `Disjunction(Conjunction(T, ...), ...)`
 */
internal object DisjunctionNormalForm {
    /**
     * A model of disjunction of [values], where each value is conjunction of [T].
     */
    internal class Disjunction<T>(val values: List<Conjunction<T>>)

    /**
     * A data model represents conjunction of [values].
     */
    internal class Conjunction<T>(val values: List<T>)
}
