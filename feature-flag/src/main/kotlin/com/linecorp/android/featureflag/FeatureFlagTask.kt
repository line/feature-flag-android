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

import com.android.utils.FileUtils
import com.linecorp.android.featureflag.loader.FeatureFlagFileTokenizer
import com.linecorp.android.featureflag.loader.FeatureFlagOptionParser
import com.linecorp.android.featureflag.loader.FeatureFlagSelectorEvaluator
import com.linecorp.android.featureflag.loader.FeatureFlagSelectorParser
import com.linecorp.android.featureflag.loader.FeatureFlagValueOptimizer
import com.linecorp.android.featureflag.model.BuildEnvironment
import com.linecorp.android.featureflag.model.FeatureFlagData
import com.linecorp.android.featureflag.model.FeatureFlagProperties
import com.linecorp.android.featureflag.model.ForciblyOverriddenFeatureFlags
import org.gradle.api.DefaultTask
import org.gradle.api.Project
import org.gradle.api.tasks.CacheableTask
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction
import org.gradle.kotlin.dsl.findByType
import org.gradle.util.VersionNumber
import java.io.File

/**
 * A gradle task to generate feature flag Java file for the current module.
 */
@CacheableTask
abstract class FeatureFlagTask : DefaultTask() {

    @get:InputFile
    internal abstract var sourceFile: File

    @get:OutputDirectory
    internal abstract var outputDirectory: File

    @get:Input
    internal abstract var packageName: String

    @get:Internal
    internal abstract var phaseMap: Map<String, Boolean>

    /**
     * Key of [phaseMap] cache to ensure Gradle task uniqueness.
     */
    @Suppress("unused")
    @get:Input
    internal val phaseMapCacheKey: String
        get() = phaseMap.toString()

    @get:Input
    internal abstract var isReleaseVariant: Boolean

    @get:Input
    internal abstract var applicationVersionName: String

    @get:Input
    internal abstract var currentUserName: String

    @get:Internal
    internal abstract var forciblyOverriddenFeatureFlags: ForciblyOverriddenFeatureFlags

    /**
     * Key of [forciblyOverriddenFeatureFlags] cache to ensure Gradle task uniqueness.
     */
    @Suppress("unused")
    @get:Input
    internal val forciblyOverriddenFeatureFlagsCacheKey: String
        get() = forciblyOverriddenFeatureFlags.toString()

    @Suppress("unused")
    @TaskAction
    fun action() {
        FileUtils.cleanOutputDir(outputDirectory)
        // Create mapping here instead of before task creation due to module configuration order.
        val moduleNameToFeatureFlagPackageMap = createModuleToFeatureFlagPackageMap(project)
        val buildEnvironment = BuildEnvironment(
            phaseMap,
            VersionNumber.parse(applicationVersionName),
            currentUserName
        )
        val properties = sourceFile.useLines(block = FeatureFlagFileTokenizer::parse)
        val featureFlags = properties.entries.map {
            convertToFeatureFlagData(
                it,
                buildEnvironment,
                forciblyOverriddenFeatureFlags.forciblyEnabledFlags,
                forciblyOverriddenFeatureFlags.forciblyDisabledFlags
            )
        }
        val writer = FeatureFlagJavaFileWriter(
            outputDirectory,
            packageName,
            featureFlags,
            isReleaseVariant,
            moduleNameToFeatureFlagPackageMap
        )
        writer.write()
    }

    private fun createModuleToFeatureFlagPackageMap(project: Project): Map<String, String> =
        project.rootProject.subprojects.mapNotNull(::getModuleToFeatureFlagPackagePairOrNull)
            .toMap()

    private fun getModuleToFeatureFlagPackagePairOrNull(project: Project): Pair<String, String>? {
        val moduleName = project.path.substring(1) // To remove the leading ":"
        val packageName = getFeatureFlagPackageOrNull(project) ?: return null
        return moduleName to packageName
    }

    private fun getFeatureFlagPackageOrNull(project: Project): String? =
        project.extensions.findByType<FeatureFlagExtension>()?.packageName

    private fun convertToFeatureFlagData(
        entry: FeatureFlagProperties.Entry,
        buildEnvironment: BuildEnvironment,
        forciblyEnabledFlags: Set<String>,
        forciblyDisabledFlags: Set<String>
    ): FeatureFlagData {
        val options = FeatureFlagOptionParser.parse(entry.option)
        val flagName = entry.name
        val value = when (flagName) {
            in forciblyEnabledFlags -> FeatureFlagData.Value.True
            in forciblyDisabledFlags -> FeatureFlagData.Value.False
            else -> evaluateFlagValue(entry.value, buildEnvironment)
        }
        return FeatureFlagData(flagName, value, options)
    }

    private fun evaluateFlagValue(
        rawValue: String,
        buildEnvironment: BuildEnvironment
    ): FeatureFlagData.Value {
        val selectors = FeatureFlagSelectorParser.parse(rawValue)
        val evaluatedSelectors = FeatureFlagSelectorEvaluator.evaluate(selectors, buildEnvironment)
        return FeatureFlagValueOptimizer.optimize(evaluatedSelectors)
    }
}
