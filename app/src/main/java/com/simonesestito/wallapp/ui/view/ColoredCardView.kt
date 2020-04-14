/*
 * This file is part of WallApp for Android.
 * Copyright © 2020 Simone Sestito. All rights reserved.
 */

package com.simonesestito.wallapp.ui.view

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import androidx.annotation.ColorInt
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
    @ColorInt
    private val colorSurface: Int

    // --- onDraw necessary objects
    private val srcImageRect = Rect()
    private val destImageRect = Rect()
    private val fullViewRect = Rect()
    private val coverGradientPaint = Paint()
    var coverImage: Bitmap? = null
        set(value) {
            field = value
            onCoverImageChanged(value)
        }

    init {
        // Get styleable attributes
        context.theme
                .obtainStyledAttributes(attrs, R.styleable.ColoredCardView, 0, 0)
                .use {
                    destImageRect.bottom = it.getDimensionPixelSize(R.styleable.ColoredCardView_coverImageHeight, 0)
                }

        // Get theme attributes
        val themeAttr = TypedValue()
        context.theme.resolveAttribute(R.attr.colorSurface, themeAttr, true)
        colorSurface = themeAttr.data
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)

        if (childCount != 1)
            throw IllegalArgumentException("ColoredCardView expects exactly 1 child view")

        // Add image height to measured dimensions
        setMeasuredDimension(measuredWidth, View.resolveSize(
                destImageRect.height() + MeasureSpec.getSize(measuredHeight), heightMeasureSpec
        ))
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        // Position the child view at the bottom of the parent (this ViewGroup)
        (children.first().layoutParams as LayoutParams).gravity = Gravity.BOTTOM
        super.onLayout(changed, left, top, right, bottom)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        // Update rectangles
        destImageRect.right = w
        fullViewRect.right = w
        fullViewRect.bottom = h
    }

    override fun onDraw(canvas: Canvas) {
        if (coverImage != null && coverGradientPaint.shader != null) {
            // Draw background image first
            canvas.drawBitmap(coverImage!!, srcImageRect, destImageRect, null)
            canvas.drawRect(fullViewRect, coverGradientPaint)
        }

        // Continue drawing as usual
        super.onDraw(canvas)
    }

    private fun onCoverImageChanged(bitmap: Bitmap?) {
        if (bitmap == null) {
            invalidate()
            return
        }

        updateSrcImageRect(bitmap)
        updateCoverGradient(bitmap)
    }

    private fun updateSrcImageRect(bitmap: Bitmap) {
        // Get image dimensions
        val imageWidth = bitmap.width
        val imageHeight = bitmap.height

        // Calculate dimensions of the rect to actually draw
        // Assume the image is horizontal as the layout
        // The width is the same as image width
        // Calculate the height with a simple proportion
        // imageWidth : srcHeight = dest width : dest height
        val srcHeight = imageWidth * destImageRect.height() / destImageRect.width()

        // Calculate the amount of pixels not drawn on each side (top and bottom)
        val imageBorder = (imageHeight - srcHeight) / 2

        // Update src rect
        srcImageRect.apply {
            left = 0
            top = imageBorder
            right = imageWidth
            bottom = imageBorder + srcHeight
        }
    }

    private fun updateCoverGradient(bitmap: Bitmap) {
        coverGradientPaint.shader = null

        Palette.from(bitmap).generate {
            val primaryColor = it?.getDominantColor(colorSurface) ?: colorSurface

            // TODO
            //  Light/dark color check
            //  If color is light and theme is dark, or viceversa,
            //  change color brightness

            val child = children.first()
            val childHeight = if (child.height > 0) child.height else child.measuredHeight

            val fadeHeight = childHeight * 0.75f
            val halfFadeHeight = fadeHeight / 2
            val transparentHeight = destImageRect.height() - halfFadeHeight
            val startSolid = transparentHeight + fadeHeight

            val coverGradient = LinearGradient(
                    0f, 0f, 0f, destImageRect.height().toFloat(),
                    intArrayOf(Color.TRANSPARENT, Color.TRANSPARENT, primaryColor, primaryColor),
                    floatArrayOf(0f, 0f, transparentHeight, startSolid),
                    Shader.TileMode.CLAMP
            )

            coverGradientPaint.isDither = true
            coverGradientPaint.shader = coverGradient

            invalidate()
        }
    }
}