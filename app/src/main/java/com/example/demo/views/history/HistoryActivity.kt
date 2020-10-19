package com.example.demo.views.history

import android.annotation.SuppressLint
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
import com.example.demo.viewmodels.HistoryViewModel
import com.example.demo.viewmodels.MainVMFactory
import com.example.demo.viewmodels.MainViewModel
import com.google.android.material.appbar.AppBarLayout
import kotlinx.android.synthetic.main.activity_history.*
import java.util.*


class HistoryActivity : AppCompatActivity() {
    private var isShow = true
    private var scrollRange = -1
    private lateinit var historyViewModel: HistoryViewModel
    private lateinit var mainViewModel: MainViewModel
    private lateinit var addItemViewModel: AddItemViewModel
    private lateinit var adapter: HistoryDataAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_history)
        setSupportActionBar(toolbar)
        getViewModel()
        initViews()
        setListeners()
        initObservers()
        mainViewModel.loadHistoryData()
        mainViewModel.loadTotalBalance()
    }

    override fun onResume() {
        super.onResume()
    }

    private fun startAnimation() {
        val controller = LayoutAnimationController(historyViewModel.getAnimationSetAlpha())
        recyclerView.layoutAnimation = controller
        recyclerView.adapter?.notifyDataSetChanged()
        recyclerView.scheduleLayoutAnimation()
    }


    private fun initViews() {
        //collapsingToolbar.title = " "
        adapter = HistoryDataAdapter()
        tvCurrentDate.text = historyViewModel.getDate(compactcalendar_view.firstDayOfCurrentMonth)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.setHasFixedSize(true)
        recyclerView.adapter = adapter
        vAddItem.setViewModel(mainViewModel)
        compactcalendar_view.shouldDrawIndicatorsBelowSelectedDays(true)
    }

    private fun getViewModel() {
        historyViewModel = ViewModelProvider(this).get(HistoryViewModel::class.java)
        mainViewModel =
            ViewModelProvider(this, MainVMFactory(Repository(this))).get(MainViewModel::class.java)
        addItemViewModel = ViewModelProvider(this).get(AddItemViewModel::class.java)
    }

    private var selectedDate: Date? = null
    private fun initObservers() {
        historyViewModel.selectedDay.observe(this, Observer { day ->
            mainViewModel.getDataByDay(day)
            selectedDate = day
        })

        historyViewModel.selectedMonth.observe(this, Observer { firstDayOfMonth ->
            tvCurrentDate.text = historyViewModel.getDate(firstDayOfMonth)
            mainViewModel.getDataByMonth(firstDayOfMonth)
        })

        mainViewModel.historyData.observe(this, Observer { historyData ->
            compactcalendar_view.removeAllEvents()
            compactcalendar_view.addEvents(historyViewModel.getEvents(historyData))
            if (selectedDate != null) {
                mainViewModel.getDataByDay(selectedDate!!)
                mainViewModel.getDataByMonth(selectedDate!!)
            }
        })
        mainViewModel.dayData.observe(this, Observer { dayData ->
            if (dayData.isEmpty()) {
                ivEdit.visibility = View.INVISIBLE
            } else {
                ivEdit.visibility = View.VISIBLE
            }
            mainViewModel.getDailyBalance(dayData)
            adapter.setList(dayData)
            startAnimation()
        })

        mainViewModel.monthData.observe(this, Observer { monthData ->
            mainViewModel.getMonthlyBalance(monthData)
        })

        historyViewModel.isAddItem.observe(this, Observer { isAdded ->
            if (isAdded) {
                //recyclerView.smoothScrollToPosition(adapter.itemCount)
            }
        })
        vAddItem.isShow().observe(this, Observer { isShow ->
            if (isShow) {
                disableToolbarScroll()
                vBlocker.visibility = View.VISIBLE
                addItemViewModel.hideAddBtn(btAdd)
            } else {
                enableToolbarScroll()
                vBlocker.visibility = View.GONE
                addItemViewModel.showAddBtn(btAdd)
            }
        })
        historyViewModel.isEditMode.observe(this, Observer { isEditMode ->
            if (isEditMode) {
                activateEditMode()
            } else {
                deactivateEditMode()
            }
        })
        mainViewModel.isDeleteCompleted.observe(this, Observer { isDeleteCompleted ->
            if (isDeleteCompleted) {
                historyViewModel.setEditMode(false)
            }
        })
        adapter.getSelectedId().observe(this, Observer { selectedId ->
            if (selectedId.size <= 0) {
                btDelete.setBackgroundResource(R.drawable.rounded_delete_btn_bg_not_selected)
            } else {
                btDelete.setBackgroundResource(R.drawable.rounded_delete_btn_bg_selected)
            }
        })
        mainViewModel.totalBalance.observe(this, Observer { totalBalance ->
            tvTotalBalance.text = totalBalance
        })
        mainViewModel.monthlyBalance.observe(this, Observer { monthlyBalance ->
            tvMonthlyBalance.text = monthlyBalance
        })
        mainViewModel.dailyBalance.observe(this, Observer { dailyBalance ->
            tvDailyBalance.text = dailyBalance
        })
    }

    private fun disableToolbarScroll() {
        //todo to disable toolbar scroll
    }

    private fun enableToolbarScroll() {
        //todo to enable toolbar scroll
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun activateEditMode() {
        ivEdit.visibility = View.INVISIBLE
        tvCancel.visibility = View.VISIBLE
        btDelete.visibility = View.VISIBLE
        adapter.setIsEditMode(true)
        compactcalendar_view.setOnTouchListener { _, _ -> true }
        addItemViewModel.hideAddBtn(btAdd)
    }

    private fun deactivateEditMode() {
        ivEdit.visibility = View.VISIBLE
        tvCancel.visibility = View.INVISIBLE
        btDelete.visibility = View.INVISIBLE
        adapter.setIsEditMode(false)
        compactcalendar_view.setOnTouchListener(null)
        addItemViewModel.showAddBtn(btAdd)
    }

    private fun setListeners() {
        compactcalendar_view.setListener(historyViewModel.getCalendarListener())
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
            if (historyViewModel.isEditMode.value == true) {
                return@setOnClickListener
            }
            finish()
        }
        btAdd.setOnClickListener {
            if (historyViewModel.isEditMode.value == true) {
                return@setOnClickListener
            }
            vAddItem.setDate(historyViewModel.selectedDay.value)
            vAddItem.show()
        }
        adapter.onItemClick = { historyData -> vAddItem.resumeData(historyData) }
        ivEdit.setOnClickListener { historyViewModel.onEditModeClicked() }
        btDelete.setOnClickListener {
            if (!adapter.getSelectedId().value.isNullOrEmpty()) {
                mainViewModel.deleteHistoryData(adapter.getSelectedId().value!!)
            }
        }
        tvCancel.setOnClickListener {
            historyViewModel.onEditModeClicked()
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