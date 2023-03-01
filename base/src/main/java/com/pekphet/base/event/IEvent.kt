// Copyright (c) 2019-present Pekphet.  All rights reserved
// Confidential and Proprietary
package com.pekphet.base.event

import android.content.Context
import kotlin.reflect.KClass
import kotlin.reflect.full.allSuperclasses

enum class AppEvent : IEvent {
    @SingleRegEvent
    SplashShown,

    @SingleRegEvent
    AppInitialized,

    @SingleRegEvent
    @EventDataType(Int::class)
    TestDataEvent,
}

enum class DataEvent() : IEvent {
    @MultiRegEvent
    UDSTokenLoaded,

    @MultiRegEvent
    UserBooksLoaded,
}

enum class LoadingEvent() : IEvent {
    @SingleRegEvent
    @EventDataType(Context::class)
    LoadEvent,

    @SingleRegEvent
    @EventDataType(Context::class)
    LoadCompleteEvent,

}

enum class UDSTask: IEvent {
    @SingleRegEvent
    @EventDataType(Float::class)
    Progress,

    @SingleRegEvent
    @EventDataType(String::class)
    Completed,
}

enum class TipEvent(): IEvent {

    @SingleRegEvent
    NetworkError,

    @SingleRegEvent
    SvcCopyFailed,

    @SingleRegEvent
    SvcMoveFailed,

    @SingleRegEvent
    SvcCreateFolderFailed,

    @SingleRegEvent
    SvcUpdateFailed,

    @SingleRegEvent
    SvcUploadFailed,

    @SingleRegEvent
    SvcDuplicate,

    @SingleRegEvent
    SvcError,
}


fun IEvent.isSingleRegEvent() =
    this::class.java.getField((this as Enum<*>).name).annotations.any {
        it is SingleRegEvent
    }

fun IEvent.isMultiRegEvent() =
    this::class.java.getField((this as Enum<*>).name).annotations.any {
        it is MultiRegEvent
    }

fun IEvent.checkDataType(klz: KClass<*>): Boolean{
    this::class.java.getField((this as Enum<*>).name).annotations.map {
        if (it is EventDataType) {
            if (it.clz == klz) return true
            klz.allSuperclasses.forEach {
            }
            return klz.allSuperclasses.contains(it.clz)
        }
    }
    return false
}

fun IEvent.noData() = this::class.java.getField((this as Enum<*>).name).annotations.none { it is EventDataType }

interface IEvent

@Target(AnnotationTarget.PROPERTY, AnnotationTarget.CLASS, AnnotationTarget.VALUE_PARAMETER)
@Retention(AnnotationRetention.RUNTIME)
annotation class SingleRegEvent

@Target(AnnotationTarget.PROPERTY, AnnotationTarget.CLASS, AnnotationTarget.VALUE_PARAMETER)
@Retention(AnnotationRetention.RUNTIME)
annotation class MultiRegEvent

@Target(AnnotationTarget.PROPERTY, AnnotationTarget.CLASS, AnnotationTarget.VALUE_PARAMETER)
@Retention(AnnotationRetention.RUNTIME)
annotation class EventDataType(val clz: KClass<*>)