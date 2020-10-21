package com.example.demo.views

import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.demo.R
import com.example.demo.model.HistoryData
import com.example.demo.model.Variable
import com.example.demo.utils.CommonUtils
import com.example.demo.utils.OnSwipeTouchListener
import com.example.demo.viewmodels.MainViewModel
import com.example.demo.views.main.MainActivity
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.view_add_item.view.*
import java.lang.StringBuilder
import java.text.DecimalFormat
import java.text.NumberFormat
import java.util.*

class AddItemView(context: Context, attrs: AttributeSet?) : LinearLayout(context, attrs) {

    private var view: View =
        LayoutInflater.from(context).inflate(R.layout.view_add_item, this, true)
    private var mainActivity: MainActivity? = null
    private lateinit var mainViewModel: MainViewModel
    private lateinit var iconAdapter: AddItemIconAdapter
    private var sourceAdapter =
        AddItemSourceAdapter(arrayListOf(HistoryData.SOURCE_CASH))
    private var total = 0L
    private var isShow = MutableLiveData(false)
    private var isKeyboardShow = false

    private var curMode = MODE_ADD
    private val curSource = Variable(HistoryData.SOURCE_CASH)
    private var curType = HistoryData.TYPE_EXPENSE
    private var curIconPosition = 0
    private var resumeData: HistoryData? = null
    private val disposableSource: Disposable

    private companion object {
        private const val ADD_ANIM_DURATION = 300L
        private const val ALPHA_SHOW = 1F
        private const val ALPHA_HIDE = 0F
        private const val DOT = 10
        private const val BACK = 12
        private const val ZERO = 11
        private const val MODE_ADD = 0
        private const val MODE_EDIT = 1
        private const val PREVIOUS_DAY = -1
        private const val NEXT_DAY = 1
    }

    private var date: Date? = null

    init {
        setDate(Calendar.getInstance().time)
        setListeners()
        disposableSource = curSource.observable.subscribe { source ->
            if (source == HistoryData.SOURCE_CASH) {
                tvSource.text = view.context.getString(R.string.cash)
            } else {
                tvSource.text = source
            }
            sourceAdapter.setSelectedSource(source)
        }
    }

    fun init(source: String, mainViewModel: MainViewModel, mainActivity: MainActivity) {
        curSource.value = source
        this.mainViewModel = mainViewModel
        this.mainActivity = mainActivity
        initViews()
    }

    fun init(mainViewModel: MainViewModel, mainActivity: MainActivity) {
        this.mainViewModel = mainViewModel
        this.mainActivity = mainActivity
        initViews()
    }

    fun init(mainViewModel: MainViewModel) {
        this.mainViewModel = mainViewModel
        initViews()
    }

    private fun initViews() {
        iconAdapter = AddItemIconAdapter(mainViewModel.getIconList())
        ivIcon.setImageResource(mainViewModel.getIconList()[0])
        iconAdapter.setOnItemClickListener { icon ->
            ivIcon.setImageResource(icon)
            curIconPosition = mainViewModel.getIconPosition(icon)
        }
        setRecyclerView(rvIcons, iconAdapter)

        sourceAdapter.setOnItemClickListener { source ->
            curSource.value = source
        }

        setRecyclerView(rvSource, sourceAdapter)

    }

    private fun setRecyclerView(
        recyclerView: RecyclerView,
        adapter: RecyclerView.Adapter<RecyclerView.ViewHolder>
    ) {
        recyclerView.layoutManager =
            LinearLayoutManager(view.context, LinearLayoutManager.HORIZONTAL, false)
        recyclerView.setHasFixedSize(true)
        recyclerView.adapter = adapter
    }

    fun updateSourceList() {
        sourceAdapter.setSourceList(mainViewModel.getSourceList(arrayListOf(HistoryData.SOURCE_CASH)))
    }

    fun setSource(source: String) {
        curSource.value = source
    }

    fun setDate(date: Date?) {
        this.date = date
        if (date != null) tvAddItemDate.text = CommonUtils.addItemDate().format(date)
    }

    fun getTotal(): Long {
        return total
    }

    /**
     * amount: -1 go previous day, 1 go next day
     */

    private fun goDay(amount: Int) {
        if (date == null) return
        val calendar = Calendar.getInstance()
        calendar.time = date!!
        calendar.add(Calendar.DAY_OF_YEAR, amount)
        setDate(calendar.time)
    }

    private fun setListeners() {
        ivPreDay.setOnClickListener { goDay(PREVIOUS_DAY) }
        ivNextDay.setOnClickListener { goDay(NEXT_DAY) }

        etName.setOnClickListener {
            dismissKeyboard()
        }
        tvPrice.setOnClickListener {
            etName.clearFocus()
            if (isKeyboardShow) {
                dismissKeyboard()
            } else {
                showKeyboard()
            }
        }
        btConfirm.setOnClickListener {
            addItem()
        }

        btCancel.setOnClickListener {
            dismiss()
        }
        llIcon.setOnClickListener {
            dismissKeyboard()
            if (elIcons.isExpanded) {
                elIcons.collapse()
            } else {
                elIcons.expand()
                rvIcons.scrollToPosition(curIconPosition)
            }
        }
        llSource.setOnClickListener {
            dismissKeyboard()
            if (elSources.isExpanded) {
                elSources.collapse()
            } else {
                elSources.expand()
                rvSource.smoothScrollToPosition(mainViewModel.getSourcePosition(curSource.value))
            }
        }

        view.setOnTouchListener(object : OnSwipeTouchListener(context) {
            override fun onSwipeBottom() {
                super.onSwipeBottom()
                dismiss()
            }
        })
        setKeyboardListener()
        btIncome.setOnClickListener {
            dismissKeyboard()
            curType = HistoryData.TYPE_INCOME
            setTypeBackground(btIncome, btExpense)
        }
        btExpense.setOnClickListener {
            dismissKeyboard()
            curType = HistoryData.TYPE_EXPENSE
            setTypeBackground(btExpense, btIncome)
        }
    }

    private fun setTypeBackground(vSelected: View, vNotSelected: View) {
        vSelected.setBackgroundResource(R.drawable.rounded_bg_type_selected)
        vNotSelected.setBackgroundResource(R.drawable.rounded_bg_type_not_selected)
        (vSelected as TextView).setTextColor(Color.WHITE)
        (vNotSelected as TextView).setTextColor(Color.DKGRAY)
    }

    private fun setKeyboardListener() {
        val count = vKeyboard.childCount
        for (i in 0 until count) {
            vKeyboard.getChildAt(i).setOnClickListener {
                val num = i + 1
                val tmp = tvPrice.text.toString()

                val sb = StringBuilder(tmp)
                when {
                    num < 10 || num == ZERO || num == DOT -> {
                        setPrice(sb, num)
                    }
                    num == BACK -> {
                        if (tmp.isEmpty()) return@setOnClickListener
                        tvPrice.text = sb.deleteCharAt(tmp.lastIndex)
                    }
                }
                //if (total > 0) tvPrice.text = formatter.format(total)
                //else tvPrice.text = "請輸入價格"
            }
        }
    }

    private fun addItem() {
        if (!isInputValid()) {
            return
        }
        val name = etName.text.toString().trim()
        val price = tvPrice.text.toString().trim().replace(",", "").toDouble()
        val type = curType
        val source = this.curSource.value
        val date = tvAddItemDate.text.toString()
        val iconPosition = iconAdapter.getSelectedPosition()
        if (curMode == MODE_ADD) {
            val historyData = HistoryData.create(name, type, price, date, source, iconPosition)
            mainViewModel.addItem(historyData, source)
        } else if (resumeData != null) {
            resumeData!!.name = name
            resumeData!!.price = price
            resumeData!!.type = type
            resumeData!!.source = source
            resumeData!!.date = date
            resumeData!!.iconPosition = iconPosition
            mainViewModel.updateItem(resumeData!!)
            dismiss()
        }
        clearData()
    }

    private fun isInputValid(): Boolean {
        val name = etName.text.toString().trim()
        if (name.isEmpty()) {
            return false
        }
        val price: String = tvPrice.text.toString().trim()
        if (price.isEmpty()) {
            return false
        }
        return true
    }

    fun resumeData(historyData: HistoryData) {
        //tvAddItemDate.text = historyData.date
        curMode = MODE_EDIT
        resumeData = historyData
        //curSource = historyData.source
        curSource.value = historyData.source
        curType = historyData.type
        if (curType == HistoryData.TYPE_INCOME) {
            setTypeBackground(btIncome, btExpense)
        }
        setDate(CommonUtils.addItemDate().parse(historyData.date))
        tvPrice.text = formatter.format(historyData.price)
        etName.setText(historyData.name)
        ivIcon.setImageResource(mainViewModel.getIconList()[historyData.iconPosition])
        curIconPosition = historyData.iconPosition
        iconAdapter.setSelectedIdx(historyData.iconPosition)
        show()
    }

    private fun setPrice(sb: StringBuilder, num: Int) {
        if (sb.toString().length > 10) {
            Toast.makeText(context, "the number is too large", Toast.LENGTH_SHORT)
                .show()
            return
        }
        when {
            num < 10 -> {
                tvPrice.text = sb.append(num).toString()
            }
            num == ZERO -> {
                tvPrice.text = sb.append(0).toString()
            }
            num == DOT -> {
                if (sb.toString().contains(".")) return
                tvPrice.text = sb.append('.').toString()
            }
        }
    }

    private val formatter: NumberFormat = DecimalFormat("#,###.##")

    fun setDate(date: String) {
        tvAddItemDate.text = date
    }

    fun isShow(): LiveData<Boolean> {
        return isShow
    }

    fun show(source: String) {
        curSource.value = source
        show()
    }

    fun show() {
        mainActivity?.hideIndicator()
        isShow.value = true
        if (view.visibility != View.VISIBLE) {
            val y = view.height.toFloat()
            view.animate()
                .translationY(y)
                .alpha(ALPHA_HIDE)
                .setDuration(200)
                .withEndAction {
                    view.visibility = View.VISIBLE

                    view.animate()
                        .translationY(0f)
                        .setDuration(ADD_ANIM_DURATION)
                        .alpha(ALPHA_SHOW)
                        .start()
                }
                .start()
        } else {
            view.animate()
                .translationY(0f)
                .setDuration(ADD_ANIM_DURATION)
                .alpha(ALPHA_SHOW)
                .start()
        }
    }

    private fun clearData() {
        curMode = MODE_ADD
        etName.setText("")
        tvPrice.text = ""
        iconAdapter.setSelectedIdx(0)
        ivIcon.setImageResource(mainViewModel.getIconList()[0])
        curIconPosition = 0
        setTypeBackground(btExpense, btIncome)
        curType = HistoryData.TYPE_EXPENSE
    }

    fun dismiss() {
        mainActivity?.showIndicator()
        isShow.value = false
        dismissKeyboard()
        elIcons.collapse()
        elSources.collapse()
        hideSoftKeyboard()
        val y = view.height.toFloat()
        view.animate()
            .translationY(y)
            .alpha(ALPHA_HIDE)
            .setDuration(ADD_ANIM_DURATION)
            .start()
        clearData()
        //disposableSource.dispose()
    }

    private fun dismissKeyboard() {
        isKeyboardShow = false
        elKeyboard.collapse()
    }

    private fun showKeyboard() {
        hideSoftKeyboard()
        isKeyboardShow = true
        elKeyboard.expand()
    }

    private fun View.hideSoftKeyboard() {
        val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(windowToken, 0)
    }
}