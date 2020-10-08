package com.example.demo.viewmodels

import android.annotation.SuppressLint
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.viewpager2.widget.ViewPager2
import com.example.demo.R
import com.example.demo.Repository
import com.example.demo.model.BankData
import com.example.demo.model.HistoryData
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

class MainViewModel(private val repository: Repository) : ViewModel() {
    val curPage = MutableLiveData(0)
    fun getChangeCallback() = object : ViewPager2.OnPageChangeCallback() {
        override fun onPageSelected(position: Int) {
            super.onPageSelected(position)
            curPage.value = position
        }
    }

    // cash fragment
    val recentCashData = MutableLiveData<List<HistoryData>>()

    /*
    fun loadRecentCashData() {
        val compositeDisposable = CompositeDisposable()
        compositeDisposable.add(
            repository.getRecentCashData().subscribeOn(Schedulers.io()).observeOn(
                AndroidSchedulers.mainThread()
            ).doOnError { e -> Log.e("PP", "Error when getting saved records: $e") }
                .subscribe { list ->
                    list.reversed()
                    recentCashData.postValue(list)
                }
        )
    }
    */
    /*
    fun loadRecentCashData() {
        val list = arrayListOf<HistoryData>()
        val data1 = HistoryData.create("薪水", HistoryData.TYPE_INCOME, 32000, "2020-10/05")
        list.add(data1)
        val data2 = HistoryData.create("海鮮麵", HistoryData.TYPE_EXPENSE, 190, "2020-09/30")
        list.add(data2)
        val data3 = HistoryData.create("ＶＳ簽帳消費", HistoryData.TYPE_EXPENSE, 499, "2020-09/23")
        list.add(data3)
        val data4 = HistoryData.create("瓦斯費", HistoryData.TYPE_EXPENSE, 650, "2020-09/30")
        list.add(data4)
        recentCashData.value = list
    }
     */

    // bank fragment
    val recentBankData = MutableLiveData<List<HistoryData>>()
    val bankList = MutableLiveData<List<BankData?>>()
    val curBank = MutableLiveData<BankData>()

    fun getBankColor() = arrayListOf(
        R.drawable.icon_bank_green,
        R.drawable.icon_bank_blue,
        R.drawable.icon_card_blue_2,
        R.drawable.icon_card_red,
        R.drawable.icon_card_purple
    )

    fun getBankChangeCallback() = object : ViewPager2.OnPageChangeCallback() {
        override fun onPageSelected(position: Int) {
            super.onPageSelected(position)
            if (bankList.value == null) return
            curBank.value = bankList.value!![position]
        }
    }

    val dbErrorMsg = MutableLiveData<String>()

    @SuppressLint("CheckResult")
    fun addBank(bankData: BankData) {
        repository.addBank(bankData).doOnComplete {
            loadBankListData()
        }.doOnError {
            Log.e("123", "add bank error, ${it.toString()}")
        }.subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({}, { dbErrorMsg.postValue("Error when adding banks")})
    }

    @SuppressLint("CheckResult")
    fun addItem(historyData: HistoryData, source: String) {
        repository.addHistory(historyData).doOnComplete {
            loadRecentHistoryData(source)
        }.subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread()).subscribe(
                {}, { dbErrorMsg.postValue("Error when adding items") }
            )
    }

    fun removeBank(bankData: BankData) {
        repository.removeBank(bankData).doOnComplete {
            loadBankListData()
        }.doOnError {
            Log.e("123", "remove bank error, ${it.toString()}")
        }.subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread()).subscribe()
    }

    fun loadBankListData() {
        val compositeDisposable = CompositeDisposable()
        compositeDisposable.add(
            repository.getBank().subscribeOn(Schedulers.io()).observeOn(
                AndroidSchedulers.mainThread()
            ).doOnError { e -> Log.e("PP", "Error when getting saved records: $e") }
                .subscribe { list ->
                    list.reversed()
                    (list as ArrayList).add(null)
                    bankList.postValue(list)
                }
        )
    }

    fun loadRecentHistoryData(source: String) {
        val compositeDisposable = CompositeDisposable()
        compositeDisposable.add(
            repository.getRecentHistoryData(source).subscribeOn(Schedulers.io()).observeOn(
                AndroidSchedulers.mainThread()
            ).doOnError { e -> Log.e("PP", "Error when getting saved records: $e") }
                .subscribe { list ->
                    list.reversed()
                    if (source == HistoryData.SOURCE_CASH) {
                        recentCashData.postValue(list)
                    } else {
                        recentBankData.postValue(list)
                    }
                }
        )
    }

    fun getBankData(curBank: BankData) {

    }

    /*
    fun loadRecentBankData() {
        val list = arrayListOf<HistoryData>()
        val data1 = HistoryData.create("薪水", HistoryData.TYPE_INCOME, 32000, "2020-10/05")
        list.add(data1)
        val data2 = HistoryData.create("提款", HistoryData.TYPE_EXPENSE, 2000, "2020-09/30")
        list.add(data2)
        val data3 = HistoryData.create("ＶＳ簽帳消費", HistoryData.TYPE_EXPENSE, 499, "2020-09/23")
        list.add(data3)
        val data4 = HistoryData.create("提款", HistoryData.TYPE_EXPENSE, 1000, "2020-09/30")
        list.add(data4)
        recentData.value = list
    }
     */
}