package com.andrew.tasky.core

import android.content.Context
import androidx.annotation.StringRes

sealed class UiText {
    data class DynamicString(val value: String) : UiText()
    class Resource(
        @StringRes val resId: Int,
        vararg val args: Any
    ) : UiText()

    fun asString(context: Context): String {
        return when (this) {
            is DynamicString -> value
            is Resource -> context.getString(resId, *args)
        }
    }
}
