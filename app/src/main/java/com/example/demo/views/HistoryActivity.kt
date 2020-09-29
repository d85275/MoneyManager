package com.example.demo.views

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_history)
        setSupportActionBar(toolbar)
        getViewModel()
        setListeners()
        initViews()
        initObservers()
    }

    private fun initViews() {
        collapsingToolbar.title = " "
        tvCurrentDate.text = viewModel.getDate(compactcalendar_view.firstDayOfCurrentMonth)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter =
            SimpleRecyclerAdapter((1..30).map { "Item $it" })
        recyclerView.addItemDecoration(DividerItemDecoration(this, DividerItemDecoration.VERTICAL))
    }

    private fun getViewModel() {
        viewModel = ViewModelProvider(this).get(HistoryViewModel::class.java)
    }

    private fun initObservers() {
        viewModel.selectedDay.observe(this, androidx.lifecycle.Observer { day ->
            val selectedDay = viewModel.getDay(day)
            Log.e(javaClass.name, "selected day: $selectedDay")
        })

        viewModel.selectedMonth.observe(this, androidx.lifecycle.Observer { firstDayOfMonth ->
            tvCurrentDate.text = viewModel.getDate(firstDayOfMonth)
        })
    }

    private fun setListeners() {
        Log.e("his","set listeners")
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

        ivPreMonth.setOnClickListener {
            compactcalendar_view.scrollLeft() }
        ivNextMonth.setOnClickListener { compactcalendar_view.scrollRight() }
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