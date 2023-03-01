// Copyright (c) 2019-present Pekphet.  All rights reserved
// Confidential and Proprietary
package com.pekphet.base

typealias B = () -> Unit
typealias Cb<T> = (T) -> Unit
typealias Ext<T> = T.() -> Unit
typealias Cbr<T, R> = (T) -> R
typealias Exr<T, R> = T.() -> R

//SUSPENDS
typealias Sync = suspend () -> Unit
typealias TypedSync<T> = suspend T.() -> Unit
typealias TypedSyncRet<T, R> = suspend T.() -> R