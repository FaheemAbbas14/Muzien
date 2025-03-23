package com.tt.muzien.ui.views


import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.os.Build
import android.util.AttributeSet
import android.view.View
import androidx.annotation.RequiresApi
import androidx.core.graphics.ColorUtils
import com.tt.muzien.R


/**
 * Created by Faheem Abbas on 05/07/2024.
 * Technical Lead(Mobile Apps)
 * faheemabbas60@yahoo.com
 * +923115284424
 */
class CustomCircularLoader @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    @RequiresApi(Build.VERSION_CODES.M)
    private val paint = Paint().apply {
        isAntiAlias = true
        color = context.getColor(R.color.white)
        style = Paint.Style.STROKE
        strokeWidth = 10f
    }

    private var rotationAngle = 0f

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val centerX = width / 2f
        val centerY = height / 2f
        val radius = Math.min(centerX, centerY) - paint.strokeWidth
        canvas.drawCircle(centerX, centerY, radius, paint)
        canvas.save()
        canvas.rotate(rotationAngle, centerX, centerY)
        canvas.drawLine(centerX, centerY - radius, centerX, centerY - radius / 2, paint)
        canvas.restore()

        rotationAngle += 10f
        if (rotationAngle >= 360f) {
            rotationAngle = 0f
        }
        invalidate()
    }

    @RequiresApi(Build.VERSION_CODES.M)
    fun setColor(color: Int) {
        val startColor = paint.color
        val animator = ValueAnimator.ofFloat(0f, 1f).apply {
            duration = 500 // 500ms for the transition
            addUpdateListener {
                val fraction = it.animatedFraction
                paint.color = ColorUtils.blendARGB(startColor, color, fraction)
                invalidate()
            }
        }
        animator.start()
    }
}