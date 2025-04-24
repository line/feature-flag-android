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

import com.github.zafarkhaja.semver.Version
import com.linecorp.android.featureflag.loader.FeatureFlagFileTokenizer
import com.linecorp.android.featureflag.loader.FeatureFlagOptionParser
import com.linecorp.android.featureflag.loader.FeatureFlagSelectorEvaluator
import com.linecorp.android.featureflag.loader.FeatureFlagSelectorParser
import com.linecorp.android.featureflag.loader.FeatureFlagValueOptimizer
import com.linecorp.android.featureflag.model.BuildEnvironment
import com.linecorp.android.featureflag.model.FeatureFlagData
import com.linecorp.android.featureflag.model.FeatureFlagEntry
import com.linecorp.android.featureflag.model.ForciblyOverriddenFeatureFlags
import org.gradle.api.DefaultTask
import org.gradle.api.GradleException
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.file.FileCollection
import org.gradle.api.provider.Property
import org.gradle.api.tasks.CacheableTask
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.PathSensitive
import org.gradle.api.tasks.PathSensitivity
import org.gradle.api.tasks.TaskAction

/**
 * A gradle task to generate feature flag Java file for the current module.
 */
@CacheableTask
abstract class FeatureFlagTask : DefaultTask() {

    @get:InputFiles
    @get:PathSensitive(PathSensitivity.RELATIVE)
    internal abstract var sourceFiles: FileCollection

    @get:OutputDirectory
    internal abstract val outputDirectory: DirectoryProperty

    @get:Input
    internal abstract val packageName: Property<String>

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
        val outputDirectoryFile = outputDirectory.asFile.get()
        outputDirectoryFile.deleteRecursively()
        val buildEnvironment = BuildEnvironment(
            phaseMap,
            Version.valueOf(applicationVersionName),
            currentUserName
        )
        val entries = sourceFiles
            .map { it.useLines(block = FeatureFlagFileTokenizer::parse) }
            .flatten()
        val duplicatedFeatureFlagNameList = entries.groupingBy { it.name }
            .eachCount()
            .filter { it.value > 1 }
            .map { it.key }
        if (duplicatedFeatureFlagNameList.isNotEmpty()) {
            val errorMessage = duplicatedFeatureFlagNameList.joinToString(
                prefix = "Duplicated feature flag definition found: "
            )
            throw GradleException(errorMessage)
        }
        val featureFlags = entries.map {
            convertToFeatureFlagData(
                it,
                buildEnvironment,
                forciblyOverriddenFeatureFlags.forciblyEnabledFlags,
                forciblyOverriddenFeatureFlags.forciblyDisabledFlags
            )
        }
        val writer = FeatureFlagJavaFileWriter(
            outputDirectoryFile,
            packageName.get(),
            featureFlags,
            isReleaseVariant
        )
        writer.write()
    }

    private fun convertToFeatureFlagData(
        entry: FeatureFlagEntry,
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
