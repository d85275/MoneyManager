package com.example.demo.views

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.demo.R
import com.example.demo.utils.OnSwipeTouchListener
import kotlinx.android.synthetic.main.view_add_item.view.*

class AddItemView(context: Context, attrs: AttributeSet?) : LinearLayout(context, attrs) {
    private var view: View =
        LayoutInflater.from(context).inflate(R.layout.view_add_item, this, true)

    private companion object {
        private const val ADD_ANIM_DURATION = 300L
        private const val ALPHA_SHOW = 1F
        private const val ALPHA_HIDE = 0F
        private const val TAG = "ADD_VIEW"

    }

    init {
        setListeners()
    }

    private fun setListeners() {
        tvAddItemDate.setOnClickListener {
            Log.e(TAG, "date clicked")
        }
        tvPrice.setOnClickListener {
            if (isKeyboardShow) {
                dismissKeyboard()
            } else {
                showKeyboard()
            }
        }
        btCancel.setOnClickListener {
            dismiss()
        }
        view.setOnTouchListener(object : OnSwipeTouchListener(context) {
            override fun onSwipeBottom() {
                super.onSwipeBottom()
                dismiss()
            }
        })
    }

    fun setDate(date: String) {
        tvAddItemDate.text
    }

    private var isShow = MutableLiveData(false)
    private var isKeyboardShow = false

    fun isShow(): LiveData<Boolean> {
        return isShow
    }

    fun show() {
        isShow.value = true
        if (view.visibility != View.VISIBLE) {
            val y = view.height.toFloat()
            view.animate()
                .translationY(y)
                .alpha(ALPHA_HIDE)
                .setDuration(200)
                .withEndAction {
                    view.visibility = View.VISIBLE

                    view.animate()
                        .translationY(0f)
                        .setDuration(ADD_ANIM_DURATION)
                        .alpha(ALPHA_SHOW)
                        .start()
                }
                .start()
        } else {
            view.animate()
                .translationY(0f)
                .setDuration(ADD_ANIM_DURATION)
                .alpha(ALPHA_SHOW)
                .start()
        }
    }

    fun dismiss() {
        isShow.value = false
        dismissKeyboard()
        val y = view.height.toFloat()
        view.animate()
            .translationY(y)
            .alpha(ALPHA_HIDE)
            .setDuration(ADD_ANIM_DURATION)
            .start()
    }

    private fun dismissKeyboard() {
        isKeyboardShow = false
        vKeyboard.animate()
            .alpha(0f)
            .setDuration(300)
            .withEndAction {
                vKeyboard.visibility = View.INVISIBLE
                btConfirm.visibility = View.VISIBLE
            }.start()
    }

    private fun showKeyboard() {
        isKeyboardShow = true
        vKeyboard.visibility = View.VISIBLE
        vKeyboard.alpha = 0f
        btConfirm.visibility = View.INVISIBLE
        vKeyboard.animate()
            .alpha(1f)
            .setDuration(300)
            .start()
    }

}