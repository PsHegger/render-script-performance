package io.github.pshegger.playground.rsperformance.mandelbrot

import android.graphics.Bitmap
import android.graphics.RectF

interface MandelbrotGenerator {

    suspend fun generate(buffer: Bitmap, rect: RectF)
}
