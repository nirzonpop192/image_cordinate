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
import coil.target.Target
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

    private var finalHeight: Float=0.0f
    private var finalWidth: Float=0.0f

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



        val vto: ViewTreeObserver = binding.ivImage.viewTreeObserver
//        vto.addOnPreDrawListener(object : ViewTreeObserver.OnPreDrawListener {
//            override fun onPreDraw(): Boolean {
//                binding.ivImage.viewTreeObserver.removeOnPreDrawListener(this)
//                finalHeight = binding.ivImage.measuredHeight
//                finalWidth = binding.ivImage.measuredWidth
//             "Height: $finalHeight Width: $finalWidth".log("dim")
//                return true
//            }
//        })
        binding.ivImage.viewTreeObserver.addOnGlobalLayoutListener {
            // binding.ivImage.viewTreeObserver.removeOnGlobalLayoutListener(this)

            // Get the width and height of the imageView
            finalWidth = binding.ivImage.width.toFloat()
            finalHeight = binding.ivImage.height.toFloat()
            "finalHeight: $finalHeight finalWidth: $finalWidth".log("dim")
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

                    // calculate inverse matrix
                    val inverse = Matrix()
                    binding.ivImage.getImageMatrix().invert(inverse)

// map touch point from ImageView to image

// map touch point from ImageView to image
                    val touchPoint = floatArrayOf(motionEvent.x, motionEvent.y)
                    inverse.mapPoints(touchPoint)
// touchPoint now contains x and y in image's coordinate system
                    "touchPoint now contains x and y in image's coordinate system".log("dim")

                    "Pont of value  of cordinate X = $pointAxisX \n Y = $pointAxisY".log("dim")
                    ratioX = (100.00f / finalWidth) * pointAxisX
                    "Pont of value  of cordinate inpercentage  X = $ratioX".log("dim")
                    ratioY = (100.00f / finalHeight) * pointAxisY
                    "Pont of value  of cordinate inpercentage  Y = $ratioY".log("dim")

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
                    //  markingImageViewModel.getSewingQcOperations()

                    // drawnBitmap?.let { sendModifiedImageToBackend(it) }
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
            .load("https://www.aces.edu/wp-content/uploads/2023/04/iStock-1232014586.jpg")
            .error(R.drawable.logo)
            .into(binding.ivImage)
    }
}