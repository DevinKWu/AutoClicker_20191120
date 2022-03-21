package com.chiaruy.autoclick

import android.content.Context
import android.widget.FrameLayout
import android.view.View.OnTouchListener
import android.content.Intent
import android.graphics.PixelFormat
import android.graphics.Point
import com.chiaruy.autoclick.AutoService
import com.chiaruy.autoclick.R
import android.os.Build
import android.util.Log
import android.view.*
import android.widget.ImageView
import java.lang.Exception

class MainFloat(context: Context) : FrameLayout(context) {
    private val TAG = "MainFloat"
    private val mContext: Context
    private val mView_add: View
    private val mWindowManager: WindowManager
    private val mWindowParams_add: WindowManager.LayoutParams?
    private val SizeX: Int
    private val SizeY: Int
    private val mImage_play: ImageView
    private var Play = false

    init {
        mContext = context.applicationContext
        val mLayoutInflater = LayoutInflater.from(context)
        mView_add = mLayoutInflater.inflate(R.layout.floating_add, null)
        mImage_play = mView_add.findViewById<View>(R.id.play) as ImageView
        mWindowManager = mContext.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val display = mWindowManager.defaultDisplay
        val point = Point()
        display.getSize(point)
        SizeX = point.x
        SizeY = point.y
        mWindowParams_add = WindowManager.LayoutParams()
        mWindowParams_add.gravity = Gravity.START
        mWindowParams_add.x = 0
        mWindowParams_add.y = 0
        mWindowParams_add.format = PixelFormat.RGBA_8888
        mWindowParams_add.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or
                WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL or
                WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN or
                WindowManager.LayoutParams.FLAG_LAYOUT_INSET_DECOR or
                WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH
        mWindowParams_add.width = LayoutParams.WRAP_CONTENT
        mWindowParams_add.height = LayoutParams.WRAP_CONTENT
        if (Build.VERSION.SDK_INT >= 26) {
            mWindowParams_add.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
        } else {
            mWindowParams_add.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT
        }
        try {
            mWindowManager.addView(mView_add, mWindowParams_add)
        } catch (e: Exception) {
            Log.d("$TAG Exception", "addView")
            e.printStackTrace()
        }

        mView_add.setOnTouchListener{ view, event ->
            if (mWindowParams_add == null) return@setOnTouchListener false
            mWindowParams_add.x = event.rawX.toInt()
            mWindowParams_add.y = event.rawY.toInt() - SizeY / 2
            if (event.action == MotionEvent.ACTION_MOVE) {
                try {
                    mWindowManager.updateViewLayout(view, mWindowParams_add)
                } catch (e: Exception) {
                    Log.d("Exception", "Add_MotionEvent")
                    e.printStackTrace()
                }
            }
            true
        }

        mImage_play.setOnClickListener{
            val autoServiceIntent = Intent(getContext(), AutoService::class.java)
            if (!Play) {
                Play = true
                mImage_play.setImageDrawable(resources.getDrawable(R.drawable.stop, null))
                autoServiceIntent.putExtra(AutoService.ACTION, AutoService.PLAY)
                getContext().startService(autoServiceIntent)
            } else {
                Play = false
                mImage_play.setImageDrawable(resources.getDrawable(R.drawable.play, null))
                autoServiceIntent.putExtra(AutoService.ACTION, AutoService.STOP)
                getContext().startService(autoServiceIntent)
            }
        }
    }
}