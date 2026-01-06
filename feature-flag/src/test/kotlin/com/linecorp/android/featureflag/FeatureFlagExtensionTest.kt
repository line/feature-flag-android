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
import io.mockk.every
import io.mockk.mockk
import java.io.File
import org.gradle.api.Project
import org.gradle.api.file.ConfigurableFileCollection

class FeatureFlagExtensionTest : FunSpec({
    val mockedSourceFiles = mockk<ConfigurableFileCollection>()
    val project: Project = mockk {
        every { rootDir } returns File("/tmp/")
        every { files(File("/tmp/FEATURE_FLAG")) } returns mockedSourceFiles
    }
    val extension = FeatureFlagExtension(project)

    test("buildType") {
        extension.buildType("release") shouldBe BuildVariant.Element.BuildType("release")
    }
    test("flavor") {
        extension.flavor("release") shouldBe BuildVariant.Element.Flavor("release")
    }
})
