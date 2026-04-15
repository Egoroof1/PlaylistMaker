package com.diego.playlistmaker.player.custom_view

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.RectF
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import androidx.annotation.AttrRes
import androidx.annotation.StyleRes
import androidx.core.graphics.drawable.toBitmap
import com.diego.playlistmaker.R

class PlaybackButtonView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    @AttrRes defStyleAttr: Int = 0,
    @StyleRes defStyleRes: Int = 0
) : View(context, attrs, defStyleAttr, defStyleRes) {

    private var imageBitmap: Bitmap? = null
    private var imageRect = RectF(0f, 0f, 0f, 0f)

    private var isPlaying = false

    private var playBitmap: Bitmap?
    private var pauseBitmap: Bitmap?

    init {
        val defaultDrawable = context.getDrawable(R.drawable.ic_btn_play)
        val pauseDrawable = context.getDrawable(R.drawable.ic_btn_pause)

        playBitmap = defaultDrawable?.toBitmap()
        pauseBitmap = pauseDrawable?.toBitmap()

        context.theme.obtainStyledAttributes(
            attrs,
            R.styleable.PlaybackButtonView,
            defStyleAttr,
            defStyleRes
        ).apply {
            try {
                val customPlayDrawable = getDrawable(R.styleable.PlaybackButtonView_playIcon)
                val customPauseDrawable = getDrawable(R.styleable.PlaybackButtonView_pauseIcon)

                if (customPlayDrawable != null) {
                    (this@PlaybackButtonView).playBitmap = customPlayDrawable.toBitmap()
                }
                if (customPauseDrawable != null) {
                    (this@PlaybackButtonView).pauseBitmap = customPauseDrawable.toBitmap()
                }
            } finally {
                recycle()
            }
        }

        imageBitmap = playBitmap
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        when (event?.action) {
            MotionEvent.ACTION_DOWN -> {
                // Опционально: визуальный эффект нажатия (например, уменьшение альфы)
                alpha = 0.7f
                return true
            }
            MotionEvent.ACTION_UP -> {

                alpha = 1.0f


                val x = event.x
                val y = event.y
                if (x >= 0 && x <= width && y >= 0 && y <= height) {

                    togglePlayPause()
                }
                performClick()
                return true
            }
            MotionEvent.ACTION_CANCEL -> {
                alpha = 1.0f
                return true
            }
        }
        return super.onTouchEvent(event)
    }

    override fun performClick(): Boolean {
        super.performClick()
        return true
    }

    fun setPlaying(playing: Boolean) {
        if (isPlaying == playing) return
        isPlaying = playing
        imageBitmap = if (isPlaying) pauseBitmap else playBitmap
        invalidate()
    }

    private fun togglePlayPause() {
        isPlaying = !isPlaying

        imageBitmap = if (isPlaying) {
            pauseBitmap
        } else {
            playBitmap
        }

        invalidate()
        onPlaybackStateChanged?.invoke(isPlaying)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        imageRect = RectF(0f, 0f, measuredWidth.toFloat(), measuredHeight.toFloat())
    }

    override fun onDraw(canvas: Canvas) {
        imageBitmap?.let { bitmap ->
            canvas.drawBitmap(bitmap, null, imageRect, null)
        }
    }

    var onPlaybackStateChanged: ((Boolean) -> Unit)? = null
}