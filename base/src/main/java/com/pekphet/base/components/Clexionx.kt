// Copyright (c) 2019-present Pekphet.  All rights reserved
// Confidential and Proprietary
package com.pekphet.base.components

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import com.pekphet.base.Ext
import java.util.*
import kotlin.reflect.KClass

/**
 * COLLECTIONS TOOLS
 *
 */
private val _G = Gson()
private val _G2 = GsonBuilder().disableHtmlEscaping().create()


infix fun <T> T.dsl(setters: Ext<T>) {
    setters()
}

fun <T> Collection<T>.limit(predicate: T.() -> Int): T? {
    if (isEmpty()) {
        ZInfoRecorder.e("limit function", "Collection size is 0")
        return null
    }
    val tmpMap = TreeMap<Int, T>(Comparator<Int> { o1, o2 ->
        o1 - o2
    })
    map {
        tmpMap[it.predicate()] = it
    }
    return tmpMap[tmpMap.firstKey()]
}

inline fun <reified T> Gson.parseList(json: String): MutableList<T>? = safe {
    fromJson<MutableList<T>>(json, object : TypeToken<MutableList<T>>() {}.type)
}

infix fun <LIST : MutableCollection<E>, E> LIST.addNew(content: MutableCollection<E>) {
    synchronized(this) {
        clear()
        addAll(content)
    }
}

fun MutableCollection<String>.content(spliter: String = "\n") =
    reduce { ori, adder -> "$ori$spliter$adder" }

infix fun <T, R> Pair<T, T>.transfer(operation: T.() -> R) =
    Pair(first.operation(), second.operation())

fun Any.toJson() = _G2.toJson(this)



inline infix fun <reified T : Any> String?.toObj(klz: KClass<T>) = Gson().fromJson(this, klz.java)

infix fun <E> MutableList<E>.addNotNull(element: E?) {
    if (element != null) {
        add(element)
    }
}

infix fun <E> MutableCollection<E>.select(element: E) {
    if (contains(element))
        remove(element)
    else
        add(element)
}


inline fun <T, R : Comparable<R>> MutableList<T>.sortSafety(crossinline selector: (T) -> R?) {
    ZInfoRecorder.e("sort", "pre sync")
    synchronized(this) {
        ZInfoRecorder.e("sort", "in sync")
        val temp = this.sortedBy(selector).toMutableList()
        clear()
        addAll(temp)
    }
}

fun <T> MutableList<T>.pull(predicate: T.() -> Boolean): T? {
    val position = indexOfFirst(predicate)
    if (position < 0) return null
    val result = get(position)
    removeAt(position)
    return result
}

fun <T, K> MutableList<T>.mutableGroupBy(keySelector: (T) -> K) = groupByTo(mutableMapOf(), keySelector)


fun ByteArray.toHexStringL() = joinToString("") { "%02x".format(it) }
fun ByteArray.toHexStringU() = joinToString("") { "%02X".format(it) }