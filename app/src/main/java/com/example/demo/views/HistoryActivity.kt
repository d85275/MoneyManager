package com.example.demo.views

import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.animation.LayoutAnimationController
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.demo.R
import com.example.demo.viewmodels.HistoryViewModel
import com.google.android.material.appbar.AppBarLayout
import kotlinx.android.synthetic.main.activity_history.*

class HistoryActivity : AppCompatActivity() {
    private var isShow = true
    private var scrollRange = -1
    private lateinit var viewModel: HistoryViewModel
    private lateinit var adapter: HistoryAdapter

    private companion object {
        private const val PLAY_BAR_ANIM_DURATION = 300L
        private const val PLAY_BAR_ALPHA_SHOW = 1F
        private const val PLAY_BAR_ALPHA_HIDE = 0F
    }

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

        viewModel.historyData.observe(this, Observer { historyData ->
            adapter.setList(historyData)
        })
        viewModel.isAddItem.observe(this, Observer { isAdded ->
            Log.e("123", "add item. is added: $isAdded")
            if (isAdded) {
                Log.e("123", "position: ${adapter.itemCount}")
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
            Log.e("123", "on close clicked")
        }
        ivPreMonth.setOnClickListener { compactcalendar_view.scrollLeft() }
        ivNextMonth.setOnClickListener { compactcalendar_view.scrollRight() }
        btAdd.setOnClickListener {
            //viewModel.addItem(compactcalendar_view.firstDayOfCurrentMonth)
            //vAddItem.visibility = View.VISIBLE
            showAddItemView()
            vBlocker.visibility = View.VISIBLE
            hideAddBtn()
            //btAdd.visibility = View.GONE
        }
        vAddItem.findViewById<Button>(R.id.btCancel).setOnClickListener {
            //vAddItem.visibility = View.GONE
            hideAddItemView()
            vBlocker.visibility = View.GONE
            showAddBtn()
            //btAdd.visibility = View.VISIBLE
        }
        vAddItem.findViewById<Button>(R.id.btConfirm).setOnClickListener {
            //vAddItem.visibility = View.GONE
            hideAddItemView()
            vBlocker.visibility = View.GONE
            showAddBtn()
            //btAdd.visibility = View.VISIBLE
        }
    }

    private fun hideAddBtn() {
        val x = btAdd.width.toFloat()
        btAdd.animate()
            .translationX(x + 40)
            .setDuration(PLAY_BAR_ANIM_DURATION)
            .start()
    }

    private fun showAddBtn() {
        val x = btAdd.width.toFloat()
        btAdd.animate()
            .translationX(0 - 40f)
            .setDuration(PLAY_BAR_ANIM_DURATION)
            .start()
    }

    private fun hideAddItemView() {
        val y = vAddItem.height.toFloat()
        vAddItem.animate()
            .translationY(y)
            .alpha(PLAY_BAR_ALPHA_HIDE)
            .setDuration(PLAY_BAR_ANIM_DURATION)
            .start()
    }

    private fun showAddItemView() {
        if (vAddItem.visibility != View.VISIBLE) {
            val y = vAddItem.height.toFloat()
            vAddItem.animate()
                .translationY(y)
                .alpha(PLAY_BAR_ALPHA_HIDE)
                .setDuration(200)
                .withEndAction {
                    vAddItem.visibility = View.VISIBLE

                    vAddItem.animate()
                        .translationY(0f)
                        .setDuration(PLAY_BAR_ANIM_DURATION)
                        .alpha(PLAY_BAR_ALPHA_SHOW)
                        .start()
                }
                .start()
            //playBar.visibility = View.VISIBLE
        } else {
            vAddItem.animate()
                .translationY(0f)
                .setDuration(PLAY_BAR_ANIM_DURATION)
                .alpha(PLAY_BAR_ALPHA_SHOW)
                .start()
        }
    }
}