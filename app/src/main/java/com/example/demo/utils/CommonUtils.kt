package com.example.demo.utils

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.GestureDetector
import android.view.MotionEvent
import com.example.demo.views.HistoryActivity
import kotlin.math.abs

object CommonUtils {

    fun getGesture(activity: Activity, action: () -> Unit, isEnter: Boolean): GestureDetector {
        return GestureDetector(
            activity,
            object : GestureDetector.SimpleOnGestureListener() {
                override fun onDown(e: MotionEvent): Boolean {
                    return true
                }

                override fun onFling(
                    e1: MotionEvent, e2: MotionEvent, velocityX: Float,
                    velocityY: Float
                ): Boolean {
                    Log.i("Bank", "onFling has been called!")
                    val SWIPE_MIN_DISTANCE = 120
                    val SWIPE_MAX_OFF_PATH = 250
                    val SWIPE_THRESHOLD_VELOCITY = 200
                    try {
                        if (abs(e1.y - e2.y) > SWIPE_MAX_OFF_PATH) return false
                        if (e1.x - e2.x > SWIPE_MIN_DISTANCE
                            && abs(velocityX) > SWIPE_THRESHOLD_VELOCITY
                        ) {
                            Log.e("SWIPE", "Right to Left")
                            if (isEnter) action.invoke()
                        } else if (e2.x - e1.x > SWIPE_MIN_DISTANCE
                            && abs(velocityX) > SWIPE_THRESHOLD_VELOCITY
                        ) {
                            if (!isEnter) action.invoke()
                            Log.e("SWIPE", "Left to Right")
                        }
                    } catch (e: Exception) {
                        // nothing
                    }
                    return super.onFling(e1, e2, velocityX, velocityY)
                }
            })
    }

    const val FROM_CASH = 0
    const val FROM_BANK = 1
    const val KEY_FROM = "KEY_FROM"
    fun goHistory(context: Context, from: Int) {
        val intent = Intent()
        val bundle = Bundle()
        intent.setClass(context, HistoryActivity::class.java)
        bundle.putInt(KEY_FROM,from)
        intent.putExtras(bundle)
        context.startActivity(intent)
    }
}