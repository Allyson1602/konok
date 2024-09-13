
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.konok.ui.myBooks.OwnedBooksFragment
import com.example.konok.ui.myBooks.ReadBooksFragment

class MyBooksPagerAdapter(activity: FragmentActivity) : FragmentStateAdapter(activity) {

    override fun getItemCount(): Int = 2

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> ReadBooksFragment()
            1 -> OwnedBooksFragment()
            else -> throw IllegalStateException("Invalid position $position")
        }
    }

    override fun getItemId(position: Int): Long = position.toLong()

    override fun containsItem(itemId: Long): Boolean = itemId in 0..1
}
