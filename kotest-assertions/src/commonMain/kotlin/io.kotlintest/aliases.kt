package io.kotlintest

import io.kotest.matchers.Matcher
import io.kotest.matchers.should
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.kotest.matchers.string.endWith
import io.kotest.matchers.string.haveLength
import io.kotest.matchers.string.match
import io.kotest.matchers.string.startWith

@Deprecated(
   "All packages are now io.kotest",
   ReplaceWith("shouldThrowAnyUnit(block)", "io.kotest.assertions.throwables")
)
inline fun shouldThrowAnyUnit(block: () -> Unit) = io.kotest.assertions.throwables.shouldThrowAnyUnit(block)

@Deprecated("All packages are now io.kotest", ReplaceWith("io.kotest.assertions.throwables.shouldThrow(block)", "io"))
inline fun <reified T : Throwable> shouldThrow(block: () -> Any?): T =
   io.kotest.assertions.throwables.shouldThrow(block)

@Deprecated("All package names are now io.kotest")
infix fun String?.shouldHaveLength(length: Int) = this should haveLength(length)

@Deprecated("All package names are now io.kotest")
infix fun String?.shouldMatch(regex: String) = this should match(regex)

@Deprecated("All package names are now io.kotest")
infix fun String?.shouldMatch(regex: Regex) = this should match(regex)

@Deprecated("All package names are now io.kotest")
infix fun String?.shouldEndWith(suffix: String) = this should endWith(suffix)

@Deprecated("All package names are now io.kotest")
infix fun String?.shouldStartWith(prefix: String) = this should startWith(prefix)

@Deprecated("All package names are now io.kotest")
infix fun <T, U : T> T.shouldBe(any: U?) = when (any) {
   is Matcher<*> -> this shouldBe (any as Matcher<T>)
   else -> this shouldBe (any as T)
}

@Deprecated("All package names are now io.kotest")
infix fun <T> T.shouldNotBe(any: Any?) = when (any) {
   is Matcher<*> -> this shouldNotBe (any as Matcher<T>)
   else -> this shouldNotBe (any as T)
}

@Deprecated("All package names are now io.kotest")
fun <T> assertSoftly(block: () -> T): T = io.kotest.assertions.assertSoftly(block)
