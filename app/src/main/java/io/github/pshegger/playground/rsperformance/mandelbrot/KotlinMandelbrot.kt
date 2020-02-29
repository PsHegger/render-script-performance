package io.github.pshegger.playground.rsperformance.mandelbrot

import android.graphics.*
import io.github.pshegger.playground.rsperformance.MainActivity.Companion.MAX_ITER
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class KotlinMandelbrot(width: Int, height: Int) : BaseKotlinMandelbrot(width, height) {

    override suspend fun generate(buffer: Bitmap, rect: RectF) = withContext(Dispatchers.IO) {
        val canvas = Canvas(buffer)
        val paint = Paint()

        for (y in (0 until height)) {
            for (x in (0 until width)) {
                paint.color = calculatePoint(x, y, rect)
                canvas.drawPoint(x.toFloat(), y.toFloat(), paint)
            }
        }
    }
}
