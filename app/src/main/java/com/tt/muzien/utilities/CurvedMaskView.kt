package com.tt.muzien.utilities


import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View

class CurvedMaskView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : View(context, attrs) {

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.BLACK  // Set to black
        style = Paint.Style.FILL
    }

    private val shapePath = Path()
    private var bulgeRadius: Float = 150f  // Increased from 100f to 150f for bigger shape

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val width = width.toFloat()
        val height = height.toFloat()
        val centerX = width / 2f

        val bulgeWidth = bulgeRadius * 3.5f  // Wider bulge
        val leftX = centerX - bulgeWidth / 2f
        val rightX = centerX + bulgeWidth / 2f

        shapePath.reset()
        shapePath.moveTo(0f, bulgeRadius)

        // Left to bulge
        shapePath.lineTo(leftX, bulgeRadius)

        // Bulge curve
        shapePath.cubicTo(
            leftX + bulgeWidth * 0.25f, bulgeRadius,
            centerX - bulgeWidth * 0.25f, 0f,
            centerX, 0f
        )
        shapePath.cubicTo(
            centerX + bulgeWidth * 0.25f, 0f,
            rightX - bulgeWidth * 0.25f, bulgeRadius,
            rightX, bulgeRadius
        )

        // Complete shape
        shapePath.lineTo(width, bulgeRadius)
        shapePath.lineTo(width, height)
        shapePath.lineTo(0f, height)
        shapePath.close()

        canvas.drawPath(shapePath, paint)
    }
}
