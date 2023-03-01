// Copyright (c) 2019-present Pekphet.  All rights reserved
// Confidential and Proprietary
package com.pekphet.base.event

import com.pekphet.base.net.exception.FishNetException

enum class GlobalEvent : IEvent {
    @MultiRegEvent
    @EventDataType(FishNetException::class)
    HttpExceptionEvent,
}