# feature-flag-android

[![Gradle Plugin Portal](https://img.shields.io/maven-metadata/v/https/plugins.gradle.org/m2/com/linecorp/android/feature-flag/com.linecorp.android.feature-flag.gradle.plugin/maven-metadata.xml.svg?colorB=007ec6&label=Gradle%20Plugin%20Portal)](https://plugins.gradle.org/plugin/com.linecorp.android.feature-flag)

A Gradle plugin to achieve feature flag based development for Android applications.

## Overview

This plugin generates feature flags from a property file to achieve feature flag based development.
The flag values are visible as boolean values in source code, and useful to enable or disable features.
You can specify which feature is enabled by [build variant](https://developer.android.com/studio/build/build-variants), user name, [application version](https://developer.android.com/studio/publish/versioning), another flag value, and their combinations.

## Getting started

### Add library dependencies

The plugin is available on [Gradle Plugin Portal](https://plugins.gradle.org/). Add configurations in the `build.gradle.kts` file as follows.

```build.gradle.kts
// In your module's `build.gradle.kts`
plugins {
  id("com.linecorp.android.feature-flag") version "x.y.z"
}
```

### Add configuration

We assume this project has two build types: `debug` and `release` as follows.

```build.gradle
android {
    buildTypes {
        debug {
            // snip
        }
        release {
            // snip
        }
    }
}
```

We may define flag phases as follows, for example.

- `DEBUG`: Enabled when the build type is `debug`.
- `RELEASE`: Enabled when the build type is `debug` or `release`.

The following code is actual configuration example.

```build.gradle.kts
featureFlag {
    sourceFiles.setFrom(file("FEATURE_FLAG"))
    // You can also specify multiple files as follows.
    // sourceFiles.setFrom(files("FEATURE_FLAG", "FEATURE_FLAG_2"))

    packageName = "com.example.featureflag"
    phases = mapOf(
        "DEBUG" to setOf(buildType("debug")),
        "RELEASE" to setOf(buildType("debug"), buildType("release"))
    )
    releasePhaseSet = setOf(buildType("release"))
}
```

Definition of each property is as follows.

- `sourceFiles`: A set of feature flag property `File`s to read.
- `packageName`: (Optional) A package name of generated `FeatureFlag` class. If this is not set, packageName will be set from `namespace` of Android Gradle Plugin
- `phases`: A list of pairs of phase and the corresponding build variants.
- `releasePhaseSet`: Build variants to allow using primitive boolean values as flag values. An optimizer may inline flag values with the variants. `buildType` or `flavor` can be specified as a variant.
- `versionName`: (Optional) A version name which can override application version name.

   Also, this property can be assigned for library module since Android Gradle Plugin 7.0 or higher.

## How to use

### Create property files

Create a feature flag property file in your module.
An example is as follows.

```FEATURE_FLAG
# Simple property
FLAG_1 = DEBUG              # Enabled when build in `DEBUG` phase.
FLAG_2 = 1.2.0~             # Enabled when module version is `1.2.0` or later.
FLAG_3 = @user              # Enabled if the username is `user`.
FLAG_4 = packageName:FLAG_A # Delegates flag enability to `FLAG_A` in module which has `packageName` as packageName property.

# Property with options
OVERRIDABLE FLAG_5 = DEBUG  # Makes the flag modifiable at runtime.
PRIVATE FLAG_6 = DEBUG      # Makes the flag not accessible from a flag property file in another module.
LITERALIZE FLAG_7 = DEBUG   # Try to use a primitive boolean as the flag value.

# Property combination
# Enabled if either of the following conditions satisfies
# 1. Built in `DEBUG` phase.
# 2. Built in `RELEASE` phase and version `1.3.0` or later.
FLAG_8 = DEBUG, RELEASE & 1.3.0~

PRIVATE FLAG_9_USERS = @user1, @user2  # Enabled if built by `user1` or `user2`
FLAG_9 = FLAG_9_USERS & DEBUG          # Enabled if `FLAG_9_USERS` is enabled and built in `DEBUG` phase.
```

### Use flag value from application code

```kotlin
// Change view visibility
view.isVisible = FeatureFlag.FLAG_1

// Change activity
val targetActivityClass = if(FeatureFlag.FLAG_2) FooActivity::class.java else BarActivity::class.java
val intent = Intent(context, targetActivityClass)

// Change presenter
val presenter = if (FeatureFlag.FLAG_3) FooPresenter() else BarPresenter()
presenter.show()

// Modify the flag value programmatically (available when it's `OVERRIDABLE`)
FeatureFlag.FLAG_5 = false
```

## IntelliJ Plugin

We also provide [FeatureFlag Android Support](https://plugins.jetbrains.com/plugin/MARKETPLACE_ID), an IDE Plugin to support this `FEATURE_FLAG` file in IntelliJ IDEs including Android Studio.

See [here](intellij-plugin/README.md) for an overview and installation instructions.

## How to contribute

See [CONTRIBUTING.md](CONTRIBUTING.md).

If you believe you have discovered a vulnerability or have an issue related to security, please contact the maintainer directly or send us a email to dl_oss_dev@linecorp.com before sending a pull request.

## LICENSE

```
Copyright 2019 LINE Corporation

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

   http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```

See [LICENSE](LICENSE) for more detail.
