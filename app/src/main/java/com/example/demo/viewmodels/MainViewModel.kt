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
import com.example.demo.utils.CommonUtils
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


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
    val curBankPosition = MutableLiveData(-1)

    fun getCurrentBank(): BankData? {
        if (curBankPosition.value == null || curBankPosition.value == -1) {
            return null
        }
        val bankList = this.bankList.value
        val curPosition = curBankPosition.value!!
        if (bankList == null || bankList.size <= curPosition) {
            return null
        }
        return bankList[curPosition]
    }

    fun getBankColorPosition(color: Int?): Int {
        if (color == null) return 0
        val list = getBankColor()
        for (i in list.indices) {
            if (list[i] == color) return i
        }
        return 0
    }

    fun getAddItemIconPosition(icon: Int?): Int {
        if (icon == null) return 0
        val list = getIconList()
        for (i in list.indices) {
            if (list[i] == icon) return i
        }
        return 0
    }

    fun getSourcePosition(source: String): Int {
        val list = getSourceList(arrayListOf())
        if (!list.contains(source)) return 0
        for (i in list.indices) {
            if (source == list[i]) {
                return i + 1
            }
        }
        return 0
    }

    fun getSourceList(list: ArrayList<String>): ArrayList<String> {
        val bankList = bankList.value
        if (!bankList.isNullOrEmpty()) {
            for (i in bankList.indices) {
                val name = bankList[i]?.name ?: continue
                list.add(name)
            }
        }
        return list
    }

    fun getIconPosition(icon: Int): Int {
        val list = getIconList()
        for (i in list.indices) {
            if (icon == list[i]) return i
        }
        return 0
    }

    fun getIconList() = arrayListOf(
        R.drawable.icon_restaurant,
        R.drawable.icon_fastfood,
        R.drawable.icon_commute,
        R.drawable.icon_fitness,
        R.drawable.icon_flight,
        R.drawable.icon_hotel,
        R.drawable.icon_grocery,
        R.drawable.icon_gas,
        R.drawable.icon_school,
        R.drawable.icon_viedo_game,
        R.drawable.icon_bar,
        R.drawable.icon_cafe,
        R.drawable.icon_music
    )

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
            //curBank.value = bankList.value!![position]
            curBankPosition.value = position
        }
    }

    @SuppressLint("CheckResult")
    fun updateBank(bankData: BankData, oldName: String) {
        repository.updateSourceName(bankData.name, oldName).subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread()).subscribe()

        repository.updateBank(bankData).doOnComplete {
            loadBankListData()
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
            loadHistoryData()
            loadTotalBalance()
        }.subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread()).subscribe(
                {}, { dbErrorMsg.postValue(1) }
            )
    }

    fun updateItem(historyData: HistoryData) {
        repository.updateHistory(historyData).doOnComplete {
            loadRecentHistoryData(HistoryData.SOURCE_CASH)
            if (getCurrentBank() != null) {
                loadRecentHistoryData(getCurrentBank()!!.name)
            }
            loadHistoryData()
            loadTotalBalance()
        }.subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread()).subscribe()
    }

    val historyData = MutableLiveData<List<HistoryData>>()
    val dayData = MutableLiveData<List<HistoryData>>()
    val monthData = MutableLiveData<List<HistoryData>>()

    fun getDataByMonth(date: Date) {
        if (historyData.value == null) return
        val dateFormat = SimpleDateFormat("yyyy/MM", Locale.getDefault())
        val curMonth = dateFormat.format(date)
        monthData.value = historyData.value!!.filter { it.date.startsWith(curMonth) }
    }

    fun getDataByDay(date: Date) {
        if (historyData.value == null) return
        val curDate = CommonUtils.addItemDate().format(date)
        val list = historyData.value!!.filter { it.date == curDate }
        dayData.value = list
    }

    fun loadHistoryData() {
        val compositeDisposable = CompositeDisposable()
        compositeDisposable.add(
            repository.getHistoryData().subscribeOn(Schedulers.io()).observeOn(
                AndroidSchedulers.mainThread()
            ).doOnError { e -> Log.e("PP", "Error when getting saved records: $e") }
                .subscribe { list ->
                    list.reversed()
                    historyData.postValue(list)
                }
        )
    }

    fun deleteHistoryData(selectedId: Set<Int>) {
        val deletedData = arrayListOf<Int>()
        for (key in selectedId) {
            deletedData.add(dayData.value!![key].id)
        }
        repository.deleteFromIds(deletedData).doOnComplete {
            isDeleteCompleted.postValue(true)
            loadHistoryData()
        }.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).doOnError {
            CommonUtils.e("error: $it")
        }
            .subscribe()
    }

    val isDeleteCompleted = MutableLiveData(false)

    fun removeBank(bankData: BankData) {
        repository.removeBank(bankData).doOnComplete {
            loadBankListData()
            loadTotalBalance()
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

    val totalBalance = MutableLiveData<String>("0.0")
    val totalBalanceForBank = MutableLiveData<String>("0.0")
    val dailyBalance = MutableLiveData<String>("0.0")
    val monthlyBalance = MutableLiveData<String>("0.0")
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

    fun getMonthlyBalance(alData: List<HistoryData>) {
        monthlyBalance.value = getBalance(alData)
    }

    fun getDailyBalance(alData: List<HistoryData>) {
        dailyBalance.value = getBalance(alData)
    }

    private fun getBalance(alData: List<HistoryData>): String {
        var total: Double = 0.0
        for (i in alData.indices) {
            val data = alData[i]
            if (data.type == HistoryData.TYPE_EXPENSE) {
                total -= data.price
            } else {
                total += data.price
            }
        }
        return total.toString()
    }

    fun loadRecentHistoryData(source: String?) {
        if (source == null) return
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