package com.chiaruy.autoclick

import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.GestureDescription
import android.accessibilityservice.GestureDescription.StrokeDescription
import android.annotation.TargetApi
import android.content.Intent
import android.graphics.Path
import android.graphics.Point
import android.os.Handler
import android.os.HandlerThread
import android.util.Log
import android.view.accessibility.AccessibilityEvent
import android.widget.Toast

class AutoService : AccessibilityService() {
    var TAG: String = "AutoService"
    private var mMainFloat: MainFloat? = null
    private var mHandler: Handler? = null
    private var intervalRunnable_index: Int = 0

    companion object {
        // Extra Key
        const val ACTION = "action"

        // Extra Value
        const val PLAY = "play"
        const val STOP = "stop"
    }

    override fun onCreate() {
        super.onCreate()
        mMainFloat = MainFloat(this)
        val handlerThread = HandlerThread("auto-handler")
        handlerThread.start()
        mHandler = Handler(handlerThread.looper)
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        Log.i(TAG, "onStartCommand: ")

        if (intent != null) {
            val action = intent.getStringExtra(ACTION)
            if (action == PLAY) {
                if (mRunnable == null) {
                    mRunnable = IntervalRunnable()
                }
                intervalRunnable_index = 0
                mHandler!!.postDelayed(mRunnable!!, 0L)
                Toast.makeText(baseContext, getString(R.string.start_script), Toast.LENGTH_SHORT)
                    .show()
            } else if (action == STOP) {
                mHandler!!.removeCallbacksAndMessages(null)
                Toast.makeText(baseContext, getString(R.string.stop_script), Toast.LENGTH_SHORT)
                    .show()
            }
        }

        return super.onStartCommand(intent, flags, startId)
    }


    override fun onAccessibilityEvent(event: AccessibilityEvent) {
    }

    override fun onInterrupt() {
    }

    private var mRunnable: IntervalRunnable? = null

    private inner class IntervalRunnable : Runnable {
        override fun run() {
            val delayMillis = runPlanA()
            intervalRunnable_index++
            mHandler!!.postDelayed(mRunnable!!, delayMillis)
        }

        fun runPlanA(): Long {
            val total = 3;
            val step = intervalRunnable_index % total
            Log.i(TAG, "runPlan: $step")

            when (step) {
                0 -> {
                    click(300F, 800F)
                }
                1 -> {
                    click(400F, 800F)
                    return 800L
                }
                2 -> {
                    click(500F, 800F)
                    return 600L
                }
            }
            return 200L
        }
    }

    private fun click(point_x: Float, point_y: Float) {
        Log.i(TAG, "run: click($point_x, $point_y)")
        val builder: GestureDescription.Builder = GestureDescription.Builder()
        val path = Path()
        path.moveTo(point_x, point_y)
        path.lineTo(5 + point_x, 5 + point_y)
        builder.addStroke(StrokeDescription(path, 0L, 200L))

        val gestureDescription = builder.build()
        dispatchGesture(gestureDescription, object : GestureResultCallback() {
            override fun onCompleted(gestureDescription: GestureDescription) {
                super.onCompleted(gestureDescription)
            }

            override fun onCancelled(gestureDescription: GestureDescription) {
                super.onCancelled(gestureDescription)
            }
        }, null)
    }
}