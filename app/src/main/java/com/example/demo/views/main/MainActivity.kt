package com.example.demo.views.main

import ZoomOutPageTransformer
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.example.demo.viewmodels.MainViewModel
import com.example.demo.R
import com.example.demo.Repository
import com.example.demo.model.HistoryData
import com.example.demo.utils.CommonUtils
import com.example.demo.viewmodels.MainVMFactory
import com.example.demo.views.main.bank_frag.BankFragment
import com.example.demo.views.main.cash_frag.CashFragment
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : FragmentActivity() {

    private lateinit var viewModel: MainViewModel
    private lateinit var viewPager: ViewPager2

    private companion object {
        private const val NUM_PAGES = 2
        private const val CASH = 0
        private const val BANK = 1

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initViewPager()
        getViewModel()
        setListener()
        initObservers()
    }

    override fun onResume() {
        super.onResume()
        loadRecentData()
    }

    private fun initViewPager() {
        viewPager = findViewById(R.id.pager)
        val pagerAdapter = ScreenSlidePagerAdapter(this)
        viewPager.adapter = pagerAdapter
        viewPager.setPageTransformer(ZoomOutPageTransformer())
    }

    private fun loadRecentData() {
        viewModel.loadBankListData()
        viewModel.loadRecentHistoryData(HistoryData.SOURCE_CASH)
        viewModel.loadHistoryData()
    }

    private fun initObservers() {
        viewModel.curPage.observe(this, Observer { curPage ->
            when (curPage) {
                CASH -> {
                    ivCashDot.setImageResource(R.drawable.dot_selected)
                    ivBankDot.setImageResource(R.drawable.dot_default)
                    ivSearch.setImageResource(R.drawable.search_cash)
                    ivHistory.setImageResource(R.drawable.history_cash)
                    vBackground.setBackgroundResource(R.drawable.cash_background)
                    llSearch.visibility = View.GONE
                }
                else -> {
                    ivCashDot.setImageResource(R.drawable.dot_default)
                    ivBankDot.setImageResource(R.drawable.dot_selected)
                    ivSearch.setImageResource(R.drawable.search_bank)
                    ivHistory.setImageResource(R.drawable.history_bank)
                    vBackground.setBackgroundResource(R.drawable.bank_background)
                    llSearch.visibility = View.GONE
                }
            }
        })
    }

    override fun onBackPressed() {
        /*
        if (viewPager.currentItem == 0) {
            // If the user is currently looking at the first step, allow the system to handle the
            // Back button. This calls finish() on this activity and pops the back stack.
            super.onBackPressed()
        } else {
            // Otherwise, select the previous step.
            viewPager.currentItem = viewPager.currentItem - 1
        }
         */
        var handled = false
        val list = supportFragmentManager.fragments
        for (i in list.indices) {
            if (list[i] is CashFragment && viewModel.curPage.value == CASH) {
                handled = (list[i] as CashFragment).onBackPressed()
                break
            } else if (list[i] is BankFragment && viewModel.curPage.value == BANK) {
                handled = (list[i] as BankFragment).onBackPressed()
                break
            }
        }
        if (!handled) {
            super.onBackPressed()
        }
    }

    fun hideIndicator() {
        llIndicator.visibility = View.INVISIBLE
        viewPager.isUserInputEnabled = false
    }

    fun showIndicator() {
        llIndicator.visibility = View.VISIBLE
        viewPager.isUserInputEnabled = true
    }

    private fun getViewModel() {
        viewModel =
            ViewModelProvider(this, MainVMFactory(Repository(this))).get(MainViewModel::class.java)
    }

    private fun setListener() {
        ivSearch.setOnClickListener {
            if (llSearch.visibility == View.GONE) {
                llSearch.visibility = View.VISIBLE
            } else {
                llSearch.visibility = View.GONE
            }
        }
        ivHistory.setOnClickListener {
            CommonUtils.goHistory(this, 0)
        }
        viewPager.registerOnPageChangeCallback(viewModel.getChangeCallback())

    }

    /**
     * A simple pager adapter that represents 5 ScreenSlidePageFragment objects, in
     * sequence.
     */
    private inner class ScreenSlidePagerAdapter(fa: FragmentActivity) : FragmentStateAdapter(fa) {
        override fun getItemCount(): Int =
            NUM_PAGES

        override fun createFragment(position: Int): Fragment {
            return when (position) {
                0 -> CashFragment()
                else -> BankFragment()
            }
        }
    }
}
