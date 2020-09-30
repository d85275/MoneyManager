package com.example.demo.utils

import android.os.Handler
import android.os.Message
import android.view.View
import com.example.demo.views.HistoryActivity
import kotlinx.android.synthetic.main.activity_history.*

class AnimHandler(private val v: View) : Handler() {
    companion object {
        const val ANIM_DELAY: Long = 2000
    }

    override fun handleMessage(msg: Message) {
        super.handleMessage(msg)
        v.animate().withLayer()
            .rotationY(90f)
            .setDuration(300)
            .withEndAction(
                Runnable {
                    // second quarter turn
                    v.rotationY = -90f;
                    v.animate().withLayer()
                        .rotationY(0f)
                        .setDuration(300)
                        .start();

                }
            ).start()
        sendEmptyMessageDelayed(
            0,
            ANIM_DELAY
        )
    }
}