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
import com.example.demo.Repository
import com.example.demo.model.BankData
import com.example.demo.utils.AnimHandler
import com.example.demo.utils.CommonUtils
import com.example.demo.viewmodels.AddItemViewModel
import com.example.demo.viewmodels.BankVMFactory
import com.example.demo.viewmodels.BankViewModel
import kotlinx.android.synthetic.main.fragment_bank.*

class BankFragment : Fragment() {

    private lateinit var addItemViewModel: AddItemViewModel
    private lateinit var viewModel: BankViewModel
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
        viewModel.loadRecentData()
        viewModel.loadBankData()
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
    }

    override fun onDestroy() {
        super.onDestroy()
        vpBank.unregisterOnPageChangeCallback(viewModel.getChangeCallback())
    }

    private fun initView() {
        animHandler = AnimHandler(ivMoney)
        adapter = HistoryAdapter()
        adapter.setType(1)
        rvRecent.layoutManager = LinearLayoutManager(requireContext())
        rvRecent.setHasFixedSize(true)
        rvRecent.adapter = adapter

        bankAdapter = BankAdapter()
        bankAdapter.setViewModel(viewModel)
        vpBank.orientation = ViewPager2.ORIENTATION_VERTICAL
        vpBank.adapter = bankAdapter
        vpBank.clipChildren = false
        vpBank.offscreenPageLimit = 3
        vpBank.setPageTransformer(viewModel.getTransformer())
        /*
        rvBank.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        rvBank.setHasFixedSize(true)
        rvBank.adapter = bankAdapter
         */
    }

    private fun initObservers() {
        viewModel.recentData.observe(viewLifecycleOwner, Observer { recentData ->
            adapter.setList(recentData)
        })
        viewModel.bankData.observe(viewLifecycleOwner, Observer { bankData ->
            bankAdapter.setList(bankData)
        })
        vAddItem.isShow().observe(viewLifecycleOwner, Observer { isShow ->
            if (isShow) {
                addItemViewModel.hideAddBtn(ivAdd)
            } else {
                addItemViewModel.showAddBtn(ivAdd)
            }
        })
        viewModel.curBank.observe(viewLifecycleOwner, Observer { curBank ->
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
        viewModel.getBankData(curBank)
    }

    private fun getViewModel() {
        viewModel = ViewModelProvider(
            this,
            BankVMFactory(Repository(requireContext()))
        ).get(BankViewModel::class.java)
        addItemViewModel = ViewModelProvider(this).get(AddItemViewModel::class.java)
    }

    private fun setListeners() {
        ivAdd.setOnClickListener {
            vAddItem.show()
        }
        vpBank.registerOnPageChangeCallback(viewModel.getChangeCallback())

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