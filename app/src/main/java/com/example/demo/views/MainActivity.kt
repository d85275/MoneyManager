package com.example.demo.views

import ZoomOutPageTransformer
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.example.demo.viewmodels.MainViewModel
import com.example.demo.R
import com.example.demo.utils.CommonUtils
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_cash.*

class MainActivity : FragmentActivity() {


    private lateinit var viewModel: MainViewModel
    private lateinit var viewPager: ViewPager2

    //private lateinit var navController:NavController
    private companion object {
        private const val NUM_PAGES = 2
    }

    private val listener = NavController.OnDestinationChangedListener { _, destination, _ ->
        // react on change
        // you can check destination.id or destination.label and act based on that
        viewModel.setDesId(destination.id)
        invalidateOptionsMenu()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initViewPager()
        getViewModel()
        //navController = findNavController(R.id.nav_host_fragment)
        setListener()
        initObservers()
    }

    override fun onDestroy() {
        super.onDestroy()
        viewPager.unregisterOnPageChangeCallback(viewModel.getChangeCallback())
    }

    private fun initViewPager() {
        viewPager = findViewById(R.id.pager)
        val pagerAdapter = ScreenSlidePagerAdapter(this)
        viewPager.adapter = pagerAdapter
        viewPager.setPageTransformer(ZoomOutPageTransformer())
    }

    private fun initObservers() {
        viewModel.curPage.observe(this, Observer { curPage ->
            when (curPage) {
                0 -> {
                    ivCashDot.setImageResource(R.drawable.dot_selected)
                    ivBankDot.setImageResource(R.drawable.dot_default)
                }
                else -> {
                    ivCashDot.setImageResource(R.drawable.dot_default)
                    ivBankDot.setImageResource(R.drawable.dot_selected)
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
            if (list[i] is CashFragment) {
                handled = (list[i] as CashFragment).onBackPressed()
                break
            }
        }
        if (!handled) {
            super.onBackPressed()
        }
    }

    private fun getViewModel() {
        viewModel = ViewModelProvider(this).get(MainViewModel::class.java)
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
