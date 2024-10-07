package com.linecorp.android.featureflag.ij.plugin

import com.intellij.testFramework.ParsingTestCase
import com.linecorp.android.featureflag.ij.plugin.parser.FeatureFlagParserDefinition

/**
 * A test class for parsing feature flag files and generating PSI tree.
 */
class FeatureFlagParsingTest : ParsingTestCase("", "feature_flag", FeatureFlagParserDefinition()) {
    override fun getTestDataPath(): String = "src/test/resources/testData"
    override fun includeRanges(): Boolean = true
    fun testParsingTestData() = doTest(true)
}
