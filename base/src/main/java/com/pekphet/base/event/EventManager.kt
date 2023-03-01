// Copyright (c) 2019-present Pekphet.  All rights reserved
// Confidential and Proprietary
package com.pekphet.base.event

import android.os.Handler
import android.os.Looper
import com.pekphet.base.components.Debug
import com.pekphet.base.components.safe

object EventManager {

    private val mSingleEventMap = mutableMapOf<IEvent, (Any?) -> Unit>()
    private val mMutableEventMap = mutableMapOf<IEvent, MutableMap<String, (Any?) -> Unit>>()
    private val mMainHandler = Handler(Looper.getMainLooper())

    private var mTipEventCallback: (TipEvent) -> Unit = {}

    private var mLoadingCount = 0

    fun Any?.getNullableClass() = if (this == null) Unit::class else this::class

    fun postEvent(event: IEvent, data: Any? = null) {

        if (!event.checkDataType(data.getNullableClass())) {
            Debug.showStack()
        }
//        ZInfoRecorder.e("HttpExceptionEvent", "$event: ${event.checkDataType(data.getNullableClass())} // ${data.getNullableClass()}")
        if (event.noData() || (data != null && event.checkDataType(data::class))) {
            when (event) {
                LoadingEvent.LoadEvent -> mMainHandler.post { mSingleEventMap[LoadingEvent.LoadEvent]?.invoke(data) }
                LoadingEvent.LoadCompleteEvent -> mMainHandler.post { mSingleEventMap[LoadingEvent.LoadCompleteEvent]?.invoke(data) }
                UDSTask.Progress -> mMainHandler.post { mSingleEventMap[event]?.invoke(data) }
                GlobalEvent.HttpExceptionEvent -> mMainHandler.post {
                    mMutableEventMap[event]?.forEach {
                        safe {
                            it.value(data)
                        }
                    }
                }
                is TipEvent -> mMainHandler.post { mTipEventCallback(event) }
                else -> return
            }
        }
    }

    fun setEventReceiver(@SingleRegEvent event: IEvent, eventData: (Any?) -> Unit) {
        if (!event.isSingleRegEvent()) return
        mSingleEventMap[event] = eventData
    }

    fun addEventReceiver(@MultiRegEvent event: IEvent, tag: String, dataCB: (Any?) -> Unit) {
        if (!event.isMultiRegEvent()) return
        if (mMutableEventMap.containsKey(event)) {
            mMutableEventMap[event]!![tag] = dataCB
        } else {
            mMutableEventMap[event] = mutableMapOf(Pair(tag, dataCB))
        }
    }
    
    fun setTipCallback(tipCb: (TipEvent) -> Unit) {
        mTipEventCallback = tipCb
    }

    fun removeSingleEventReceiver(@SingleRegEvent event: IEvent) {
        mSingleEventMap.remove(event)
    }

    fun removeMutableEventReceiver(@MultiRegEvent event: IEvent, tag: String) {
        mMutableEventMap[event]?.remove(tag)
    }

    fun cleanRecvs() {
        mSingleEventMap.clear()
        mMutableEventMap.clear()
        mTipEventCallback = {}
    }

}

