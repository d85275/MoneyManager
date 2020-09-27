package com.example.demo

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.*
import android.view.GestureDetector.SimpleOnGestureListener
import android.view.View.OnTouchListener
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import kotlinx.android.synthetic.main.fragment_cash.*

class BankFragment : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_bank, container, false)
        val gesture = CommonUtils.getGesture(requireActivity(),{
            findNavController().popBackStack() },false)
        view.setOnTouchListener { _, event -> gesture.onTouchEvent(event) }
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
    }
    private lateinit var animHandler: AnimHandler
    override fun onResume() {
        super.onResume()
        animHandler.sendEmptyMessageDelayed(0,AnimHandler.ANIM_DELAY)

    }

    override fun onPause() {
        super.onPause()
        animHandler.removeMessages(0)
    }

    private fun initView(){
        animHandler = AnimHandler(ivMoney)
    }
}