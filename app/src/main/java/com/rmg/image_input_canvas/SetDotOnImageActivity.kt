package com.rmg.image_input_canvas

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.ViewTreeObserver
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.drawToBitmap
import com.bumptech.glide.Glide
import com.rmg.image_input_canvas.databinding.ActivitySetDotOnImageBinding

class SetDotOnImageActivity : AppCompatActivity() {
    lateinit var binding: ActivitySetDotOnImageBinding

    private var reverseRatioX:Float=0.0f
    private var reverseRatioY:Float=0.0f

    private var finalHeight: Float=0.0f
    private var finalWidth: Float=0.0f

    private var mDrawnBitmap: Bitmap? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySetDotOnImageBinding.inflate(layoutInflater, null, false)
        setContentView(binding.root)
        initialImageOnCanvas()



        val displayMetrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(displayMetrics)
        val height = displayMetrics.heightPixels
        val width =  displayMetrics.widthPixels
        "display  width = $width".log("dim")
        "display  height = $height".log("dim")
        binding.imgShow.viewTreeObserver.addOnGlobalLayoutListener {
            // binding.ivImage.viewTreeObserver.removeOnGlobalLayoutListener(this)

            // Get the width and height of the imageView
            finalWidth = binding.imgShow.width.toFloat()
            finalHeight = binding.imgShow.height.toFloat()
            "finalHeight: $finalHeight finalWidth: $finalWidth".log("dim")
        }
        

        reverseRatioX=intent.getFloatExtra("X",0.0f)
        reverseRatioY=intent.getFloatExtra("Y",0.0f)

        binding.btnShow.setOnClickListener {
            mDrawnBitmap = binding.imgShow.drawToBitmap()
            val pointAxisX=(finalWidth*reverseRatioX)/100.00f
            "Pont of value  of cordinate inpercentage reverse X = $pointAxisX".log("dim")
            val pointAxisY=(finalHeight*reverseRatioY)/100.00f
            "Pont of value  of cordinate inpercentage reverse Y = $pointAxisY".log("dim")

            drawOnBitmap(pointAxisX, pointAxisY)
        }



    }

    private fun drawOnBitmap(x: Float, y: Float) {
        mDrawnBitmap?.let {
            val canvas = Canvas(it)
            // Draw on the canvas at the touched coordinates
            // Example: Draw a red circle with radius 10 pixels
            val paint = Paint().apply {
                color = Color.RED
                style = Paint.Style.FILL
            }
            canvas.drawCircle(x, y, 10f, paint)
            binding.imgShow.setImageBitmap(it)
        }
    }

    private fun initialImageOnCanvas() {
        Glide.with(this)
            .load("https://www.aces.edu/wp-content/uploads/2023/04/iStock-1232014586.jpg")
            .error(R.drawable.logo)
            .into(binding.imgShow)

    }
}