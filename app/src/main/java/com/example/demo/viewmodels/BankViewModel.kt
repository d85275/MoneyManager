package com.example.demo.viewmodels

import android.annotation.SuppressLint
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.viewpager2.widget.ViewPager2
import com.example.demo.Repository
import com.example.demo.model.BankData
import com.example.demo.model.HistoryData
import com.example.demo.model.HistoryData.Companion.TYPE_EXPENSE
import com.example.demo.model.HistoryData.Companion.TYPE_INCOME
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlin.collections.ArrayList

class BankViewModel(private val repository: Repository) : ViewModel() {
    val recentData = MutableLiveData<List<HistoryData>>()
    val bankData = MutableLiveData<List<BankData?>>()

    val curBank = MutableLiveData<BankData>()
    fun getChangeCallback() = object : ViewPager2.OnPageChangeCallback() {
        override fun onPageSelected(position: Int) {
            super.onPageSelected(position)
            if (bankData.value == null) return
            curBank.value = bankData.value!![position]
        }
    }

    fun addBank(bankData: BankData) {
        repository.addBank(bankData).doOnComplete {
            loadBankData()
        }.doOnError {
            Log.e("123", "add bank error, ${it.toString()}")
        }.subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread()).subscribe()
    }

    fun removeBank(bankData: BankData) {
        repository.removeBank(bankData).doOnComplete {
            loadBankData()
        }.doOnError {
            Log.e("123", "remove bank error, ${it.toString()}")
        }.subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread()).subscribe()
    }

    fun loadBankData() {
        val compositeDisposable = CompositeDisposable()
        compositeDisposable.add(
            repository.getBank().subscribeOn(Schedulers.io()).observeOn(
                AndroidSchedulers.mainThread()
            ).doOnError { e -> Log.e("PP", "Error when getting saved records: $e") }
                .subscribe { list ->
                    list.reversed()
                    (list as ArrayList).add(null)
                    bankData.postValue(list)
                }
        )
    }

    fun getBankData(curBank: BankData) {

    }

    fun loadRecentData() {
        val list = arrayListOf<HistoryData>()
        val data1 = HistoryData.create("薪水", TYPE_INCOME, 32000, "2020-10/05")
        list.add(data1)
        val data2 = HistoryData.create("提款", TYPE_EXPENSE, 2000, "2020-09/30")
        list.add(data2)
        val data3 = HistoryData.create("ＶＳ簽帳消費", TYPE_EXPENSE, 499, "2020-09/23")
        list.add(data3)
        val data4 = HistoryData.create("提款", TYPE_EXPENSE, 1000, "2020-09/30")
        list.add(data4)
        recentData.value = list
    }


    fun getTransformer() = ViewPager2.PageTransformer { page, position ->
        val myOffset: Float = position * -(2 * 30)
        when {
            position < -1 -> {
                page.translationY = -myOffset
            }
            position <= 1 -> {
                //val scaleFactor = max(0.7f, 1 - abs(position - 0.14285715f))
                page.translationY = myOffset
                //page.alpha = scaleFactor
            }
            else -> {
                page.translationY = myOffset
                //page.alpha = 0f
            }
        }
    }
}