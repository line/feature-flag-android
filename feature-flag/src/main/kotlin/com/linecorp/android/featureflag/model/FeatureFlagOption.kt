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
 * Type of feature flag options to specify the flag visibility and mutability.
 */
internal enum class FeatureFlagOption {
    /**
     * An option to limit access scope of a flag.
     * Other flags can access to a flag with this option only when they are declared in the same
     * module.
     */
    PRIVATE,
    /**
     * An option to make a flag modifiable on runtime.
     * This option cannot be used with [LITERALIZE] option at the same time.
     */
    OVERRIDABLE,
    /**
     * An option to allow a flag value to use a boolean literal even in non-release phase.
     * Flags with this option does not wrap the value by dynamic instantiation.
     * Then, the flags won't suppress static analyser warnings.
     * This option cannot be used with [OVERRIDABLE] option at the same time.
     */
    LITERALIZE,
}
