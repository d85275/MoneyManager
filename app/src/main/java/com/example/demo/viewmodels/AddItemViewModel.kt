package com.example.demo.viewmodels

import android.view.View
import androidx.lifecycle.ViewModel
import com.example.demo.utils.CommonUtils

class AddItemViewModel : ViewModel() {
    private companion object {
        private const val ADD_ANIM_DURATION = 300L
        private const val OFFSET = 40f
    }

    fun hideAddBtn(btAdd: View) {
        val x = btAdd.width.toFloat()
        btAdd.animate()
            .translationX(x + OFFSET)
            .setDuration(ADD_ANIM_DURATION)
            .start()
    }

    fun showAddBtn(btAdd: View) {
        val x = btAdd.width.toFloat()
        btAdd.animate()
            .translationX(0 - OFFSET)
            .setDuration(ADD_ANIM_DURATION)
            .start()
    }
}