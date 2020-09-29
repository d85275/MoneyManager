package com.example.demo.views

import android.os.Bundle
import android.util.Log
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.observe
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.demo.R
import com.example.demo.viewmodels.HistoryViewModel
import com.google.android.material.appbar.AppBarLayout
import kotlinx.android.synthetic.main.activity_history.*
import java.text.SimpleDateFormat
import java.util.*

class HistoryActivity : AppCompatActivity() {
    private var isShow = true
    private var scrollRange = -1
    private lateinit var viewModel: HistoryViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_history)
        setSupportActionBar(toolbar)
        //tvCurrentDate.text = dateFormatForMonth.format(compactcalendar_view.firstDayOfCurrentMonth)
        setListeners()
        initViews()
        getViewModel()
        initObservers()
    }

    private fun initViews(){
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter =
            SimpleRecyclerAdapter((1..30).map { "Item $it" })
        recyclerView.addItemDecoration(DividerItemDecoration(this, DividerItemDecoration.VERTICAL))
    }

    private fun getViewModel(){
        viewModel = ViewModelProvider(this).get(HistoryViewModel::class.java)
    }

    private fun initObservers(){
        viewModel.selectedDay.observe(this, androidx.lifecycle.Observer { day->
            val selectedDay = viewModel.getDay(day)
            Log.e(javaClass.name,"selected day: $selectedDay")
        })

        viewModel.selectedMonth.observe(this, androidx.lifecycle.Observer { firstDayOfMonth->
            tvCurrentDate.text = viewModel.getDate(firstDayOfMonth)
        })
    }

    private fun setListeners() {
        compactcalendar_view.setListener(viewModel.getCalendarListener())
        appbar.addOnOffsetChangedListener(AppBarLayout.OnOffsetChangedListener { barLayout, verticalOffset ->
            if (scrollRange == -1) {
                scrollRange = barLayout?.totalScrollRange!!
            }
            if (scrollRange + verticalOffset == 0) {
                collapsingToolbar.title =viewModel.getDate(compactcalendar_view.firstDayOfCurrentMonth)
                isShow = true
            } else if (isShow) {
                collapsingToolbar.title =
                    " " //careful there should a space between double quote otherwise it wont work
                isShow = false
            }
        })
    }


}

class SimpleRecyclerAdapter(private val items: List<String>) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        ViewHolder(parent)

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as ViewHolder).bind(items[position])
    }

    override fun getItemCount() = items.size

    class ViewHolder(parent: ViewGroup) : RecyclerView.ViewHolder(
        TextView(parent.context).apply {
            val p = 16
            setPadding(p, p, p, p)
        }) {
        fun bind(item: String) = with(itemView as TextView) {
            text = item
        }
    }
}