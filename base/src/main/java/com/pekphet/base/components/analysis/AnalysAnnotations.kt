// Copyright (c) 2019-present Pekphet.  All rights reserved
// Confidential and Proprietary
package com.pekphet.base.components.analysis

import kotlin.reflect.KClass


@Target(AnnotationTarget.FIELD)
@Retention(AnnotationRetention.RUNTIME)
annotation class CountAnalysisEvent

@Target(AnnotationTarget.FIELD)
@Retention(AnnotationRetention.RUNTIME)
annotation class PageAnalysisEvent

@Target(AnnotationTarget.FIELD)
@Retention(AnnotationRetention.RUNTIME)
annotation class RouteAnalysisEvent

@Target(AnnotationTarget.FIELD)
@Retention(AnnotationRetention.RUNTIME)
annotation class KValueAnalysisEvent

@Target(AnnotationTarget.FIELD)
@Retention(AnnotationRetention.RUNTIME)
annotation class DurationAnalysisEvent

@Target(AnnotationTarget.FIELD)
@Retention(AnnotationRetention.RUNTIME)
annotation class UExpAnalysisEventKey

@Target(AnnotationTarget.FIELD)
@Retention(AnnotationRetention.RUNTIME)
annotation class UExpAnalysisParamKey

fun isCountAnalysisEvent(obj: Any) =
    hasAnnotationFieldInKlz(obj, AnalysisConstants.Count::class, CountAnalysisEvent::class)

fun isPageAnalysisEvent(obj: Any) =
    hasAnnotationFieldInKlz(obj, AnalysisConstants.Page::class, PageAnalysisEvent::class)

fun isRouteAnalysisEvent(obj: Any) =
    hasAnnotationFieldInKlz(obj, AnalysisConstants.Route::class, RouteAnalysisEvent::class)

fun isKValueAnalysisEvent(obj: Any) =
    hasAnnotationFieldInKlz(obj, AnalysisConstants.KValue::class, KValueAnalysisEvent::class)

fun isDurationAnalysisEvent(obj: Any) =
    hasAnnotationFieldInKlz(obj, AnalysisConstants.Duration::class, DurationAnalysisEvent::class)

/**
 * Only for JAVA Class!
 */
private fun <T : Any> hasAnnotationFieldInKlz(
    obj: Any,
    checkClz: KClass<in T>,
    annoAnalysisKlz: KClass<*>,
    instance: T? = null
) =
    checkClz.java.fields
        .find { obj == it.get(instance) }
        ?.annotations
        ?.find { it.annotationClass == annoAnalysisKlz } != null