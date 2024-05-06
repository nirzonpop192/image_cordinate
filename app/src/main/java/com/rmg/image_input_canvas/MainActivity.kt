package com.rmg.image_input_canvas

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Matrix
import android.graphics.Paint
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.MotionEvent
import android.view.ViewTreeObserver
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.drawToBitmap
import com.bumptech.glide.Glide
import com.rmg.image_input_canvas.databinding.ActivityMainBinding

/**
 * https://stackoverflow.com/questions/4649438/how-to-get-the-dimensions-of-a-drawable-in-an-imageview/4649842
 * https://stackoverflow.com/questions/6038867/android-how-to-detect-touch-location-on-imageview-if-the-image-view-is-scaled-b
 * https://stackoverflow.com/questions/4680499/how-to-get-the-width-and-height-of-an-android-widget-imageview
 * https://stackoverflow.com/questions/4743116/get-screen-width-and-height-in-android
 * https://stackoverflow.com/questions/47107105/android-button-has-setontouchlistener-called-on-it-but-does-not-override-perform
 */
class MainActivity : AppCompatActivity() {

    lateinit var binding: ActivityMainBinding

    private var mDrawnBitmap: Bitmap? = null

    private var pointAxisX: Float = 0.0f
    private var pointAxisY: Float = 0.0f

    private var ratioX: Float = 0.0f
    private var ratioY: Float = 0.0f

    private var finalHeightOfImageView: Float=0.0f
    private var finalWidthOfImageView: Float=0.0f
    private val IMAGE_URL_LINK="https://www.aces.edu/wp-content/uploads/2023/04/iStock-1232014586.jpg"

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)


        initialImageOnCanvas()
        binding.btnNxt.setOnClickListener {
            val intent = Intent(this@MainActivity, SetDotOnImageActivity::class.java)
            intent.putExtra("X", ratioX)
            intent.putExtra("Y", ratioY)
            startActivity(intent)
        }
        val displayMetrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(displayMetrics)
        val height = displayMetrics.heightPixels
        val width = displayMetrics.widthPixels
        "display  width = $width".log("dim")
        "display  height = $height".log("dim")




        binding.ivImage.viewTreeObserver.addOnGlobalLayoutListener {


            // Get the width and height of the imageView
            finalWidthOfImageView = binding.ivImage.width.toFloat()
            finalHeightOfImageView = binding.ivImage.height.toFloat()
            "finalHeight: $finalHeightOfImageView finalWidth: $finalWidthOfImageView".log("dim")
        }
        binding.ivImage.setOnTouchListener { view, motionEvent ->
            when (motionEvent.action) {
                MotionEvent.ACTION_DOWN -> {
                    // no need
                    //initialImageOnCanvas()
                    // Initialize the drawnBitmap with the loaded image
                    mDrawnBitmap = binding.ivImage.drawToBitmap()

                    // Draw on the bitmap
                    pointAxisX = motionEvent.x
                    pointAxisY = motionEvent.y



                    // calculate inverse matrix
                    val inverse = Matrix()
                    binding.ivImage.imageMatrix.invert(inverse)

                    // map touch point from ImageView to image

                    // map touch point from ImageView to image
                    val touchPoint = floatArrayOf(motionEvent.x, motionEvent.y)
                    inverse.mapPoints(touchPoint)
                    // touchPoint now contains x and y in image's coordinate system
                    "touchPoint now contains x and y in image's coordinate system".log("dim")

                    "Pont of value  of cordinate X = $pointAxisX \n Y = $pointAxisY".log("dim")
                    ratioX = (100.00f / finalWidthOfImageView) * pointAxisX
                    "Pont of value  of cordinate inpercentage  X = $ratioX".log("dim")
                    ratioY = (100.00f / finalHeightOfImageView) * pointAxisY
                    "Pont of value  of cordinate inpercentage  Y = $ratioY".log("dim")

                    // todo why need it
                    val reverseX = (width * ratioX) / 100.00f
                    "Pont of value  of cordinate inpercentage reverse X = $reverseX".log("dim")


                    drawOnBitmap(pointAxisX, pointAxisY)
                    true
                }

                MotionEvent.ACTION_MOVE -> {


                    true
                }

                MotionEvent.ACTION_UP -> {
                    // Drawing completed, send the modified image to the backend



                    true
                }

                else -> false
            }
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
            binding.ivImage.setImageBitmap(it)
        }
    }

    private fun initialImageOnCanvas() {
        Glide.with(this)
            .load(IMAGE_URL_LINK)
            .error(R.drawable.logo)
            .into(binding.ivImage)
    }
}