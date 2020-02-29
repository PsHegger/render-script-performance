package io.github.pshegger.playground.rsperformance

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.RectF
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.content.ContextCompat
import io.github.pshegger.playground.rsperformance.mandelbrot.KotlinMandelbrot
import io.github.pshegger.playground.rsperformance.mandelbrot.MandelbrotGenerator
import io.github.pshegger.playground.rsperformance.mandelbrot.ParallelKotlinMandelbrot
import io.github.pshegger.playground.rsperformance.mandelbrot.RsMandelbrot
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.*
import kotlin.system.measureNanoTime

@SuppressLint("SetTextI18n")
class MainActivity : AppCompatActivity() {

    companion object {
        const val MAX_ITER = 128
        const val GENERATION_COUNT = 30
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        kotlinTest.setOnClickListener {
            val mandelbrot =
                KotlinMandelbrot(
                    resultHolder.width,
                    resultHolder.height
                )
            testType.text = "Kotlin"

            runTest(mandelbrot)
        }

        kotlinParallelTest.setOnClickListener {
            val mandelbrot = ParallelKotlinMandelbrot(
                resultHolder.width,
                resultHolder.height
            )
            testType.text = "Kotlin Parallel"

            runTest(mandelbrot)
        }

        rsTest.setOnClickListener {
            val mandelbrot =
                RsMandelbrot(
                    resultHolder.width,
                    resultHolder.height,
                    this
                )
            testType.text = "RenderScript"

            runTest(mandelbrot)
        }
    }

    private fun runTest(generator: MandelbrotGenerator) =
        launchCoroutineScope {
            kotlinTest.isEnabled = false
            kotlinParallelTest.isEnabled = false
            rsTest.isEnabled = false
            testProgress.max = GENERATION_COUNT
            testProgress.progress = 0
            avgTime.text = "- ms"

            val buffer = Bitmap.createBitmap(resultHolder.width, resultHolder.height, Bitmap.Config.ARGB_8888)
            val results = mutableListOf<Float>()

            for (i in (0 until GENERATION_COUNT)) {
                val t = measureNanoTime { generator.generate(buffer, calculateRect()) }
                resultHolder.setImageBitmap(buffer)
                results.add(t / 1000000f)
                testProgress.progress = i + 1
                avgTime.text = String.format("%.2f ms", results.average())
            }

            kotlinTest.isEnabled = true
            kotlinParallelTest.isEnabled = true
            rsTest.isEnabled = true
        }

    private fun calculateRect(): RectF {
        val ratio = 2f / resultHolder.height

        val rectHeight = resultHolder.height * ratio
        val rectWidth = resultHolder.width * ratio
        return RectF(-rectWidth / 2, -rectHeight / 2, rectWidth / 2, rectHeight / 2)
    }

    private val Context.mainCoroutineDispatcher: CoroutineDispatcher
        get() = ContextCompat.getMainExecutor(this).asCoroutineDispatcher()

    private fun Context.launchCoroutineScope(block: suspend CoroutineScope.() -> Unit) =
        CoroutineScope(mainCoroutineDispatcher).launch(block = block)
}
