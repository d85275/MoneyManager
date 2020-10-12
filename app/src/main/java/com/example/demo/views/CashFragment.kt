package com.example.demo.views

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.demo.utils.AnimHandler
import com.example.demo.utils.CommonUtils
import com.example.demo.R
import com.example.demo.model.HistoryData
import com.example.demo.viewmodels.AddItemViewModel
import com.example.demo.viewmodels.CashViewModel
import com.example.demo.viewmodels.MainViewModel
import kotlinx.android.synthetic.main.fragment_cash.*
import kotlinx.android.synthetic.main.fragment_cash.vAddItem


class CashFragment : Fragment() {

    private companion object {
        private const val ANIM_DELAY: Long = 2 * 1000
    }

    private lateinit var cashViewModel: CashViewModel
    private lateinit var mainViewModel: MainViewModel
    private lateinit var addItemViewModel: AddItemViewModel
    private lateinit var animHandler: AnimHandler
    private lateinit var adapter: RecentDataAdapter

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_cash, container, false)
        val gesture =
            CommonUtils.getGesture(requireActivity(), {
                findNavController().navigate(R.id.action_cashFragment_to_bankFragment2)
            }, true)
        view.setOnTouchListener { _, event -> gesture.onTouchEvent(event) }
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        getViewModel()
        initView()
        setListener()
        initObservers()
        mainViewModel.loadRecentHistoryData(HistoryData.SOURCE_CASH)
    }

    override fun onResume() {
        super.onResume()
        animHandler.sendEmptyMessageDelayed(
            0,
            AnimHandler.ANIM_DELAY
        )
    }

    override fun onPause() {
        super.onPause()
        vAddItem.dismiss()
        animHandler.removeMessages(0)
    }

    private fun initView() {
        animHandler = AnimHandler(ivMoney)
        adapter = RecentDataAdapter()
        adapter.setType(0)
        rvRecent.layoutManager = LinearLayoutManager(requireContext())
        rvRecent.setHasFixedSize(true)
        rvRecent.adapter = adapter
        vAddItem.init(HistoryData.SOURCE_CASH, mainViewModel)
    }


    private fun setListener() {
        ivAdd.setOnClickListener {
            vAddItem.show()
        }
        adapter.onItemClick = { historyData -> vAddItem.resumeData(historyData) }
    }

    private fun getViewModel() {
        cashViewModel = ViewModelProvider(this).get(CashViewModel::class.java)
        mainViewModel = ViewModelProvider(requireActivity()).get(MainViewModel::class.java)
        addItemViewModel = ViewModelProvider(this).get(AddItemViewModel::class.java)
    }

    private fun initObservers() {
        mainViewModel.recentCashData.observe(viewLifecycleOwner, Observer { recentData ->
            rvRecent.visibility = View.VISIBLE
            adapter.setList(recentData)
        })
        vAddItem.isShow().observe(viewLifecycleOwner, Observer { isShow ->
            if (isShow) {
                addItemViewModel.hideAddBtn(ivAdd)
            } else {
                addItemViewModel.showAddBtn(ivAdd)
            }
        })
        mainViewModel.dbErrorMsg.observe(viewLifecycleOwner, Observer { errorCode ->
            val msg =
                if (errorCode == 0) getString(R.string.add_bank_error) else getString(R.string.add_history_error)
            CommonUtils.showToast(requireContext(), msg)
        })
        mainViewModel.totalBalance.observe(viewLifecycleOwner, Observer { totalBalance ->
            tvBalanced.text = totalBalance
        })
    }

    /**
     * Could handle back press.
     * @return true if back press was handled
     */
    fun onBackPressed(): Boolean {
        if (vAddItem.isShow().value == true) {
            vAddItem.dismiss()
            return true
        }
        return false
    }

}