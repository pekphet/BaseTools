// Copyright (c) 2019-present Pekphet.  All rights reserved
// Confidential and Proprietary
package com.pekphet.base.view

import android.animation.TimeInterpolator

class JellyInterpolator(val freQ: Float = 0.15f) : TimeInterpolator {
    override fun getInterpolation(input: Float) =
        (Math.pow(2.0, -10.0 * input) * Math.sin((input - freQ / 4) * (2 * Math.PI) / freQ) + 1.0).toFloat()
}