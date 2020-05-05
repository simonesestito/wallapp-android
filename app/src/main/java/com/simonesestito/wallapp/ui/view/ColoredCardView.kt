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
import android.view.animation.AlphaAnimation
import android.view.animation.Transformation
import androidx.annotation.ColorInt
import androidx.cardview.widget.CardView
import androidx.core.content.res.use
import androidx.core.graphics.ColorUtils
import androidx.core.view.children
import androidx.palette.graphics.Palette
import com.simonesestito.wallapp.R
import com.simonesestito.wallapp.utils.isDarkTheme
import com.simonesestito.wallapp.utils.isLightColor

/**
 * A [androidx.cardview.widget.CardView] with a background image.
 * Then, it has a gradient starting from the bottom, with the primary color in the image.
 */
class ColoredCardView @JvmOverloads constructor(
        context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : CardView(context, attrs, defStyleAttr) {
    @ColorInt
    private val colorSurface: Int

    @ColorInt
    private var imageColor: Int = Color.BLUE

    // --- onDraw necessary objects
    // Image rectangles
    private val srcImageRect = Rect()
    private val destImageRect = Rect()

    // Entire view rectangle
    private val fullViewRect = Rect()

    // Gradient rectangles
    private val coverGradientRect = RectF()
    private val solidBackgroundRect = RectF()

    // Gradient Paint objects
    private val coverGradientPaint = Paint()
    private val solidBackgroundPaint = Paint()

    // Fade animation objects
    private val fadeAnimationDuration: Int
    private val fadeTransformation = Transformation()
    private val fadeAnimation = AlphaAnimation(0f, 1f)

    // Cover image
    private var coverImage: Bitmap? = null
    private val coverImagePaint = Paint()

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

        // Get other resources
        fadeAnimationDuration = context.resources.getInteger(android.R.integer.config_shortAnimTime)

        // Init animation objects
        fadeAnimation.duration = fadeAnimationDuration.toLong()
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
        fullViewRect.right = w
        fullViewRect.bottom = h

        // Recalculate dimensions
        updateCoverImage(coverImage, imageColor)
    }

    override fun onDraw(canvas: Canvas) {
        if (coverImage != null && imageColor != 0) {
            // Apply fade animation
            fadeAnimation.getTransformation(System.currentTimeMillis(), fadeTransformation)
            coverImagePaint.alpha = (fadeTransformation.alpha * 255).toInt()

            if (alpha < 255) {
                postInvalidateDelayed(16)
            }

            // Draw background image first
            canvas.drawBitmap(coverImage!!, srcImageRect, destImageRect, coverImagePaint)
            canvas.drawRect(coverGradientRect, coverGradientPaint)
            canvas.drawRect(solidBackgroundRect, solidBackgroundPaint)
        }

        // Continue drawing as usual
        super.onDraw(canvas)
    }

    fun updateCoverImage(bitmap: Bitmap?, palette: Palette?) {
        if (palette == null)
            return updateCoverImage(bitmap)

        val isDarkTheme = context.isDarkTheme()
        val dominantColor = palette.getDominantColor(0)
        val lightDarkMutedColor = if (isDarkTheme) {
            palette.getDarkMutedColor(0)
        } else {
            palette.getLightMutedColor(0)
        }

        if (dominantColor.isLightColor() != isDarkTheme) {
            // Best color!
            return updateCoverImage(bitmap, dominantColor)
        }

        // Try muted color
        if (lightDarkMutedColor != 0) {
            return updateCoverImage(bitmap, lightDarkMutedColor)
        }

        // Try dominant color anyway
        if (dominantColor != 0) {
            return updateCoverImage(bitmap, dominantColor)
        }

        // No color found
        updateCoverImage(bitmap)
    }

    private fun updateCoverImage(bitmap: Bitmap?, @ColorInt color: Int = colorSurface) {
        if (this.coverImage == null && bitmap != null) {
            // Start fade in animation
            fadeAnimation.start()
            fadeAnimation.startTime = System.currentTimeMillis()
        }

        this.coverImage = bitmap

        if (bitmap != null) {
            updateSrcImageRect(bitmap)
            updateCoverGradient(color)
        }

        invalidate()
    }

    private fun updateSrcImageRect(bitmap: Bitmap) {
        destImageRect.right = fullViewRect.right

        if (destImageRect.right == 0)
            return

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

    private fun updateCoverGradient(@ColorInt givenColor: Int) {
        this.imageColor = adjustImageColor(givenColor)

        // Update rectangles
        val viewWidth = fullViewRect.right.toFloat()
        val viewHeight = fullViewRect.bottom.toFloat()

        val gradientEnd = destImageRect.bottom.toFloat()
        val gradientStart = gradientEnd * 0.8f

        coverGradientRect.apply {
            top = gradientStart
            bottom = gradientEnd
            left = 0f
            right = viewWidth
        }

        solidBackgroundRect.apply {
            top = gradientEnd
            bottom = viewHeight
            left = 0f
            right = viewWidth
        }

        // Remove existing shader
        coverGradientPaint.shader = null

        // Update paint objects
        coverGradientPaint.apply {
            isDither = true
            shader = LinearGradient(
                    0f, gradientStart, 0f, gradientEnd,
                    Color.TRANSPARENT, imageColor,
                    Shader.TileMode.CLAMP)
        }

        solidBackgroundPaint.apply {
            style = Paint.Style.FILL
            color = imageColor
        }
    }

    @ColorInt
    private fun adjustImageColor(@ColorInt color: Int): Int {
        // Fix white percentage according to the current theme
        val hslColor = floatArrayOf(0f, 0f, 0f)
        ColorUtils.colorToHSL(color, hslColor)
        val isDarkTheme = context.isDarkTheme()
        if (isDarkTheme == hslColor[2] >= 0.4) {
            hslColor[1] *= 0.7f
            if (isDarkTheme) {
                hslColor[2] *= 0.7f
            } else {
                hslColor[2] *= 1.2f
            }
        }
        return ColorUtils.HSLToColor(hslColor)
    }
}