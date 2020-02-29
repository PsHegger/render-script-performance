package io.github.pshegger.playground.rsperformance.mandelbrot

import android.graphics.Color
import android.graphics.RectF
import io.github.pshegger.playground.rsperformance.MainActivity

abstract class BaseKotlinMandelbrot(protected val width: Int, protected val height: Int) :
    MandelbrotGenerator {

    protected fun calculatePoint(x: Int, y: Int, rect: RectF): Int {
        val cr = rect.left + (rect.right - rect.left) * (x / width.toDouble())
        val ci = rect.top + (rect.bottom - rect.top) * (y / height.toDouble())
        var zr = 0.0
        var zi = 0.0

        var i = 0
        while (i < MainActivity.MAX_ITER && zr * zr + zi * zi < 4) {
            val nzr = zr * zr - zi * zi + cr
            val nzi = 2 * zr * zi + ci
            zr = nzr
            zi = nzi
            i++
        }

        val hue = 360 * i.toFloat() / MainActivity.MAX_ITER
        val sat = 1f
        val value = if (i < MainActivity.MAX_ITER) 1f else 0f
        return Color.HSVToColor(floatArrayOf(hue, sat, value))
    }
}
