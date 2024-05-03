package com.bennyapi.fakes

import com.bennyapi.transfer.core.StringStore

internal class FakeStringStore(vararg pairs: Pair<Int, String>) : StringStore {
    private val stringMap = pairs.toMap()
    override fun get(resId: Int) = stringMap[resId]!!
}
