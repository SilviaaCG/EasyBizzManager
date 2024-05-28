package com.silvia.easybizzmanager3.models

import android.graphics.Bitmap
import android.graphics.BitmapShader
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.Shader
import android.widget.ImageView
import com.squareup.picasso.Transformation
class TransformImage(private val radius: Float) : Transformation {

    override fun transform(source: Bitmap): Bitmap {
        val bitmap = Bitmap.createBitmap(source.width, source.height, Bitmap.Config.ARGB_8888)

        val canvas = Canvas(bitmap)
        val paint = Paint()
        val shader = BitmapShader(source, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP)
        paint.shader = shader
        paint.isAntiAlias = true

        val rect = RectF(0f, 0f, source.width.toFloat(), source.height.toFloat())
        canvas.drawRoundRect(rect, radius, radius, paint)

        source.recycle()
        return bitmap
    }

    override fun key(): String {
        return "rounded_corners"
    }
}