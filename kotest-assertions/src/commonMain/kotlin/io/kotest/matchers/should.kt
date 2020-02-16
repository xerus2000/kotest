package io.kotest.matchers

import io.kotest.assertions.AssertionCounter
import io.kotest.assertions.ErrorCollector
import io.kotest.assertions.Failures
import io.kotest.assertions.clueContextAsString
import io.kotest.assertions.collectOrThrow
import io.kotest.assertions.compare
import io.kotest.assertions.diffLargeString
import io.kotest.assertions.stringRepr
import io.kotest.mpp.sysprop
import kotlin.internal.OnlyInputTypes

infix fun Number.shouldBe(other: Number) = compareOrThrow(this, other)

//infix fun Int.shouldBe(l: Long) = this.toLong() shouldBe l
//infix fun Int.shouldBe(i: Int) = compareOrThrow(this, i)
//infix fun Int.shouldBe(s: Short) = this shouldBe s.toInt()
//infix fun Int.shouldBe(b: Byte) = this shouldBe b.toInt()
//
//infix fun Long.shouldBe(l: Long) = compareOrThrow(this, l)
//infix fun Long.shouldBe(i: Int) = this shouldBe i.toLong()
//infix fun Long.shouldBe(s: Short) = this shouldBe s.toLong()
//infix fun Long.shouldBe(b: Byte) = this shouldBe b.toLong()
//
//infix fun Short.shouldBe(l: Long) = this.toLong() shouldBe l
//infix fun Short.shouldBe(i: Int) = this.toInt() shouldBe i
//infix fun Short.shouldBe(s: Short) = compareOrThrow(this, s)
//infix fun Short.shouldBe(b: Byte) = this shouldBe b.toShort()
//
//infix fun Byte.shouldBe(l: Long) = this.toLong() shouldBe l
//infix fun Byte.shouldBe(i: Int) = this.toInt() shouldBe i
//infix fun Byte.shouldBe(s: Short) = this.toShort() shouldBe s
//infix fun Byte.shouldBe(b: Byte) = compareOrThrow(this, b)
//
//infix fun Float.shouldBe(d: Double) = this.toDouble() shouldBe d
//infix fun Double.shouldBe(f: Float) = this shouldBe f.toDouble()

infix fun <@OnlyInputTypes T : U, U> T.shouldBe(other: U?) = compareOrThrow(this, other)

private fun <T, U> compareOrThrow(t: T, u: U) {
   AssertionCounter.inc()
   if (t == null && u != null) {
      ErrorCollector.collectOrThrow(equalsError(u, t))
   } else if (!compare(t, u)) {
      ErrorCollector.collectOrThrow(equalsError(u, t))
   }
}

infix fun <@OnlyInputTypes T> T.shouldNotBe(any: T?) {
   shouldNot(equalityMatcher(any))
}

infix fun <@OnlyInputTypes T, U : Matcher<T>> T.shouldBe(matcher: U) = should(matcher)
infix fun <@OnlyInputTypes T, U : Matcher<T>> T.shouldHave(matcher: U) = should(matcher)
infix fun <@OnlyInputTypes T, U : Matcher<T>> T.should(matcher: U) {
   AssertionCounter.inc()
   val result = matcher.test(this)
   if (!result.passed()) {
      ErrorCollector.collectOrThrow(Failures.failure(clueContextAsString() + result.failureMessage()))
   }
}

infix fun <T, U : Matcher<T>> T.shouldNotBe(matcher: U) = shouldNot(matcher)
infix fun <T, U : Matcher<T>> T.shouldNotHave(matcher: U) = shouldNot(matcher)
infix fun <T, U : Matcher<T>> T.shouldNot(matcher: U) = should(matcher.invert())

infix fun <T, U : T> T.should(matcher: (T) -> Unit) = matcher(this)

fun <T> be(expected: T) = equalityMatcher(expected)
fun <T> equalityMatcher(expected: T) = object : Matcher<T> {
   override fun test(value: T): MatcherResult {
      val expectedRepr = stringRepr(expected)
      val valueRepr = stringRepr(value)
      return MatcherResult(
         compare(expected, value),
         { equalsErrorMessage(expectedRepr, valueRepr) },
         { "$expectedRepr should not equal $valueRepr" }
      )
   }
}

internal fun equalsError(expected: Any?, actual: Any?): Throwable {
   val largeStringDiffMinSize = sysprop("kotest.assertions.multi-line-diff-size", "50").toInt()
   val (expectedRepr, actualRepr) = diffLargeString(stringRepr(expected), stringRepr(actual), largeStringDiffMinSize)
   val message = clueContextAsString() + equalsErrorMessage(expectedRepr, actualRepr)
   return Failures.failure(message, expectedRepr, actualRepr)
}

private val linebreaks = Regex("\r?\n|\r")

// This is the format intellij requires to do the diff: https://github.com/JetBrains/intellij-community/blob/3f7e93e20b7e79ba389adf593b3b59e46a3e01d1/plugins/testng/src/com/theoryinpractice/testng/model/TestProxy.java#L50
internal fun equalsErrorMessage(expected: Any?, actual: Any?): String {
   return when {
      expected is String && actual is String &&
         linebreaks.replace(expected, "\n") == linebreaks.replace(actual, "\n") -> {
         "line contents match, but line-break characters differ"
      }
      else -> "expected: $expected but was: $actual"
   }
}

