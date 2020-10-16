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
import com.example.demo.viewmodels.*
import com.google.android.material.appbar.AppBarLayout
import kotlinx.android.synthetic.main.activity_history.*
import kotlinx.android.synthetic.main.activity_history.vAddItem
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
    }

    override fun onResume() {
        super.onResume()
        startAnimation()
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
        historyViewModel = ViewModelProvider(
            this,
            HistoryVMFactory(Repository(this))
        ).get(HistoryViewModel::class.java)
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
        })

        mainViewModel.historyData.observe(this, Observer { historyData ->
            compactcalendar_view.removeAllEvents()
            compactcalendar_view.addEvents(historyViewModel.getEvents(historyData))
            if (selectedDate != null) {
                mainViewModel.getDataByDay(selectedDate!!)
            }
        })
        mainViewModel.dayData.observe(this, Observer { dayData ->
            if (dayData.isEmpty()) {
                ivEdit.visibility = View.INVISIBLE
            } else {
                ivEdit.visibility = View.VISIBLE
            }
            adapter.setList(dayData)
        })

        historyViewModel.isAddItem.observe(this, Observer { isAdded ->
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
                btDelete.setBackgroundColor(getColor(android.R.color.darker_gray))
            } else {
                btDelete.setBackgroundColor(getColor(R.color.selected_day))
            }
        })
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
            mainViewModel.deleteHistoryData(adapter.getSelectedId().value!!)
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