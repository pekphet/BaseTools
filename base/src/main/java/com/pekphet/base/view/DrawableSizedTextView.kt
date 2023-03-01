// Copyright (c) 2019-present Pekphet.  All rights reserved
// Confidential and Proprietary
package com.pekphet.base.view

import android.content.Context
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatTextView
import com.pekphet.base.R

class DrawableSizedTextView(ctx: Context, attrs: AttributeSet) : AppCompatTextView(ctx, attrs) {
    var mDrawableLeft: Drawable? = null
    var mDrawableTop: Drawable? = null
    var mDrawableRight: Drawable? = null
    var mDrawableBottom: Drawable? = null

    var mDrawableWidthPX = 0
    var mDrawableHeightPX = 0

    init {
        val typedArray = ctx.obtainStyledAttributes(attrs, R.styleable.DrawableSizedTextView)
        for (i in 0 until typedArray.length()) {
            val attr = typedArray.getIndex(i)
            when (attr) {
                R.styleable.DrawableSizedTextView_drawableLeft -> mDrawableLeft =
                    typedArray.getDrawable(attr)
                R.styleable.DrawableSizedTextView_drawableTop -> mDrawableTop =
                    typedArray.getDrawable(attr)
                R.styleable.DrawableSizedTextView_drawableRight -> mDrawableRight =
                    typedArray.getDrawable(attr)
                R.styleable.DrawableSizedTextView_drawableBottom -> mDrawableBottom =
                    typedArray.getDrawable(attr)

                R.styleable.DrawableSizedTextView_drawableWidth -> mDrawableWidthPX =
                    typedArray.getDimensionPixelSize(attr, 0)
                R.styleable.DrawableSizedTextView_drawableHeight -> mDrawableHeightPX =
                    typedArray.getDimensionPixelSize(attr, 0)
            }
        }
        mDrawableLeft?.setBounds(0, 0, mDrawableWidthPX, mDrawableHeightPX)
        mDrawableTop?.setBounds(0, 0, mDrawableWidthPX, mDrawableHeightPX)
        mDrawableRight?.setBounds(0, 0, mDrawableWidthPX, mDrawableHeightPX)
        mDrawableBottom?.setBounds(0, 0, mDrawableWidthPX, mDrawableHeightPX)

//        ZLog.e("drawable text view", "w: $mDrawableWidthPX  H: $mDrawableHeightPX")
//        ZLog.e("drawable text view", "${mDrawableLeft == null} ${mDrawableTop == null}  ${mDrawableRight == null}  ${mDrawableBottom == null}")
        setCompoundDrawables(mDrawableLeft, mDrawableTop, mDrawableRight, mDrawableBottom)
    }
}