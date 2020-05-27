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

import com.google.common.base.Charsets
import com.linecorp.android.featureflag.model.FeatureFlagData
import com.linecorp.android.featureflag.model.FeatureFlagOption
import com.linecorp.android.featureflag.model.FlagLink
import com.linecorp.android.featureflag.util.resolvePackageName
import com.squareup.javawriter.JavaWriter
import java.io.File
import java.util.EnumSet
import javax.lang.model.element.Modifier

/**
 * A writer class of feature flag Java file to make flag values accessible from application code.
 */
internal class FeatureFlagJavaFileWriter(
    private val outputDirectory: File,
    private val packageName: String,
    private val featureFlags: List<FeatureFlagData>,
    private val isReleasePhase: Boolean,
    private val moduleNameToFeatureFlagPackageMap: Map<String, String>
) {

    fun write() {
        val packageDirectory = outputDirectory.resolvePackageName(packageName)
        if (!packageDirectory.isDirectory && !packageDirectory.mkdirs()) {
            throw RuntimeException("Failed to create " + packageDirectory.absolutePath)
        }
        val outputFile = packageDirectory.resolve("$FEATURE_FLAG_CLASS_NAME.java")
        JavaWriter(outputFile.bufferedWriter(Charsets.UTF_8)).use { writer ->
            writer.emitJavadoc("Automatically generated file. DO NOT MODIFY")
            writer.emitPackage(packageName)

            writer.writeImports()

            writer.emitType(
                FEATURE_FLAG_CLASS_NAME,
                JavaDeclaration.CLASS,
                JavaModifier.PUBLIC_FINAL
            ) {
                writeFeatureFlagClassBody()
            }
        }
    }

    private fun JavaWriter.writeConstantFlags(
        flags: List<FeatureFlagData>
    ) = flags.forEach { flag ->
        println("${flag.name} = ${flag.value}")

        val isRequiredLiteralValue = flag.hasOption(FeatureFlagOption.LITERALIZE) || isReleasePhase

        // When LITERALIZE option is enabled or the current build variant in release, we use a
        // primitive boolean as a flag value so that an optimizer can clean it up.
        // Otherwise, we wrap a boolean value with `Boolean.valueOf()` to suppress warnings by a
        // static analyzer.
        val actualFlagValue = flag.value.toJavaValue(isRequiredLiteralValue)

        if (flag.hasOption(FeatureFlagOption.DEPRECATED)) {
            emitAnnotation(DEPRECATED_ANNOTATION_CLASS_NAME)
        }
        emitField(JavaType.BOOLEAN, flag.name, flag.getFieldModifier(), actualFlagValue)
    }

    private fun JavaWriter.writeOverridableFlags(flags: List<FeatureFlagData>) {
        flags.forEach { flag ->
            val flagName = flag.name
            val flagValue = flag.value.toJavaValue(true)
            println("$flagName = $flagValue")
            if (flag.hasOption(FeatureFlagOption.DEPRECATED)) {
                emitAnnotation(DEPRECATED_ANNOTATION_CLASS_NAME)
            }
            emitField(
                JavaType.BOOLEAN,
                flagName,
                flag.getFieldModifier(),
                flagValue
            )
        }

        val fieldName = "ACCESSORS"
        emitField(
            "List<${JavaType.FEATURE_FLAG_ACCESSOR}>",
            fieldName,
            JavaModifier.PUBLIC_STATIC_FINAL
        )

        emitStaticInitializer {
            emitStatement("List<${JavaType.FEATURE_FLAG_ACCESSOR}> accessors = new ArrayList<>()")
            flags.forEach { emitOverridableFeatureFlagAccessorElement(it) }
            emitStatement("$fieldName = Collections.unmodifiableList(accessors)")
        }
    }

    private fun JavaWriter.writeImports() {
        if (isReleasePhase) {
            return
        }
        emitImports("java.util.ArrayList")
        emitImports("java.util.Collections")
        emitImports("java.util.List")
        emitEmptyLine()
    }

    private fun JavaWriter.writeFeatureFlagClassBody() {
        // In release build, don't make overridable configs.
        if (isReleasePhase) {
            writeConstantFlags(featureFlags)
            return
        }

        val (overridableFlags, constantFlags) =
            featureFlags.partition { it.hasOption(FeatureFlagOption.OVERRIDABLE) }
        writeConstantFlags(constantFlags)
        writeOverridableFlags(overridableFlags)

        writeFeatureFlagAccessorClass()
        writeSupplierClass()
        writeConsumerClass()
    }

    private fun JavaWriter.writeFeatureFlagAccessorClass() =
        emitType(
            JavaType.FEATURE_FLAG_ACCESSOR,
            JavaDeclaration.CLASS,
            JavaModifier.PUBLIC_STATIC_FINAL
        ) {
            emitField(JavaType.STRING, "name", JavaModifier.PUBLIC_FINAL)
            emitField(JavaType.BOOLEAN, "defaultValue", JavaModifier.PUBLIC_FINAL)
            emitField(JavaType.SUPPLIER, "getter", JavaModifier.PRIVATE_FINAL)
            emitField(JavaType.CONSUMER, "setter", JavaModifier.PRIVATE_FINAL)

            emitConstructor(
                JavaModifier.PRIVATE,
                JavaType.STRING to "name",
                JavaType.BOOLEAN to "defaultValue",
                JavaType.SUPPLIER to "getter",
                JavaType.CONSUMER to "setter"
            ) {
                emitStatement("this.name = name")
                emitStatement("this.defaultValue = defaultValue")
                emitStatement("this.getter = getter")
                emitStatement("this.setter = setter")
            }

            emitMethod(JavaType.BOOLEAN, "getValue", JavaModifier.PUBLIC) {
                emitStatement("return getter.get()")
            }

            emitMethod(
                JavaType.VOID,
                "setValue",
                JavaModifier.PUBLIC,
                JavaType.BOOLEAN to "value"
            ) {
                emitStatement("setter.accept(value)")
            }
        }

    private fun JavaWriter.writeSupplierClass() =
        emitType(JavaType.SUPPLIER, JavaDeclaration.INTERFACE, JavaModifier.PRIVATE) {
            emitMethod(JavaType.BOOLEAN, "get", JavaModifier.PACKAGE_PRIVATE)
        }

    private fun JavaWriter.writeConsumerClass() =
        emitType(JavaType.CONSUMER, JavaDeclaration.INTERFACE, JavaModifier.PRIVATE) {
            emitMethod(
                JavaType.VOID,
                "accept",
                JavaModifier.PACKAGE_PRIVATE,
                JavaType.BOOLEAN to "b"
            )
        }

    private fun JavaWriter.emitType(
        type: String,
        kind: String,
        modifiers: Set<Modifier>,
        body: JavaWriter.() -> Unit
    ) {
        beginType(type, kind, modifiers)
        body()
        endType()
    }

    private fun JavaWriter.emitConstructor(
        modifiers: Set<Modifier>,
        vararg parameters: Pair<String, String>,
        body: JavaWriter.() -> Unit
    ) {
        beginConstructor(modifiers, *toParameterArray(parameters))
        body()
        endConstructor()
    }

    private fun JavaWriter.emitMethod(
        returnType: String,
        name: String,
        modifiers: Set<Modifier>,
        vararg parameters: Pair<String, String>,
        body: JavaWriter.() -> Unit = {}
    ) {
        beginMethod(returnType, name, modifiers, *toParameterArray(parameters))
        body()
        endMethod()
    }

    private fun JavaWriter.emitOverridableFeatureFlagAccessorElement(flag: FeatureFlagData) {
        // We use an anonymous supplier and consumer instead of lambdas to workaround multi-dex
        // issues for old devices.
        val accessorCode =
            """
            |accessors.add(
            |    new FeatureFlagAccessor(
            |        "${flag.name}",
            |        ${flag.value.toJavaValue(true)},
            |        new Supplier() {
            |            @Override
            |            public boolean get() {
            |                return ${flag.name};
            |            }
            |        },
            |        new Consumer() {
            |            @Override
            |            public void accept(boolean b) {
            |                ${flag.name} = b;
            |            }
            |        }
            |    )
            |)""".trimMargin()
        emitStatement(accessorCode)
    }

    private fun JavaWriter.emitStaticInitializer(body: JavaWriter.() -> Unit) {
        beginInitializer(true)
        body()
        endInitializer()
    }

    private fun convertToJavaLiteralValue(value: FeatureFlagData.Value): String = when (value) {
        is FeatureFlagData.Value.True -> "true"
        is FeatureFlagData.Value.False -> "false"
        is FeatureFlagData.Value.Links -> convertLinksToJavaLiteralValue(value)
    }

    private fun convertLinksToJavaLiteralValue(linksValue: FeatureFlagData.Value.Links): String =
        linksValue.linksDisjunction.values.joinToString(separator = " || ") { conjunctions ->
            conjunctions.values.joinToString(
                separator = " && ",
                transform = ::convertLinkToJavaLiteralValue
            )
        }

    private fun convertLinkToJavaLiteralValue(link: FlagLink): String {
        if (link.moduleName.isEmpty()) {
            return link.flagName
        }
        val packageName = checkNotNull(moduleNameToFeatureFlagPackageMap[link.moduleName]) {
            "Missing feature flag plugin on module ${link.moduleName}."
        }
        return "$packageName.FeatureFlag.${link.flagName}"
    }

    private fun wrapLiteral(literal: String): String = "Boolean.valueOf($literal)"

    private fun FeatureFlagData.hasOption(option: FeatureFlagOption): Boolean =
        options.contains(option)

    private fun FeatureFlagData.Value.toJavaValue(isRequiredLiteralValue: Boolean): String {
        val literalValue = convertToJavaLiteralValue(this)
        return if (isRequiredLiteralValue) literalValue else wrapLiteral(literalValue)
    }

    private fun FeatureFlagData.getFieldModifier(): Set<Modifier> {
        val visibilityModifier =
            if (hasOption(FeatureFlagOption.PRIVATE)) Modifier.PRIVATE else Modifier.PUBLIC
        return if (hasOption(FeatureFlagOption.OVERRIDABLE)) {
            EnumSet.of(visibilityModifier, Modifier.STATIC)
        } else {
            EnumSet.of(visibilityModifier, Modifier.STATIC, Modifier.FINAL)
        }
    }

    private fun toParameterArray(array: Array<out Pair<String, String>>): Array<String> =
        array.flatMap(Pair<String, String>::toList).toTypedArray()

    object JavaDeclaration {
        const val CLASS = "class"
        const val INTERFACE = "interface"
    }

    object JavaType {
        const val VOID = "void"
        const val BOOLEAN = "boolean"
        const val STRING = "String"
        const val CONSUMER = "Consumer"
        const val SUPPLIER = "Supplier"
        const val FEATURE_FLAG_ACCESSOR = "FeatureFlagAccessor"
    }

    object JavaModifier {
        internal val PACKAGE_PRIVATE: Set<Modifier> = emptySet()
        internal val PUBLIC = setOf(Modifier.PUBLIC)
        internal val PUBLIC_FINAL = setOf(Modifier.PUBLIC, Modifier.FINAL)
        internal val PUBLIC_STATIC_FINAL = setOf(Modifier.PUBLIC, Modifier.STATIC, Modifier.FINAL)
        internal val PRIVATE = setOf(Modifier.PRIVATE)
        internal val PRIVATE_FINAL = setOf(Modifier.PRIVATE, Modifier.FINAL)
    }

    companion object {
        private const val FEATURE_FLAG_CLASS_NAME = "FeatureFlag"

        // We specify annotation by name instead of `Class` to use `java.lang` package rather than
        // `kotlin`.
        private const val DEPRECATED_ANNOTATION_CLASS_NAME = "Deprecated"
    }
}
