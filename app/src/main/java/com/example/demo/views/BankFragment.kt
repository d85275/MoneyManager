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
import androidx.viewpager2.widget.ViewPager2
import com.example.demo.R
import com.example.demo.model.BankData
import com.example.demo.utils.AnimHandler
import com.example.demo.utils.CommonUtils
import com.example.demo.viewmodels.AddItemViewModel
import com.example.demo.viewmodels.BankViewModel
import com.example.demo.viewmodels.MainViewModel
import kotlinx.android.synthetic.main.fragment_bank.*
import kotlinx.android.synthetic.main.fragment_bank.ivAdd
import kotlinx.android.synthetic.main.fragment_bank.ivMoney
import kotlinx.android.synthetic.main.fragment_bank.rvRecent
import kotlinx.android.synthetic.main.fragment_bank.vAddItem

class BankFragment : Fragment() {

    private lateinit var addItemViewModel: AddItemViewModel
    private lateinit var bankViewModel: BankViewModel
    private lateinit var mainViewModel: MainViewModel
    private lateinit var animHandler: AnimHandler
    private lateinit var adapter: HistoryAdapter
    private lateinit var bankAdapter: BankAdapter

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
        val gesture =
            CommonUtils.getGesture(requireActivity(), { findNavController().popBackStack() }, false)
        view.setOnTouchListener { _, event -> gesture.onTouchEvent(event) }
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        getViewModel()
        initView()
        setListeners()
        initObservers()
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
        animHandler.removeMessages(0)
        vAddItem.dismiss()
        vpBank.registerOnPageChangeCallback(mainViewModel.getBankChangeCallback())
    }

    override fun onStop() {
        super.onStop()
        vpBank.unregisterOnPageChangeCallback(mainViewModel.getBankChangeCallback())
    }

    private fun initView() {
        animHandler = AnimHandler(ivMoney)
        adapter = HistoryAdapter()
        adapter.setType(1)
        rvRecent.layoutManager = LinearLayoutManager(requireContext())
        rvRecent.setHasFixedSize(true)
        rvRecent.adapter = adapter

        bankAdapter = BankAdapter()
        bankAdapter.setViewModel(mainViewModel)
        vpBank.orientation = ViewPager2.ORIENTATION_VERTICAL
        vpBank.adapter = bankAdapter
        vpBank.offscreenPageLimit = 3
        vpBank.setPageTransformer(bankViewModel.getTransformer())
        /*
        rvBank.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        rvBank.setHasFixedSize(true)
        rvBank.adapter = bankAdapter
         */
    }

    private fun initObservers() {
        mainViewModel.recentBankData.observe(viewLifecycleOwner, Observer { recentData ->
            adapter.setList(recentData)
        })
        mainViewModel.bankList.observe(viewLifecycleOwner, Observer { bankData ->
            vpBank.visibility = View.VISIBLE
            bankAdapter.setList(bankData)
            if (bankData.size == 1) {
                ivAdd.visibility = View.GONE
            } else {
                ivAdd.visibility = View.VISIBLE
            }
        })
        vAddItem.isShow().observe(viewLifecycleOwner, Observer { isShow ->
            if (isShow) {
                addItemViewModel.hideAddBtn(ivAdd)
            } else {
                addItemViewModel.showAddBtn(ivAdd)
            }
        })
        mainViewModel.curBank.observe(viewLifecycleOwner, Observer { curBank ->
            if (curBank == null) {
                hideRecentData()
            } else {
                getRecentData(curBank)
            }
        })
    }

    private fun hideRecentData() {
        rvRecent.visibility = View.INVISIBLE
    }

    private fun getRecentData(curBank: BankData) {
        rvRecent.visibility = View.VISIBLE
        mainViewModel.getBankData(curBank)
    }

    private fun getViewModel() {
        bankViewModel = ViewModelProvider(this).get(BankViewModel::class.java)
        addItemViewModel = ViewModelProvider(this).get(AddItemViewModel::class.java)
        mainViewModel = ViewModelProvider(requireActivity()).get(MainViewModel::class.java)
    }

    private fun setListeners() {
        ivAdd.setOnClickListener {
            vAddItem.show()
        }
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