package com.linecorp.android.featureflag.model

import com.github.zafarkhaja.semver.Version
import com.linecorp.android.featureflag.model.ApplicationVersion.SemVer
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.comparables.shouldBeEqualComparingTo
import io.kotest.matchers.comparables.shouldBeGreaterThan
import io.kotest.matchers.comparables.shouldBeLessThan
import io.kotest.matchers.shouldBe

class SemVerApplicationVersionTest : FunSpec({

    context("instantiate") {
        context("success") {
            test("valid semantic version") {
                SemVer.from("1.2.3").version shouldBe Version.parse("1.2.3")
            }
            test("version with pre-release") {
                SemVer.from("1.2.3-alpha").version shouldBe Version.parse("1.2.3-alpha")
            }
            test("version with build metadata") {
                SemVer.from("1.2.3+build.1").version shouldBe Version.parse("1.2.3+build.1")
            }
            test("version with pre-release and build metadata") {
                SemVer.from("1.2.3-alpha+build.1").version shouldBe
                    Version.parse("1.2.3-alpha+build.1")
            }
        }
        context("failure") {
            test("invalid version format") {
                shouldThrow<Exception> {
                    SemVer.from("1.2")
                }
                shouldThrow<Exception> {
                    SemVer.from("1.2.a")
                }
                shouldThrow<Exception> {
                    SemVer.from("invalid")
                }
            }
            test("empty version") {
                shouldThrow<Exception> {
                    SemVer.from("")
                }
            }
        }
    }

    context("comparison") {
        test("equal versions") {
            SemVer.from("1.2.3") shouldBeEqualComparingTo SemVer.from("1.2.3")
        }
        test("major version differences") {
            SemVer.from("2.0.0") shouldBeGreaterThan SemVer.from("1.9.9")
            SemVer.from("1.0.0") shouldBeLessThan SemVer.from("2.0.0")
        }
        test("minor version differences") {
            SemVer.from("1.2.0") shouldBeGreaterThan SemVer.from("1.1.9")
            SemVer.from("1.1.0") shouldBeLessThan SemVer.from("1.2.0")
            SemVer.from("1.2.0") shouldBeLessThan SemVer.from("1.11.0")
        }
        test("patch version differences") {
            SemVer.from("1.2.3") shouldBeGreaterThan SemVer.from("1.2.2")
            SemVer.from("1.2.2") shouldBeLessThan SemVer.from("1.2.3")
        }
        test("pre-release versions") {
            SemVer.from("1.2.3") shouldBeGreaterThan SemVer.from("1.2.3-alpha")
            SemVer.from("1.2.3-beta") shouldBeGreaterThan SemVer.from("1.2.3-alpha")
            SemVer.from("1.2.3-alpha.2") shouldBeGreaterThan SemVer.from("1.2.3-alpha.1")
        }
    }
})
