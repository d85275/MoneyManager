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
import com.github.sundeepk.compactcalendarview.domain.Event


class MainViewModel(private val repository: Repository) : ViewModel() {
    val curPage = MutableLiveData(0)
    fun getChangeCallback() = object : ViewPager2.OnPageChangeCallback() {
        override fun onPageSelected(position: Int) {
            super.onPageSelected(position)
            curPage.value = position
        }
    }

    val recentBankData = MutableLiveData<List<HistoryData>>()
    val recentCashData = MutableLiveData<List<HistoryData>>()
    val dbErrorMsg = MutableLiveData<Int>()
    val bankList = MutableLiveData<List<BankData?>>()
    val curBank = MutableLiveData<BankData>()

    fun getBankColorPosition(color: Int?): Int {
        if (color == null) return 0
        val list = getBankColor()
        for (i in list.indices) {
            if (list[i] == color) return i
        }
        return 0
    }

    fun getBankColor() = arrayListOf(
        R.drawable.icon_bank_green,
        R.drawable.icon_card_light_green,
        R.drawable.icon_card_yellow,
        R.drawable.icon_bank_blue,
        R.drawable.icon_card_blue_2,
        R.drawable.icon_card_orange,
        R.drawable.icon_card_red,
        R.drawable.icon_card_purple,
        R.drawable.icon_card_black
    )

    fun getBankChangeCallback() = object : ViewPager2.OnPageChangeCallback() {
        override fun onPageSelected(position: Int) {
            super.onPageSelected(position)
            if (bankList.value == null) return
            curBank.value = bankList.value!![position]
        }
    }

    @SuppressLint("CheckResult")
    fun updateBank(bankData: BankData) {
        repository.updateBank(bankData).doOnComplete {
            loadBankListData()
            //todo change the data in history as well
        }.doOnError {
            Log.e("123", "add bank error, ${it.toString()}")
        }.subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({}, { dbErrorMsg.postValue(0) })
    }

    @SuppressLint("CheckResult")
    fun addBank(bankData: BankData) {
        repository.addBank(bankData).doOnComplete {
            loadBankListData()
        }.doOnError {
            Log.e("123", "add bank error, ${it.toString()}")
        }.subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({}, { dbErrorMsg.postValue(0) })
    }

    @SuppressLint("CheckResult")
    fun addItem(historyData: HistoryData, source: String) {
        repository.addHistory(historyData).doOnComplete {
            loadRecentHistoryData(source)
            loadTotalBalance()
        }.subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread()).subscribe(
                {}, { dbErrorMsg.postValue(1) }
            )
    }

    fun removeBank(bankData: BankData) {
        repository.removeBank(bankData).doOnComplete {
            loadBankListData()
        }.doOnError {
            Log.e("123", "remove bank error, ${it.toString()}")
        }.subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread()).subscribe()

        repository.deleteFromSource(bankData.name).doOnComplete {
        }.doOnError {
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

    val totalBalance = MutableLiveData<String>()
    val totalBalanceForBank = MutableLiveData<String>()
    fun loadTotalBalance() {
        val compositeDisposable = CompositeDisposable()
        compositeDisposable.add(
            repository.getHistoryData().subscribeOn(Schedulers.io()).observeOn(
                AndroidSchedulers.mainThread()
            ).doOnError { e -> Log.e("PP", "Error when getting saved records: $e") }
                .subscribe { list ->
                    getTotalBalance(list)
                }
        )
    }

    private fun getTotalBalance(alData: List<HistoryData>) {
        var total: Double = 0.0
        var totalBank: Double = 0.0
        for (i in alData.indices) {
            val data = alData[i]
            if (data.type == HistoryData.TYPE_EXPENSE) {
                total -= data.price
                if (data.source != HistoryData.SOURCE_CASH) {
                    totalBank -= data.price
                }
            } else {
                total += data.price
                if (data.source != HistoryData.SOURCE_CASH) {
                    totalBank += data.price
                }
            }
        }
        totalBalance.value = total.toString()
        totalBalanceForBank.value = totalBank.toString()
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
}