// Copyright (c) 2019-present Pekphet.  All rights reserved
// Confidential and Proprietary
package com.pekphet.base.components

import kotlin.collections.ArrayList
import kotlin.collections.HashMap


class NoHashArrayMap<K, V> : ArrayList<Pair<K, V>>() {

    private val mKeys = HashMap<K, Int>()

    fun keyAt(position: Int) = get(position).first

    fun valueAt(position: Int) = get(position).second

    fun keyPosition(key: K) = mKeys[key]

    fun put(key: K, value: V): Boolean {
        val result = mKeys.containsKey(key)
        if (!result) {
            mKeys[key] = size
            add(Pair(key, value))
        }
        return result
    }

    fun putAll(data: Map<K, V>) {
        data.map { put(it.key, it.value) }
    }

    fun delete(vararg key: K) {
        key.map {
            if (!mKeys.containsKey(it)) return@map
            removeAt(mKeys[it]!!)
        }
        val list = mutableListOf<Pair<K, V>>()
        list.addAll(this)
        mKeys.clear()
        list.map {
            put(it.first, it.second)
        }
    }

    fun getValue(key: K): V? {
        if (key in mKeys) {
            return get(mKeys[key]!!).second
        } else {
            return null
        }
    }

    override fun clear() {
        mKeys.clear()
        super.clear()
    }
}