package com.example.demo.views

import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.animation.LayoutAnimationController
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.demo.R
import com.example.demo.utils.OnSwipeTouchListener
import com.example.demo.viewmodels.AddItemViewModel
import com.example.demo.viewmodels.HistoryViewModel
import com.google.android.material.appbar.AppBarLayout
import kotlinx.android.synthetic.main.activity_history.*
import kotlinx.android.synthetic.main.item_history.tvPrice
import kotlinx.android.synthetic.main.view_add_item.*

class HistoryActivity : AppCompatActivity() {
    private var isShow = true
    private var scrollRange = -1
    private lateinit var viewModel: HistoryViewModel
    private lateinit var addItemViewModel: AddItemViewModel
    private lateinit var adapter: HistoryAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_history)
        setSupportActionBar(toolbar)
        getViewModel()
        setListeners()
        initViews()
        initObservers()
        viewModel.loadData(compactcalendar_view.firstDayOfCurrentMonth)
    }

    override fun onResume() {
        super.onResume()
        startAnimation()
    }

    private fun startAnimation() {
        val controller = LayoutAnimationController(viewModel.getAnimationSetAlpha())
        recyclerView.layoutAnimation = controller
        recyclerView.adapter?.notifyDataSetChanged()
        recyclerView.scheduleLayoutAnimation()
    }


    private fun initViews() {
        collapsingToolbar.title = " "
        adapter = HistoryAdapter()
        tvCurrentDate.text = viewModel.getDate(compactcalendar_view.firstDayOfCurrentMonth)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.setHasFixedSize(true)
        recyclerView.adapter = adapter
    }

    private fun getViewModel() {
        viewModel = ViewModelProvider(this).get(HistoryViewModel::class.java)
        addItemViewModel = ViewModelProvider(this).get(AddItemViewModel::class.java)
    }

    private fun initObservers() {
        viewModel.selectedDay.observe(this, Observer { day ->
            val selectedDay = viewModel.getDay(day)
        })

        viewModel.selectedMonth.observe(this, Observer { firstDayOfMonth ->
            tvCurrentDate.text = viewModel.getDate(firstDayOfMonth)
        })

        viewModel.historyData.observe(this, Observer { historyData ->
            adapter.setList(historyData)
        })
        viewModel.isAddItem.observe(this, Observer { isAdded ->
            if (isAdded) {
                //recyclerView.smoothScrollToPosition(adapter.itemCount)
            }
        })
    }

    private fun setListeners() {
        compactcalendar_view.setListener(viewModel.getCalendarListener())
        appbar.addOnOffsetChangedListener(AppBarLayout.OnOffsetChangedListener { barLayout, verticalOffset ->
            if (scrollRange == -1) {
                scrollRange = barLayout?.totalScrollRange!!
            }
            if (scrollRange + verticalOffset == 0) {
                //ivPreMonth.setImageResource(R.drawable.pre_arrow_black)
                //ivNextMonth.setImageResource(R.drawable.next_arrow_black)
                isShow = true
            } else if (isShow) {
                //ivPreMonth.setImageResource(R.drawable.pre_arrow_white)
                //ivNextMonth.setImageResource(R.drawable.next_arrow_white)
                isShow = false
            }
        })
        llClose.setOnClickListener {
            finish()
        }
        btAdd.setOnClickListener {
            tvAddItemDate.text = viewModel.getAddDate()
            addItemViewModel.showAddItemView(vAddItem)
            vBlocker.visibility = View.VISIBLE
            addItemViewModel.hideAddBtn(btAdd)
        }
        btCancel.setOnClickListener {
            cancelAddItem()
        }
        btConfirm.setOnClickListener {
            //viewModel.hideAddItemView(vAddItem)
            //vBlocker.visibility = View.GONE
            //viewModel.showAddBtn(btAdd)
            // todo clear data and add item to the database
            Toast.makeText(this, "The item has been saved into database", Toast.LENGTH_SHORT).show()
        }

        vAddItem.setOnTouchListener(object : OnSwipeTouchListener(this) {
            override fun onSwipeBottom() {
                super.onSwipeBottom()
                cancelAddItem()
            }
        })
        tvPrice.setOnClickListener {
            if (vKeyboard.visibility != View.VISIBLE) {
                tvAddItemDate.text = viewModel.getAddDate()
                addItemViewModel.showKeyboard(vKeyboard, btConfirm)
            } else {
                addItemViewModel.hideKeyboard(vKeyboard, btConfirm)
            }
        }
    }

    private fun cancelAddItem() {
        addItemViewModel.hideAddItemView(vAddItem)
        vBlocker.visibility = View.GONE
        addItemViewModel.showAddBtn(btAdd)
        addItemViewModel.hideKeyboard(vKeyboard, btConfirm)
    }

    override fun onBackPressed() {
        if (addItemViewModel.isAddViewShown) {
            cancelAddItem()
        } else {
            super.onBackPressed()
        }
    }
}