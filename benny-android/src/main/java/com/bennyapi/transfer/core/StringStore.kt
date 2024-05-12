package com.bennyapi.transfer.core

import android.content.Context
import androidx.annotation.StringRes

internal interface StringStore {
    operator fun get(@StringRes resId: Int): String
}

internal class RealStringStore(private val context: Context) : StringStore {
    override fun get(resId: Int) = context.getString(resId)
}
