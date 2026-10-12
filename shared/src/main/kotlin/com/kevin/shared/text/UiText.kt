package com.kevin.shared.text

import android.content.Context
import android.content.res.Resources
import androidx.annotation.PluralsRes
import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource

/** User-visible text a ViewModel can hold without a Context. Args may themselves be [UiText]. */
sealed interface UiText {
    data class Res(@StringRes val resId: Int, val args: List<Any> = emptyList()) : UiText
    data class Plural(@PluralsRes val resId: Int, val count: Int, val args: List<Any> = listOf(count)) : UiText
    data class Raw(val value: String) : UiText

    companion object {
        /** Same call shape as jump's former `data class UiText(resId, args)`: migration is an import swap. */
        operator fun invoke(@StringRes resId: Int, args: List<Any> = emptyList()): UiText.Res =
            UiText.Res(resId, args)
    }
}

/** Resource access behind [resolve]: [Resources] in production, a fake in JVM tests. */
interface StringLookup {
    fun string(@StringRes resId: Int, args: Array<out Any>): String
    fun plural(@PluralsRes resId: Int, count: Int, args: Array<out Any>): String
}

fun UiText.resolve(lookup: StringLookup): String = when (this) {
    is UiText.Raw -> value
    is UiText.Res -> lookup.string(resId, args.map { it.resolvedWith(lookup) }.toTypedArray())
    is UiText.Plural -> lookup.plural(resId, count, args.map { it.resolvedWith(lookup) }.toTypedArray())
}

private fun Any.resolvedWith(lookup: StringLookup): Any = if (this is UiText) resolve(lookup) else this

fun UiText.resolve(context: Context): String = resolve(ResourcesLookup(context.resources))

private class ResourcesLookup(private val resources: Resources) : StringLookup {
    override fun string(resId: Int, args: Array<out Any>): String = resources.getString(resId, *args)
    override fun plural(resId: Int, count: Int, args: Array<out Any>): String =
        resources.getQuantityString(resId, count, *args)
}

@Composable
fun UiText.resolve(): String = when (this) {
    is UiText.Raw -> value
    is UiText.Res -> stringResource(resId, *composeArgs(args))
    is UiText.Plural -> pluralStringResource(resId, count, *composeArgs(args))
}

@Composable
private fun composeArgs(args: List<Any>): Array<Any> =
    args.map { if (it is UiText) it.resolve() else it }.toTypedArray()
