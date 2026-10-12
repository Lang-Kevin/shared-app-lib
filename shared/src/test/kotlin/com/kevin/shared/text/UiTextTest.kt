package com.kevin.shared.text

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Test

class UiTextTest {
    private val lookup = object : StringLookup {
        override fun string(resId: Int, args: Array<out Any>) = "s$resId(${args.joinToString(",")})"
        override fun plural(resId: Int, count: Int, args: Array<out Any>) =
            "p$resId#$count(${args.joinToString(",")})"
    }

    @Test fun invokeBuildsComparableResValues() {
        assertEquals(UiText(7), UiText(7))
        assertEquals(UiText.Res(7, listOf(24)), UiText(7, listOf(24)))
        assertNotEquals(UiText(7, listOf(24)), UiText(7, listOf(25)))
    }

    @Test fun rawResolvesToItself() {
        assertEquals("hi", UiText.Raw("hi").resolve(lookup))
    }

    @Test fun resPassesArgsInOrder() {
        assertEquals("s7(24,x)", UiText(7, listOf(24, "x")).resolve(lookup))
        assertEquals("s7()", UiText(7).resolve(lookup))
    }

    @Test fun pluralDefaultsArgsToCount() {
        assertEquals("p9#3(3)", UiText.Plural(9, 3).resolve(lookup))
        assertEquals("p9#3(a)", UiText.Plural(9, 3, listOf("a")).resolve(lookup))
    }

    @Test fun nestedUiTextArgsResolveFirst() {
        assertEquals("s1(s2(5))", UiText(1, listOf(UiText(2, listOf(5)))).resolve(lookup))
        assertEquals("s1(raw)", UiText(1, listOf(UiText.Raw("raw"))).resolve(lookup))
    }
}
