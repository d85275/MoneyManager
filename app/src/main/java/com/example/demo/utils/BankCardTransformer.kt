import android.view.View
import androidx.viewpager2.widget.ViewPager2

class BankCardTransformer : ViewPager2.PageTransformer {
    override fun transformPage(page: View, position: Float) {
        page.apply {
            val myOffset: Float = position * -(2 * 30)
            when {
                position < -1 -> {
                    page.translationY = -myOffset
                }
                position <= 1 -> {
                    page.translationY = myOffset
                }
                else -> {
                    page.translationY = myOffset
                }
            }
        }
    }
}