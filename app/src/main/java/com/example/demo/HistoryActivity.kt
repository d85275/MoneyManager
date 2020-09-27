package com.example.demo

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.github.sundeepk.compactcalendarview.CompactCalendarView.CompactCalendarViewListener
import com.google.android.material.appbar.AppBarLayout
import kotlinx.android.synthetic.main.activity_history.*
import java.text.SimpleDateFormat
import java.util.*

class HistoryActivity : AppCompatActivity() {
    private val dateFormatForMonth: SimpleDateFormat =
        SimpleDateFormat("MMM - yyyy", Locale.getDefault())
    private var isShow = true
    private var scrollRange = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_history)
        setSupportActionBar(toolbar)
        tvCurrentDate.text = dateFormatForMonth.format(compactcalendar_view.firstDayOfCurrentMonth)
        setListeners()
        appbar.addOnOffsetChangedListener(AppBarLayout.OnOffsetChangedListener { barLayout, verticalOffset ->
            if (scrollRange == -1) {
                scrollRange = barLayout?.totalScrollRange!!
            }
            if (scrollRange + verticalOffset == 0) {
                collapsingToolbar.title =
                    dateFormatForMonth.format(compactcalendar_view.firstDayOfCurrentMonth)
                isShow = true
            } else if (isShow) {
                collapsingToolbar.title =
                    " " //careful there should a space between double quote otherwise it wont work
                isShow = false
            }
        })
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = SimpleRecyclerAdapter((1..30).map { "Item $it" })
        recyclerView.addItemDecoration(DividerItemDecoration(this, DividerItemDecoration.VERTICAL))
    }

    private fun setListeners() {
        compactcalendar_view.setListener(object : CompactCalendarViewListener {
            override fun onDayClick(dateClicked: Date?) {
                tvCurrentDate.text = dateFormatForMonth.format(dateClicked)
            }

            override fun onMonthScroll(firstDayOfNewMonth: Date?) {
                tvCurrentDate.text = dateFormatForMonth.format(firstDayOfNewMonth)
            }
        })
    }


}

class SimpleRecyclerAdapter(private val items: List<String>) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ViewHolder(parent)

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