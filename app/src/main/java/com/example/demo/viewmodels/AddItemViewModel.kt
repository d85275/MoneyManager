package com.example.demo.viewmodels

import android.view.View
import androidx.lifecycle.ViewModel

class AddItemViewModel:ViewModel() {
    private companion object {
        private const val ANIM_DURATION = 150L
        private const val ADD_ANIM_DURATION = 300L
        private const val ALPHA_SHOW = 1F
        private const val ALPHA_HIDE = 0F
    }
    var isAddViewShown = false

    fun hideAddBtn(btAdd: View) {
        val x = btAdd.width.toFloat()
        btAdd.animate()
            .translationX(x + 40)
            .setDuration(ADD_ANIM_DURATION)
            .start()
    }

    fun showAddBtn(btAdd: View) {
        val x = btAdd.width.toFloat()
        btAdd.animate()
            .translationX(0 - 40f)
            .setDuration(ADD_ANIM_DURATION)
            .start()
    }

    fun hideAddItemView(vAddItem: View) {
        isAddViewShown = false
        val y = vAddItem.height.toFloat()
        vAddItem.animate()
            .translationY(y)
            .alpha(ALPHA_HIDE)
            .setDuration(ADD_ANIM_DURATION)
            .start()
    }

    fun showAddItemView(vAddItem: View) {
        isAddViewShown = true
        if (vAddItem.visibility != View.VISIBLE) {
            val y = vAddItem.height.toFloat()
            vAddItem.animate()
                .translationY(y)
                .alpha(ALPHA_HIDE)
                .setDuration(200)
                .withEndAction {
                    vAddItem.visibility = View.VISIBLE

                    vAddItem.animate()
                        .translationY(0f)
                        .setDuration(ADD_ANIM_DURATION)
                        .alpha(ALPHA_SHOW)
                        .start()
                }
                .start()
        } else {
            vAddItem.animate()
                .translationY(0f)
                .setDuration(ADD_ANIM_DURATION)
                .alpha(ALPHA_SHOW)
                .start()
        }
    }

    fun showKeyboard(vKeyboard: View, btConfirm: View) {
        vKeyboard.visibility = View.VISIBLE
        vKeyboard.alpha = 0f
        btConfirm.visibility = View.INVISIBLE
        vKeyboard.animate()
            .alpha(1f)
            .setDuration(300)
            .start()
    }

    fun hideKeyboard(vKeyboard: View, btConfirm: View){
        vKeyboard.animate()
            .alpha(0f)
            .setDuration(300)
            .withEndAction{
                vKeyboard.visibility = View.INVISIBLE
                btConfirm.visibility = View.VISIBLE
            }.start()
    }
}