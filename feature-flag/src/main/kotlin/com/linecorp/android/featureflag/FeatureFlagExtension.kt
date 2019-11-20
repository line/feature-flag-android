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
import org.gradle.api.Project
import java.io.File

/**
 * A Gradle extension to provide a feature flag file.
 *
 * **Note**: Keep this an open class because a proxy class is created by the gradle system.
 */
open class FeatureFlagExtension(project: Project) {
    var sourceFile: File = project.rootDir.resolve(DEFAULT_FEATURE_FLAG_PROPERTY_FILE_NAME)
    var phases: Map<String, Set<BuildVariant.Element>> = mapOf()
    var releasePhaseSet: Set<BuildVariant.Element> = setOf()
    var packageName: String = ""

    fun buildType(name: String): BuildVariant.Element = BuildVariant.Element.BuildType(name)

    fun flavor(name: String): BuildVariant.Element = BuildVariant.Element.Flavor(name)

    companion object {
        private const val DEFAULT_FEATURE_FLAG_PROPERTY_FILE_NAME = "FEATURE_FLAG"
    }
}
