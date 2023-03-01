// Copyright (c) 2019-present Pekphet.  All rights reserved
// Confidential and Proprietary
package com.pekphet.base.components

fun <T, R> T.safe(default: R? = null, block: T.() -> R): R? = try {
    block()
} catch (ex: Exception) {
    ex.printStackTrace()
    default
}
