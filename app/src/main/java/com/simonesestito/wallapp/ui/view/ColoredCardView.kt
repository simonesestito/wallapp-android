/*
 * This file is part of WallApp for Android.
 * Copyright © 2020 Simone Sestito. All rights reserved.
 */

package com.simonesestito.wallapp.ui.view

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Rect
import android.util.AttributeSet
import android.view.Gravity
import android.view.View
import androidx.cardview.widget.CardView
import androidx.core.content.res.use
import androidx.core.view.children
import androidx.palette.graphics.Palette
import com.simonesestito.wallapp.R

/**
 * A [androidx.cardview.widget.CardView] with a background image.
 * Then, it has a gradient starting from the bottom, with the primary color in the image.
 */
class ColoredCardView @JvmOverloads constructor(
        context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : CardView(context, attrs, defStyleAttr) {
    private var currentWidth = 0
    private var coverPalette: Palette? = null
        set(value) {
            field = value
            invalidate()
        }

    private var coverImageHeight: Int = 0
        set(value) {
            field = value
            invalidate()
        }

    var coverImage: Bitmap? = null
        set(value) {
            field = value
            calculateCoverPalette(value)
            invalidate()
        }

    init {
        context.theme
                .obtainStyledAttributes(attrs, R.styleable.ColoredCardView, 0, 0)
                .use {
                    coverImageHeight = it.getDimensionPixelSize(
                            R.styleable.ColoredCardView_coverImageHeight,
                            0)
                }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)

        if (childCount != 1)
            throw IllegalArgumentException("ColoredCardView expects exactly 1 child view")

        // Add image height to measured dimensions
        setMeasuredDimension(measuredWidth, View.resolveSize(
                coverImageHeight + MeasureSpec.getSize(measuredHeight), heightMeasureSpec
        ))
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        // Position the child view at the bottom of the parent (this ViewGroup)
        (children.first().layoutParams as LayoutParams).gravity = Gravity.BOTTOM
        super.onLayout(changed, left, top, right, bottom)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        currentWidth = w
    }

    override fun onDraw(canvas: Canvas) {
        if (coverImage != null /* TODO && coverPalette != null */) {
            // Draw background image first
            drawBackgroundImage(coverImage!!, canvas)
            // TODO drawGradientPalette(coverPalette!!, canvas)
        }

        // Continue drawing as usual
        super.onDraw(canvas)
    }

    private fun drawBackgroundImage(bitmap: Bitmap, canvas: Canvas) {
        // Get image dimensions
        val imageWidth = bitmap.width
        val imageHeight = bitmap.height

        // Calculate dimensions of the rect to actually draw
        // Assume the image is horizontal as the layout
        // The width is the same as image width
        // Calculate the height with a simple proportion
        // imageWidth : srcHeight = currentWidth : coverImageHeight
        val srcHeight = imageWidth * coverImageHeight / currentWidth

        // Calculate the amount of pixels not drawn on each side (top and bottom)
        val imageBorder = (imageHeight - srcHeight) / 2

        // Calculate the coordinates of the image rect to draw
        val srcBottom = imageBorder + srcHeight
        val srcRect = Rect(0, imageBorder, imageWidth, srcBottom)

        // Determine where to draw the selected image area
        val destRect = Rect(0, 0, currentWidth, coverImageHeight)

        canvas.drawBitmap(bitmap, srcRect, destRect, null)
    }

    private fun calculateCoverPalette(bitmap: Bitmap?) {
        if (bitmap == null) {
            coverPalette = null
            return
        }

        // TODO
    }

    private fun drawGradientPalette(palette: Palette, canvas: Canvas) {
        // TODO
    }
}