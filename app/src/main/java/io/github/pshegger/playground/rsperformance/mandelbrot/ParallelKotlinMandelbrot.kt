package io.github.pshegger.playground.rsperformance.mandelbrot

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.launch
import java.util.concurrent.Executors
import kotlin.math.sqrt

class ParallelKotlinMandelbrot(width: Int, height: Int) : BaseKotlinMandelbrot(width, height) {

    override suspend fun generate(buffer: Bitmap, rect: RectF) {
        val numThreads = Runtime.getRuntime().availableProcessors() - 1
        val context = Executors.newFixedThreadPool(numThreads).asCoroutineDispatcher()

        var h = sqrt(numThreads.toDouble()).toInt()
        while (numThreads % h != 0) h--
        val w = numThreads / h

        val segWidth = width / w
        val segHeight = height / h

        val canvas = Canvas(buffer)

        val calculation = CoroutineScope(context).launch {
            for (mod in (0 until numThreads)) {
                val segX = mod % w
                val segY = mod / w
                val segXStart = segWidth * segX
                val segXEnd = segWidth * (segX + 1)
                val segYStart = segHeight * segY
                val segYEnd = segHeight * (segY + 1)
                val paint = Paint()

                launch {
                    for (y in (segYStart until segYEnd)) {
                        for (x in (segXStart until segXEnd)) {
                            val result = calculatePoint(x, y, rect)

                            paint.color = result
                            canvas.drawPoint(x.toFloat(), y.toFloat(), paint)
                        }
                    }
                }
            }
        }
        calculation.join()
    }
}
