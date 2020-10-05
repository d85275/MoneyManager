package com.example.demo.views

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.demo.utils.AnimHandler
import com.example.demo.utils.CommonUtils
import com.example.demo.R
import com.example.demo.utils.OnSwipeTouchListener
import com.example.demo.viewmodels.AddItemViewModel
import com.example.demo.viewmodels.CashViewModel
import kotlinx.android.synthetic.main.activity_history.*
import kotlinx.android.synthetic.main.fragment_cash.*
import kotlinx.android.synthetic.main.fragment_cash.vAddItem
import kotlinx.android.synthetic.main.fragment_cash.vBlocker
import kotlinx.android.synthetic.main.view_add_item.*
import kotlinx.android.synthetic.main.view_add_item.tvPrice


class CashFragment : Fragment() {

    private companion object {
        private const val ANIM_DELAY: Long = 2 * 1000
    }

    private lateinit var viewModel: CashViewModel
    private lateinit var addItemViewModel: AddItemViewModel
    private lateinit var animHandler: AnimHandler
    private lateinit var adapter: HistoryAdapter

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
        setListener()
        initView()
        getViewModel()
        initObservers()
        viewModel.loadRecentData()
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

    private fun initView() {
        animHandler = AnimHandler(ivMoney)
        adapter = HistoryAdapter()
        rvRecent.layoutManager = LinearLayoutManager(requireContext())
        rvRecent.setHasFixedSize(true)
        rvRecent.adapter = adapter
    }


    private fun setListener() {
        ivAdd.setOnClickListener {
            tvAddItemDate.text = "current date"
            addItemViewModel.showAddItemView(vAddItem)
            //vBlocker.visibility = View.VISIBLE
            addItemViewModel.hideAddBtn(ivAdd)
        }
        btCancel.setOnClickListener {
            cancelAddItem()
        }
        btConfirm.setOnClickListener {
            //viewModel.hideAddItemView(vAddItem)
            //vBlocker.visibility = View.GONE
            //viewModel.showAddBtn(btAdd)
            // todo clear data and add item to the database
            Toast.makeText(
                requireContext(),
                "The item has been saved into database",
                Toast.LENGTH_SHORT
            ).show()
        }

        vAddItem.setOnTouchListener(object : OnSwipeTouchListener(requireContext()) {
            override fun onSwipeBottom() {
                super.onSwipeBottom()
                cancelAddItem()
            }
        })
        tvPrice.setOnClickListener {
            if (vKeyboard.visibility != View.VISIBLE) {
                tvAddItemDate.text = "current Date"
                addItemViewModel.showKeyboard(vKeyboard, btConfirm)
            } else {
                addItemViewModel.hideKeyboard(vKeyboard, btConfirm)
            }
        }
    }

    private fun getViewModel() {
        viewModel = ViewModelProvider(this).get(CashViewModel::class.java)
        addItemViewModel = ViewModelProvider(this).get(AddItemViewModel::class.java)
    }

    private fun initObservers() {
        viewModel.recentData.observe(viewLifecycleOwner, Observer { recentData ->
            adapter.setList(recentData)
        })
    }

    private fun cancelAddItem() {
        addItemViewModel.hideAddItemView(vAddItem)
        //vBlocker.visibility = View.GONE
        addItemViewModel.showAddBtn(ivAdd)
        addItemViewModel.hideKeyboard(vKeyboard, btConfirm)
    }

    /**
     * Could handle back press.
     * @return true if back press was handled
     */
    fun onBackPressed(): Boolean {
        if (addItemViewModel.isAddViewShown) {
            cancelAddItem()
            return true
        }
        return false
    }

}