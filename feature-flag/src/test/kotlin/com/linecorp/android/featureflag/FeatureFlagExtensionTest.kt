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
import io.mockk.every
import io.mockk.mockk
import org.gradle.api.Project
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe
import java.io.File
import kotlin.test.assertEquals

object FeatureFlagExtensionTest : Spek({
    describe("public functions return correct values") {
        val project: Project = mockk {
            every { rootDir } returns File("/tmp/")
        }
        val extension by memoized {
            FeatureFlagExtension(
                project
            )
        }

        it("buildType") {
            assertEquals(
                BuildVariant.Element.BuildType("release"),
                extension.buildType("release")
            )
        }
        it("flavor") {
            assertEquals(
                BuildVariant.Element.Flavor("production"),
                extension.flavor("production")
            )
        }
    }
})
