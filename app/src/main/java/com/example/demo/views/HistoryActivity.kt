package com.example.demo.views

import android.os.Bundle
import android.view.View
import android.view.animation.LayoutAnimationController
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.demo.R
import com.example.demo.Repository
import com.example.demo.viewmodels.AddItemViewModel
import com.example.demo.viewmodels.HistoryVMFactory
import com.example.demo.viewmodels.HistoryViewModel
import com.google.android.material.appbar.AppBarLayout
import kotlinx.android.synthetic.main.activity_history.*

class HistoryActivity : AppCompatActivity() {
    private var isShow = true
    private var scrollRange = -1
    private lateinit var viewModel: HistoryViewModel
    private lateinit var addItemViewModel: AddItemViewModel
    private lateinit var adapter: HistoryDataAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_history)
        setSupportActionBar(toolbar)
        getViewModel()
        setListeners()
        initViews()
        initObservers()
        //viewModel.loadData(compactcalendar_view.firstDayOfCurrentMonth)
        viewModel.loadHistoryData()
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
        adapter = HistoryDataAdapter()
        tvCurrentDate.text = viewModel.getDate(compactcalendar_view.firstDayOfCurrentMonth)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.setHasFixedSize(true)
        recyclerView.adapter = adapter
    }

    private fun getViewModel() {
        viewModel = ViewModelProvider(
            this,
            HistoryVMFactory(Repository(this))
        ).get(HistoryViewModel::class.java)
        addItemViewModel = ViewModelProvider(this).get(AddItemViewModel::class.java)
    }

    private fun initObservers() {
        viewModel.selectedDay.observe(this, Observer { day ->
            //val selectedDay = viewModel.getDay(day)
            adapter.setList(viewModel.getDataByDay(day))
        })

        viewModel.selectedMonth.observe(this, Observer { firstDayOfMonth ->
            tvCurrentDate.text = viewModel.getDate(firstDayOfMonth)
        })

        viewModel.historyData.observe(this, Observer { historyData ->
            compactcalendar_view.addEvents(viewModel.getEvents(historyData))
        })

        viewModel.isAddItem.observe(this, Observer { isAdded ->
            if (isAdded) {
                //recyclerView.smoothScrollToPosition(adapter.itemCount)
            }
        })
        vAddItem.isShow().observe(this, Observer { isShow ->
            if (isShow) {
                vBlocker.visibility = View.VISIBLE
                addItemViewModel.hideAddBtn(btAdd)
            } else {
                vBlocker.visibility = View.GONE
                addItemViewModel.showAddBtn(btAdd)
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
                isShow = true
            } else if (isShow) {
                isShow = false
            }
        })
        llClose.setOnClickListener {
            finish()
        }
        btAdd.setOnClickListener {
            vAddItem.setDate(viewModel.selectedDay.value)
            vAddItem.show()
        }
    }


    override fun onBackPressed() {
        if (vAddItem.isShow().value == true) {
            vAddItem.dismiss()
        } else {
            super.onBackPressed()
        }
    }
}