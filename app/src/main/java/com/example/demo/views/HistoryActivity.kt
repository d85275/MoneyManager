package com.example.demo.views

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.demo.R
import com.example.demo.viewmodels.HistoryViewModel
import com.google.android.material.appbar.AppBarLayout
import kotlinx.android.synthetic.main.activity_history.*

class HistoryActivity : AppCompatActivity() {
    private var isShow = true
    private var scrollRange = -1
    private lateinit var viewModel: HistoryViewModel
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

    private fun initViews() {
        collapsingToolbar.title = " "
        adapter = HistoryAdapter()
        tvCurrentDate.text = viewModel.getDate(compactcalendar_view.firstDayOfCurrentMonth)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.setHasFixedSize(true)
        recyclerView.adapter = adapter
        recyclerView.addItemDecoration(DividerItemDecoration(this, DividerItemDecoration.VERTICAL))
    }

    private fun getViewModel() {
        viewModel = ViewModelProvider(this).get(HistoryViewModel::class.java)
    }

    private fun initObservers() {
        viewModel.selectedDay.observe(this, Observer { day ->
            val selectedDay = viewModel.getDay(day)
        })

        viewModel.selectedMonth.observe(this, Observer { firstDayOfMonth ->
            tvCurrentDate.text = viewModel.getDate(firstDayOfMonth)
        })

        viewModel.historyData.observe(this, Observer {  historyData->
            adapter.setList(historyData)
        })
        viewModel.isAddItem.observe(this, Observer { isAdded->
            Log.e("123","add item. is added: $isAdded")
            if (isAdded){
                Log.e("123","position: ${adapter.itemCount}")
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
                tvCurrentDate.setTextColor(Color.BLACK)
                ivPreMonth.setImageResource(R.drawable.pre_arrow_black)
                ivNextMonth.setImageResource(R.drawable.next_arrow_black)
                isShow = true
            } else if (isShow) {
                tvCurrentDate.setTextColor(Color.WHITE)
                ivPreMonth.setImageResource(R.drawable.pre_arrow_white)
                ivNextMonth.setImageResource(R.drawable.next_arrow_white)
                isShow = false
            }
        })

        ivPreMonth.setOnClickListener { compactcalendar_view.scrollLeft() }
        ivNextMonth.setOnClickListener { compactcalendar_view.scrollRight() }
        btAdd.setOnClickListener {
            viewModel.addItem(compactcalendar_view.firstDayOfCurrentMonth)
        }
    }
}