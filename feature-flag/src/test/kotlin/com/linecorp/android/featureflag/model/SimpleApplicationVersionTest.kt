package com.linecorp.android.featureflag.model

import com.linecorp.android.featureflag.model.ApplicationVersion.Simple
import io.kotest.assertions.throwables.shouldThrowWithMessage
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.comparables.shouldBeEqualComparingTo
import io.kotest.matchers.comparables.shouldBeGreaterThan
import io.kotest.matchers.shouldBe

class SimpleApplicationVersionTest : FunSpec({

    context("instantiate") {
        context("success") {
            test("single number") {
                Simple.from("1").versionNumberList shouldBe listOf(1)
            }
            test("multiple numbers") {
                Simple.from("1.2.3").versionNumberList shouldBe listOf(1, 2, 3)
            }
            test("started by 0") {
                Simple.from("0.01.10").versionNumberList shouldBe listOf(0, 1, 10)
            }
            test("zero only versions") {
                Simple.from("0").versionNumberList shouldBe listOf(0)
                Simple.from("0.0").versionNumberList shouldBe listOf(0, 0)
                Simple.from("0.0.0").versionNumberList shouldBe listOf(0, 0, 0)
            }
            test("large numbers") {
                Simple.from("2147483647").versionNumberList shouldBe listOf(2147483647)
                Simple.from("999999999.888888888.777777777").versionNumberList shouldBe listOf(
                    999999999,
                    888888888,
                    777777777
                )
            }
        }
        context("failure") {
            test("not-number") {
                shouldThrowWithMessage<IllegalArgumentException>("Invalid version: a") {
                    Simple.from("a")
                }
                shouldThrowWithMessage<IllegalArgumentException>("Invalid version: 1.b") {
                    Simple.from("1.b")
                }
            }
            test("empty") {
                shouldThrowWithMessage<IllegalArgumentException>("Input value is empty") {
                    Simple.from("")
                }
                shouldThrowWithMessage<IllegalArgumentException>("Input value is empty") {
                    Simple.from(" ")
                }
            }
            test("multi-dot") {
                shouldThrowWithMessage<IllegalArgumentException>("Invalid version: 1..0") {
                    Simple.from("1..0")
                }
                shouldThrowWithMessage<IllegalArgumentException>("Invalid version: ..0") {
                    Simple.from("..0")
                }
                shouldThrowWithMessage<IllegalArgumentException>("Invalid version: 0..") {
                    Simple.from("0..")
                }
                shouldThrowWithMessage<IllegalArgumentException>("Invalid version: ..") {
                    Simple.from("..")
                }
            }
            test("leading or trailing dot") {
                shouldThrowWithMessage<IllegalArgumentException>("Invalid version: .1") {
                    Simple.from(".1")
                }
                shouldThrowWithMessage<IllegalArgumentException>("Invalid version: 1.") {
                    Simple.from("1.")
                }
                shouldThrowWithMessage<IllegalArgumentException>("Invalid version: .1.2") {
                    Simple.from(".1.2")
                }
                shouldThrowWithMessage<IllegalArgumentException>("Invalid version: 1.2.") {
                    Simple.from("1.2.")
                }
            }
            test("negative numbers") {
                shouldThrowWithMessage<IllegalArgumentException>("Invalid version: -1") {
                    Simple.from("-1")
                }
                shouldThrowWithMessage<IllegalArgumentException>("Invalid version: 1.-2") {
                    Simple.from("1.-2")
                }
                shouldThrowWithMessage<IllegalArgumentException>("Invalid version: -1.2.3") {
                    Simple.from("-1.2.3")
                }
            }
            test("number overflow") {
                shouldThrowWithMessage<IllegalArgumentException>("Invalid version: 2147483648") {
                    Simple.from("2147483648")
                }
                shouldThrowWithMessage<IllegalArgumentException>(
                    "Invalid version: 99999999999999999999"
                ) {
                    Simple.from("99999999999999999999")
                }
            }
        }
    }

    context("comparison") {
        context("success") {
            test("single number") {
                Simple.from("2") shouldBeGreaterThan Simple.from("1")
            }
            test("multiple numbers") {
                Simple.from("1.2") shouldBeGreaterThan Simple.from("1.1")
                Simple.from("1.0.0") shouldBeEqualComparingTo Simple.from("1.0.0")
                Simple.from("1.0.1") shouldBeGreaterThan Simple.from("1.0.0")
                Simple.from("1.1.0") shouldBeGreaterThan Simple.from("1.0.0")
                Simple.from("2.0.0") shouldBeGreaterThan Simple.from("1.0.0")
                Simple.from("1.2.3.4") shouldBeGreaterThan Simple.from("0.2.3.4")
            }
            test("started by 0") {
                Simple.from("1.02") shouldBeGreaterThan Simple.from("1.01")
                Simple.from("1.00.0") shouldBeEqualComparingTo Simple.from("1.00.0")
                Simple.from("1.00.01") shouldBeGreaterThan Simple.from("1.00.00")
                Simple.from("1.01.0") shouldBeGreaterThan Simple.from("1.0.0")
            }
            test("boundary values") {
                Simple.from("0") shouldBeEqualComparingTo Simple.from("0")
                Simple.from("1") shouldBeGreaterThan Simple.from("0")
                Simple.from("0.0.1") shouldBeGreaterThan Simple.from("0.0.0")
            }
            test("large number comparisons") {
                Simple.from("2147483647") shouldBeGreaterThan Simple.from("2147483646")
                Simple.from("999999999.0") shouldBeGreaterThan Simple.from("999999998.999999999")
            }
        }
        context("failure") {
            test("different dot count") {
                shouldThrowWithMessage<IllegalArgumentException>(
                    "Incompatible version format: 1.0 and 1.0.0"
                ) {
                    Simple.from("1.0") shouldBeEqualComparingTo Simple.from("1.0.0")
                }
            }
        }
    }
})
