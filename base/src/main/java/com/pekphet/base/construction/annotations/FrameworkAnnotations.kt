// Copyright (c) 2019-present Pekphet.  All rights reserved
// Confidential and Proprietary
package com.pekphet.base.construction.annotations

import androidx.lifecycle.ViewModel
import androidx.viewbinding.ViewBinding
import kotlin.reflect.KClass

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class AActivityViewBinding(val viewBinding: KClass<out ViewBinding>)

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class Binding(val viewBinding: KClass<out ViewBinding>)


@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class VMBinding(val viewModelBinding: KClass<out ViewModel>)
