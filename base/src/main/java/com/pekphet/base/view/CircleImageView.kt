// Copyright (c) 2019-present Pekphet.  All rights reserved
// Confidential and Proprietary
package com.pekphet.base.view

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.widget.ImageView
import java.lang.Integer.min

class CircleImageView : androidx.appcompat.widget.AppCompatImageView {
    constructor(ctx: Context) : super(ctx)
    constructor(ctx: Context, attrs: AttributeSet) : super(ctx, attrs)
    constructor(ctx: Context, attrs: AttributeSet, defStyleAttr: Int) : super(
        ctx,
        attrs,
        defStyleAttr
    )

    private var mRadius = 0f //圆形图片的半径
    private var mScale = 1f //图片的缩放比例
    private var mPaint = Paint()

    private var mBitmap: Bitmap? = null

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        //因为是圆形图片，所以应该让宽高保持一致
        val size = min(measuredWidth, measuredHeight)
        mRadius = size / 2f
        setMeasuredDimension(size, size)
    }

    override fun setImageBitmap(bm: Bitmap?) {
        mBitmap = bm
        invalidate()
    }

    override fun setImageResource(resId: Int) {
        mBitmap = null
        super.setImageResource(resId)
    }

    override fun onDraw(canvas: Canvas) {
        //初始化BitmapShader，传入bitmap对象
        if (mBitmap == null) {
            super.onDraw(canvas)
            return
        }
        val bitmapShader = BitmapShader(mBitmap!!, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);

        //计算缩放比例
        mScale = (mRadius * 2.0f) / min(mBitmap!!.height, mBitmap!!.width).toFloat()

        val matrix = Matrix()
        matrix.setScale(mScale, mScale);
        bitmapShader.setLocalMatrix(matrix);


        mPaint.shader = bitmapShader;

        //画圆形，指定好中心点坐标、半径、画笔
        canvas.drawCircle(mRadius, mRadius, mRadius, mPaint);
    }

}