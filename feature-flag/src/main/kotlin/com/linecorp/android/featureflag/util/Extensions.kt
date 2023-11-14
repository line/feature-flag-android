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

package com.linecorp.android.featureflag.util

import com.android.build.gradle.BaseExtension
import com.android.build.gradle.api.BaseVariant
import com.linecorp.android.featureflag.model.BuildVariant
import org.gradle.api.Project
import org.gradle.api.file.DirectoryProperty
import java.io.File

internal val Project.android: BaseExtension
    get() = project.extensions.getByName("android") as BaseExtension

internal fun BaseVariant.getProductFlavorSet(): Set<BuildVariant.Element.Flavor> =
    productFlavors.map { BuildVariant.Element.Flavor(it.name) }.toSet()

internal fun File.resolvePackageName(packageName: String): File {
    val packagePath = packageName.replace('.', File.separatorChar)
    return resolve(packagePath)
}

internal fun Project.getFeatureFlagOutputDir(variant: BaseVariant): File =
    resolve(layout.buildDirectory, "generated", "source", "featureFlag", variant.dirName)

private fun resolve(directory: DirectoryProperty, vararg paths: String): File {
    val combinedPath = paths.joinToString(File.separator)
    return directory.file(combinedPath).get().asFile
}
