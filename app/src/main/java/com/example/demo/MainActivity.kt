package com.example.demo

import ZoomOutPageTransformer
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : FragmentActivity() {


    private lateinit var viewModel:MainViewModel
    private lateinit var viewPager: ViewPager2
    //private lateinit var navController:NavController
    private companion object{
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
    }

    override fun onResume() {
        super.onResume()
        //navController.addOnDestinationChangedListener(listener)
    }

    override fun onPause() {
        super.onPause()
        //navController.removeOnDestinationChangedListener(listener)
    }

    private fun initViewPager(){
        viewPager = findViewById(R.id.pager)
        val pagerAdapter = ScreenSlidePagerAdapter(this)
        viewPager.adapter = pagerAdapter
        viewPager.setPageTransformer(ZoomOutPageTransformer())
    }


    override fun onBackPressed() {
        if (viewPager.currentItem == 0) {
            // If the user is currently looking at the first step, allow the system to handle the
            // Back button. This calls finish() on this activity and pops the back stack.
            super.onBackPressed()
        } else {
            // Otherwise, select the previous step.
            viewPager.currentItem = viewPager.currentItem - 1
        }
    }

        private fun getViewModel() {
            viewModel = ViewModelProvider(this).get(MainViewModel::class.java)
        }

        private fun setListener() {
            Log.e("123", "set listener")
            ivSearch.setOnClickListener {
                Log.e("main", "on search clicked")
                if (llSearch.visibility == View.GONE) {
                    llSearch.visibility = View.VISIBLE
                } else {
                    llSearch.visibility = View.GONE
                }
            }
        }
    /**
     * A simple pager adapter that represents 5 ScreenSlidePageFragment objects, in
     * sequence.
     */
    private inner class ScreenSlidePagerAdapter(fa: FragmentActivity) : FragmentStateAdapter(fa) {
        override fun getItemCount(): Int = NUM_PAGES

        override fun createFragment(position: Int): Fragment {
            return when(position){
                0-> CashFragment()
                else->BankFragment()
            }
        }
    }
}
