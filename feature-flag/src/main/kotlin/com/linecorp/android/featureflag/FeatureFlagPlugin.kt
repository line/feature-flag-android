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

import com.android.build.api.variant.ApplicationAndroidComponentsExtension
import com.android.build.api.variant.ApplicationVariant
import com.android.build.api.variant.LibraryAndroidComponentsExtension
import com.android.build.api.variant.LibraryVariant
import com.android.build.api.variant.Variant
import com.android.build.gradle.AppPlugin
import com.android.build.gradle.LibraryPlugin
import com.linecorp.android.featureflag.model.BuildVariant
import com.linecorp.android.featureflag.model.ForciblyOverriddenFeatureFlags
import java.util.Locale
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.create
import org.gradle.kotlin.dsl.register

/**
 * A gradle plugin adding a task to create a feature flag Java file from a property file.
 */
@Suppress("unused")
class FeatureFlagPlugin : Plugin<Project> {

    override fun apply(project: Project) {
        val extension = project.extensions.create<FeatureFlagExtension>("featureFlag", project)

        project.plugins.withType(AppPlugin::class.java) {
            project.extensions.getByType(ApplicationAndroidComponentsExtension::class.java)
                .onVariants { variant ->
                    installFeatureFlagGenerationTaskForApplication(project, variant, extension)
                }
        }
        project.plugins.withType(LibraryPlugin::class.java) {
            project.extensions.getByType(LibraryAndroidComponentsExtension::class.java)
                .onVariants { variant ->
                    installFeatureFlagGenerationTaskForLibrary(project, variant, extension)
                }
        }
    }

    private fun installFeatureFlagGenerationTaskForApplication(
        project: Project,
        variant: ApplicationVariant,
        extension: FeatureFlagExtension
    ) {
        val versionName = extension.versionName.takeIf(String::isNotEmpty)
            ?: variant.outputs.firstNotNullOfOrNull { it.versionName.orNull }
            ?: throw RuntimeException(
                "Missing `featureFlag.versionName` or `android.defaultConfig.versionName` option"
            )
        installFeatureFlagGenerationTask(project, variant, extension, versionName)
    }

    private fun installFeatureFlagGenerationTaskForLibrary(
        project: Project,
        variant: LibraryVariant,
        extension: FeatureFlagExtension
    ) {
        val versionName = extension.versionName.takeIf(String::isNotEmpty)
            ?: throw RuntimeException("Missing `featureFlag.versionName` option")
        installFeatureFlagGenerationTask(project, variant, extension, versionName)
    }

    private fun installFeatureFlagGenerationTask(
        project: Project,
        variant: Variant,
        extension: FeatureFlagExtension,
        versionName: String
    ) {
        val packageNameProvider = extension.packageName.takeIf(String::isNotEmpty)
            ?.let { packageName -> project.provider { packageName } }
            ?: variant.namespace
            ?: throw RuntimeException(
                "Missing `featureFlag.packageName` or `android.namespace` option"
            )

        val currentBuildVariant = BuildVariant(
            BuildVariant.Element.BuildType(checkNotNull(variant.buildType)),
            variant.getProductFlavorSet()
        )

        val capitalizedVariantName = variant.name.replaceFirstChar {
            if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString()
        }
        val taskName = "generate${capitalizedVariantName}FeatureFlag"
        val taskProvider = project.tasks.register<FeatureFlagTask>(taskName) {
            sourceFiles = extension.sourceFiles
            packageName.set(packageNameProvider)
            phaseMap = getPhaseMap(extension.phases, currentBuildVariant)
            isReleaseVariant = extension.releasePhaseSet.any(currentBuildVariant::includes)
            applicationVersionName = versionName
            currentUserName = System.getProperty("user.name")
            forciblyOverriddenFeatureFlags = ForciblyOverriddenFeatureFlags.parse(project)
        }
        variant.sources.java
            ?.addGeneratedSourceDirectory(taskProvider, FeatureFlagTask::outputDirectory)
    }

    internal fun getPhaseMap(
        phases: Map<String, Set<BuildVariant.Element>>,
        currentBuildVariant: BuildVariant
    ): Map<String, Boolean> = phases.mapValues { it.value.any(currentBuildVariant::includes) }

    private fun Variant.getProductFlavorSet(): Set<BuildVariant.Element.Flavor> =
        productFlavors.map { BuildVariant.Element.Flavor(it.second) }.toSet()
}
