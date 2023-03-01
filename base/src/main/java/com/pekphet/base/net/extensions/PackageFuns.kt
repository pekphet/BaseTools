// Copyright (c) 2019-present Pekphet.  All rights reserved
// Confidential and Proprietary
package com.pekphet.base.net.extensions

import java.util.concurrent.Executors

/**
 * Created by fish on 18-2-27.
 */
object ThreadPool {
    val sThreadService by lazy { Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors() * 2) }
    fun addTask(task: () -> Unit) {
        sThreadService.submit(task)
    }
}