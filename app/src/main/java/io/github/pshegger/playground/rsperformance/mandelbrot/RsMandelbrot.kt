package io.github.pshegger.playground.rsperformance.mandelbrot

import android.content.Context
import android.graphics.Bitmap
import android.graphics.RectF
import android.renderscript.Allocation
import android.renderscript.RenderScript
import io.github.pshegger.playground.rsperformance.MainActivity.Companion.MAX_ITER
import io.github.pshegger.playground.rsperformance.ScriptC_mandelbrot

class RsMandelbrot(width: Int, height: Int, context: Context) :
    MandelbrotGenerator {

    private val rs = RenderScript.create(context)
    private val script = ScriptC_mandelbrot(rs).apply {
        _width = width
        _height = height
        _maxIterations = MAX_ITER
    }

    override suspend fun generate(buffer: Bitmap, rect: RectF) {
        script.apply {
            _left = rect.left
            _top = rect.top
            _right = rect.right
            _bottom = rect.bottom
        }

        val bufferAllocation = Allocation.createFromBitmap(rs, buffer)
        script.forEach_mandelbrot(bufferAllocation, bufferAllocation)
        bufferAllocation.copyTo(buffer)
        bufferAllocation.destroy()
    }
}
