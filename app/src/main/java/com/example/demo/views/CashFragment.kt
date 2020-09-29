package com.example.demo.views

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.demo.utils.AnimHandler
import com.example.demo.utils.CommonUtils
import com.example.demo.R
import kotlinx.android.synthetic.main.fragment_cash.*


class CashFragment : Fragment() {

    private companion object{
        private const val ANIM_DELAY:Long = 2 *1000
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_cash,container,false)
        val gesture =
            CommonUtils.getGesture(requireActivity(), {
                findNavController().navigate(R.id.action_cashFragment_to_bankFragment2)
            }, true)
        view.setOnTouchListener { _, event -> gesture.onTouchEvent(event) }
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setListener()
        initView()
    }

    private lateinit var animHandler: AnimHandler
    override fun onResume() {
        super.onResume()
        animHandler.sendEmptyMessageDelayed(0,
            AnimHandler.ANIM_DELAY
        )

    }

    override fun onPause() {
        super.onPause()
        animHandler.removeMessages(0)
    }

    private fun initView(){
        animHandler = AnimHandler(ivMoney)
    }


    private fun setListener(){
        llHistory.setOnClickListener {
            CommonUtils.goHistory(
                requireContext(),
                CommonUtils.FROM_CASH
            )
        }
    }
}