import android.view.View
import androidx.viewpager2.widget.ViewPager2

private const val MIN_SCALE = 0.85f
private const val MIN_ALPHA = 0.5f

class BankCardTransformer : ViewPager2.PageTransformer {
    override fun transformPage(page: View, position: Float) {
        page.apply {
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
}