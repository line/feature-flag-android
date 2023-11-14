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

import com.android.build.gradle.AppExtension
import com.android.build.gradle.BaseExtension
import com.android.build.gradle.LibraryExtension
import com.android.build.gradle.api.BaseVariant
import com.linecorp.android.featureflag.model.BuildVariant
import com.linecorp.android.featureflag.model.ForciblyOverriddenFeatureFlags
import com.linecorp.android.featureflag.util.android
import com.linecorp.android.featureflag.util.getFeatureFlagOutputDir
import com.linecorp.android.featureflag.util.getProductFlavorSet
import org.gradle.api.DomainObjectSet
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.kotlin.dsl.create
import java.io.File
import java.util.Locale

/**
 * A gradle plugin adding a task to create a feature flag Java file from a property file.
 */
@Suppress("unused")
class FeatureFlagPlugin : Plugin<Project> {

    override fun apply(project: Project) {
        // Note: We must call `registerJavaGeneratingTask` before applying `kotlin-android`.
        if (project.plugins.hasPlugin("kotlin-android")) {
            throw RuntimeException(
                "`feature-flag` plugin must apply before apply `kotlin-android` plugin"
            )
        }

        val extension: FeatureFlagExtension =
            project.extensions.create("featureFlag", project)

        project.afterEvaluate { installFeatureFlagGenerationTask(extension) }
    }

    private fun Project.installFeatureFlagGenerationTask(
        extension: FeatureFlagExtension
    ) {
        val androidExtension = project.android as? BaseExtension
            ?: throw RuntimeException("`feature-flag` plugin requires any of android plugin")

        val applicationVersionName = extension.versionName.takeIf(String::isNotEmpty)
            ?: androidExtension.defaultConfig.versionName
            ?: throw RuntimeException(
                "Missing `featureFlag.versionName` or `android.defaultConfig.versionName` option"
            )

        val localSourceFile = extension.sourceFile.takeIf(File::exists)
            ?: throw RuntimeException("Missing `sourceFile` option or file isn't exist")

        val localPackageName = extension.packageName.takeIf(String::isNotEmpty)
            ?: androidExtension.namespace
            ?: throw RuntimeException(
                "Missing `featureFlag.packageName` or `android.namespace` option"
            )

        // Here, we call `DefaultDomainObjectSet.all` instead of standard iterator extensions.
        // For more details, refer to `AppExtension.getApplicationVariants()`
        androidExtension.getVariants()?.all {
            installFeatureFlagGenerationTask(
                this,
                extension,
                applicationVersionName,
                localSourceFile,
                localPackageName
            )
        }
    }

    private fun BaseExtension.getVariants(): DomainObjectSet<out BaseVariant>? = when (this) {
        is AppExtension -> applicationVariants
        is LibraryExtension -> libraryVariants
        else -> null
    }

    private fun Project.installFeatureFlagGenerationTask(
        variant: BaseVariant,
        extension: FeatureFlagExtension,
        versionName: String,
        featureFlagSourceFile: File,
        applicationPackageName: String?
    ) {
        val currentBuildVariant = BuildVariant(
            BuildVariant.Element.BuildType(variant.buildType.name),
            variant.getProductFlavorSet()
        )

        val capitalizedVariantName = variant.name.replaceFirstChar {
            if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString()
        }
        val taskName = "generate${capitalizedVariantName}FeatureFlag"
        val taskProvider = tasks.register(taskName, FeatureFlagTask::class.java) {
            markNotCompatibleWithConfigurationCache(this)
            sourceFile = featureFlagSourceFile
            outputDirectory = getFeatureFlagOutputDir(variant)
            packageName = applicationPackageName ?: variant.applicationId
            phaseMap = getPhaseMap(extension.phases, currentBuildVariant)
            isReleaseVariant = extension.releasePhaseSet.any(currentBuildVariant::includes)
            applicationVersionName = versionName
            currentUserName = System.getProperty("user.name")
            forciblyOverriddenFeatureFlags = ForciblyOverriddenFeatureFlags.parse(project)
        }
        val task = taskProvider.get()
        variant.registerJavaGeneratingTask(task, task.outputDirectory)
    }

    internal fun getPhaseMap(
        phases: Map<String, Set<BuildVariant.Element>>,
        currentBuildVariant: BuildVariant
    ): Map<String, Boolean> = phases.mapValues { it.value.any(currentBuildVariant::includes) }

    private fun markNotCompatibleWithConfigurationCache(task: Task) {
        try {
            // Configuration cache method incubating in Gradle 7.4
            val method = Task::class.java.getDeclaredMethod(
                "notCompatibleWithConfigurationCache",
                java.lang.String::class.java
            )
            method.invoke(
                task,
                "Requires Project instance to resolve build variants during task execution."
            )
        } catch (ex: NoSuchMethodException) {
            // pass
        }
    }
}
