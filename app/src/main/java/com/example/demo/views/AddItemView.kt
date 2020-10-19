package com.example.demo.views

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.LinearLayout
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.demo.R
import com.example.demo.model.HistoryData
import com.example.demo.utils.CommonUtils
import com.example.demo.utils.OnSwipeTouchListener
import com.example.demo.viewmodels.MainViewModel
import com.example.demo.views.main.MainActivity
import kotlinx.android.synthetic.main.view_add_item.view.*
import kotlinx.android.synthetic.main.view_add_item.view.tvPrice
import java.lang.StringBuilder
import java.text.DecimalFormat
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*

class AddItemView(context: Context, attrs: AttributeSet?) : LinearLayout(context, attrs) {

    private var view: View =
        LayoutInflater.from(context).inflate(R.layout.view_add_item, this, true)
    private var source = HistoryData.SOURCE_CASH
    private var mainActivity: MainActivity? = null
    private lateinit var mainViewModel: MainViewModel
    private lateinit var iconAdapter: AddItemIconAdapter
    private var total = 0L
    private var curMode = MODE_ADD
    private var resumeData: HistoryData? = null

    private companion object {
        private const val ADD_ANIM_DURATION = 300L
        private const val ALPHA_SHOW = 1F
        private const val ALPHA_HIDE = 0F
        private const val DOT = 10
        private const val BACK = 12
        private const val ZERO = 11
        private const val MODE_ADD = 0
        private const val MODE_EDIT = 1
    }

    private var date: Date? = null

    init {
        setDate(Calendar.getInstance().time)
        setListeners()
    }

    fun init(source: String, mainViewModel: MainViewModel, mainActivity: MainActivity) {
        this.source = source
        this.mainViewModel = mainViewModel
        this.mainActivity = mainActivity
        setIconView()
    }

    fun init(mainViewModel: MainViewModel, mainActivity: MainActivity) {
        this.mainViewModel = mainViewModel
        this.mainActivity = mainActivity
        setIconView()
    }

    fun init(mainViewModel: MainViewModel) {
        this.mainViewModel = mainViewModel
        setIconView()
    }

    private fun setIconView() {
        iconAdapter = AddItemIconAdapter(mainViewModel.getIconList())
        ivIcon.setImageResource(mainViewModel.getIconList()[0])
        iconAdapter.setOnItemClickListener { icon ->
            ivIcon.setImageResource(icon)
        }
        rvIcons.layoutManager =
            LinearLayoutManager(view.context, LinearLayoutManager.HORIZONTAL, false)
        rvIcons.setHasFixedSize(true)
        rvIcons.adapter = iconAdapter
    }

    fun setSource(source: String) {
        this.source = source
    }

    fun setDate(date: Date?) {
        this.date = date
        if (date != null) tvAddItemDate.text = CommonUtils.addItemDate().format(date)
    }

    fun getTotal(): Long {
        return total
    }

    private fun goPreDay() {
        if (date == null) return
        val calendar = Calendar.getInstance()
        calendar.time = date!!
        calendar.add(Calendar.DAY_OF_YEAR, -1)
        setDate(calendar.time)
    }

    private fun goNextDay() {
        if (date == null) return
        val calendar = Calendar.getInstance()
        calendar.time = date!!
        calendar.add(Calendar.DAY_OF_YEAR, 1)
        date = calendar.time
        setDate(calendar.time)
    }

    private fun setListeners() {
        ivPreDay.setOnClickListener { goPreDay() }
        ivNextDay.setOnClickListener { goNextDay() }

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
            if (elIcons.isExpanded) {
                elIcons.collapse()
            } else {
                elIcons.expand()
            }
        }
        view.setOnTouchListener(object : OnSwipeTouchListener(context) {
            override fun onSwipeBottom() {
                super.onSwipeBottom()
                dismiss()
            }
        })
        setKeyboardListener()
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
        val type = HistoryData.TYPE_EXPENSE
        val source = this.source
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

    private fun clearData() {
        curMode = MODE_ADD
        etName.setText("")
        tvPrice.text = ""
    }

    fun resumeData(historyData: HistoryData) {
        //tvAddItemDate.text = historyData.date
        curMode = MODE_EDIT
        resumeData = historyData
        source = historyData.source
        setDate(CommonUtils.addItemDate().parse(historyData.date))
        tvPrice.text = formatter.format(historyData.price)
        etName.setText(historyData.name)
        CommonUtils.e("icon position: ${historyData.iconPosition}")
        ivIcon.setImageResource(mainViewModel.getIconList()[historyData.iconPosition])
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

    private var isShow = MutableLiveData(false)
    private var isKeyboardShow = false

    fun isShow(): LiveData<Boolean> {
        return isShow
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

    fun dismiss() {
        mainActivity?.showIndicator()
        isShow.value = false
        dismissKeyboard()
        elIcons.collapse()
        hideSoftKeyboard()
        val y = view.height.toFloat()
        view.animate()
            .translationY(y)
            .alpha(ALPHA_HIDE)
            .setDuration(ADD_ANIM_DURATION)
            .start()
        clearData()
        iconAdapter.setSelectedIdx(0)
        ivIcon.setImageResource(mainViewModel.getIconList()[0])
    }

    private fun dismissKeyboard() {
        isKeyboardShow = false
        /*
        vKeyboard.animate()
            .alpha(0f)
            .setDuration(300)
            .withEndAction {
                vKeyboard.visibility = View.INVISIBLE

            }.start()
         */
        elKeyboard.collapse()
        //btConfirm.visibility = View.VISIBLE
    }

    private fun showKeyboard() {
        hideSoftKeyboard()
        isKeyboardShow = true
        elKeyboard.expand()
        //btConfirm.visibility = View.INVISIBLE
        /*
        vKeyboard.visibility = View.VISIBLE
        vKeyboard.alpha = 0f
        vKeyboard.animate()
            .alpha(1f)
            .setDuration(300)
            .start()
         */
    }

    private fun View.hideSoftKeyboard() {
        val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(windowToken, 0)
    }
}