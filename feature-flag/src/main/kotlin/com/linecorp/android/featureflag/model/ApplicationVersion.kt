//
// Copyright 2026 LY Corporation
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

import org.jetbrains.annotations.VisibleForTesting
import com.github.zafarkhaja.semver.Version as SemVerImpl

/**
 * An interface representing the application version and its comparison method.
 */
internal sealed interface ApplicationVersion {

    fun isHigherOrEqualThan(versionString: String): Boolean

    /**
     * An application version in Semantic Versioning (SemVer) notation.
     */
    class SemVer(@VisibleForTesting internal val version: SemVerImpl) :
        ApplicationVersion,
        Comparable<SemVer> {

        override fun isHigherOrEqualThan(versionString: String): Boolean =
            this >= from(versionString)

        override fun compareTo(other: SemVer): Int = version.compareTo(other.version)

        companion object {
            fun from(versionString: String): SemVer = SemVer(SemVerImpl.parse(versionString))
        }
    }

    companion object {
        fun from(versionString: String, versionNotation: VersionNotation): ApplicationVersion =
            when (versionNotation) {
                VersionNotation.SEM_VER -> SemVer.from(versionString)
            }
    }
}
