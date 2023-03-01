// Copyright (c) 2019-present Pekphet.  All rights reserved
// Confidential and Proprietary
package com.pekphet.base.view

import android.content.Context
import android.util.AttributeSet
import androidx.recyclerview.widget.RecyclerView
import com.pekphet.base.R

open class ThumbFixedRecyclerView(val mCtx: Context, val mAttrs: AttributeSet?) :
    RecyclerView(mCtx, mAttrs) {
    val mThumbLen = context.obtainStyledAttributes(mAttrs, R.styleable.ThumbFixedRecyclerView)
        .getDimensionPixelSize(R.styleable.ThumbFixedRecyclerView_thumbLen, 0)


    override fun computeVerticalScrollExtent() = mThumbLen

    override fun computeHorizontalScrollExtent() = mThumbLen

    override fun computeVerticalScrollOffset(): Int {
        val oRange = super.computeVerticalScrollRange()
        val oExtent = super.computeVerticalScrollExtent()
        val range = oRange - oExtent
        val nRange = oRange - mThumbLen
        return if (range == 0) 0 else (nRange * super.computeVerticalScrollOffset()* 1.0f / range).toInt()
    }

    override fun computeHorizontalScrollOffset(): Int {
        val oRange = super.computeHorizontalScrollRange()
        val oExtent = super.computeHorizontalScrollExtent()
        val range = oRange - oExtent
        val nRange = oRange - mThumbLen
        return if (range == 0) 0 else (oRange * super.computeHorizontalScrollOffset() * 1.0f / range).toInt()
    }
}